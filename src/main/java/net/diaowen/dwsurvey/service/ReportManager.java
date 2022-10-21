package net.diaowen.dwsurvey.service;

import net.diaowen.common.plugs.page.Page;
import net.diaowen.common.service.BaseService;
import net.diaowen.dwsurvey.entity.Report;
import net.diaowen.dwsurvey.entity.SurveyDirectory;

import java.io.IOException;
import java.util.List;

/**
 * 报告处理
 * @author keyuan(keyuan258@gmail.com)
 *
 * https://github.com/wkeyuan/DWSurvey
 * http://dwsurvey.net
 */
public interface ReportManager extends BaseService<Report, String>{

	public Report findUniqueBy(String id);

	public Report getReportByUser(String id, String userId);

	public void saveReport(Report entity);

	public Page<Report> findPage(Page<Report> page,String surveyName,Integer surveyState);

	public Page<SurveyDirectory> findByUser(Page<SurveyDirectory> page, String surveyName);

	public List<SurveyDirectory> findByIndex();

}
