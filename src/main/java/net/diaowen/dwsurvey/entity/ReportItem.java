package net.diaowen.dwsurvey.entity;

import net.diaowen.common.base.entity.IdEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * 具体的一份问卷报告
 */
@Entity
@Table(name="t_report_item")
public class ReportItem  extends IdEntity {

    private String sid;
    private String surveyAnswerId;
    private String reportId;
    private String pdfAddr;
    private Date createDate;
    private String userId;
    private String generateStatus;
    private String generateMsg;


    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }


    public String getSurveyAnswerId() {
        return surveyAnswerId;
    }

    public void setSurveyAnswerId(String surveyAnswerId) {
        this.surveyAnswerId = surveyAnswerId;
    }


    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }


    public String getPdfAddr() {
        return pdfAddr;
    }

    public void setPdfAddr(String pdfAddr) {
        this.pdfAddr = pdfAddr;
    }


    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public String getGenerateStatus() {
        return generateStatus;
    }

    public void setGenerateStatus(String generateStatus) {
        this.generateStatus = generateStatus;
    }


    public String getGenerateMsg() {
        return generateMsg;
    }

    public void setGenerateMsg(String generateMsg) {
        this.generateMsg = generateMsg;
    }

}
