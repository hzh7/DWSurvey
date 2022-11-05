package net.diaowen.dwsurvey.service.impl;

import net.diaowen.common.base.entity.User;
import net.diaowen.common.base.service.AccountManager;
import net.diaowen.common.plugs.page.Page;
import net.diaowen.common.service.BaseServiceImpl;
import net.diaowen.dwsurvey.dao.ReportDirectoryDao;
import net.diaowen.dwsurvey.dao.SurveyDirectoryDao;
import net.diaowen.dwsurvey.entity.ReportDirectory;
import net.diaowen.dwsurvey.entity.ReportItem;
import net.diaowen.dwsurvey.entity.SurveyDetail;
import net.diaowen.dwsurvey.entity.SurveyDirectory;
import net.diaowen.dwsurvey.service.ReportDirectoryManager;
import net.diaowen.dwsurvey.service.ReportItemManager;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service("reportDirectoryManager")
public class ReportDirectoryManagerImpl extends BaseServiceImpl<ReportDirectory, String> implements ReportDirectoryManager {
    @Autowired
    private ReportDirectoryDao reportDirectoryDao;
    @Autowired
    ReportItemManager reportItemManager;
    @Autowired
    private AccountManager accountManager;
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


//    @Transactional
//    @Override
//    public void saveBaseUp(ReportDirectory t) {
//        //判断有无，有则更新
//        ReportDirectory reportDirectory = findById(t.getId());
//        if(reportDirectory!=null){
//
//            reportDirectory.setReportName(t.getReportName());
//            reportDirectory.setReportNum(t.getReportNum());
//            reportDirectory.setReportState(t.getReportState());
//            reportDirectory.setUserId(t.getUserId());
//            reportDirectory.setVisibility(t.getVisibility());
//            super.save(reportDirectory);
//        }
//
//    }

    @Transactional
    @Override
    public void delete(String id) {
        //设为不可见
        ReportDirectory reportDirectory = get(id);
        reportDirectory.setVisibility(0);
        reportDirectoryDao.save(reportDirectory);
    }
}
