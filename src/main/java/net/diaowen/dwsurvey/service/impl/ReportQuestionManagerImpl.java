package net.diaowen.dwsurvey.service.impl;

import net.diaowen.common.plugs.page.Page;
import net.diaowen.common.service.BaseServiceImpl;
import net.diaowen.dwsurvey.dao.ReportQuestionDao;
import net.diaowen.dwsurvey.dao.SurveyDetailDao;
import net.diaowen.dwsurvey.entity.ReportDirectory;
import net.diaowen.dwsurvey.entity.ReportQuestion;
import net.diaowen.dwsurvey.service.ReportQuestionManager;
import org.hibernate.criterion.Criterion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("ReportQuestionManagerImpl")
public class ReportQuestionManagerImpl extends BaseServiceImpl<ReportQuestion, String> implements ReportQuestionManager {

    @Autowired
    ReportQuestionDao reportQuestionDao;
    @Override
    public void setBaseDao() {
        this.baseDao=reportQuestionDao;
    }

    @Override
    public ReportQuestion findUniqueBy(String id) {
        if(id==null || "".equals(id)){
            return new ReportQuestion();
        }
        return reportQuestionDao.findUniqueBy("id",id);
    }

    @Override
    public Page<ReportQuestion> findByUser(Page<ReportQuestion> page, String reportName) {
        return null;
    }

    @Override
    public List<ReportQuestion> findByIndex() {
        return null;
    }

    @Override
    public void saveBaseUp(ReportQuestion t) {
        //判断有无，有则更新，无则新建
        ReportQuestion reportQuestion = findUniqueBy(t.getId());
        if(reportQuestion != null){
            reportQuestion.setReportId(t.getReportId());
            reportQuestion.setReportQuType(t.getReportQuType());
            reportQuestion.setQuId(t.getQuId());
            reportQuestion.setVisibility(t.getVisibility());
            super.save(reportQuestion);
        }
        else {
           super.save(t);
        }
    }

}