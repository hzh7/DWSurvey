package net.diaowen.dwsurvey.service.impl;

import net.diaowen.dwsurvey.entity.Question;
import net.diaowen.dwsurvey.entity.SurveyAnswer;
import net.diaowen.dwsurvey.service.SurveyAnswerManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.List;
import java.util.Map;

@SpringBootTest
class SurveyAnswerManagerImplTest {
    @Autowired
    SurveyAnswerManager surveyAnswerManager;

    @Test
    void getQuAnswerInfo() {
        SurveyAnswer surveyAnswer = surveyAnswerManager.get("db4b32db-4200-4395-b1a2-58e551b70162");
        List<Question> answerDetail = surveyAnswerManager.findAnswerDetail(surveyAnswer);
        Map<String, Map<String, Object>> quAnswerInfo = surveyAnswerManager.parseQuAnswerInfo(surveyAnswer);
        System.out.println(quAnswerInfo);
        System.out.println(quAnswerInfo.get("2af691f8-f973-49f2-ad0e-b06b0128808d1"));
        System.out.println(quAnswerInfo.get("2af691f8-f973-49f2-ad0e-b06b0128808d").get("answer2"));

    }

    @Test
    void parseUserFromAnswer() {
        Date date = new Date();
        date.setTime(date.getTime() - 15*60*1000);
        System.out.println(date);
        List<SurveyAnswer> surveyAnswers = surveyAnswerManager.findByCreateTimeAfter(date);
        for (SurveyAnswer surveyAnswer : surveyAnswers) {
            System.out.println(surveyAnswer.getSurveyId());
        }
    }

    @Test
    void getCountByUserId() {
        Long countByUserId = surveyAnswerManager.getCountByUserId("661f9ac4-c738-447e-b470-d3b29addcc6d", "1", 1);
        System.out.println(countByUserId);

    }
}
