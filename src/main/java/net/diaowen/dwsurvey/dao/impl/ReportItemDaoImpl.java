package net.diaowen.dwsurvey.dao.impl;

import net.diaowen.common.dao.BaseDaoImpl;
import net.diaowen.dwsurvey.dao.ReportItemDao;
import net.diaowen.dwsurvey.entity.ReportItem;
import net.diaowen.dwsurvey.entity.SurveyAnswer;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
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
}
