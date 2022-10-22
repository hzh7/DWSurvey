package net.diaowen.dwsurvey.service.impl;

import net.diaowen.common.base.entity.User;
import net.diaowen.common.base.service.AccountManager;
import net.diaowen.common.plugs.page.Page;
import net.diaowen.common.utils.RandomUtils;
import net.diaowen.dwsurvey.entity.Report;
import net.diaowen.dwsurvey.entity.SurveyDetail;
import net.diaowen.dwsurvey.entity.SurveyDirectory;
import net.diaowen.dwsurvey.service.ReportManager;
import org.hibernate.criterion.Criterion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("reportManager")
public class ReportManagerImpl implements ReportManager {
    @Autowired
    private AccountManager accountManager;

    @Override
    public void setBaseDao() {
    }

    @Transactional
    @Override
    public void save(Report t) {
//        User user = accountManager.getCurUser();
//        String userId=t.getUserId();
//        String id=t.getId();
//        if(id==null){
//            t.setUserId(user.getId());
//            userId=t.getUserId();
//        }
//        if(userId!=null && userId.equals(user.getId())){
//            reportDao.save(t);
//            //保存SurveyDirectory
//            if(t.getDirType()==2){
//                SurveyDetail surveyDetailTemp=t.getSurveyDetail();
//
//                SurveyDetail surveyDetail=surveyDetailManager.getBySurveyId(id);
//                if(surveyDetail!=null){
//                    if(surveyDetailTemp!=null){
//                        surveyDetail.setSurveyNote(surveyDetailTemp.getSurveyNote());
//                    }
//                }else{
//                    surveyDetail=new SurveyDetail();
//                    surveyDetail.setSurveyNote("非常感谢您的参与！如有涉及个人信息，我们将严格保密。");
//                }
//                surveyDetail.setDirId(t.getId());
//                surveyDetailManager.save(surveyDetail);
//            }
//        }
    }

    @Override
    public void delete(Report report) {

    }

    @Override
    public void delete(String s) {

    }

    @Override
    public Report get(String s) {
        return null;
    }

    @Override
    public Report getModel(String s) {
        return null;
    }

    @Override
    public Report findById(String s) {
        return null;
    }

    @Override
    public List<Report> findList(Criterion... criterions) {
        return null;
    }

    @Override
    public Page<Report> findPage(Page<Report> page, Criterion... criterion) {
        return null;
    }

    @Override
    public Report findUniqueBy(String id) {
        return null;
    }

    @Override
    public Report getReportByUser(String id, String userId) {
        return null;
    }

    @Override
    public void saveReport(Report entity) {

    }

    @Override
    public Page<Report> findPage(Page<Report> page, String surveyName, Integer surveyState) {
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
}
