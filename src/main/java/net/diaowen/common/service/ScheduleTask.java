package net.diaowen.common.service;

import net.diaowen.dwsurvey.entity.ReportDirectory;
import net.diaowen.dwsurvey.entity.ReportItem;
import net.diaowen.dwsurvey.entity.SurveyAnswer;
import net.diaowen.dwsurvey.entity.SurveyDirectory;
import net.diaowen.dwsurvey.service.ReportDirectoryManager;
import net.diaowen.dwsurvey.service.ReportItemManager;
import net.diaowen.dwsurvey.service.SurveyAnswerManager;
import net.diaowen.dwsurvey.service.SurveyDirectoryManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static net.diaowen.dwsurvey.common.CommonStatic.*;

@Configuration      //1.主要用于标记配置类，兼备Component的效果。
@EnableScheduling   // 2.开启定时任务
public class ScheduleTask {
    private static final Logger logger = LoggerFactory.getLogger(ScheduleTask.class);

    @Autowired
    ReportDirectoryManager reportDirectoryManager;
    @Autowired
    ReportItemManager reportItemManager;
    @Autowired
    SurveyDirectoryManager surveyDirectoryManager;
    @Autowired
    SurveyAnswerManager surveyAnswerManager;
    //3.添加定时任务
    //或直接指定时间间隔，例如：5秒
    //@Scheduled(fixedRate=5000)

    /**
     * 监控报告对应问卷的样本量，当样本量达到预设值，则初始化该报告
     */
    @Scheduled(cron = "0 */1 * * * ?")
    private void reportMonitorTasks() {
        logger.info(LocalDateTime.now() + "  执行报告监控定时任务");
        List<ReportDirectory> reportDirectories = reportDirectoryManager.findByState(REPORT_STATUS_ACTIVATED);
        List<String> surveyIds = reportDirectories.stream().map(ReportDirectory::getSurveyId).collect(Collectors.toList());
        List<SurveyDirectory> surveyDirectories = surveyDirectoryManager.findByIds(surveyIds);
        for (ReportDirectory reportDirectory : reportDirectories) {
            SurveyDirectory surveyDirectory = surveyDirectories.stream().filter(x -> x.getId().equals(reportDirectory.getSurveyId())).findFirst().orElse(null);
            // 若当前问卷数达到配置的最小样本量，则初始化报告
            if (surveyDirectory.getAnswerNum() != null && reportDirectory.getMinSampleSize() <= surveyDirectory.getAnswerNum()) {
                logger.info("init reportItem for report {}", reportDirectory.getId());
                reportItemManager.initReportItem(reportDirectory.getId(), false);
                // 报告状态由激活态转为生效中
                reportDirectory.setReportState(REPORT_STATUS_EFFECTIVE);
                reportDirectoryManager.save(reportDirectory);
            }

            // 拿到初始化的报告进行生成pdf
//            List<ReportItem> reportItems = reportItemManager.findByStatus(reportDirectory.getId(), REPORT_ITEM_STATUS_INIT);
//            for (ReportItem reportItem : reportItems) {
//                try {
//                    reportItemManager.generatePdfReport(reportItem);
//                } catch (Exception e) {
//                    logger.error(e.getMessage());
//                }
//                logger.info("generatePdfReport for reportItem {}", reportItem.getId());
//            }
        }
        logger.info(LocalDateTime.now() + "  报告监控定时任务执行完成");
    }

    /**
     * 获取初始化状态的报告项，依次生成
     */
    @Scheduled(cron = "0 */1 * * * ?")
    private void generatePdfReport() throws Exception {
        logger.info(LocalDateTime.now() + "  执行报告生成定时任务");
        List<ReportDirectory> reportDirectories = reportDirectoryManager.findByState(REPORT_STATUS_EFFECTIVE);
        for (ReportDirectory reportDirectory : reportDirectories) {
            // 拿到初始化的报告进行生成pdf
            List<ReportItem> reportItems = reportItemManager.findByStatus(reportDirectory.getId(), REPORT_ITEM_STATUS_INIT);
            for (ReportItem reportItem : reportItems) {
                try {
                    logger.info("generatePdfReport for reportItem {}", reportItem.getId());
                    reportItemManager.generatePdfReport(reportItem);
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error(e.getMessage());
                }
            }
        }
        logger.info(LocalDateTime.now() + "  报告监控定时任务执行完成");
    }

    /**
     * 补丁任务，提取答卷的用户信息
     */
    @Scheduled(cron = "0 */5 * * * ?")
    private void parseUserFromAnswer() {
        logger.info(LocalDateTime.now() + "  提取答卷的用户信息定时任务");
        // 获取过去8分钟里提交的答卷
        Date date = new Date();
        date.setTime(date.getTime() - 8*60*1000);
        List<SurveyAnswer> surveyAnswers = surveyAnswerManager.findByCreateTimeAfter(date);
        for (SurveyAnswer surveyAnswer : surveyAnswers) {
            logger.info("parseUserFromAnswer for surveyAnswer {}", surveyAnswer.getId());
            surveyAnswerManager.parseUserFromSurveyAnswer(surveyAnswer);
        }
        logger.info(LocalDateTime.now() + "  提取答卷的用户信息任务执行完成");
    }
}
