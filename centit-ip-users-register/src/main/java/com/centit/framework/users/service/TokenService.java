package com.centit.framework.users.service;

import com.alibaba.fastjson.JSONObject;
import com.centit.framework.users.common.ServiceResult;
import com.centit.framework.users.config.AppConfig;
import com.centit.framework.users.config.UrlConstant;
import com.centit.framework.users.po.AccessToken;
import com.centit.framework.users.utils.FileUtil;
import com.centit.support.algorithm.DatetimeOpt;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiGetJsapiTicketRequest;
import com.dingtalk.api.request.OapiGettokenRequest;
import com.dingtalk.api.request.OapiServiceGetCorpTokenRequest;
import com.dingtalk.api.response.OapiGetJsapiTicketResponse;
import com.dingtalk.api.response.OapiGettokenResponse;
import com.dingtalk.api.response.OapiServiceGetCorpTokenResponse;
import com.taobao.api.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 获取access_token 和 jsTicket方法
 */
@Service
public class TokenService {
    private static final Logger log = LoggerFactory.getLogger(TokenService.class);
    /**
     * 缓存时间：一小时50分钟
     */
    private static final long CACHE_TTL = 60 * 55 * 2 * 1000;

    private AppConfig appConfig;

    @Autowired
    public TokenService(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    @Autowired
    private AccessTokenService accessTokenService;

    @Autowired
    private DingTalkSuiteService dingTalkSuiteService;

    public String getTokenFromCache() {
        return getFromCache("accessToken", "access_token");
    }

    /**
     * 在此方法中，为了避免频繁获取access_token，
     * 在距离上一次获取access_token时间在两个小时之内的情况，
     * 将直接从持久化存储中读取access_token
     *
     * @return accessToken 或错误信息
     */
    public ServiceResult<String> getCorpAccessToken() {
        // 从持久化存储中读取
        String accessToken = getFromCache("accessToken", "access_token");
        if (accessToken != null) {
            return ServiceResult.success(accessToken);
        }

        DefaultDingTalkClient client = new DefaultDingTalkClient(UrlConstant.URL_GET_TOKEN_NEW);
        OapiServiceGetCorpTokenRequest request = new OapiServiceGetCorpTokenRequest();
        OapiServiceGetCorpTokenResponse response;
        request.setAuthCorpid(appConfig.getCorpId());

        String suiteTicket = "94t717KmkkijItbs3fgik2UWYwHcBcO1pafJtgAkNQeqRuD4r8rxaNS6odLvA0nl6SMzQ2j7b6vpPRnRKCBKcF";
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("suiteid", "1");
        suiteTicket = dingTalkSuiteService.getDingTalkSuiteByProperty(paramsMap).getSuitTicket();
        try {
            //response = client.execute(request);
            response = client.execute(request, appConfig.getAppKey(), appConfig.getAppSecret(), suiteTicket, appConfig.getCorpId());
        } catch (ApiException e) {
            log.error("getAccessToken failed", e);
            return ServiceResult.failure(e.getErrCode(), e.getErrMsg());
        }

        accessToken = response.getAccessToken();
        //putToCache("accessToken", "access_token", accessToken);
        saveTokenTodb(accessToken, response.getExpiresIn());
        return ServiceResult.success(accessToken);
    }

    public ServiceResult<String> getAccessToken() {
        // 从持久化存储中读取
        //String accessToken = getFromCache("accessToken", "access_token");
        String accessToken = getFromDb();
        if (accessToken != null) {
            return ServiceResult.success(accessToken);
        }

        DefaultDingTalkClient client = new DefaultDingTalkClient(UrlConstant.URL_GET_TOKEN);
        OapiGettokenRequest request = new OapiGettokenRequest();
        OapiGettokenResponse response;

        request.setAppkey(appConfig.getAppKey());
        request.setAppsecret(appConfig.getAppSecret());
        request.setHttpMethod("GET");

        try {
            response = client.execute(request);
        } catch (ApiException e) {
            log.error("getAccessToken failed", e);
            return ServiceResult.failure(e.getErrCode(), e.getErrMsg());
        }

        accessToken = response.getAccessToken();
        //putToCache("accessToken", "access_token", accessToken);
        saveTokenTodb(accessToken, response.getExpiresIn());
        return ServiceResult.success(accessToken);
    }

    /**
     * 模拟从持久化存储中获取token并检查是否已过期
     *
     * @param section 存储key
     * @param field   token字段名
     * @return token值 或 null (过期或未查到)
     */
    private String getFromCache(String section, String field) {
        JSONObject o = (JSONObject) FileUtil.getValue(section, appConfig.getAppKey());
        if (o != null && System.currentTimeMillis() - o.getLong("begin_time") <= CACHE_TTL) {
            return o.getString(field);
        }
        return null;
    }

    private void putToCache(String section, String field, String value) {
        JSONObject fieldObj = new JSONObject(2);
        fieldObj.put(field, value);
        fieldObj.put("begin_time", System.currentTimeMillis());
        JSONObject wrapperObj = new JSONObject(1);
        wrapperObj.put(appConfig.getAppKey(), fieldObj);

        FileUtil.write2File(wrapperObj, section);
    }

    public String getFromDb() {
        AccessToken accessToken = accessTokenService.getObjectById(appConfig.getAppKey());
        if (null != accessToken) {
            long createTime = DatetimeOpt.convertStringToDate(accessToken.getCreateTime(),
                DatetimeOpt.getDateTimePattern()).getTime();
            if (System.currentTimeMillis() - createTime <= CACHE_TTL) {
                return accessToken.getAccessToken();
            }
        }
        return null;
    }

    private void saveTokenTodb(String value, Long expiresIn) {
        AccessToken accessToken = new AccessToken();
        accessToken.setAppId(appConfig.getAppKey());
        accessToken.setAccessToken(value);
        accessToken.setExpireIn(expiresIn);
        accessToken.setExpireTime(DatetimeOpt.convertDatetimeToString(
            new Date((System.currentTimeMillis()+expiresIn*1000))));
        accessTokenService.saveAccessToke(accessToken);
    }
}
