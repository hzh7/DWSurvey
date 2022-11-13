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
	public List<ReportItem> findByUserId(String userId, String surveyAnswerId);
	public List<ReportItem> findByStatus(String reportId, Integer generateStatus);

	public List<SurveyDirectory> findByIndex();

	/**
	 * 生成pdf报告
	 */
	public ReportItem initAndGeneratePdfReport(String reportId, String surveyAnswerId) throws Exception;

	/**
	 * 将surveyAnswer答卷相关的所有报告归档
	 */
	public void archivedReportItemBySurveyAnswer(SurveyAnswer surveyAnswer);

	/**
	 * 新增了一份答卷时，触发问卷对应所有报告的生成
	 */
	public void initReportItemBySurveyAnswer(SurveyAnswer surveyAnswer) throws Exception;
	public ReportItem generatePdfReport(ReportItem reportItem) throws Exception;

	/**
	 * 初始化问卷的报告,用于当新配置了报告历史报告未生成情况
	 */
	public void initReportItem(String reportId, Boolean rebuild);

	public ReportItem initReportItem(String reportId, String surveyAnswerId) throws Exception;

	/**
	 * 生成一份配置的报告的预览pdf
	 * @param reportId 报告id
	 */
	public boolean generatePreviewPdfReport(String reportId) throws Exception;

	public List<SurveyAnswer> getSameAnswerInSurveyQu(String surveyId, String quId, String targetAnswer);

	public List<ReportItem> findByReportId(String reportId);

	/**
	 * 获取配置报告下的当前报告数量
	 * @param report
	 */
	public int getReportItemNum(ReportDirectory report);

}
