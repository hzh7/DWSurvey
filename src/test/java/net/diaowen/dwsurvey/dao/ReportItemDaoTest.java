package net.diaowen.dwsurvey.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ReportItemDaoTest {

    @Autowired
    ReportItemDao reportItemDao;

    @Test
    void updateStatue() {
        int i = reportItemDao.updateStatue("1496ccfb-6a0e-4f38-8295-a3d8d07fc0df", 1, 1);
        System.out.println(i);
    }
}