package com.centit.framework.ip.app.service.impl;

import com.centit.framework.appclient.AppSession;
import com.centit.framework.appclient.RestfulHttpRequest;
import com.centit.framework.ip.po.DatabaseInfo;
import com.centit.framework.ip.po.OsInfo;
import com.centit.framework.ip.po.UserAccessToken;
import com.centit.framework.ip.service.impl.AbstractIntegrationEnvironment;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.impl.client.CloseableHttpClient;

import java.util.List;

/**
 * Created by codefan on 17-7-3.
 */
public class IPClientIntegrationEnvironment extends AbstractIntegrationEnvironment {

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
    public List<OsInfo> reloadOsInfos() {
        return  RestfulHttpRequest.getResponseObjectList(
            appSession,
            "/ipenvironment/osinfo",
            OsInfo.class);
    }

    @Override
    public List<DatabaseInfo> reloadDatabaseInfos() {
        return  RestfulHttpRequest.getResponseObjectList(
            appSession,
            "/ipenvironment/databaseinfo",
            DatabaseInfo.class);
    }

    @Override
    public List<UserAccessToken> reloadAccessTokens() {
        return  RestfulHttpRequest.getResponseObjectList(
            appSession,
            "/ipenvironment/allUserToken",
            UserAccessToken.class);
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
