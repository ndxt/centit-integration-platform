package com.centit.framework.ip.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.centit.framework.components.CodeRepositoryCache;
import com.centit.framework.ip.po.DatabaseInfo;
import com.centit.framework.ip.po.OsInfo;
import com.centit.framework.ip.po.UserAccessToken;
import com.centit.framework.ip.service.IntegrationEnvironment;
import com.centit.support.common.CachedObject;
import com.centit.support.file.FileIOOpt;
import com.centit.support.file.FileSystemOpt;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.List;

/**
 * Created by codefan on 17-7-3.
 */
public abstract class AbstractIntegrationEnvironment implements IntegrationEnvironment {


    private CachedObject<List<OsInfo>> osInfoCache;
    private CachedObject<List<DatabaseInfo>> databaseInfoCache;
    private CachedObject<List<UserAccessToken>> accessTokenCache;


    public AbstractIntegrationEnvironment(){
        osInfoCache = new CachedObject<>(this::reloadOsInfos, CodeRepositoryCache.CACHE_FRESH_PERIOD_MINITES);
        databaseInfoCache = new CachedObject<>(this::reloadDatabaseInfos, CodeRepositoryCache.CACHE_FRESH_PERIOD_MINITES);
        accessTokenCache = new CachedObject<>(this::reloadAccessTokens, CodeRepositoryCache.CACHE_FRESH_PERIOD_MINITES);
    }

    public abstract List<OsInfo> reloadOsInfos();

    public abstract List<DatabaseInfo> reloadDatabaseInfos();

    public abstract List<UserAccessToken> reloadAccessTokens();

    /**
     * 刷新集成环境相关信息
     * 包括：业务系统、数据库信息
     * @return  boolean 刷新集成环境相关信息
     */
    @Override
    public boolean reloadIPEnvironmen(){
        osInfoCache.evictCahce();
        databaseInfoCache.evictCahce();
        accessTokenCache.evictCahce();
        return true;
    }

    @Override
    public OsInfo getOsInfo(String osId) {
        List<OsInfo> osInfos = osInfoCache.getCachedTarget();
        if(osInfos==null)
            return null;
        for(OsInfo oi : osInfos){
            if(StringUtils.equals(oi.getOsId(),osId))
                return oi;
        }
        return null;
    }

    @Override
    public DatabaseInfo getDatabaseInfo(String databaseCode) {
        List<DatabaseInfo> databaseInfos = databaseInfoCache.getCachedTarget();
        if(databaseInfos==null)
            return null;
        for(DatabaseInfo di : databaseInfos){
            if(StringUtils.equals(di.getDatabaseCode(),databaseCode))
                return di;
        }
        return null;
    }

    @Override
    public List<OsInfo> listOsInfos() {
        return osInfoCache.getCachedTarget();
    }

    @Override
    public List<DatabaseInfo> listDatabaseInfo() {
        return databaseInfoCache.getCachedTarget();
    }

    @Override
    public String checkAccessToken(String tokenId, String accessKey) {
        List<UserAccessToken> accessTokens = accessTokenCache.getCachedTarget();
        if(accessTokens==null)
            return null;
        for(UserAccessToken at : accessTokens){
            if(StringUtils.equals(at.getTokenId(),tokenId)){
                if( StringUtils.equals(at.getIsValid(),"T")
                        && StringUtils.equals(at.getSecretAccessKey(), accessKey) )
                    return at.getUserCode();
                else
                    return null;
            }
        }
        return null;
    }
}
