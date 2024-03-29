package net.diaowen.dwsurvey.entity;

import net.diaowen.common.base.entity.IdEntity;
import org.hibernate.annotations.Proxy;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
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
    private String reportName;
    private String pdfAddr;
    private Date createDate;
    private String userId;
    private String userName;
    private Integer generateStatus;
    private String generateMsg;
    private Date generateDate;
    private Integer visibility=1;
    private String quAnswerInfo;

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }


    public Date getGenerateDate() {
        return generateDate;
    }

    public void setGenerateDate(Date generateDate) {
        this.generateDate = generateDate;
    }
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    public Integer getVisibility() {
        return visibility;
    }

    public void setVisibility(Integer visibility) {
        this.visibility = visibility;
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


    public Integer getGenerateStatus() {
        return generateStatus;
    }

    public void setGenerateStatus(Integer generateStatus) {
        this.generateStatus = generateStatus;
    }


    public String getGenerateMsg() {
        return generateMsg;
    }

    public void setGenerateMsg(String generateMsg) {
        this.generateMsg = generateMsg;
    }

    @Transient
    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public String getQuAnswerInfo() {
        return quAnswerInfo;
    }

    public void setQuAnswerInfo(String quAnswerInfo) {
        this.quAnswerInfo = quAnswerInfo;
    }

    @Override
    public String toString() {
        return "ReportItem{" +
                "sid='" + sid + '\'' +
                ", surveyAnswerId='" + surveyAnswerId + '\'' +
                ", reportId='" + reportId + '\'' +
                ", pdfAddr='" + pdfAddr + '\'' +
                ", createDate=" + createDate +
                ", userId='" + userId + '\'' +
                ", generateStatus='" + generateStatus + '\'' +
                ", generateMsg='" + generateMsg + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
