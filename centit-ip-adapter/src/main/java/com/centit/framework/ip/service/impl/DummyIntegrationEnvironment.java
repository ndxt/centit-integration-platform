package com.centit.framework.ip.service.impl;

import com.centit.framework.ip.po.DatabaseInfo;
import com.centit.framework.ip.po.OsInfo;
import com.centit.framework.ip.po.UserAccessToken;
import com.centit.support.algorithm.DatetimeOpt;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by codefan on 17-7-3.
 */
public class DummyIntegrationEnvironment extends AbstractIntegrationEnvironment {

    public DummyIntegrationEnvironment(){
        super();
    }

    @Override
    public List<OsInfo> reloadOsInfos() {
        List<OsInfo>  osInfos = new ArrayList<>(2);
        OsInfo osInfo = new OsInfo();
        osInfo.setOsId("dummy");
        osInfo.setOsName("dummy.app.key");
        osInfos.add(osInfo);
        return osInfos;
    }

    @Override
    public List<DatabaseInfo> reloadDatabaseInfos() {
        List<DatabaseInfo>  databaseInfos = new ArrayList<>(2);
        DatabaseInfo databaseInfo = new DatabaseInfo();
        databaseInfo.setOsId("dummy");
        databaseInfo.setDatabaseCode("dummy");
        databaseInfo.setDatabaseUrl("placeholder");
        databaseInfo.setUsername("placeholder");
        databaseInfo.setPassword("placeholder");
        databaseInfo.setDatabaseDesc("placeholder");
        databaseInfos.add(databaseInfo);
        return databaseInfos;
    }

    @Override
    public List<UserAccessToken> reloadAccessTokens() {
        List<UserAccessToken> tokens = new ArrayList<>(2);
        UserAccessToken token = new UserAccessToken();
        token.setTokenId("dummy");
        token.setSecretAccessKey("centit.1");
        token.setUserCode("admin");
        token.setCreateTime(DatetimeOpt.currentUtilDate());
        token.setIsValid("T");
        tokens.add(token);
        return tokens;
    }
}
