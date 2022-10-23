package net.diaowen.dwsurvey.dao;

import net.diaowen.common.dao.BaseDao;

public interface ReportItemDao extends BaseDao<ReportItemDao, String> {
    int getSumScore();
}
