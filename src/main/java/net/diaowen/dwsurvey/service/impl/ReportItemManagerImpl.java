package net.diaowen.dwsurvey.service.impl;

import net.diaowen.common.dao.BaseDaoImpl;
import net.diaowen.common.plugs.httpclient.HttpResult;
import net.diaowen.common.plugs.page.Page;
import net.diaowen.common.service.BaseServiceImpl;
import net.diaowen.dwsurvey.dao.ReportDirectoryDao;
import net.diaowen.dwsurvey.dao.ReportItemDao;
import net.diaowen.dwsurvey.entity.*;
import net.diaowen.dwsurvey.service.QuestionManager;
import net.diaowen.dwsurvey.service.ReportDirectoryManager;
import net.diaowen.dwsurvey.service.ReportItemManager;
import net.diaowen.dwsurvey.service.ReportQuestionManager;
import org.apache.poi.ss.formula.functions.T;
import org.hibernate.criterion.Criterion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service("ReportItemManagerImpl")
public class ReportItemManagerImpl extends BaseServiceImpl<ReportItem, String> implements ReportItemManager {
    @Autowired
    private ReportItemDao reportItemDao;
    @Autowired
    private ReportDirectoryManager reportDirectoryManager;
    @Autowired
    private ReportQuestionManager reportQuestionManager;
    @Autowired
    private QuestionManager questionManager;

    @Override
    public void setBaseDao() {
        this.baseDao = reportItemDao;
    }


    @Override
    public ReportItem findUniqueBy(String id) {
        return null;
    }

    @Override
    public ReportItem getReportByUser(String id, String userId) {
        return null;
    }

    @Override
    public void saveReport(ReportItem entity) {

    }

    @Override
    public Page<ReportItem> findPage(Page<ReportItem> page, String surveyName, Integer surveyState) {
        return null;
    }

    @Override
    public Page<SurveyDirectory> findByUser(Page<SurveyDirectory> page, String surveyName) {
        return null;
    }

    @Override
    public List<SurveyDirectory> findByIndex() {
        return null;
    }

    @Override
    public boolean generatePdfReport(String reportId, String itemId) throws Exception {
        if (reportId == null && itemId == null) {
            throw new Exception("参数错误");
        }
        if (itemId == null || itemId.equals("") || itemId.equals("0")) {
            return generatePreviewPdfReport(reportId);
        }
        return false;
    }

    private boolean generatePreviewPdfReport(String reportId) {
        ReportDirectory report = reportDirectoryManager.getReport(reportId);
        // 报告中选中的题目
        List<ReportQuestion> reportQuestions = reportQuestionManager.findByReportId(reportId);
        System.out.println(reportQuestions);

        // 报告维度
        HashMap<String, String> dimMap = new HashMap<>();
        // 报告量表
        HashMap<String, String> metricMap = new HashMap<>();
        // 选中题目的报告中题型：维度or量表
//        HashMap<String, Integer> quTypeMap = new HashMap<>();
//        reportQuestions.forEach(x -> quTypeMap.put(x.getId(), x.getReportQuType()));
//        // 选中的题目所对应的原始问卷题
//        String[] quIds = reportQuestions.stream().map(ReportQuestion::getQuId).toArray(String[]::new);
//        List<Question> surveyQus = questionManager.findByQuIds(quIds, false);
//        surveyQus.stream().filter(x -> quTypeMap.get(x.getId()).equals(0))
//                .map(x -> matcherText(x.getQuTitle()))
//                .forEach(x -> dimMap.put(x, "xxx"));
//        surveyQus.stream().filter(x -> quTypeMap.get(x.getId()).equals(1))
//                .map(x -> matcherText(x.getQuTitle()))
//                .forEach(x -> metricMap.put(x, "xxx"));  // 题目：xxx

        reportQuestions.stream().filter(x -> x.getReportQuType().equals(0))
                .map(x -> matcherText(x.getQuTitle()))
                .forEach(x -> dimMap.put(x, "xxx"));  // 题目：内容，如 姓名：张三
        reportQuestions.stream().filter(x -> x.getReportQuType().equals(1))
                .map(x -> matcherText(x.getQuTitle()))
                .forEach(x -> metricMap.put(x, String.format("%.1f", (float) (Math.random() * 5))));  // 题目：得分，如 学习迁移：4.5
        System.out.println(dimMap);
        System.out.println(metricMap);
        return false;
    }

    private String matcherText(String s) {
        if (s == null) {
            return "";
        }
        String[] split = s.split("</span></b>")[0].split(">");
        return split[split.length - 1];
    }

}
