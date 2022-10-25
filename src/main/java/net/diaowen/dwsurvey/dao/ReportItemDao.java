package net.diaowen.dwsurvey.dao;

import net.diaowen.common.dao.BaseDao;
import net.diaowen.dwsurvey.entity.ReportItem;
import net.diaowen.dwsurvey.entity.SurveyAnswer;

import java.util.List;

public interface ReportItemDao extends BaseDao<ReportItem, String> {
    public List<String> getSameAnswerInSurveyQu(String surveyId, String quId, String targetAnswer);
}
