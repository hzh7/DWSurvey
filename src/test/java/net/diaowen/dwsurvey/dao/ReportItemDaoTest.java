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
        int i = reportItemDao.updateStatue("1706942d-3641-49ad-adbb-10fb9b7084e7", 1, 2);
        System.out.println(i);
    }
}