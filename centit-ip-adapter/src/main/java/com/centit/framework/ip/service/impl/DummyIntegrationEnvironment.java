package com.centit.framework.ip.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.centit.framework.common.SysParametersUtils;
import com.centit.framework.components.CodeRepositoryUtil;
import com.centit.framework.ip.po.DatabaseInfo;
import com.centit.framework.ip.po.OsInfo;
import com.centit.framework.ip.po.UserAccessToken;
import com.centit.framework.ip.service.IntegrationEnvironment;
import com.centit.support.database.DBConnect;
import com.centit.support.database.DataSourceDescription;
import com.centit.support.database.DatabaseAccess;
import com.centit.support.database.DbcpConnectPools;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.DocumentException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by codefan on 17-7-3.
 */
public class DummyIntegrationEnvironment implements IntegrationEnvironment {



    @Override
    public boolean reloadIPEnvironmen(){
       return true;
    }


    @Override
    public OsInfo getOsInfo(String osId) {
        OsInfo osInfo = new OsInfo();
        osInfo.setOsId(osId);
        osInfo.setOsName(SysParametersUtils.getStringValue("app.key"));
        return osInfo;
    }

    @Override
    public DatabaseInfo getDatabaseInfo(String databaseCode) {
        DatabaseInfo databaseInfo = new DatabaseInfo();
        databaseInfo.setOsId(databaseCode);
        databaseInfo.setDatabaseCode(databaseCode);
        databaseInfo.setDatabaseUrl(SysParametersUtils.getStringValue("jdbc.url"));
        databaseInfo.setUsername(SysParametersUtils.getStringValue("jdbc.user"));
        databaseInfo.setPassword(SysParametersUtils.getStringValue("jdbc.password"));
        databaseInfo.setDatabaseDesc(SysParametersUtils.getStringValue("jdbc.dialect"));
        return databaseInfo;
    }

    @Override
    public List<OsInfo> listOsInfos() {

        List<OsInfo>  osInfos = new ArrayList<>(2);
        osInfos.add(getOsInfo("dummy"));
        return osInfos;
    }

    @Override
    public List<DatabaseInfo> listDatabaseInfo() {
        List<DatabaseInfo>  databaseInfos = new ArrayList<>(2);
        databaseInfos.add(getDatabaseInfo("dummy"));
        return databaseInfos;
    }

    @Override
    public String checkAccessToken(String tokenId, String accessKey) {
        return tokenId;
    }

}
