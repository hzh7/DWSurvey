package net.diaowen.dwsurvey.controller.report;

import net.diaowen.common.base.entity.User;
import net.diaowen.common.base.service.AccountManager;
import net.diaowen.common.plugs.httpclient.HttpResult;
import net.diaowen.common.plugs.httpclient.HttpStatus;
import net.diaowen.common.plugs.httpclient.PageResult;
import net.diaowen.common.plugs.httpclient.ResultUtils;
import net.diaowen.common.plugs.page.Page;
import net.diaowen.dwsurvey.entity.ReportDirectory;
import net.diaowen.dwsurvey.entity.SurveyDetail;
import net.diaowen.dwsurvey.entity.SurveyDirectory;
import net.diaowen.dwsurvey.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.Map;

@Controller
@RequestMapping("/api/dwsurvey/app/report")
public class MyReportController {

    @Autowired
    private AccountManager accountManager;
    @Autowired
    private ReportDirectoryManager reportDirectoryManager;
//    @Autowired
//    private SurveyAnswerManager surveyAnswerManager;
//    @Autowired
//    private SurveyStatsManager surveyStatsManager;

    /**
     * 拉取问卷列表
     * @param pageResult
     * @return
     */
    @RequestMapping(value = "/list.do",method = RequestMethod.GET)
    @ResponseBody
    public PageResult<ReportDirectory> list(PageResult<ReportDirectory> pageResult, String reportName) {

        User user = accountManager.getCurUser();
        if(user!=null){
            Page page = ResultUtils.getPageByPageResult(pageResult);
            page = reportDirectoryManager.findByUser(page, reportName);
            pageResult = ResultUtils.getPageResultByPage(page, pageResult);
        }
        return pageResult;
    }


    /**
     * 获取问卷详情
     * @param id
     * @return
     */
//    @RequestMapping(value = "/info.do",method = RequestMethod.GET)
//    @ResponseBody
//    public HttpResult<SurveyDirectory> info(String id) {
//        try{
//            User user = accountManager.getCurUser();
//            if(user!=null){
//                surveyStatsManager.findBySurvey(id);
//                SurveyDirectory survey = surveyDirectoryManager.findUniqueBy(id);
//                survey = surveyAnswerManager.upAnQuNum(survey);
//                return HttpResult.SUCCESS(survey);
//            }else{
//                return HttpResult.buildResult(HttpStatus.NOLOGIN);
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        return HttpResult.FAILURE();
//    }

    /**
     * 创建新报告
     */
    @RequestMapping(value = "/add.do",method = RequestMethod.POST)
    @ResponseBody
    public HttpResult add(@RequestBody ReportDirectory reportDirectory) {
        try{
            reportDirectory.setReportNameText(reportDirectory.getReportName());
            reportDirectoryManager.save(reportDirectory);
            return HttpResult.SUCCESS(reportDirectory);
        }catch (Exception e){
            e.printStackTrace();
        }
        return HttpResult.FAILURE();
    }


    /**
     * 问卷删除
     * @param map
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/delete.do",method = RequestMethod.DELETE)
    @ResponseBody
    public HttpResult delete(@RequestBody Map<String, String[]> map) throws Exception {
        String result = null;
        try{
            User curUser = accountManager.getCurUser();
            if(curUser!=null){
                if(map!=null){
                    if(map.containsKey("id")){
                        String[] ids = map.get("id");
                        if(ids!=null){
                            reportDirectoryManager.delete(Arrays.toString(ids));
                            return HttpResult.SUCCESS();
                        }
                    }
                }
            }
        }catch (Exception e) {
            result=e.getMessage();
        }
        return HttpResult.FAILURE(result);
    }


//    /**
//     * 修改状态
//     * @return
//     */
//    @RequestMapping(value = "/up-survey-status.do",method = RequestMethod.POST)
//    @ResponseBody
//    public HttpResult<SurveyDirectory> upSurveyState(String surveyId, Integer surveyState) {
//        try{
//            reportDirectoryManager.upSurveyState(surveyId,surveyState);
//            return HttpResult.SUCCESS();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        return HttpResult.FAILURE();
//    }


    /**
     * 保存更新基本属性
     */
    @RequestMapping(value = "/survey-base-attr.do",method = RequestMethod.PUT)
    @ResponseBody
    public HttpResult<SurveyDirectory> saveBaseAttr(@RequestBody ReportDirectory reportDirectory) {
        try{
            reportDirectoryManager.saveBaseUp(reportDirectory);
            return HttpResult.SUCCESS();
        }catch (Exception e){
            e.printStackTrace();
        }
        return HttpResult.FAILURE();
    }


}
