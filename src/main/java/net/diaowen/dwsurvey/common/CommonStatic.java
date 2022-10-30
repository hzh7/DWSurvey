package net.diaowen.dwsurvey.common;

import java.util.Arrays;
import java.util.List;

public class CommonStatic {

    public static String PROJECT_MANAGER = "MANAGER";
    public static String PROJECT_LIST = "LIST";
    public static String PROJECT_CREATE = "CREATE";
    public static String PROJECT_EDIT = "EDIT";
    public static String PROJECT_DELETE = "DELETE";
    public static String PROJECT_DEV = "DEV";

    // 报告状态
    public static Integer REPORT_STATUS_EDIT = 0;  // 编辑中
    public static Integer REPORT_STATUS_ACTIVATED = 1;  // 激活中
    public static Integer REPORT_STATUS_EFFECTIVE = 2;  // 生效中
    public static Integer REPORT_STATUS_DISABLED = 3;  // 不可用

    public static Integer REPORT_ITEM_STATUS_INIT = 0;  // 初始化
    public static Integer REPORT_ITEM_STATUS_GENERATING = 1;  // 生成中
    public static Integer REPORT_ITEM_STATUS_SUCCESS = 2;  // 生成完成
    public static Integer REPORT_ITEM_STATUS_FAILED = 3;  // 生成失败


    public static List<String> PRIMARY_SCHOOL = Arrays.asList("一年级", "二年级", "三年级", "四年级", "五年级");  // 小学
    public static List<String> JUNIOR_HIGH_SCHOOL = Arrays.asList("预备初一", "初一", "初二", "初三");  // 初中
    public static List<String> HIGH_SCHOOL = Arrays.asList("高一", "高二", "高三");  // 高中

}
