package com.centit.framework.ip.service.impl;

import com.centit.framework.ip.po.DatabaseInfo;
import com.centit.framework.ip.po.OsInfo;
import com.centit.framework.ip.service.IntegrationEnvironment;

import java.util.ArrayList;
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
        osInfo.setOsName("dummy.app.key");
        return osInfo;
    }

    @Override
    public DatabaseInfo getDatabaseInfo(String databaseCode) {
        DatabaseInfo databaseInfo = new DatabaseInfo();
        databaseInfo.setOsId(databaseCode);
        databaseInfo.setDatabaseCode(databaseCode);
        databaseInfo.setDatabaseUrl("placeholder");
        databaseInfo.setUsername("placeholder");
        databaseInfo.setPassword("placeholder");
        databaseInfo.setDatabaseDesc("placeholder");
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
