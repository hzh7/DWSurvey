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

import java.util.*;
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

    /**
     * 生成配置的报告的预览pdf
     * 需要构造数据，形如：
     *  {
     *   "reportId": "111",  设计的报告id
     *   "surveyId": "222",  问卷id
     *   "reportItemId": "333",  具体一份报告的id
     *   "statistics": {
     *         "grade_range_uv": 123,  # 同学段人数（初中）
     *         "same_grade_uv": 123,  # 同年级人数
     *         "school_num": 123,  # 学校数量
     *         "same_school_uv": 123,  # 同学校人数
     *         "same_school_grade_uv": 123  # 同学校同年级人数
     *     },,
     *   "dim": [
     *     {
     *       "key": "姓名",
     *       "value": "xxx"
     *     },
     *     {
     *       "key": "年级",
     *       "value": "xxx"
     *     },
     *     {
     *       "key": "在班级中的排名",
     *       "value": "xxx"
     *     }
     *   ],
     *   "metric": [
     *     {
     *       "key": "学习迁移",
     *       "score": 2.9,
     *       "agv_score_grade": 2.9,
     *       "agv_score_school": 2.4,
     *       "agv_score_all": 2.2,
     *       "percentile": 79
     *     },
     *     {
     *       "key": "学习情感",
     *       "score": 2.9,
     *       "agv_score_grade": 2.9,
     *       "agv_score_school": 2.4,
     *       "agv_score_all": 2.2,
     *       "percentile": 79
     *     }
     *   ]
     * }
     */
    private boolean generatePreviewPdfReport(String reportId) {
        ReportDirectory report = reportDirectoryManager.getReport(reportId);
        // 报告中选中的题目
        List<ReportQuestion> reportQuestions = reportQuestionManager.findByReportId(reportId);
        System.out.println(reportQuestions);

        HashMap<String, Object> reportData = new HashMap<>();
        reportData.put("reportId", reportId);
        reportData.put("surveyId", report.getSurveyId());
        reportData.put("reportItemId", null);

        // 报告量表、维度
        ArrayList<Map<String, Object>> dimMap = new ArrayList<>();
        ArrayList<Map<String, Object>> metricMap = new ArrayList<>();
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
                .forEach(x -> {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("key", matcherText(x.getQuTitle()));
                    map.put("value", "xxx");
                    dimMap.add(map);
                });
        reportQuestions.stream().filter(x -> x.getReportQuType().equals(1))
                .forEach(x -> metricMap.add(buildMetric(x)));
        reportData.put("dim",dimMap);
        reportData.put("metric", metricMap);
        System.out.println(reportData);
        return false;
    }

    private HashMap<String, Object> buildMetric(ReportQuestion reportQuestion){
        HashMap<String, Object> scoreMap =new HashMap<>();
        scoreMap.put("key", reportQuestion.getReportQuTitle());
        // 该题得分
        scoreMap.put("score", String.format("%.1f", (float) (Math.random() * 5)));
        // 该题年级均分
        scoreMap.put("agv_score_grade", String.format("%.1f", (float) (Math.random() * 5)));
        // 该题全校均分
        scoreMap.put("agv_score_school", String.format("%.1f", (float) (Math.random() * 5)));
        // 该题全体均分
        scoreMap.put("agv_score_all", String.format("%.1f", (float) (Math.random() * 5)));
        // 百分位数
        scoreMap.put("percentile", String.format("%.1f", (float) (Math.random() * 100)));
        return scoreMap;
    }

    public static String matcherText(String s) {
        if (s == null) {
            return "";
        }
        String[] split = s.split("</span></b>")[0].split(">");
        return split[split.length - 1];
    }

}
