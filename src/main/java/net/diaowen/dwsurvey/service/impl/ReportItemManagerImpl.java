package net.diaowen.dwsurvey.service.impl;

import net.diaowen.common.QuType;
import net.diaowen.common.plugs.page.Page;
import net.diaowen.common.service.BaseServiceImpl;
import net.diaowen.dwsurvey.dao.ReportItemDao;
import net.diaowen.dwsurvey.entity.*;
import net.diaowen.dwsurvey.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
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
    @Autowired
    private SurveyAnswerManager surveyAnswerManager;
    @Autowired
    private AnAnswerManager anAnswerManager;

    @Override
    public void setBaseDao() {
        this.baseDao = reportItemDao;
    }


//    @Override
//    public ReportItem findUniqueBy(String id) {
//        return reportItemDao.findUniqueBy("id", id);
//    }

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
    public ReportItem generatePdfReport(String reportId, String surveyAnswerId) throws Exception {
        if (reportId == null || surveyAnswerId == null) {
            throw new Exception("参数错误");
        }
        ReportItem newReportItem = new ReportItem();
        newReportItem.setReportId(reportId);
        newReportItem.setCreateDate(new Date());
        newReportItem.setGenerateStatus("初始化");
        newReportItem.setSurveyAnswerId(surveyAnswerId);

        // 报告量表、维度
        ArrayList<Map<String, Object>> dimMap = new ArrayList<>();
        ArrayList<Map<String, Object>> metricMap = new ArrayList<>();

        // 报告
        ReportDirectory report = reportDirectoryManager.getReport(reportId);
        // 答卷
        SurveyAnswer surveyAnswer = surveyAnswerManager.get(surveyAnswerId);
        // 答卷的题目及答案内容
        List<Question> questions = surveyAnswerManager.findAnswerDetail(surveyAnswer);

        // 报告中选中的题目
        List<ReportQuestion> reportQuestions = reportQuestionManager.findByReportId(reportId);
        for (Question question : questions) {
            ReportQuestion reportQuestion = reportQuestions.stream().filter(x -> x.getQuId().equals(question.getId())).findFirst().orElse(null);
            if (reportQuestion == null) {
                // 不是报告选中的题则跳过
                continue;
            }

            AnAnswer anAnswer = anAnswerManager.findAnswer(surveyAnswerId, reportQuestion.getQuId());
            if (reportQuestion.getReportQuType().equals(0)) {
                // 维度信息题
                HashMap<String, Object> quAnswerMap = new HashMap<>();
                quAnswerMap.put("key", reportQuestion.getQuTitle());
                quAnswerMap.put("value", getReportAnswer(question));
                dimMap.add(quAnswerMap);
            } else {
                // 量表题
                HashMap<String, Object> quAnswerMap = new HashMap<>();
                quAnswerMap.put("key", reportQuestion.getQuTitle());  // 问题题目
                quAnswerMap.put("score", getReportAnswer(question));  // 大题得分
                quAnswerMap.put("agv_score_grade", reportQuestion.getQuTitle());  // 该题年级均分
                quAnswerMap.put("agv_score_school", reportQuestion.getQuTitle());  // 该题全校均分
                quAnswerMap.put("agv_score_all", anAnswer.getAnswer());  // 该题全体均分
                dimMap.add(quAnswerMap);
            }

        }
        // 报告选中题目的答案
        System.out.println(reportQuestions);


        HashMap<String, Object> reportData = new HashMap<>();
        reportData.put("reportId", reportId);
        reportData.put("surveyId", report.getSurveyId());
//        reportData.put("reportItemId", reportItemId);

        // 人数相关的统计信息
        Map<String, Integer> statisticsMap = new HashMap<>();

        return null;
    }

    /**
     * 解析问题答案获取报告需要呈现的内容
     * @param question
     * @return
     */
    private Object getReportAnswer(Question question) {
        if (question.getQuType().equals(QuType.FILLBLANK)) {
            return question.getAnFillblank().getAnswer();
        }
        if (question.getQuType().equals(QuType.SCORE)) {
            List<AnScore> scores = question.getAnScores();
            int sum = scores.stream().mapToInt(x -> Integer.parseInt(x.getAnswserScore())).sum();
            return (float)sum/scores.size();
        }
        if (question.getQuType().equals(QuType.RADIO)) {
            String quItemId = question.getAnRadio().getQuItemId();
            QuRadio quRadio = question.getQuRadios().stream().filter(x -> x.getId().equals(quItemId)).findFirst().orElse(null);
            assert quRadio != null;
            return quRadio.getOptionName();
        }
        return null;
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
     *     },
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
    @Override
    public boolean generatePreviewPdfReport(String reportId) {
        ReportDirectory report = reportDirectoryManager.getReport(reportId);
        // 报告中选中的题目
        List<ReportQuestion> reportQuestions = reportQuestionManager.findByReportId(reportId);
        System.out.println(reportQuestions);

        HashMap<String, Object> reportData = new HashMap<>();
        reportData.put("reportId", reportId);
        reportData.put("surveyId", report.getSurveyId());
        reportData.put("reportItemId", null);

        // 人数相关的统计信息
        Map<String, Integer> statisticsMap = new HashMap<>();
        statisticsMap.put("grade_range_uv", 12);
        statisticsMap.put("same_grade_uv", 23);
        statisticsMap.put("school_num", 34);
        statisticsMap.put("same_school_uv", 45);
        statisticsMap.put("same_school_grade_uv", 56);
        reportData.put("statistics", statisticsMap);

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

    /**
     * 获取一份问卷中指定题目回答了相同答案的答卷列表
     * 如获取所有三年级学生的答卷信息
     * @param surveyId
     * @param quId
     * @param targetAnswer
     * @return
     */
    public List<SurveyAnswer> getSameAnswerInSurveyQu(String surveyId, String quId, String targetAnswer) {
        List<String> sameAnswerInSurveyQu = reportItemDao.getSameAnswerInSurveyQu(surveyId, quId, targetAnswer);
        System.out.println(sameAnswerInSurveyQu);
        return null;
    }

}
