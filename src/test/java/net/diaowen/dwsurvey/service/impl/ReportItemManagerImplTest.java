package net.diaowen.dwsurvey.service.impl;

import net.diaowen.dwsurvey.entity.ReportItem;
import net.diaowen.dwsurvey.service.ReportItemManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ReportItemManagerImplTest {

    @Autowired
    ReportItemManager reportItemManager;

    @Test
    void generatePdfReport() throws Exception {
        ReportItem b = reportItemManager.generatePdfReport("984539ed-e06d-4cc6-8396-3eda62ae1d1b", "");
        System.out.println(b);
    }


    @Test
    void matcherText() throws Exception {
//        String s = "<b><span style=\"font-size:14.0pt;font-family:宋体;\n" +
//                "mso-ascii-theme-font:major-fareast;mso-fareast-theme-font:major-fareast;\n" +
//                "mso-hansi-theme-font:major-fareast;mso-bidi-font-family:&quot;Times New Roman&quot;;\n" +
//                "mso-font-kerning:1.0pt;mso-ansi-language:EN-US;mso-fareast-language:ZH-CN;\n" +
//                "mso-bidi-language:AR-SA\">学习情感</span></b>";
        String s = "学习情感";
        int i = s.indexOf("</span></b>");
        System.out.println(i);
        String[] split = s.split("</span></b>")[0].split(">");
        System.out.println(Arrays.toString(split));
        System.out.println(split[split.length - 1]);

    }

    @Test
    void getSameAnswerInSurveyQu() {
        reportItemManager.getSameAnswerInSurveyQu("2fb7e1a7-faf3-48ce-bd17-0f96e62fd168", "37fd7a6e-15b1-4927-a8bb-4501182a58e4", "三年级");
    }
}
