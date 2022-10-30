package net.diaowen.dwsurvey.dao.impl;

import net.diaowen.common.dao.BaseDaoImpl;
import net.diaowen.dwsurvey.dao.ReportItemDao;
import net.diaowen.dwsurvey.entity.ReportItem;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.NativeQuery;
import org.springframework.stereotype.Repository;

import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public class ReportItemDaoImpl extends BaseDaoImpl<ReportItem, String> implements ReportItemDao {

    @Override
    public List<String> getSameAnswerInSurveyQu(String surveyId, String quId, String targetAnswer) {
        //        String sql="select count(case when answer='' then answer end) emptyCount, count(case when answer!='' then answer end) blankCount from t_an_answer where visibility=1 and qu_id=?";
        String sql = "select t_survey_answer.id \n" +
                "from t_survey_answer \n" +
                "join t_question on t_survey_answer.survey_id = t_question.belong_id \n" +
                "join t_an_radio on t_an_radio.qu_id = t_question.id \n" +
                "join t_qu_radio on t_qu_radio.qu_id = t_question.id and t_an_radio.qu_item_id = t_qu_radio.id \n" +
                "where survey_id = ? and t_question.tag = '2' and t_question.id = ? and t_qu_radio.option_name = ?\n";
        List resultList = this.getSession().createSQLQuery(sql)
                .setParameter(1, surveyId)
                .setParameter(2, quId)
                .setParameter(3, targetAnswer)
                .getResultList();
        return resultList;

    }

    @Override
    public ReportItem findByReportIdAndSurveyAnswerId(String reportId, String surveyAnswerId) {
        Criterion criterion1= Restrictions.eq("reportId", reportId);
        Criterion criterion2= Restrictions.eq("surveyAnswerId", surveyAnswerId);
        return findFirst(criterion1, criterion2);
    }

    @Override
    public List<ReportItem> findByReportId(String reportId) {
        Criterion criterion = Restrictions.eq("reportId", reportId);
        return find(criterion);
    }

    @Transactional
    @Override
    public int updateStatue(String Id, Integer newStatus, Integer oldStatus) {
        String sql="UPDATE t_report_item SET generate_status=? WHERE id=? and generate_status=?";
        NativeQuery query=this.getSession().createSQLQuery(sql);
        query.setParameter(1, newStatus);
        query.setParameter(2, Id);
        query.setParameter(3, oldStatus);
        return query.executeUpdate();
    }

    @Transactional
    @Override
    public int updateStatue(String Id, Integer newStatus) {
        String sql="UPDATE t_report_item SET generate_status=? WHERE id=?";
        NativeQuery query=this.getSession().createSQLQuery(sql);
        query.setParameter(1, newStatus);
        query.setParameter(2, Id);
        return query.executeUpdate();
    }
}
