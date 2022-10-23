package net.diaowen.dwsurvey.service.impl;

import net.diaowen.common.plugs.page.Page;
import net.diaowen.dwsurvey.entity.ReportQuestion;
import net.diaowen.dwsurvey.service.ReportQuestionManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ReportQuestionManagerImplTest {

    @Autowired
    ReportQuestionManager reportQuestionManager;

    @Test
    void findByReportId() {
        List<ReportQuestion> byReportId = reportQuestionManager.findByReportId("984539ed-e06d-4cc6-8396-3eda62ae1d1b");
        System.out.println(byReportId);
    }
}