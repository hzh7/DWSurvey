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
    public static Integer REPORT_STATUS_EDIT = 1;  // 编辑中
    public static Integer REPORT_STATUS_ACTIVATED = 2;  // 激活中
    public static Integer REPORT_STATUS_EFFECTIVE = 3;  // 生效中
    public static Integer REPORT_STATUS_DISABLED = 4;  // 不可用

    public static Integer REPORT_ITEM_STATUS_INIT = 0;  // 初始化
    public static Integer REPORT_ITEM_STATUS_GENERATING = 2;  // 生成中
    public static Integer REPORT_ITEM_STATUS_SUCCESS = 3;  // 生成完成
    public static Integer REPORT_ITEM_STATUS_FAILED = 4;  // 生成失败


    public static List<String> PRIMARY_SCHOOL = Arrays.asList("一年级", "二年级", "三年级", "四年级", "五年级");  // 小学
    public static List<String> JUNIOR_HIGH_SCHOOL = Arrays.asList("预备初一", "初一", "初二", "初三");  // 初中
    public static List<String> HIGH_SCHOOL = Arrays.asList("高一", "高二", "高三");  // 高中

}
