package net.diaowen.dwsurvey.controller.report;

import net.diaowen.common.base.entity.User;
import net.diaowen.common.base.service.AccountManager;
import net.diaowen.common.dao.BaseHttpDao;
import net.diaowen.common.plugs.httpclient.HttpResult;
import net.diaowen.common.plugs.httpclient.HttpStatus;
import net.diaowen.common.plugs.httpclient.PageResult;
import net.diaowen.common.plugs.httpclient.ResultUtils;
import net.diaowen.common.plugs.page.Page;
import net.diaowen.dwsurvey.entity.*;
import net.diaowen.dwsurvey.service.*;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.management.remote.rmi._RMIConnection_Stub;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

@Controller
@RequestMapping("/api/dwsurvey/app/report")
public class MyReportController {
    private static final Logger logger = LogManager.getLogger(MyReportController.class.getName());

    @Autowired
    private AccountManager accountManager;
    @Autowired
    private ReportDirectoryManager reportDirectoryManager;
    @Autowired
    private ReportItemManager reportItemManager;
    @Autowired
    private SurveyAnswerManager surveyAnswerManager;
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
            return HttpResult.SUCCESS(reportItemManager.generatePdfReport(reportId, surveyAnswerId));
        } catch (Exception e) {
            return HttpResult.SUCCESS("failed: " + e.getMessage());
        }
    }

    @RequestMapping("/readPdf")
    private void readPdf(HttpServletResponse response, String reportItemId) {
        response.reset();
        response.setContentType("application/pdf");
        try {
            ReportItem reportItem = reportItemManager.get(reportItemId);
            if (Objects.equals(reportItem.getGenerateStatus(), "生成完毕") && reportItem.getPdfAddr() != null) {
                String replace = reportItem.getPdfAddr().replace("\\", "\\\\"); // fixme for win
                System.out.println("replace: " + replace);
                File file = new File(replace);
                FileInputStream fileInputStream = new FileInputStream(file);
                OutputStream outputStream = response.getOutputStream();
                IOUtils.write(IOUtils.toByteArray(fileInputStream), outputStream);
                response.setHeader("Content-Disposition",
                        "inline; filename= file");
                outputStream.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
