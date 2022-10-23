package net.diaowen.dwsurvey.service.impl;

import net.diaowen.dwsurvey.entity.SurveyDetail;
import net.diaowen.dwsurvey.service.SurveyDetailManager;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SurveyDetailManagerImplTest {

    @Autowired
    SurveyDetailManager surveyDetailManager;

    @Test
    void save() {
        System.out.println("hello");
        SurveyDetail bySurveyId = surveyDetailManager.getBySurveyId("58960016-ff92-4de7-9585-9ac27f961247");
        System.out.println(bySurveyId.getId());
    }
}