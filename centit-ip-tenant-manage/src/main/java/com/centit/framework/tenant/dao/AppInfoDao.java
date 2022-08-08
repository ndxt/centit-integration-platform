package com.centit.framework.tenant.dao;

import com.alibaba.fastjson.JSONObject;
import com.centit.framework.jdbc.dao.BaseDaoImpl;
import com.centit.framework.jdbc.dao.DatabaseOptUtils;
import com.centit.framework.tenant.po.AppInfo;
import com.centit.support.database.utils.QueryAndNamedParams;
import com.centit.support.database.utils.QueryUtils;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * @author tian_y
 */
@Repository
public class AppInfoDao extends BaseDaoImpl<AppInfo,String>{

    public JSONObject getLastAppInfo(String appType){
        Map<String, Object> filter = new HashMap<>();
        filter.put("appType", appType);
        String sql = " SELECT * FROM F_APP_INFO WHERE VERSION_ID = (SELECT MAX(VERSION_ID) FROM F_APP_INFO WHERE 1 = 1 [ :appType| AND APP_TYPE = :appType] ) ";
        QueryAndNamedParams queryAndNamedParams = QueryUtils.translateQuery(sql, filter);
        return DatabaseOptUtils.getObjectBySqlAsJson(this, queryAndNamedParams.getQuery(), queryAndNamedParams.getParams());
    }
}
