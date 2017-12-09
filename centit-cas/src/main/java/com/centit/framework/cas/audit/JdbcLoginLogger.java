package com.centit.framework.cas.audit;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.centit.framework.cas.config.JdbcLoggerProperties;
import com.centit.framework.cas.model.ComplexAuthCredential;
import com.centit.support.algorithm.DatetimeOpt;
import com.centit.support.database.utils.DatabaseAccess;
import com.centit.support.database.utils.TransactionHandler;
import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.authentication.Authentication;
import org.apereo.inspektr.common.web.ClientInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class JdbcLoginLogger implements LoginLogger{
    private static Logger logger = LoggerFactory.getLogger(JdbcLoginLogger.class);
    /*
    # 通用参数 登录时间 loginTime 日志型  username 登录名 macAddr Mac地址 loginIp 登录地址 authType 认证类型 credential 认证详细信息json格式
    # 成功特有参数 principle 登录名可能和username不通， auth 成功登录的所有信息 json格式
    */

    private JdbcLoggerProperties jdbcLoggerConfig;

    private Map<String, Object> makeParams(ComplexAuthCredential credential, ClientInfo clientInfo){
        Map<String, Object> params = new HashMap<>(20);
        params.put("username", credential.getId());
        params.put("loginTime", DatetimeOpt.currentUtilDate());
        params.put("loginIp", clientInfo==null?"":clientInfo.getClientIpAddress());
        params.put("macAddr", credential.getMacAddr());
        params.put("authType", credential.getAuthType());
        JSONObject jsonObject = (JSONObject) JSON.toJSON(credential);
        jsonObject.remove("password");
        params.put("credential",jsonObject.toJSONString());
        return params;
    }

    private void writeLogBySql(String sql, Map<String, Object> params){
        try {
            TransactionHandler.executeInTransaction(
                jdbcLoggerConfig.getDatasource(),
                (conn) -> DatabaseAccess.doExecuteNamedSql(conn,sql,params));
        } catch (SQLException e) {
            logger.error(e.getLocalizedMessage());
        }

    }
    @Override
    public void logSuccess(ComplexAuthCredential credential, ClientInfo clientInfo, Authentication auth) {
        if(credential==null)
            return;
        if(StringUtils.isNotBlank(jdbcLoggerConfig.getSuccessSql())){
            Map<String, Object> params = makeParams(credential, clientInfo);
            if(auth!=null) {
                params.put("principle", auth.getPrincipal());
                params.put("auth", JSON.toJSONString(auth.getAttributes()));
            }
            writeLogBySql(jdbcLoggerConfig.getSuccessSql(),params);
        }
    }

    @Override
    public void logError(ComplexAuthCredential credential, ClientInfo clientInfo) {
        if(credential==null)
            return;
        if(StringUtils.isNotBlank(jdbcLoggerConfig.getErrorSql())){
            writeLogBySql(jdbcLoggerConfig.getErrorSql(),makeParams(credential, clientInfo));
        }
    }

    @Override
    public void logWarn(ComplexAuthCredential credential, ClientInfo clientInfo) {
        if(credential==null)
            return;
        if(StringUtils.isNotBlank(jdbcLoggerConfig.getWarnSql())){
            writeLogBySql(jdbcLoggerConfig.getWarnSql(),makeParams(credential, clientInfo));
        }
    }

    @Override
    public void logFailedLogin(ComplexAuthCredential credential, ClientInfo clientInfo) {
        if(credential==null)
            return;
        if(StringUtils.isNotBlank(jdbcLoggerConfig.getFailedLoginSql())){
            writeLogBySql(jdbcLoggerConfig.getFailedLoginSql(),makeParams(credential, clientInfo));
        }
    }

    public void setJdbcLoggerConfig(JdbcLoggerProperties jdbcLoggerConfig) {
        this.jdbcLoggerConfig = jdbcLoggerConfig;
    }
}
