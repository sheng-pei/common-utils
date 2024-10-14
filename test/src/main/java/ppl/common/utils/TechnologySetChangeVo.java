package ppl.common.utils;

import java.util.Date;

//TODO 参数校验，内容过滤

/**
 * 待修改技术集基本信息
 */
public class TechnologySetChangeVo {
    /** ID */
    private Long id;
    /** 技术集名称 */
    private String name;
    /** 技术集ID */
    private String setIdentity;
    /** 技术集编号 */
    private String code;
    /** 密级名称 */
    private String secretLevelName;
    /** 技术集申请人 */
    private Long applicant;
    /** 责任单位 */
    private String dutyOrganization;
    /** 提交日期, xxxxxxxx */
    private Date commitDate;
    /** 技术领域 */
    private String technologyField;
    /** 技术方向 */
    private String technologyTend;
    /** 摘要 */
    private String _abstract;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSetIdentity() {
        return setIdentity;
    }

    public void setSetIdentity(String setIdentity) {
        this.setIdentity = setIdentity;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSecretLevelName() {
        return secretLevelName;
    }

    public void setSecretLevelName(String secretLevelName) {
        this.secretLevelName = secretLevelName;
    }

    public Long getApplicant() {
        return applicant;
    }

    public void setApplicant(Long applicant) {
        this.applicant = applicant;
    }

    public String getDutyOrganization() {
        return dutyOrganization;
    }

    public void setDutyOrganization(String dutyOrganization) {
        this.dutyOrganization = dutyOrganization;
    }

    public Date getCommitDate() {
        return commitDate;
    }

    public void setCommitDate(Date commitDate) {
        this.commitDate = commitDate;
    }

    public String getTechnologyField() {
        return technologyField;
    }

    public void setTechnologyField(String technologyField) {
        this.technologyField = technologyField;
    }

    public String getTechnologyTend() {
        return technologyTend;
    }

    public void setTechnologyTend(String technologyTend) {
        this.technologyTend = technologyTend;
    }

    public String getAbstract() {
        return _abstract;
    }

    public void setAbstract(String _abstract) {
        this._abstract = _abstract;
    }
}
