package net.diaowen.dwsurvey.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import net.diaowen.common.base.entity.IdEntity;
import org.hibernate.annotations.Proxy;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * 报告目录及报告
 */
@Entity
@Table(name="t_report_directory")
@Proxy(lazy=false)
 public class ReportDirectory extends IdEntity{

	private String reportName;
	private String reportNameText;
	private String surveyId;
	private String userId;
	private String reportTemplateId;
	private String fileDataId;
	private Integer reportNum=0;
	private Date createDate;
	private Integer reportQuNum;
	private Integer reportState;
	//是否显示  1显示 0不显示
	private Integer visibility=1;
	private Integer surveyType;
	private Integer previewPdfState=0;
	private Integer minSampleSize=0;

	public Integer getMinSampleSize() {
		return minSampleSize;
	}

	public void setMinSampleSize(Integer minSampleSize) {
		this.minSampleSize = minSampleSize;
	}

	public Integer getPreviewPdfState() {
		return previewPdfState;
	}

	public void setPreviewPdfState(Integer previewPdfState) {
		this.previewPdfState = previewPdfState;
	}

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}


	public String getReportNameText() {
		return reportNameText;
	}

	public void setReportNameText(String reportNameText) {
		this.reportNameText = reportNameText;
	}


	public String getSurveyId() {
		return surveyId;
	}

	public void setSurveyId(String surveyId) {
		this.surveyId = surveyId;
	}


	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}


	public String getReportTemplateId() {
		return reportTemplateId;
	}

	public void setReportTemplateId(String reportTemplateId) {
		this.reportTemplateId = reportTemplateId;
	}


	public String getFileDataId() {
		return fileDataId;
	}

	public void setFileDataId(String fileDataId) {
		this.fileDataId = fileDataId;
	}


	public Integer getReportNum() {
		return reportNum;
	}

	public void setReportNum(Integer reportNum) {
		this.reportNum = reportNum;
	}


	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}


	public Integer getReportQuNum() {
		return reportQuNum;
	}

	public void setReportQuNum(Integer reportQuNum) {
		this.reportQuNum = reportQuNum;
	}


	public Integer getReportState() {
		return reportState;
	}

	public void setReportState(Integer reportState) {
		this.reportState = reportState;
	}


	public Integer getVisibility() {
		return visibility;
	}

	public void setVisibility(Integer visibility) {
		this.visibility = visibility;
	}

	public Integer getSurveyType() {
		return surveyType;
	}

	public void setSurveyType(Integer surveyType) {
		this.surveyType = surveyType;
	}

}
