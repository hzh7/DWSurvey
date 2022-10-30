package net.diaowen.dwsurvey.controller.report;

import net.diaowen.common.base.entity.User;
import net.diaowen.common.base.service.AccountManager;
import net.diaowen.common.plugs.httpclient.HttpResult;
import net.diaowen.common.plugs.httpclient.PageResult;
import net.diaowen.common.plugs.httpclient.ResultUtils;
import net.diaowen.common.plugs.page.Page;
import net.diaowen.dwsurvey.entity.ReportDirectory;
import net.diaowen.dwsurvey.entity.ReportItem;
import net.diaowen.dwsurvey.entity.SurveyAnswer;
import net.diaowen.dwsurvey.service.ReportDirectoryManager;
import net.diaowen.dwsurvey.service.ReportItemManager;
import net.diaowen.dwsurvey.service.SurveyAnswerManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/api/dwsurvey/app/reportItem")
public class MyReportItemController {
    private static final Logger logger = LogManager.getLogger(MyReportItemController.class.getName());

    @Autowired
    private AccountManager accountManager;
    @Autowired
    private ReportDirectoryManager reportDirectoryManager;
    @Autowired
    private ReportItemManager reportItemManager;
    @Autowired
    private SurveyAnswerManager surveyAnswerManager;

    /**
     * 拉取问卷列表
     * @param pageResult
     * @return
     */
    @RequestMapping(value = "/list.do",method = RequestMethod.GET)
    @ResponseBody
    public PageResult<ReportItem> list(PageResult<ReportItem> pageResult, String reportId, String userName) {

        User user = accountManager.getCurUser();
        if(user!=null){
            Page page = ResultUtils.getPageByPageResult(pageResult);
            page = reportItemManager.findPage(page, reportId, userName);
            pageResult = ResultUtils.getPageResultByPage(page, pageResult);
        }
        return pageResult;
    }

    /**
     * 拉取指定用户的指定问卷的报告列表
     * @return
     */
    @RequestMapping(value = "/my-list.do",method = RequestMethod.GET)
    @ResponseBody
    public HttpResult myList(String surveyAnswerId) {
        User user = accountManager.getCurUser();
        if(user!=null){
            List<ReportItem> reportItems = reportItemManager.findByUserId(user.getId(), surveyAnswerId);
            return HttpResult.SUCCESS(reportItems);
        }
        return HttpResult.FAILURE();

    }


    /**
     * 生成一份答卷的具体报告
     * @param reportId 报告id
     * @param surveyAnswerId 相应的问卷答案id
     */
    @RequestMapping(value = "/generate.do",method = RequestMethod.GET)
    @ResponseBody
    public HttpResult generateReportItem(String reportId, String surveyAnswerId){
        // 无具体的surveyAnswerId， 针对报告设计完成后进行报告展示内容的预览
        if (surveyAnswerId == null || surveyAnswerId.equals("")) {
            try {
                reportItemManager.generatePreviewPdfReport(reportId);
            } catch (Exception e) {
                e.printStackTrace();
                return HttpResult.FAILURE(e.getMessage());
            }
        }
        // 生成报告
        ReportDirectory report = reportDirectoryManager.getReport(reportId);
        SurveyAnswer surveyAnswer = surveyAnswerManager.get(surveyAnswerId);
        if (!Objects.equals(report.getSurveyId(), surveyAnswer.getSurveyId())) {
            return HttpResult.FAILURE("答卷与报告配置不匹配");
        }
        try {
            return HttpResult.SUCCESS(reportItemManager.initAndGeneratePdfReport(reportId, surveyAnswerId));
        } catch (Exception e) {
            return HttpResult.SUCCESS("failed: " + e.getMessage());
        }
    }


    /**
     * 初始化问卷的报告,用于当新配置了报告历史报告未生成情况
     */
    @RequestMapping(value = "/init.do",method = RequestMethod.GET)
    @ResponseBody
    public HttpResult init(String reportId){
        try {
            reportItemManager.initReportItem(reportId);
            return HttpResult.SUCCESS();
        } catch (Exception e) {
            return HttpResult.SUCCESS("failed: " + e.getMessage());
        }
    }


    /**
     * 报告的状态
     * @param reportId
     * @param itemId
     * @return
     */
    @RequestMapping(value = "/state.do",method = RequestMethod.GET)
    @ResponseBody
    public HttpResult reportItemState(String reportId, String itemId){
        User curUser = accountManager.getCurUser();
        if (curUser == null) {
            return HttpResult.FAILURE("failed: 需要进行登录才可操作");
        }
        // 无 itemId， 针对报告设计完成后进行报告展示内容的预览
        if (itemId == null || itemId.equals("") || itemId.equals("0") ||
                reportId == null || reportId.equals("") || reportId.equals("0")) {
            return HttpResult.FAILURE("报告不存在");
        }
        ReportDirectory report = reportDirectoryManager.getReport(reportId);
        if (report == null || report.getId() == null) {
            return HttpResult.FAILURE("报告不存在");
        }
        ReportItem reportItem = reportItemManager.get(itemId);
        if (!report.getUserId().equals(curUser.getId()) && !reportItem.getUserId().equals(curUser.getId())) {
            return HttpResult.FAILURE("failed: 无权查看");
        }
        if (reportItem == null || reportItem.getGenerateStatus() == null) {
            return HttpResult.FAILURE("failed: 报告不存在");
        }
        return HttpResult.SUCCESS(reportItem.getGenerateStatus());
    }

}
