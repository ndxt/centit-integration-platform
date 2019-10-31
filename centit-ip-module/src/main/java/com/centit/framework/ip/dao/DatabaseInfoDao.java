package com.centit.framework.ip.dao;

import com.alibaba.fastjson.JSONArray;
import com.centit.framework.core.dao.CodeBook;
import com.centit.framework.ip.po.DatabaseInfo;
import com.centit.framework.jdbc.dao.BaseDaoImpl;
import com.centit.framework.jdbc.dao.JdbcTemplateUtils;
import com.centit.support.algorithm.StringBaseOpt;
import com.centit.support.database.utils.DataSourceDescription;
import com.centit.support.database.utils.PageDesc;
import com.centit.support.database.utils.QueryUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class DatabaseInfoDao extends BaseDaoImpl<DatabaseInfo,String> {

    public Map<String, String> getFilterField() {
        if (filterField == null) {
            filterField = new HashMap<>();
            filterField.put("databaseName", CodeBook.LIKE_HQL_ID);
            filterField.put("databaseCode", CodeBook.EQUAL_HQL_ID);
            filterField.put("databaseType", CodeBook.LIKE_HQL_ID);
            filterField.put("hostPort", CodeBook.LIKE_HQL_ID);
            filterField.put("databaseUrl", CodeBook.LIKE_HQL_ID);
            filterField.put("username", CodeBook.LIKE_HQL_ID);
            filterField.put("password", CodeBook.LIKE_HQL_ID);
            filterField.put("dataDesc", CodeBook.LIKE_HQL_ID);
            filterField.put("createTime", CodeBook.LIKE_HQL_ID);
            filterField.put("created", CodeBook.LIKE_HQL_ID);
        }
        return filterField;
    }

    public boolean connectionTest(DatabaseInfo databaseInfo) {
        return DataSourceDescription.testConntect(new DataSourceDescription(
                databaseInfo.getDatabaseUrl(),
                databaseInfo.getUsername(),
                databaseInfo.getPassword()));
    }

    public List<DatabaseInfo> listDatabase() {
        return this.listObjects();
    }

    public DatabaseInfo getDatabaseInfoById(String databaseCode) {
        DatabaseInfo dbi=this.getObjectById(databaseCode);
        return dbi;
    }

    //jdbc
    public String getNextKey() {
        return StringBaseOpt.fillZeroForString(
                String.valueOf(
                    JdbcTemplateUtils.getSequenceNextValue(
                        this.jdbcTemplate, "S_DATABASECODE")), 10);
    }

    public JSONArray queryDatabaseAsJson(String databaseName, PageDesc pageDesc){
        if(StringUtils.isBlank(databaseName)){
            return super.listObjectsAsJson(new HashMap<>(1), pageDesc);
        }
        String matchStr = QueryUtils.getMatchString(databaseName);
        return super.listObjectsByFilterAsJson("where DATABASE_NAME like ? or DATABASE_URL like ?", new Object[]{matchStr,matchStr},pageDesc);
    }
}
