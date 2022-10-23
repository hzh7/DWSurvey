package net.diaowen.dwsurvey.dao;

import net.diaowen.common.dao.BaseDao;

public interface ReportItem extends BaseDao<ReportItem, String> {
    int getSumScore();
}
