package net.diaowen.dwsurvey.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import net.diaowen.common.base.entity.IdEntity;
import org.hibernate.annotations.Formula;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;
import java.util.List;

/**
 * 在问卷中被选中需要作为报告内容的题目
 * @author keyuan
 *
 * https://github.com/wkeyuan/DWSurvey
 * http://dwsurvey.net
 */
@Entity
@Table(name="t_report_survey_question")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler" })
public class ReportSurveyQuestion extends IdEntity{

	private String surveyId;
	private String quId;
	private String quType;
	private Integer visibility=1;
	//是否公开结果  0不  1公开

	public String getSurveyId() {
		return surveyId;
	}

	public void setSurveyId(String surveyId) {
		this.surveyId = surveyId;
	}

	public String getQuId() {
		return quId;
	}

	public void setQuId(String quId) {
		this.quId = quId;
	}

	public String getQuType() {
		return quType;
	}

	public void setQuType(String quType) {
		this.quType = quType;
	}

	public Integer getVisibility() {
		return visibility;
	}

	public void setVisibility(Integer visibility) {
		this.visibility = visibility;
	}
}
