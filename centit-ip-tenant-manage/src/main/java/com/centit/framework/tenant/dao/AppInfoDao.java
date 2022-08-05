package com.centit.framework.tenant.dao;

import com.alibaba.fastjson.JSONObject;
import com.centit.framework.jdbc.dao.BaseDaoImpl;
import com.centit.framework.jdbc.dao.DatabaseOptUtils;
import com.centit.framework.tenant.po.AppInfo;
import com.centit.support.database.utils.QueryAndNamedParams;
import com.centit.support.database.utils.QueryUtils;
import org.springframework.stereotype.Repository;

/**
 * @author tian_y
 */
@Repository
public class AppInfoDao extends BaseDaoImpl<AppInfo,String>{

    public JSONObject getLastAppInfo(){
        String sql = " SELECT * FROM F_APP_INFO WHERE VERSION_ID = (SELECT MAX(VERSION_ID) FROM F_APP_INFO ) LIMIT 1 ";
        QueryAndNamedParams queryAndNamedParams = QueryUtils.translateQuery(sql, null);
        return DatabaseOptUtils.getObjectBySqlAsJson(this, queryAndNamedParams.getQuery(), queryAndNamedParams.getParams());
    }
}
