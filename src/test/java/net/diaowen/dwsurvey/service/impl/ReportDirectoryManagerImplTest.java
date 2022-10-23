package net.diaowen.dwsurvey.service.impl;

import net.diaowen.dwsurvey.entity.ReportDirectory;
import net.diaowen.dwsurvey.service.ReportDirectoryManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.Transient;

import java.lang.Thread;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ReportDirectoryManagerImplTest {

    @Autowired
    ReportDirectoryManager reportDirectoryManager;

    @Transient
    @Test
    void save() throws InterruptedException {
        System.out.println("hello");
        ReportDirectory report = reportDirectoryManager.getReport("984539ed-e06d-4cc6-8396-3eda62ae1d1b");
//        Thread.sleep(10000);
        System.out.println(report.getReportName());
        System.out.println(report);
        Thread.sleep(10000);
    }
}