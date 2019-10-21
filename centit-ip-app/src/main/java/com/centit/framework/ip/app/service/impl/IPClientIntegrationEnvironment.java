package com.centit.framework.ip.app.service.impl;

import com.centit.framework.appclient.AppSession;
import com.centit.framework.appclient.RestfulHttpRequest;
import com.centit.framework.ip.po.DatabaseInfo;
import com.centit.framework.ip.po.OsInfo;
import com.centit.framework.ip.po.UserAccessToken;
import com.centit.framework.ip.service.impl.AbstractIntegrationEnvironment;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Created by codefan on 17-7-3.
 */
public class IPClientIntegrationEnvironment extends AbstractIntegrationEnvironment {

    public IPClientIntegrationEnvironment() {
        super();
    }

    private AppSession appSession;

    public AppSession getPlatAppSession() {
        return this.appSession;
    }

    public void createPlatAppSession(String appServerUrl,boolean needAuthenticated,String userCode,String password){
        appSession = new AppSession(appServerUrl,needAuthenticated,userCode,password);
    }

    @Override
    public List<OsInfo> reloadOsInfos() {
        return  RestfulHttpRequest.getResponseObjectList(
            appSession,
            "/platform/ipenvironment/osinfo",
            OsInfo.class);
    }

    @Override
    public List<DatabaseInfo> reloadDatabaseInfos() {
        return  RestfulHttpRequest.getResponseObjectList(
            appSession,
            "/platform/ipenvironment/databaseinfo",
            DatabaseInfo.class);
    }

    @Override
    public List<UserAccessToken> reloadAccessTokens() {
        return  RestfulHttpRequest.getResponseObjectList(
            appSession,
            "/platform/ipenvironment/allUserToken",
            UserAccessToken.class);
    }

    @Override
    public String checkAccessToken(String tokenId, String accessKey) {
        UserAccessToken at =
                RestfulHttpRequest.getResponseObject(
                        appSession,
                        "/platform/ipenvironment/userToken/"+tokenId,
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
