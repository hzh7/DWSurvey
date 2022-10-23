package net.diaowen.dwsurvey.service.impl;

import net.diaowen.dwsurvey.entity.SurveyDirectory;
import net.diaowen.dwsurvey.service.SurveyDetailManager;
import net.diaowen.dwsurvey.service.SurveyDirectoryManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SurveyDirectoryManagerImplTest {

    @Autowired
    SurveyDirectoryManager surveyDirectoryManager;
    @Test
    void getSurvey() {
        SurveyDirectory survey = surveyDirectoryManager.getSurvey("58960016-ff92-4de7-9585-9ac27f961247");
        System.out.println(survey);

    }
}