package net.diaowen.dwsurvey.service.impl;

import net.diaowen.common.base.entity.User;
import net.diaowen.common.base.service.AccountManager;
import net.diaowen.common.plugs.page.Page;
import net.diaowen.common.service.BaseServiceImpl;
import net.diaowen.dwsurvey.dao.ReportDirectoryDao;
import net.diaowen.dwsurvey.dao.SurveyDirectoryDao;
import net.diaowen.dwsurvey.entity.*;
import net.diaowen.dwsurvey.service.QuestionManager;
import net.diaowen.dwsurvey.service.ReportDirectoryManager;
import net.diaowen.dwsurvey.service.ReportItemManager;
import net.diaowen.dwsurvey.service.SurveyDirectoryManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static net.diaowen.dwsurvey.common.CommonStatic.QU_JOIN_CHAR;

@Service("reportDirectoryManager")
public class ReportDirectoryManagerImpl extends BaseServiceImpl<ReportDirectory, String> implements ReportDirectoryManager {
    @Autowired
    private ReportDirectoryDao reportDirectoryDao;
    @Autowired
    ReportItemManager reportItemManager;
    @Autowired
    private AccountManager accountManager;
    @Autowired
    private QuestionManager questionManager;
    @Autowired
    private SurveyDirectoryManager surveyDirectoryManager;
    @Override
    public void setBaseDao() {
        this.baseDao = reportDirectoryDao;
    }

    public ReportDirectory getReport(String id) {
        if(id==null || "".equals(id)){
            return new ReportDirectory();
        }
        return get(id);
    }


//    @Override
    public Page<ReportDirectory> findPage(Page<ReportDirectory> page, String surveyName, Integer surveyState) {
        return null;
    }

    @Override
    public Page<ReportDirectory> findByUser(Page<ReportDirectory> page, String reportName) {
        User user=accountManager.getCurUser();
        if(user!=null){
            List<Criterion> criterions=new ArrayList<Criterion>();
            criterions.add(Restrictions.eq("userId", user.getId()));
            criterions.add(Restrictions.eq("visibility", 1));
            if(StringUtils.isNotEmpty(reportName)) criterions.add(Restrictions.like("reportName", "%"+reportName+"%"));
            page.setOrderBy("createDate");
            page.setOrderDir("desc");
            page=reportDirectoryDao.findPageList(page,criterions);
            // 报告数量
            for (ReportDirectory reportDirectory : page.getResult()) {
                reportDirectory.setReportNum(reportItemManager.getReportItemNum(reportDirectory));
            }
        }
        return page;
    }

    @Override
    public List<ReportDirectory> findByState(Integer reportState) {
        Criterion c1 = Restrictions.eq("reportState", reportState);
        Criterion c2 = Restrictions.eq("visibility", 1);
        return reportDirectoryDao.find(c1, c2);
    }

    @Override
    public List<ReportDirectory> findBySurveyId(String surveyId) {
        Criterion c1 = Restrictions.eq("surveyId", surveyId);
        Criterion c2 = Restrictions.eq("visibility", 1);
        return reportDirectoryDao.find(c1, c2);
    }


    @Override
    public boolean reportQuSave(List<Question> questions, String reportId) throws Exception {
        // 报告所选题目进行有效性检查
        List<Question> showQuestions = questions.stream().filter(Question::getShowInReport).collect(Collectors.toList());
        surveyDirectoryManager.devCheck(showQuestions);

        // 配置了需要显示在报告中的题目id
        ArrayList<String> showInReportQus = new ArrayList<>();
        // 保存题目
        for (Question question : questions) {
            questionManager.save(question);
            if (question.getShowInReport()) {
                showInReportQus.add(question.getId());
            }
        }
        ReportDirectory reportDirectory = findById(reportId);
        reportDirectory.setReportQuIds(Strings.join(showInReportQus, QU_JOIN_CHAR));
        save(reportDirectory);
        return true;
    }

    @Transactional
    @Override
    public void delete(String id) {
        //设为不可见
        ReportDirectory reportDirectory = get(id);
        reportDirectory.setVisibility(0);
        reportDirectoryDao.save(reportDirectory);
    }
}
