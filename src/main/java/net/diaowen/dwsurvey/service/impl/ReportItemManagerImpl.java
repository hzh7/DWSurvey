package net.diaowen.dwsurvey.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import net.diaowen.common.QuType;
import net.diaowen.common.base.entity.User;
import net.diaowen.common.base.service.AccountManager;
import net.diaowen.common.plugs.page.Page;
import net.diaowen.common.service.BaseServiceImpl;
import net.diaowen.common.utils.HttpRequest;
import net.diaowen.common.utils.parsehtml.HtmlUtil;
import net.diaowen.dwsurvey.dao.ReportItemDao;
import net.diaowen.dwsurvey.entity.*;
import net.diaowen.dwsurvey.service.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static net.diaowen.dwsurvey.common.CommonStatic.*;
import static net.diaowen.dwsurvey.config.DWSurveyConfig.DWSURVEY_PDF_GENERATE_SERVER_URL;

@Service("ReportItemManagerImpl")
public class ReportItemManagerImpl extends BaseServiceImpl<ReportItem, String> implements ReportItemManager {

    private static final Logger logger = LogManager.getLogger(ReportItemManagerImpl.class.getName());

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
    @Autowired
    private AccountManager accountManager;
    @Override
    public void setBaseDao() {
        this.baseDao = reportItemDao;
    }


    @Override
    public ReportItem getReportByUser(String id, String userId) {
        return null;
    }

    @Override
    public void saveReport(ReportItem entity) {

    }

    @Override
    public Page<ReportItem> findPage(Page<ReportItem> page, String reportId, String userName) {
        List<Criterion> criterions = new ArrayList<>();
        criterions.add(Restrictions.eq("reportId", reportId));
        if (userName != null) {
            criterions.add(Restrictions.like("userName", "%"+userName+"%"));
        }
//        criterions.add(Restrictions.eq("visibility", 1));
        page.setOrderBy("createDate");
        page.setOrderDir("desc");
        page=reportItemDao.findPageList(page,criterions);
        return page;
    }

    @Override
    public List<ReportItem> findByUserId(String userId, String surveyAnswerId) {
        List<ReportItem> reportItems = null;
        Criterion c1 = Restrictions.eq("userId", userId);
        if (!Strings.isEmpty(surveyAnswerId)) {
            Criterion c2 = Restrictions.eq("surveyAnswerId", surveyAnswerId);
            reportItems = reportItemDao.find(c1, c2);
        }else {
            reportItems = reportItemDao.find(c1);
        }
//        if (!Strings.isEmpty(surveyId)) {
//            // 如果指定了surveyId，则根据reportIds关联出问卷id进行过滤
//            List<String> reportIds = reportItems.stream().map(ReportItem::getReportId).collect(Collectors.toList());
//            List<ReportDirectory> reportDirectories = reportDirectoryManager.findList(Restrictions.in("id", reportIds));
//            List<String> targetReportIds = reportDirectories.stream().filter(x -> x.getSurveyId().equals(surveyId)).map(IdEntity::getId).collect(Collectors.toList());
//            reportItems = reportItems.stream().filter(x -> targetReportIds.contains(x.getReportId())).collect(Collectors.toList());
//        }
        List<String> reportIds = reportItems.stream().map(ReportItem::getReportId).collect(Collectors.toList());
        List<ReportDirectory> reportDirectories = reportDirectoryManager.findList(Restrictions.in("id", reportIds));
        HashMap<String, String> reportIdNameMap = new HashMap<>();
        reportDirectories.forEach(x->reportIdNameMap.put(x.getId(), x.getReportName()));
        reportItems.forEach(x->x.setReportName(reportIdNameMap.get(x.getReportId())));
        return reportItems;
    }

    @Override
    public List<ReportItem> findByStatus(String reportId, Integer generateStatus) {
        Criterion c1 = Restrictions.eq("reportId", reportId);
        Criterion c2 = Restrictions.eq("generateStatus", generateStatus);
        return reportItemDao.find(c1, c2);
    }

    @Override
    public List<SurveyDirectory> findByIndex() {
        return null;
    }

    @Override
    public ReportItem initAndGeneratePdfReport(String reportId, String surveyAnswerId) throws Exception {
        if (reportId == null || surveyAnswerId == null) {
            throw new Exception("参数错误");
        }
        ReportItem reportItem = initReportItem(reportId, surveyAnswerId);
        return generatePdfReport(reportItem);
    }

    @Override
    public void initAndGenerateReportItem(SurveyAnswer surveyAnswer) throws Exception {
        String surveyId = surveyAnswer.getSurveyId();
        List<ReportDirectory> reportDirectories = reportDirectoryManager.findBySurveyId(surveyId);
        for (ReportDirectory reportDirectory : reportDirectories) {
            // 方案一 仅初始化 异步生成报告
            initReportItem(reportDirectory.getId(), surveyAnswer.getId());

            // 方案二 根据状态同步生成报告
            /* 若报告是激活中（样本量未达到预设值），仅初始化报告
            if (reportDirectory.getReportState().equals(REPORT_STATUS_ACTIVATED)) {
                initReportItem(reportDirectory.getId(), surveyAnswer.getId());
            }
            // 若报告是生效中，初始化报告并生成
            if (reportDirectory.getReportState().equals(REPORT_STATUS_EFFECTIVE)) {
                initAndGeneratePdfReport(reportDirectory.getId(), surveyAnswer.getId());
            }*/
        }

    }

    @Override
    public ReportItem generatePdfReport(ReportItem reportItem) {
        // if (reportItemDao.updateStatue(reportItem.getId(), REPORT_ITEM_STATUS_GENERATING) == 0) {
        //     logger.error("reportItem id: {} 更新为生成中状态失败", reportItem.getId());
        //     throw new Exception("更新为生成中状态失败");
        // }
        String reportId = reportItem.getReportId();
        // 报告量表、维度
        ArrayList<Map<String, Object>> dimMap = new ArrayList<>();
        ArrayList<Map<String, Object>> metricMap = new ArrayList<>();

        // 报告
        ReportDirectory report = reportDirectoryManager.get(reportId);
        // 答卷信息
        Map<String, Map<String, Object>> quAnswerInfo =  buildQuAnswerInfo(reportItem);

        // 当前报告下的所有报告项
        List<ReportItem> allReportItems = findByReportId(reportId);
        // 报告中选中的题目
        String[] quIds = report.getReportQuIds().split(String.valueOf(QU_JOIN_CHAR));
        List<Question> reportQuestions = questionManager.findByQuIds(quIds, true);

        // 同年级的答卷的量表题得分
        Map<String, List<Double>> sameGradeScoreList = new HashMap<>();
        // 同年级的答卷的量表题得分
        Map<String, List<Double>> sameSchoolScoreList = new HashMap<>();
        // 全体答卷的量表题得分
        Map<String, List<Double>> allScoreList = getTargetQuAgvScore(allReportItems, reportQuestions);
        // 年级 学校名称
        String gradeQuId = "";
        String grade = "";
        String schoolQuId = "";

        // 处理维度题
        for (Question question : reportQuestions) {
            if (question != null && question.getReportQuType().equals(0)) {
                // 维度信息题
                Map<String, Object> stringObjectMap = quAnswerInfo.get(question.getId());
                HashMap<String, Object> quAnswerMap = new HashMap<>();
                String title = stringObjectMap.get("title").toString();
                quAnswerMap.put("key", title);
                quAnswerMap.put("value", stringObjectMap.get("answer"));
                dimMap.add(quAnswerMap);
                // 若为年级题，获取同年级下答卷的所有得分列表
                if (quAnswerInfo.get(question.getId()).get("title").equals("年级")) {
                    gradeQuId = question.getId();
                    grade = quAnswerInfo.get(question.getId()).get("answer").toString();
                    sameGradeScoreList = getTargetQuAgvScore(allReportItems, reportQuestions, question, grade);
                }
                // 若为学校名称题，获取同学校名称下答卷的所有得分列表
                if (quAnswerInfo.get(question.getId()).get("title").equals("学校名称")) {
                    schoolQuId = question.getId();
                    String school = quAnswerInfo.get(question.getId()).get("answer").toString();
                    sameSchoolScoreList = getTargetQuAgvScore(allReportItems, reportQuestions, question, school);
                }
            }
        }

        // 处理量表题
        for (Question question : reportQuestions) {
            if (question == null || question.getReportQuType().equals(0)) {
                // 不是报告选中的题或者维度题则跳过
                continue;
            }
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
            // 该题得分在全年级中的百分位数
            quAnswerMap.put("percentile", percentileCalculate(Double.parseDouble(stringObjectMap.get("answer").toString()), sameGradeScoreList.get(question.getId())));
            metricMap.add(quAnswerMap);
        }

        // 人数相关的统计信息
        Map<String, Object> statisticsMap = new HashMap<>();
        statisticsMap.put("grade", grade);
        String gradeRange;
        if (PRIMARY_SCHOOL.contains(grade)) {
            gradeRange = "小学";
        } else if (JUNIOR_HIGH_SCHOOL.contains(grade)) {
            gradeRange = "初中";
        } else {
            gradeRange = "高中";
        }
        statisticsMap.put("grade_range", gradeRange);
        statisticsMap.put("grade_range_uv", (int) getGradeRangeUv(allReportItems, gradeQuId, grade));
        String finalGradeQuId = gradeQuId;
        statisticsMap.put("same_grade_uv",  (int) allReportItems.stream().filter(
                x -> {
                    Map<String, Map<String, Object>> theQuAnswerInfo =  buildQuAnswerInfo(x);
                    return theQuAnswerInfo.get(finalGradeQuId).get("answer").toString().equals(quAnswerInfo.get(finalGradeQuId).get("answer").toString());
                }
        ).count());

        String finalSchoolQuId = schoolQuId;
        statisticsMap.put("school_num", (int) allReportItems.stream().map(x -> {
            Map<String, Map<String, Object>> theQuAnswerInfo =  buildQuAnswerInfo(x);
            return theQuAnswerInfo.get(finalSchoolQuId).get("answer").toString();
        }).distinct().count());

        statisticsMap.put("same_school_uv", (int) allReportItems.stream().filter(
                x -> {
                    Map<String, Map<String, Object>> theQuAnswerInfo =  buildQuAnswerInfo(x);
                    return theQuAnswerInfo.get(finalSchoolQuId).get("answer").toString().equals(quAnswerInfo.get(finalSchoolQuId).get("answer").toString());
                }
        ).count());

        statisticsMap.put("same_school_grade_uv", (int) allReportItems.stream().filter(
                x -> {
                    Map<String, Map<String, Object>> theQuAnswerInfo =  buildQuAnswerInfo(x);
                    return (theQuAnswerInfo.get(finalGradeQuId).get("answer").toString().equals(quAnswerInfo.get(finalGradeQuId).get("answer").toString()) &&
                            theQuAnswerInfo.get(finalSchoolQuId).get("answer").toString().equals(quAnswerInfo.get(finalSchoolQuId).get("answer").toString()));
                }
        ).count());

        HashMap<String, Object> reportData = new HashMap<>();
        reportData.put("reportId", reportId);
        reportData.put("surveyId", report.getSurveyId());
        reportData.put("reportItemId", reportItem.getId());
        reportData.put("dimMap", dimMap);
        reportData.put("metricMap", metricMap);
        reportData.put("statisticsMap", statisticsMap);

        // 把构建的reportData来生成报告
        System.out.println("reportData: " + reportData);
        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(reportData));
        String pdfPath;
        try {
            pdfPath = postGeneratePdf(jsonObject);
            logger.info("reportItem id: {} pdf path {}", reportItem.getId(), pdfPath);
        } catch (Exception e) {
            reportItem.setGenerateStatus(REPORT_ITEM_STATUS_FAILED);
            reportItemDao.save(reportItem);
            throw new RuntimeException(e);
        }
        reportItem.setPdfAddr(pdfPath);
        // if (reportItemDao.updateStatue(reportItem.getId(), REPORT_ITEM_STATUS_SUCCESS, REPORT_ITEM_STATUS_GENERATING) == 0) {
        //     logger.error("reportItem id: {} 更新为成功状态失败", reportItem.getId());
        //     throw new Exception("更新为成功状态失败");
        // }
        reportItem.setGenerateStatus(REPORT_ITEM_STATUS_SUCCESS);
        reportItem.setGenerateDate(new Date());
        reportItemDao.save(reportItem);
        logger.info("reportItem id: {} 报告生成完成", reportItem.getId());
        return reportItem;
    }

    /**
     * 统计同年级段的人数
     */
    private long getGradeRangeUv(List<ReportItem> allReportItems, String gradeQuId, String grade) {
        List<String> targetGradeRange;
        if (PRIMARY_SCHOOL.contains(grade)) {
            targetGradeRange = PRIMARY_SCHOOL;
        } else if (JUNIOR_HIGH_SCHOOL.contains(grade)) {
            targetGradeRange = JUNIOR_HIGH_SCHOOL;
        } else {
            targetGradeRange = HIGH_SCHOOL;
        }
        List<String> finalTargetGradeRange = targetGradeRange;
        return allReportItems.stream().filter(
                x -> {
                    Map<String, Map<String, Object>> theQuAnswerInfo =  buildQuAnswerInfo(x);
                    return finalTargetGradeRange.contains(theQuAnswerInfo.get(gradeQuId).get("answer").toString());
                }
        ).count();
    }

    @Transactional
    @Override
    public void initReportItem(String reportId, Boolean rebuild) {
        // 报告
        ReportDirectory report = reportDirectoryManager.getReport(reportId);
        // 当前报告设定的问卷的所有答卷
        List<SurveyAnswer> allSurveyAnswers = surveyAnswerManager.findBySurveyId(report.getSurveyId());
        for (SurveyAnswer surveyAnswer : allSurveyAnswers) {
            // reportId + surveyAnswerId:唯一性约束,已有报告认为重新生成
            ReportItem newReportItem = reportItemDao.findByReportIdAndSurveyAnswerId(reportId, surveyAnswer.getId());
            if (newReportItem == null) {
                newReportItem = new ReportItem();
                newReportItem.setReportId(reportId);
                newReportItem.setCreateDate(new Date());
                newReportItem.setGenerateStatus(REPORT_ITEM_STATUS_INIT);
                newReportItem.setSurveyAnswerId(surveyAnswer.getId());
            }
            // 解析答卷的用户信息
            if (!Strings.isEmpty(surveyAnswer.getUserId())) {
                newReportItem.setUserId(surveyAnswer.getUserId());
                User user = accountManager.getUser(surveyAnswer.getUserId());
                newReportItem.setUserName(user.getName());
            }
            if (rebuild) {
                // 是否重新生成
                newReportItem.setGenerateStatus(REPORT_ITEM_STATUS_INIT);
                newReportItem.setGenerateDate(null);
            }
            // 答卷内容解析
            HashMap<String, HashMap<String, Object>> quAnswerInfo = parseQuAnswerInfo(surveyAnswer);
            String jsonString = JSON.toJSONString(quAnswerInfo);
            newReportItem.setQuAnswerInfo(jsonString);
            reportItemDao.save(newReportItem);
            logger.info("reportId {} reportItem id: {} initReportItem done", reportId, newReportItem.getId());
        }
    }

    /**
     * 构建一份答卷的问题答案信息
     */
    private HashMap<String, HashMap<String, Object>> parseQuAnswerInfo(SurveyAnswer t) {
        HashMap<String, HashMap<String, Object>> result = new HashMap<>();
        // 答卷的题目及答案内容
        List<Question> questions = surveyAnswerManager.findAnswerDetail(t);
        for (Question question : questions) {
            HashMap<String, Object> questionAnswer = getQuestionAnswer(question);
            if (questionAnswer.containsKey("title") && questionAnswer.containsKey("answer")) {
                result.put(question.getId(), questionAnswer);
            }
        }
        return result;
    }

    /**
     * 解析问题答案获取报告需要呈现的内容
     */
    private HashMap<String, Object> getQuestionAnswer(Question question) {
        HashMap<String, Object> result = new HashMap<>();
        if (question.getQuType().equals(QuType.FILLBLANK)) {
            result.put("title", HtmlUtil.removeTagFromText(question.getReportQuTitle()));
            result.put("answer", question.getAnFillblank().getAnswer());
        }
        if (question.getQuType().equals(QuType.SCORE)) {
            result.put("title", HtmlUtil.removeTagFromText(question.getQuTitle()));
            int sum = 0;
            for (QuScore quScore : question.getQuScores()) {
                AnScore first = question.getAnScores().stream().filter(y -> y.getQuRowId().equals(quScore.getId())).findFirst().get();
                if (quScore.getScoringType().equals(1)) {
                    // 如果是反向计分
                    sum += 6 - Integer.parseInt(first.getAnswserScore());
                } else {
                    sum +=  Integer.parseInt(first.getAnswserScore());
                }
            }
            result.put("answer", (float) (sum/ question.getQuScores().size()));
        }
        if (question.getQuType().equals(QuType.RADIO)) {
            String quItemId = question.getAnRadio().getQuItemId();
            QuRadio quRadio = question.getQuRadios().stream().filter(x -> x.getId().equals(quItemId)).findFirst().orElse(null);
            assert quRadio != null;
            result.put("title", HtmlUtil.removeTagFromText(question.getReportQuTitle()));
            result.put("answer", HtmlUtil.removeTagFromText(quRadio.getOptionName()));
        }
        // todo 其他题型
        return result;
    }

    @Transactional
    @Override
    public ReportItem initReportItem(String reportId, String surveyAnswerId) {
        if (reportId == null || surveyAnswerId == null) {
            return null;
        }
        // reportId + surveyAnswerId:唯一性约束,已有报告认为重新生成
        ReportItem newReportItem = reportItemDao.findByReportIdAndSurveyAnswerId(reportId, surveyAnswerId);
        if (newReportItem == null) {
            newReportItem = new ReportItem();
            newReportItem.setReportId(reportId);
            newReportItem.setCreateDate(new Date());
            newReportItem.setSurveyAnswerId(surveyAnswerId);
        }
        return initReportItem(newReportItem);
    }

    private ReportItem initReportItem(ReportItem reportItem) {
        // 解析答卷的用户信息
        SurveyAnswer surveyAnswer = surveyAnswerManager.get(reportItem.getSurveyAnswerId());
        reportItem.setUserId(surveyAnswer.getUserId());
        User user = accountManager.getUser(surveyAnswer.getUserId());
        reportItem.setUserName(user.getName());

        reportItem.setGenerateStatus(REPORT_ITEM_STATUS_INIT);  // todo
        // 答卷内容
        HashMap<String, HashMap<String, Object>> quAnswerInfo = parseQuAnswerInfo(surveyAnswer);
        String jsonString = JSON.toJSONString(quAnswerInfo);
        reportItem.setQuAnswerInfo(jsonString);
        reportItemDao.save(reportItem);
        logger.info("reportId {} reportItem id: {} initReportItem done", reportItem.getReportId(), reportItem.getId());
        return reportItem;
    }

    /**
     * 更新报告的数量
     */
    @Override
    public int getReportItemNum(ReportDirectory report) {
        List<ReportItem> byReportId = reportItemDao.findByReportId(report.getId());
        return byReportId.size();
    }

    private String postGeneratePdf(JSONObject json) throws Exception  {
        String url = DWSURVEY_PDF_GENERATE_SERVER_URL + "/generate_pdf";
        return HttpRequest.sendPost(url, json);
    }

    /**
     * 计算score在list中的百分位
     *
     * @return
     */
    private int percentileCalculate(Double score, List<Double> scores) {
        int cnt = 0;
        for (Double s : scores) {
            if (score >= s) {
                cnt++;
            }
        }
        return cnt * 100 / scores.size();
    }
    /**
     * 获取一份问卷中指定题目回答了相同答案的答卷，计算这些问卷的所有量表题得分
     */
    private Map<String, List<Double>> getTargetQuAgvScore(List<ReportItem> allReportItems, List<Question> reportQuestions, Question question, String answer) {
        // 找到目标题型的答案为 answer 的答卷
        List<ReportItem> targetReportItems = allReportItems.stream().filter(x -> {
            Map<String, Map<String, Object>> quAnswerInfo = buildQuAnswerInfo(x);
            return quAnswerInfo.get(question.getId()).get("answer").toString().equals(answer);
        }).collect(Collectors.toList());
        return getTargetQuAgvScore(targetReportItems, reportQuestions);
    }

    private Map<String, List<Double>> getTargetQuAgvScore(List<ReportItem> reportItems, List<Question> reportQuestions) {
        Map<String, List<Double>> result = new HashMap<>();
        if (reportItems.isEmpty()) {
            return result;
        }
        // 量表题的id找出来作为key
        reportQuestions.stream().filter(x->x.getReportQuType().equals(1)).forEach(x -> result.put(x.getId(), new ArrayList<>()));
        for (ReportItem reportItem : reportItems) {
            Map<String, Map<String, Object>> quAnswerInfo = buildQuAnswerInfo(reportItem);
            for (String key : quAnswerInfo.keySet()) {
                // 非需要计算均值的量表题 则跳过
                if (!result.containsKey(key)) {
                    continue;
                }
                // 量表题的得分加入list
                result.get(key).add(Double.parseDouble(quAnswerInfo.get(key).get("answer").toString()));
            }
        }
        return result;
    }

    /**
     * 获取答卷的回答信息，k-v形式（信息题）
     * 其中对于评分题，查询db计算，需要判断是否反向计分
     * metric：true；需要处理评分题；false：不处理
     */
    private Map<String, Map<String, Object>> buildQuAnswerInfo(ReportItem reportItem) {
        HashMap<String, Map<String, Object>> result = new HashMap<>();
        if (reportItem.getQuAnswerInfo() == null) {
            initReportItem(reportItem);
        }
        Map<String, Object> jsonObject = JSONObject.parseObject(reportItem.getQuAnswerInfo());
        if (jsonObject != null) {
            for (String s : jsonObject.keySet()) {
                result.put(s, JSONObject.parseObject(jsonObject.get(s).toString()));
            }
        }
        return result;
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
         *         "school_num": 123,  # 学校名称数量
         *         "same_school_uv": 123,  # 同学校名称人数
         *         "same_school_grade_uv": 123  # 同学校名称同年级人数
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
        String[] quIds = report.getReportQuIds().split(String.valueOf(QU_JOIN_CHAR));
        List<Question> reportQuestions = questionManager.findByQuIds(quIds, true);

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
                    map.put("key", HtmlUtil.removeTagFromText(x.getQuTitle()));
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

    private HashMap<String, Object> buildMetric(Question reportQuestion){
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

    public List<ReportItem> findByReportId(String reportId){
        List<ReportItem> reportItems = reportItemDao.findByReportId(reportId);
        return reportItems;
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
