package net.diaowen.dwsurvey.entity;

import net.diaowen.common.base.entity.IdEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 答卷题目小报告
 */
@Entity
@Table(name="t_an_answer_report")
public class AnAnswerReport extends IdEntity {

    private String reportItemId;
    private String answerId;
    private java.sql.Timestamp createDate;
    private long visibility;
    private long sumScore;
    private double scorePercentile;


    public String getReportItemId() {
        return reportItemId;
    }

    public void setReportItemId(String reportItemId) {
        this.reportItemId = reportItemId;
    }


    public String getAnswerId() {
        return answerId;
    }

    public void setAnswerId(String answerId) {
        this.answerId = answerId;
    }


    public java.sql.Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(java.sql.Timestamp createDate) {
        this.createDate = createDate;
    }


    public long getVisibility() {
        return visibility;
    }

    public void setVisibility(long visibility) {
        this.visibility = visibility;
    }


    public long getSumScore() {
        return sumScore;
    }

    public void setSumScore(long sumScore) {
        this.sumScore = sumScore;
    }


    public double getScorePercentile() {
        return scorePercentile;
    }

    public void setScorePercentile(double scorePercentile) {
        this.scorePercentile = scorePercentile;
    }
}
