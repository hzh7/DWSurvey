package net.diaowen.dwsurvey.service;

import net.diaowen.common.plugs.page.Page;
import net.diaowen.common.service.BaseService;
import net.diaowen.dwsurvey.entity.ReportDirectory;
import net.diaowen.dwsurvey.entity.SurveyDetail;
import net.diaowen.dwsurvey.entity.SurveyDirectory;

import java.util.List;

/**
 * 报告处理
 */
public interface ReportDirectoryManager extends BaseService<ReportDirectory, String>{

	public ReportDirectory findUniqueBy(String id);
	public ReportDirectory getReport(String id);

	public ReportDirectory getReportByUser(String id, String userId);

	public void saveReport(ReportDirectory entity);

	public Page<ReportDirectory> findPage(Page<ReportDirectory> page, String surveyName, Integer surveyState);

	public Page<ReportDirectory> findByUser(Page<ReportDirectory> page, String reportName);

	public List<ReportDirectory> findByIndex();

	public void saveBaseUp(ReportDirectory t);

}
