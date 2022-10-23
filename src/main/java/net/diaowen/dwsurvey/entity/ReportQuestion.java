package net.diaowen.dwsurvey.entity;

import java.util.Date;
import net.diaowen.common.base.entity.IdEntity;
import org.hibernate.annotations.Proxy;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 在问卷中被选中需要作为报告内容的题目
 */
@Entity
@Table(name="t_report_question")
@Proxy(lazy=false)
public class ReportQuestion extends IdEntity{

	private String quId;
	private String reportId;
	private Date createDate;
	private Integer reportQuType;
	private Integer orderById;
	private Integer visibility;
//	private Float agvScoreGrade;
//	private Float agvScoreSchool;
//	private Float agvScoreAll;
	private Float answerNum;
	private String quTitle;
	private String reportQuTitle;

	public ReportQuestion() {
		this.setCreateDate(new Date());
	}

	public ReportQuestion(String reportId, String quId, Integer reportQuType, Integer visibility) {
		this.quId = quId;
		this.reportId = reportId;
		this.reportQuType = reportQuType;
		this.visibility = visibility;
		this.createDate = new Date();
	}


	public String getQuTitle() {
		return quTitle;
	}

	public void setQuTitle(String quTitle) {
		this.quTitle = quTitle;
	}

	public String getReportQuTitle() {
		return reportQuTitle;
	}

	public void setReportQuTitle(String reportQuTitle) {
		this.reportQuTitle = reportQuTitle;
	}

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


	public Integer getReportQuType() {
		return reportQuType;
	}

	public void setReportQuType(Integer reportQuType) {
		this.reportQuType = reportQuType;
	}


	public Integer getOrderById() {
		return orderById;
	}

	public void setOrderById(Integer orderById) {
		this.orderById = orderById;
	}


	public Integer getVisibility() {
		return visibility;
	}

	public void setVisibility(Integer visibility) {
		this.visibility = visibility;
	}


	public Float getAnswerNum() {
		return answerNum;
	}

	public void setAnswerNum(Float answerNum) {
		this.answerNum = answerNum;
	}

}
