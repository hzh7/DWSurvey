package net.diaowen.dwsurvey.service;

import net.diaowen.common.plugs.page.Page;
import net.diaowen.common.service.BaseService;
import net.diaowen.dwsurvey.entity.ReportDirectory;
import net.diaowen.dwsurvey.entity.ReportQuestion;
import net.diaowen.dwsurvey.entity.SurveyDirectory;

import java.util.List;

/**
 * 报告处理
 */
public interface ReportQuestionManager extends BaseService<ReportQuestion, String>{

	public ReportQuestion findUniqueBy(String id);

	public Page<ReportQuestion> findByUser(Page<ReportQuestion> page, String reportName);

	public List<ReportQuestion> findByIndex();

	public void saveBaseUp(ReportQuestion t);

}