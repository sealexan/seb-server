/*
 * Copyright (c) 2019 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sebserver.webservice.servicelayer.session.impl;

import java.io.ByteArrayOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import ch.ethz.seb.sebserver.gbl.model.exam.Exam;
import ch.ethz.seb.sebserver.gbl.model.exam.Exam.ExamStatus;
import ch.ethz.seb.sebserver.gbl.model.session.ClientConnection;
import ch.ethz.seb.sebserver.gbl.profile.WebServiceProfile;
import ch.ethz.seb.sebserver.gbl.util.Result;
import ch.ethz.seb.sebserver.webservice.servicelayer.dao.ClientConnectionDAO;
import ch.ethz.seb.sebserver.webservice.servicelayer.dao.ExamDAO;
import ch.ethz.seb.sebserver.webservice.servicelayer.dao.RemoteProctoringRoomDAO;
import ch.ethz.seb.sebserver.webservice.servicelayer.sebconfig.ExamConfigService;

/** Handles caching for exam session and defines caching for following object:
 *
 * - Running exams (examId -> Exam)
 * - in-memory exam configuration (examId -> InMemorySEBConfig)
 * - active client connections (connectionToken -> ClientConnectionDataInternal)
 * - client event records for last ping store (connectionToken -> ReusableClientEventRecord) */
@Lazy
@Service
@WebServiceProfile
public class ExamSessionCacheService {

    public static final String CACHE_NAME_RUNNING_EXAM = "RUNNING_EXAM";
    public static final String CACHE_NAME_ACTIVE_CLIENT_CONNECTION = "ACTIVE_CLIENT_CONNECTION";
    public static final String CACHE_NAME_SEB_CONFIG_EXAM = "SEB_CONFIG_EXAM";

    private static final Logger log = LoggerFactory.getLogger(ExamSessionCacheService.class);

    private final ExamDAO examDAO;
    private final ClientConnectionDAO clientConnectionDAO;
    private final InternalClientConnectionDataFactory internalClientConnectionDataFactory;
    private final ExamConfigService sebExamConfigService;
    private final ExamUpdateHandler examUpdateHandler;

    protected ExamSessionCacheService(
            final ExamDAO examDAO,
            final ClientConnectionDAO clientConnectionDAO,
            final InternalClientConnectionDataFactory internalClientConnectionDataFactory,
            final ExamConfigService sebExamConfigService,
            final ExamUpdateHandler examUpdateHandler,
            final RemoteProctoringRoomDAO remoteProctoringRoomDAO) {

        this.examDAO = examDAO;
        this.clientConnectionDAO = clientConnectionDAO;
        this.internalClientConnectionDataFactory = internalClientConnectionDataFactory;
        this.sebExamConfigService = sebExamConfigService;
        this.examUpdateHandler = examUpdateHandler;
    }

    @Cacheable(
            cacheNames = CACHE_NAME_RUNNING_EXAM,
            key = "#examId",
            unless = "#result == null")
    public synchronized Exam getRunningExam(final Long examId) {

        if (log.isDebugEnabled()) {
            log.debug("Verify running exam for id: {}", examId);
        }

        final Result<Exam> byPK = this.examDAO.loadWithAdditionalAttributes(examId);
        if (byPK.hasError()) {
            log.error("Failed to find/load Exam with id {}", examId, byPK.getError());
            return null;
        }

        final Exam exam = byPK.get();
        if (!isRunning(exam)) {
            return null;
        }

        return exam;
    }

    @CacheEvict(
            cacheNames = CACHE_NAME_RUNNING_EXAM,
            key = "#exam.id")
    public Exam evict(final Exam exam) {

        if (log.isTraceEnabled()) {
            log.trace("Conditional eviction of running Exam from cache: {}", isRunning(exam));
        }

        return exam;
    }

    @CacheEvict(
            cacheNames = CACHE_NAME_RUNNING_EXAM,
            key = "#examId")
    public Long evict(final Long examId) {

        if (log.isTraceEnabled()) {
            log.trace("Conditional eviction of running Exam from cache: {}", examId);
        }

        return examId;
    }

    public boolean isRunning(final Exam exam) {
        if (exam == null || !exam.active) {
            return false;
        }

        switch (exam.status) {
            case RUNNING: {
                return true;
            }
            case UP_COMING:
            case FINISHED: {
                return this.examUpdateHandler.updateRunning(exam.id)
                        .map(e -> e.status == ExamStatus.RUNNING)
                        .getOr(false);
            }
            default: {
                return false;
            }
        }
    }

    @Cacheable(
            cacheNames = CACHE_NAME_ACTIVE_CLIENT_CONNECTION,
            key = "#connectionToken",
            unless = "#result == null")
    public ClientConnectionDataInternal getClientConnection(final String connectionToken) {

        if (log.isTraceEnabled()) {
            log.trace("Verify ClientConnection for running exam for caching by connectionToken: {}", connectionToken);
        }

        final ClientConnection clientConnection = getClientConnectionByToken(connectionToken);
        if (clientConnection == null) {
            return null;
        } else {
            return this.internalClientConnectionDataFactory.createClientConnectionData(clientConnection);
        }
    }

    @CacheEvict(
            cacheNames = CACHE_NAME_ACTIVE_CLIENT_CONNECTION,
            key = "#connectionToken")
    public void evictClientConnection(final String connectionToken) {
        if (log.isTraceEnabled()) {
            log.trace("Eviction of ClientConnectionData from cache: {}", connectionToken);
        }
    }

    @Cacheable(
            cacheNames = CACHE_NAME_SEB_CONFIG_EXAM,
            key = "#examId",
            sync = true)
    public InMemorySEBConfig getDefaultSEBConfigForExam(final Long examId, final Long institutionId) {
        try {

            final ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            final Long configId = this.sebExamConfigService.exportForExam(
                    byteOut,
                    institutionId,
                    examId);
            final Long followupId = this.sebExamConfigService
                    .getFollowupConfigurationId(configId)
                    .onError(error -> log.error("Failed to get follow-up id for config node: {}", configId, error))
                    .getOr(-1L);

            return new InMemorySEBConfig(configId, followupId, examId, byteOut.toByteArray());

        } catch (final Exception e) {
            log.error("Unexpected error while getting default exam configuration for running exam; {}", examId, e);
            throw e;
        }
    }

    public boolean isUpToDate(final InMemorySEBConfig inMemorySEBConfig) {
        try {
            final Long followupId = this.sebExamConfigService
                    .getFollowupConfigurationId(inMemorySEBConfig.configId)
                    .getOrThrow();

            return followupId.equals(inMemorySEBConfig.follwupId);
        } catch (final Exception e) {
            log.error("Failed to check if InMemorySEBConfig is up to date for: {}", inMemorySEBConfig);
            return true;
        }
    }

    @CacheEvict(
            cacheNames = CACHE_NAME_SEB_CONFIG_EXAM,
            key = "#examId")
    public void evictDefaultSEBConfig(final Long examId) {
        if (log.isTraceEnabled()) {
            log.trace("Eviction of default SEB Configuration from cache for exam: {}", examId);
        }
    }

    private ClientConnection getClientConnectionByToken(final String connectionToken) {
        final Result<ClientConnection> result = this.clientConnectionDAO
                .byConnectionToken(connectionToken);

        if (result.hasError()) {
            log.error("Failed to find/load ClientConnection with connectionToken {}",
                    connectionToken,
                    result.getError());
            return null;
        }
        return result.get();
    }

}
