package net.diaowen.dwsurvey.entity;

import java.util.Date;
import net.diaowen.common.base.entity.IdEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 在问卷中被选中需要作为报告内容的题目
 */
@Entity
@Table(name="t_report_question")
public class ReportQuestion extends IdEntity{

	private String quId;
	private String reportId;
	private Date createDate;
	private long reportQuType;
	private long orderById;
	private long visibility;
	private long agvScoreGrade;
	private long agvScoreSchool;
	private long agvScoreAll;
	private long answerNum;


	public String getQuId() {
		return quId;
	}

	public void setQuId(String quId) {
		this.quId = quId;
	}


	public String getReportId() {
		return reportId;
	}

	public void setReportId(String reportId) {
		this.reportId = reportId;
	}


	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}


	public long getReportQuType() {
		return reportQuType;
	}

	public void setReportQuType(long reportQuType) {
		this.reportQuType = reportQuType;
	}


	public long getOrderById() {
		return orderById;
	}

	public void setOrderById(long orderById) {
		this.orderById = orderById;
	}


	public long getVisibility() {
		return visibility;
	}

	public void setVisibility(long visibility) {
		this.visibility = visibility;
	}


	public long getAgvScoreGrade() {
		return agvScoreGrade;
	}

	public void setAgvScoreGrade(long agvScoreGrade) {
		this.agvScoreGrade = agvScoreGrade;
	}


	public long getAgvScoreSchool() {
		return agvScoreSchool;
	}

	public void setAgvScoreSchool(long agvScoreSchool) {
		this.agvScoreSchool = agvScoreSchool;
	}


	public long getAgvScoreAll() {
		return agvScoreAll;
	}

	public void setAgvScoreAll(long agvScoreAll) {
		this.agvScoreAll = agvScoreAll;
	}


	public long getAnswerNum() {
		return answerNum;
	}

	public void setAnswerNum(long answerNum) {
		this.answerNum = answerNum;
	}

}
