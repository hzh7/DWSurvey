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
 * 报告目录及报告
 * @author keyuan
 *
 * https://github.com/wkeyuan/DWSurvey
 * http://dwsurvey.net
 */
@Entity
@Table(name="t_report")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler" })
public class Report extends IdEntity{

	//用于短链接的ID
	private String sid;
	private String reportName;
	private String reportNameText;
	//创建者ID
	private String userId;
	//问卷id
	private String surveyId;
	//创建时间
	private Date createDate=new Date();
	//问卷状态  0默认设计状态  1执行中 2结束
	private Integer surveyState=0;
	//问卷下面有多少题目数
	private Integer surveyQuNum=0;
	//报告数数
	private Integer reportNum;
	//是否显示  1显示 0不显示
	private Integer visibility=1;
	//是否公开结果  0不  1公开
	private Integer viewAnswer=0;
	//静态HTML保存路径
	private String htmlPath;
	private String jsonPath;


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

	public Integer getVisibility() {
		return visibility;
	}

	public void setVisibility(Integer visibility) {
		this.visibility = visibility;
	}

	public Integer getSurveyState() {
		return surveyState;
	}

	public void setSurveyState(Integer surveyState) {
		this.surveyState = surveyState;
	}
	public Integer getSurveyQuNum() {
		return surveyQuNum;
	}

	public void setSurveyQuNum(Integer surveyQuNum) {
		this.surveyQuNum = surveyQuNum;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Integer getViewAnswer() {
		return viewAnswer;
	}

	public void setViewAnswer(Integer viewAnswer) {
		this.viewAnswer = viewAnswer;
	}

	public String getHtmlPath() {
		return htmlPath;
	}

	public void setHtmlPath(String htmlPath) {
		this.htmlPath = htmlPath;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public String getJsonPath() {
		return jsonPath;
	}

	public void setJsonPath(String jsonPath) {
		this.jsonPath = jsonPath;
	}

	public String groupName;
	@Transient
	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	private SurveyDetail surveyDetail=null;
	@Transient
	public SurveyDetail getSurveyDetail() {
		return surveyDetail;
	}

	public void setSurveyDetail(SurveyDetail surveyDetail) {
		this.surveyDetail = surveyDetail;
	}

	//用户名
	private String userName;
	@Formula("(select o.name from t_user o where o.id = user_id)")
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}

	private List<Question> questions=null;
	@Transient
	public List<Question> getQuestions() {
		return questions;
	}

	public void setQuestions(List<Question> questions) {
		this.questions = questions;
	}

	private SurveyAnswer surveyAnswer;
	@Transient
	public SurveyAnswer getSurveyAnswer() {
		return surveyAnswer;
	}

	public void setSurveyAnswer(SurveyAnswer surveyAnswer) {
		this.surveyAnswer = surveyAnswer;
	}

}
