package ch.ethz.seb.sebserver.webservice.datalayer.batis.model;

import javax.annotation.Generated;

public class ClientInstructionRecord {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2019-12-13T19:17:48.845+01:00", comments="Source field: client_instruction.id")
    private Long id;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2019-12-13T19:17:48.845+01:00", comments="Source field: client_instruction.exam_id")
    private Long examId;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2019-12-13T19:17:48.846+01:00", comments="Source field: client_instruction.type")
    private String type;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2019-12-13T19:17:48.846+01:00", comments="Source field: client_instruction.connections")
    private String connections;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2019-12-13T19:17:48.846+01:00", comments="Source field: client_instruction.attributes")
    private String attributes;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2019-12-13T19:17:48.846+01:00", comments="Source field: client_instruction.active")
    private Integer active;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2019-12-13T19:17:48.845+01:00", comments="Source Table: client_instruction")
    public ClientInstructionRecord(Long id, Long examId, String type, String connections, String attributes, Integer active) {
        this.id = id;
        this.examId = examId;
        this.type = type;
        this.connections = connections;
        this.attributes = attributes;
        this.active = active;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2019-12-13T19:17:48.845+01:00", comments="Source field: client_instruction.id")
    public Long getId() {
        return id;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2019-12-13T19:17:48.846+01:00", comments="Source field: client_instruction.exam_id")
    public Long getExamId() {
        return examId;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2019-12-13T19:17:48.846+01:00", comments="Source field: client_instruction.type")
    public String getType() {
        return type;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2019-12-13T19:17:48.846+01:00", comments="Source field: client_instruction.connections")
    public String getConnections() {
        return connections;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2019-12-13T19:17:48.846+01:00", comments="Source field: client_instruction.attributes")
    public String getAttributes() {
        return attributes;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2019-12-13T19:17:48.846+01:00", comments="Source field: client_instruction.active")
    public Integer getActive() {
        return active;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table client_instruction
     *
     * @mbg.generated Fri Dec 13 19:17:48 CET 2019
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", examId=").append(examId);
        sb.append(", type=").append(type);
        sb.append(", connections=").append(connections);
        sb.append(", attributes=").append(attributes);
        sb.append(", active=").append(active);
        sb.append("]");
        return sb.toString();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table client_instruction
     *
     * @mbg.generated Fri Dec 13 19:17:48 CET 2019
     */
    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        ClientInstructionRecord other = (ClientInstructionRecord) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getExamId() == null ? other.getExamId() == null : this.getExamId().equals(other.getExamId()))
            && (this.getType() == null ? other.getType() == null : this.getType().equals(other.getType()))
            && (this.getConnections() == null ? other.getConnections() == null : this.getConnections().equals(other.getConnections()))
            && (this.getAttributes() == null ? other.getAttributes() == null : this.getAttributes().equals(other.getAttributes()))
            && (this.getActive() == null ? other.getActive() == null : this.getActive().equals(other.getActive()));
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table client_instruction
     *
     * @mbg.generated Fri Dec 13 19:17:48 CET 2019
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getExamId() == null) ? 0 : getExamId().hashCode());
        result = prime * result + ((getType() == null) ? 0 : getType().hashCode());
        result = prime * result + ((getConnections() == null) ? 0 : getConnections().hashCode());
        result = prime * result + ((getAttributes() == null) ? 0 : getAttributes().hashCode());
        result = prime * result + ((getActive() == null) ? 0 : getActive().hashCode());
        return result;
    }
}