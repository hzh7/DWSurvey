package net.diaowen.dwsurvey.service;

import net.diaowen.common.plugs.page.Page;
import net.diaowen.common.service.BaseService;
import net.diaowen.dwsurvey.entity.ReportDirectory;
import net.diaowen.dwsurvey.entity.ReportItem;
import net.diaowen.dwsurvey.entity.SurveyDirectory;

import java.util.List;

/**
 * 报告处理
 */
public interface ReportItemManager extends BaseService<ReportItem, String>{

	public ReportItem findUniqueBy(String id);

	public ReportItem getReportByUser(String id, String userId);

	public void saveReport(ReportItem entity);

	public Page<ReportItem> findPage(Page<ReportItem> page, String surveyName, Integer surveyState);

	public Page<SurveyDirectory> findByUser(Page<SurveyDirectory> page, String surveyName);

	public List<SurveyDirectory> findByIndex();

	/**
	 * 生成pdf报告
	 * @param reportId
	 * @param itemId
	 * @return
	 */
	public boolean generatePdfReport(String reportId, String itemId) throws Exception;

}
