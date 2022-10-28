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
        reportItemDao.save(newReportItem);

        // 报告量表、维度
        ArrayList<Map<String, Object>> dimMap = new ArrayList<>();
        ArrayList<Map<String, Object>> metricMap = new ArrayList<>();

        // 报告
        ReportDirectory report = reportDirectoryManager.getReport(reportId);
        // 目标答卷
        SurveyAnswer surveyAnswer = surveyAnswerManager.get(surveyAnswerId);
        Map<String, Map<String, Object>> quAnswerInfo = SurveyAnswerManager.getQuAnswerInfo(surveyAnswer.getQuAnswerInfo());

        // 当前报告设定的问卷的所有答卷
        List<SurveyAnswer> allSurveyAnswers = surveyAnswerManager.findBySurveyId(report.getSurveyId());
        // 答卷的题目及答案内容
        List<Question> questions = surveyAnswerManager.findAnswerDetail(surveyAnswer);
        // 报告中选中的题目
        List<ReportQuestion> reportQuestions = reportQuestionManager.findByReportId(reportId);

        // 同年级的答卷的量表题得分
        Map<String, List<Double>> sameGradeScoreList = new HashMap<>();
        // 同年级的答卷的量表题得分
        Map<String, List<Double>> sameSchoolScoreList = new HashMap<>();
        // 全体答卷的量表题得分
        Map<String, List<Double>> allScoreList = getTargetQuAgvScore(allSurveyAnswers, reportQuestions);
        // 年级 学校
        String gradeQuId = "";
        String schoolQuId = "";

        // 处理维度题
        for (Question question : questions) {
            ReportQuestion reportQuestion = reportQuestions.stream().filter(x -> x.getQuId().equals(question.getId())).findFirst().orElse(null);
            if (reportQuestion != null && reportQuestion.getReportQuType().equals(0)) {
                // 维度信息题
                dimMap.add(quAnswerInfo.get(question.getId()));
                // 若为年级题，获取同年级下答卷的所有得分列表
                if (quAnswerInfo.get(question.getId()).get("title").equals("年级")) {
                    gradeQuId = question.getId();
                    String grade = quAnswerInfo.get(question.getId()).get("answer").toString();
                    sameGradeScoreList = getTargetQuAgvScore(allSurveyAnswers, reportQuestions, question, grade);
                }
                // 若为学校题，获取同学校下答卷的所有得分列表
                if (quAnswerInfo.get(question.getId()).get("title").equals("学校")) {
                    schoolQuId = question.getId();
                    String school = quAnswerInfo.get(question.getId()).get("answer").toString();
                    sameSchoolScoreList = getTargetQuAgvScore(allSurveyAnswers, reportQuestions, question, school);
                }
            }
        }

        // todo
        System.out.println(sameGradeScoreList);
        System.out.println(sameSchoolScoreList);
        System.out.println(allScoreList);

        // 处理量表题
        for (Question question : questions) {
            ReportQuestion reportQuestion = reportQuestions.stream().filter(x -> x.getQuId().equals(question.getId())).findFirst().orElse(null);
            if (reportQuestion == null || reportQuestion.getReportQuType().equals(0)) {
                // 不是报告选中的题或者维度题则跳过
                continue;
            }
            AnAnswer anAnswer = anAnswerManager.findAnswer(surveyAnswerId, reportQuestion.getQuId());
            // 量表题
            Map<String, Object> stringObjectMap = quAnswerInfo.get(question.getId());
            HashMap<String, Object> quAnswerMap = new HashMap<>() ;
            quAnswerMap.put("key", stringObjectMap.get("title"));
            quAnswerMap.put("score", stringObjectMap.get("answer"));
            // 该题年级均分
            quAnswerMap.put("agv_score_grade", sameGradeScoreList.get(question.getId()).stream().mapToDouble(x->x).average().getAsDouble());
            // 该题全校均分
            quAnswerMap.put("agv_score_school", sameSchoolScoreList.get(question.getId()).stream().mapToDouble(x->x).average().getAsDouble());
            // 该题全体均分
            quAnswerMap.put("agv_score_all", allScoreList.get(question.getId()).stream().mapToDouble(x->x).average().getAsDouble());
            metricMap.add(quAnswerMap);
        }
        // 报告选中题目的答案
        System.out.println(reportQuestions);


        // 人数相关的统计信息
        Map<String, Integer> statisticsMap = new HashMap<>();
        statisticsMap.put("grade_range_uv", 0);  // todo
        String finalGradeQuId = gradeQuId;
        statisticsMap.put("same_grade_uv",  (int) allSurveyAnswers.stream().filter(
                x -> {
                    Map<String, Map<String, Object>> theQuAnswerInfo = SurveyAnswerManager.getQuAnswerInfo(x.getQuAnswerInfo());
                    return theQuAnswerInfo.get(finalGradeQuId).get("answer").toString().equals(quAnswerInfo.get(finalGradeQuId).get("answer").toString());
                }
        ).count());

        String finalSchoolQuId = schoolQuId;
        statisticsMap.put("school_num", (int) allSurveyAnswers.stream().map(x -> {
            Map<String, Map<String, Object>> theQuAnswerInfo = SurveyAnswerManager.getQuAnswerInfo(x.getQuAnswerInfo());
            return theQuAnswerInfo.get(finalSchoolQuId).get("answer").toString();
        }).distinct().count());

        statisticsMap.put("same_school_uv", (int) allSurveyAnswers.stream().filter(
                x -> {
                    Map<String, Map<String, Object>> theQuAnswerInfo = SurveyAnswerManager.getQuAnswerInfo(x.getQuAnswerInfo());
                    return theQuAnswerInfo.get(finalSchoolQuId).get("answer").toString().equals(quAnswerInfo.get(finalSchoolQuId).get("answer").toString());
                }
        ).count());

        statisticsMap.put("same_school_grade_uv", (int) allSurveyAnswers.stream().filter(
                x -> {
                    Map<String, Map<String, Object>> theQuAnswerInfo = SurveyAnswerManager.getQuAnswerInfo(x.getQuAnswerInfo());
                    return (theQuAnswerInfo.get(finalGradeQuId).get("answer").toString().equals(quAnswerInfo.get(finalGradeQuId).get("answer").toString()) &&
                            theQuAnswerInfo.get(finalSchoolQuId).get("answer").toString().equals(quAnswerInfo.get(finalSchoolQuId).get("answer").toString()));
                }
        ).count());

        HashMap<String, Object> reportData = new HashMap<>();
        reportData.put("reportId", reportId);
        reportData.put("surveyId", report.getSurveyId());
        reportData.put("dimMap", dimMap);
        reportData.put("metricMap", metricMap);
        reportData.put("statisticsMap", statisticsMap);

        // 把构建的reportData来生成报告
        System.out.println(reportData);

        return null;
    }

    /**
     * 获取一份问卷中指定题目回答了相同答案的答卷，计算这些问卷的所有量表题得分
     * @param surveyAnswers 指定问卷下的所有答卷
     * @param question
     * @param answer
     */
    private Map<String, List<Double>> getTargetQuAgvScore(List<SurveyAnswer> surveyAnswers, List<ReportQuestion> reportQuestions, Question question, String answer) {
        // 找到目标题型的答案为 answer 的答卷
        List<SurveyAnswer> targetSurveyAnswers = surveyAnswers.stream().filter(x -> {
            Map<String, Map<String, Object>> quAnswerInfo = SurveyAnswerManager.getQuAnswerInfo(x.getQuAnswerInfo());
            return quAnswerInfo.get(question.getId()).get("answer").toString().equals(answer);
        }).collect(Collectors.toList());
        return getTargetQuAgvScore(targetSurveyAnswers, reportQuestions);
    }

    private Map<String, List<Double>> getTargetQuAgvScore(List<SurveyAnswer> surveyAnswers, List<ReportQuestion> reportQuestions) {
        Map<String, List<Double>> result = new HashMap<>();
        if (surveyAnswers.isEmpty()) {
            return result;
        }
        Map<String, List<Double>> quMapScoreList = new HashMap<>();
        // 量表题的id找出来作为key
        reportQuestions.stream().filter(x->x.getReportQuType().equals(1)).forEach(x -> quMapScoreList.put(x.getId(), new ArrayList<>()));
        surveyAnswers.forEach(x -> {
            Map<String, Map<String, Object>> quAnswerInfo = SurveyAnswerManager.getQuAnswerInfo(x.getQuAnswerInfo());
            for (String key : quAnswerInfo.keySet()) {
                // 非需要计算均值的量表题 则跳过
                if (!quMapScoreList.containsKey(key)) {
                    continue;
                }
                // 量表题的得分加入list
                quMapScoreList.get(key).add((Double) quAnswerInfo.get(key).get("answer"));
            }
        });
        return result;
        // quMapScoreList.keySet().forEach(x -> {
        //     result.put(x, quMapScoreList.get(x).stream().mapToDouble(y->y).average().getAsDouble());
        // });
        // return result;
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
        List<String> sameAnswersInSurvey = reportItemDao.getSameAnswerInSurveyQu(surveyId, quId, targetAnswer);
        System.out.println(sameAnswersInSurvey);
        return null;
    }

    /**
     * 计算在一批答卷中，各题的得分列表
     * @param surveyAnswerIds 要考虑的答卷范围
     * @param quIds 需要计算的题目，当前仅支持量表类题目，即评分题
     * @return {quId_1: [score_1, score_2... score_n]}
     */
    public Map<String, ArrayList<Float>> getQuScore(String surveyId, List<String> surveyAnswerIds, List<String> quIds){
        Map<String, ArrayList<Float>> result = new HashMap<>();
        for (String quId : quIds) {
            result.put(quId, new ArrayList<>());
        }
        for (String surveyAnswerId : surveyAnswerIds) {
            SurveyAnswer surveyAnswer = new SurveyAnswer();  // 构建个临时对象用于传参，避免查db
            surveyAnswer.setSurveyId(surveyId);
            surveyAnswer.setId(surveyAnswerId);
            List<Question> questions = surveyAnswerManager.findAnswerDetail(surveyAnswer);
            for (Question question : questions) {
                if (!question.getQuType().equals(QuType.SCORE)) {
                    // 当前仅支持对评分题计算得分
                    continue;
                }
//                if (quIds.contains(question.getId())) {
//                    HashMap<String, Object> questionAnswer = getQuestionAnswer(question);
//                    result.get(question.getId()).add((Float) questionAnswer.get("answer"));
//                }
            }

        }
        return result;
    }

}
