package net.diaowen.dwsurvey.service.impl;

import net.diaowen.common.base.entity.User;
import net.diaowen.common.base.service.AccountManager;
import net.diaowen.common.plugs.page.Page;
import net.diaowen.common.service.BaseServiceImpl;
import net.diaowen.dwsurvey.dao.ReportDirectoryDao;
import net.diaowen.dwsurvey.dao.SurveyDirectoryDao;
import net.diaowen.dwsurvey.entity.ReportDirectory;
import net.diaowen.dwsurvey.entity.SurveyDetail;
import net.diaowen.dwsurvey.entity.SurveyDirectory;
import net.diaowen.dwsurvey.service.ReportDirectoryManager;
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
    private AccountManager accountManager;
    @Autowired
    private ReportDirectoryDao reportDirectoryDao;
    @Override
    public void setBaseDao() {
    }

    @Transactional
    @Override
    public void save(ReportDirectory t) {
        User user = accountManager.getCurUser();
        String userId=t.getUserId();
        String id=t.getId();
        if(id==null){
            t.setUserId(user.getId());
            userId=t.getUserId();
        }
        if(userId!=null && userId.equals(user.getId())){
            t.setCreateDate(new Date());
            reportDirectoryDao.save(t);
        }
    }

    @Override
    public void delete(ReportDirectory report) {

    }

    @Override
    public void delete(String s) {

    }

    @Override
    public ReportDirectory get(String s) {
        return null;
    }

    @Override
    public ReportDirectory getModel(String s) {
        return null;
    }

    @Override
    public ReportDirectory findById(String s) {
        return null;
    }

    @Override
    public List<ReportDirectory> findList(Criterion... criterions) {
        return null;
    }

    @Override
    public Page<ReportDirectory> findPage(Page<ReportDirectory> page, Criterion... criterion) {
        return null;
    }

    @Override
    public ReportDirectory findUniqueBy(String id) {
        return null;
    }

    @Override
    public ReportDirectory getReport(String id) {
        if(id==null || "".equals(id)){
            return new ReportDirectory();
        }
        return get(id);
    }

    @Override
    public ReportDirectory getReportByUser(String id, String userId) {
        return null;
    }

    @Override
    public void saveReport(ReportDirectory entity) {

    }

    @Override
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
        }
        return page;
    }

    @Override
    public List<ReportDirectory> findByIndex() {
        return null;
    }

    @Transactional
    @Override
    public void saveBaseUp(ReportDirectory t) {
        //判断有无，有则更新
        ReportDirectory reportDirectory = findById(t.getId());
        if(reportDirectory!=null){

            reportDirectory.setReportName(t.getReportName());
            reportDirectory.setReportNum(t.getReportNum());
            reportDirectory.setReportState(t.getReportState());
            reportDirectory.setUserId(t.getUserId());
            reportDirectory.setVisibility(t.getVisibility());
            super.save(reportDirectory);
        }

    }
}
