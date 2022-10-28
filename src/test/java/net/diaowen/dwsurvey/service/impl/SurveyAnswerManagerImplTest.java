package net.diaowen.dwsurvey.service.impl;

import net.diaowen.dwsurvey.entity.SurveyAnswer;
import net.diaowen.dwsurvey.service.SurveyAnswerManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SurveyAnswerManagerImplTest {
    @Autowired
    SurveyAnswerManager surveyAnswerManager;

    @Test
    void getQuAnswerInfo() {
        SurveyAnswer surveyAnswer = surveyAnswerManager.get("2e219638-f768-45fe-8271-9862302b6e8f");
        Map<String, Map<String, Object>> quAnswerInfo = surveyAnswerManager.getQuAnswerInfo(surveyAnswer);
        System.out.println(quAnswerInfo);
        System.out.println(quAnswerInfo.get("2af691f8-f973-49f2-ad0e-b06b0128808d1"));
        System.out.println(quAnswerInfo.get("2af691f8-f973-49f2-ad0e-b06b0128808d").get("answer2"));

    }
}
