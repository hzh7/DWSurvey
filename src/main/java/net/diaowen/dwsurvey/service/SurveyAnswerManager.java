package net.diaowen.dwsurvey.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import net.diaowen.common.plugs.page.Page;
import net.diaowen.common.service.BaseService;
import net.diaowen.dwsurvey.entity.*;

/**
 * 问卷回答
 * @author keyuan(keyuan258@gmail.com)
 *
 * https://github.com/wkeyuan/DWSurvey
 * http://dwsurvey.net
 */
public interface SurveyAnswerManager extends BaseService<SurveyAnswer, String>{

	public void saveAnswer(SurveyAnswer surveyAnswer, Map<String, Map<String, Object>> quMaps);

	public List<Question> findAnswerDetail(SurveyAnswer answer);

	public List<SurveyAnswer> answersByIp(String surveyId, String ip);

	public SurveyAnswer getTimeInByIp(SurveyDetail surveyDetail, String ip);

	public Long getCountByIp(String surveyId, String ip);
	public Long getCountByUserId(String surveyId, String userId, Integer dateLimit);

	public String exportXLS(String surveyId, String savePath, boolean isExpUpQu);

	public SurveyStats surveyStatsData(SurveyStats surveyStats);


	/**
	 * 取出某份问卷的答卷数据
	 * @param page
	 * @param surveyId
	 * @return
	 */
	public Page<SurveyAnswer> answerPage(Page<SurveyAnswer> page, String surveyId);

	/**
	 * 取出指定用户的所有答卷数据
	 */
	public Page<SurveyAnswer> answerPageByUserId(Page<SurveyAnswer> page, String userId, String surveyName);

	public void deleteData(String[] ids);

	public int getquestionAnswer(String surveyAnswerId, Question question);

	public SurveyDirectory upAnQuNum(String surveyId);

	public SurveyDirectory upAnQuNum(SurveyDirectory surveyDirectory);

	public List<SurveyDirectory> upAnQuNum(List<SurveyDirectory> result);

	/**
	 * 获取 存在表里的json答卷的问题答案信息，转为map
	 */
	public Map<String, Map<String, Object>> parseQuAnswerInfo(SurveyAnswer t);

	public static Map<String, Map<String, Object>> parseQuAnswerInfo(String quAnswerInfo) {
		HashMap<String, Map<String, Object>> result = new HashMap<>();
		Map<String, Object> jsonObject = JSONObject.parseObject(quAnswerInfo);
		for (String s : jsonObject.keySet()) {
			result.put(s, JSONObject.parseObject(jsonObject.get(s).toString()));
		}
		return result;
	}

	public List<SurveyAnswer> findBySurveyId(String surveyId);
	public List<SurveyAnswer> findByCreateTimeAfter(Date time);

	/**
	 * 匿名下答卷解析问卷里的用户信息生成用户
	 */
	public void parseUserFromSurveyAnswer(SurveyAnswer surveyAnswer);

}
