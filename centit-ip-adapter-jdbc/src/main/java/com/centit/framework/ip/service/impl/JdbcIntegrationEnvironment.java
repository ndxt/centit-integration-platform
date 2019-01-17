package com.centit.framework.ip.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.centit.framework.core.dao.ExtendedQueryPool;
import com.centit.framework.ip.po.DatabaseInfo;
import com.centit.framework.ip.po.OsInfo;
import com.centit.framework.ip.po.UserAccessToken;
import com.centit.support.database.utils.DataSourceDescription;
import com.centit.support.database.utils.DatabaseAccess;
import com.centit.support.database.utils.DbcpConnectPools;
import org.dom4j.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by codefan on 17-7-3.
 */
public class JdbcIntegrationEnvironment extends AbstractIntegrationEnvironment  {

    private Logger logger = LoggerFactory.getLogger(JdbcIntegrationEnvironment.class);



    private DataSourceDescription dataSource;

    private Connection getConnection() throws SQLException {
        return DbcpConnectPools.getDbcpConnect(dataSource);
    }

    public void setDataBaseConnectInfo(String connectURI, String username, String pswd){
        this.dataSource = new DataSourceDescription( connectURI,  username,  pswd);
        try {
            ExtendedQueryPool.loadResourceExtendedSqlMap(dataSource.getDbType());
        } catch (DocumentException e) {
            logger.error(e.getLocalizedMessage());
        }
    }

    public void close(Connection conn){
        DbcpConnectPools.closeConnect(conn);
    }


    private <T> List<T> jsonArrayToObjectList(JSONArray jsonArray, Class<T> clazz) {
        if(jsonArray==null)
            return new ArrayList<>();
        return jsonArray.toJavaList(clazz);
    }

    @Override
    public List<OsInfo> reloadOsInfos() {
        try(Connection conn = getConnection()) {
            JSONArray userJSONArray = DatabaseAccess.findObjectsAsJSON(conn,
                ExtendedQueryPool.getExtendedSql("LIST_ALL_OS"));
            return jsonArrayToObjectList(userJSONArray, OsInfo.class);
        }catch (IOException | SQLException e ){
            logger.error(e.getLocalizedMessage());
            return null;
        }
    }

    @Override
    public List<DatabaseInfo> reloadDatabaseInfos() {
        try(Connection conn = getConnection()) {
            JSONArray optInfoJSONArray = DatabaseAccess.findObjectsAsJSON(conn,
                ExtendedQueryPool.getExtendedSql("LIST_ALL_DATABASE"));
            return jsonArrayToObjectList(optInfoJSONArray,  DatabaseInfo.class);
        }catch (IOException | SQLException e ){
            logger.error(e.getLocalizedMessage());
            return null;
        }
    }

    @Override
    public List<UserAccessToken> reloadAccessTokens() {
        try(Connection conn = getConnection()) {
            JSONArray optMethodsJSONArray = DatabaseAccess.findObjectsAsJSON(conn,
                ExtendedQueryPool.getExtendedSql("LIST_ALL_ACCESSTOKEN"));
            return jsonArrayToObjectList(optMethodsJSONArray,  UserAccessToken.class);
        }catch (IOException | SQLException e ){
            logger.error(e.getLocalizedMessage());
            return null;
        }
    }
}
