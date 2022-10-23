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
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Arrays;
import java.util.Map;

@Controller
@RequestMapping("/api/dwsurvey/app/report")
public class MyReportController {

    @Autowired
    private AccountManager accountManager;
    @Autowired
    private ReportDirectoryManager reportDirectoryManager;
    @Autowired
    private ReportItemManager reportItemManager;
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
     * 报告的状态
     * @param reportId
     * @param itemId
     * @return
     */
    @RequestMapping(value = "/state.do",method = RequestMethod.GET)
    @ResponseBody
    public HttpResult reportItemState(String reportId, String itemId){
        // 无 itemId， 针对报告设计完成后进行报告展示内容的预览
        if (itemId == null || itemId.equals("") || itemId.equals("0")) {
            ReportDirectory report = reportDirectoryManager.getReport(reportId);
            if (report == null || report.getId() == null) {
                return HttpResult.FAILURE("报告不存在");
            }
            if (report.getPreviewPdfState() == null || report.getPreviewPdfState() != 1) {  // fixme 魔法值
                try {
                    reportItemManager.generatePdfReport(reportId, itemId);
                } catch (Exception e) {
                    return HttpResult.SUCCESS("failed: " + e.getMessage());
                }
            }
            return HttpResult.SUCCESS();
        }
        // 具体的一份 报告 todo
        return HttpResult.SUCCESS("failed: 报告尚未生成");
    }

    @RequestMapping("/readPdf")
    private void readPdf(HttpServletResponse response, String reportId, String itemId) {
        response.reset();
        response.setContentType("application/pdf");
        try {
            File file = new File("C:\\Users\\iHaozz\\Desktop\\思维与策略量表\\220805报告模板.pdf");
            FileInputStream fileInputStream = new FileInputStream(file);
            OutputStream outputStream = response.getOutputStream();
            IOUtils.write(IOUtils.toByteArray(fileInputStream), outputStream);
            response.setHeader("Content-Disposition",
                    "inline; filename= file");
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
