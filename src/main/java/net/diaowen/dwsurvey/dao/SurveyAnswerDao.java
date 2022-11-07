package net.diaowen.dwsurvey.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import net.diaowen.common.dao.BaseDao;
import net.diaowen.dwsurvey.entity.SurveyStats;
import net.diaowen.dwsurvey.entity.SurveyAnswer;

public interface SurveyAnswerDao extends BaseDao<SurveyAnswer, String>{

	public void saveAnswer(SurveyAnswer surveyAnswer,
                           Map<String, Map<String, Object>> quMaps);

	public SurveyStats surveyStatsData(SurveyStats surveyStats);

    Long countResult(String id);

	/**
	 * 指定surveyId下的所有答卷
	 */
	public List<SurveyAnswer> findBySurveyId(String surveyId);

	public List<SurveyAnswer> findByCreateTimeAfter(Date time);

}
