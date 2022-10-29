package net.diaowen.dwsurvey.service;

import net.diaowen.common.plugs.page.Page;
import net.diaowen.common.service.BaseService;
import net.diaowen.dwsurvey.entity.ReportDirectory;
import net.diaowen.dwsurvey.entity.ReportItem;
import net.diaowen.dwsurvey.entity.SurveyAnswer;
import net.diaowen.dwsurvey.entity.SurveyDirectory;

import java.util.List;

/**
 * 报告处理
 */
public interface ReportItemManager extends BaseService<ReportItem, String>{

//	public ReportItem findUniqueBy(String id);

	public ReportItem getReportByUser(String id, String userId);

	public void saveReport(ReportItem entity);

	public Page<ReportItem> findPage(Page<ReportItem> page, String reportId, String userName);

	public List<SurveyDirectory> findByIndex();

	/**
	 * 生成pdf报告
	 */
	public ReportItem generatePdfReport(String reportId, String surveyAnswerId) throws Exception;

	/**
	 * 生成pdf报告
	 */
	public void initReportItem(String reportId);

	/**
	 * 生成一份配置的报告的预览pdf
	 * @param reportId 报告id
	 */
	public boolean generatePreviewPdfReport(String reportId) throws Exception;

	public List<SurveyAnswer> getSameAnswerInSurveyQu(String surveyId, String quId, String targetAnswer);

}
