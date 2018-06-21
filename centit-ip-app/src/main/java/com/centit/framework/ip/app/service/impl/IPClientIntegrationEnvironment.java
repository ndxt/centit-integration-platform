package com.centit.framework.ip.app.service.impl;

import com.centit.framework.appclient.AppSession;
import com.centit.framework.appclient.RestfulHttpRequest;
import com.centit.framework.ip.po.DatabaseInfo;
import com.centit.framework.ip.po.OsInfo;
import com.centit.framework.ip.po.UserAccessToken;
import com.centit.framework.ip.service.IntegrationEnvironment;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

/**
 * Created by codefan on 17-7-3.
 */
public class IPClientIntegrationEnvironment implements IntegrationEnvironment {

    public IPClientIntegrationEnvironment() {

    }
    private AppSession appSession;



    public CloseableHttpClient allocHttpClient() throws Exception {
        return appSession.allocHttpClient();
    }

    public void releaseHttpClient(CloseableHttpClient httpClient) {
        appSession.releaseHttpClient(httpClient);
    }

    public void setPlatServerUrl(String platServerUrl) {
        appSession = new AppSession(platServerUrl,false,null,null);
    }



    @Override
    @CacheEvict(value ="IPEnvironmen",allEntries = true)
    public boolean reloadIPEnvironmen() {
        return true;
    }

    @Override
    public OsInfo getOsInfo(String osId) {
        for(OsInfo oi : listOsInfos()){
            if(StringUtils.equals(oi.getOsId(),osId))
                return oi;
        }
        return null;
    }

    @Override
    public DatabaseInfo getDatabaseInfo(String databaseCode) {
        for(DatabaseInfo di : listDatabaseInfo()){
            if(StringUtils.equals(di.getDatabaseCode(),databaseCode))
                return di;
        }
        return null;
    }

    @Override
    @Cacheable(value="IPEnvironment",key="'OsInfo'")
    public List<OsInfo> listOsInfos() {
        return  RestfulHttpRequest.getResponseObjectList(
                appSession,
                "/ipenvironment/osinfo",
                OsInfo.class);
    }

    @Override
    @Cacheable(value="IPEnvironment",key="'DatabaseInfo'")
    public List<DatabaseInfo> listDatabaseInfo() {
        return  RestfulHttpRequest.getResponseObjectList(
                appSession,
                "/ipenvironment/databaseinfo",
                DatabaseInfo.class);
    }

    @Override
    public String checkAccessToken(String tokenId, String accessKey) {
        UserAccessToken at =
                RestfulHttpRequest.getResponseObject(
                        appSession,
                        "/ipenvironment/userToken/"+tokenId,
                        UserAccessToken.class);
        if(at==null)
            return null;
        if(StringUtils.equals(at.getTokenId(),tokenId)){
            if( StringUtils.equals(at.getIsValid(),"T")
                    && StringUtils.equals(at.getSecretAccessKey(), accessKey) )
                return at.getUserCode();
            else
                return null;
        }
        return null;
    }

}
