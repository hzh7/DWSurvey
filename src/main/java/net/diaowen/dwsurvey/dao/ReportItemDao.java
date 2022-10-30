package net.diaowen.dwsurvey.dao;

import net.diaowen.common.dao.BaseDao;
import net.diaowen.dwsurvey.entity.ReportItem;

import java.util.List;

public interface ReportItemDao extends BaseDao<ReportItem, String> {
    /**
     * 获取一份问卷中指定题目回答了相同答案的答卷列表
     *     // todo 支持其他题型
     * @param surveyId
     * @param quId
     * @param targetAnswer
     * @return
     */
    public List<String> getSameAnswerInSurveyQu(String surveyId, String quId, String targetAnswer);

    public ReportItem findByReportIdAndSurveyAnswerId(String reportId, String surveyAnswerId);
    public List<ReportItem> findByReportId(String reportId);
    public int updateStatue(String Id, Integer newStatus, Integer oldStatus);
    public int updateStatue(String Id, Integer newStatus);
}
