package com.centit.framework.jtt.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.centit.framework.common.ResponseData;
import com.centit.framework.jtt.config.JsmotConstant;
import com.centit.framework.jtt.config.JsmotSyncConfig;
import com.centit.framework.jtt.po.JttToken;
import com.centit.framework.jtt.utils.HttpUtil;
import com.centit.support.network.HttpExecutor;
import com.centit.support.network.HttpExecutorContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 获取access_token 和 jsTicket方法
 */
@Service
public class JttAccessTokenService {
    private static final Logger log = LoggerFactory.getLogger(JttAccessTokenService.class);
    /**
     * 缓存时间：3600秒（1小时）
     */
    private static final long CACHE_TTL = 60 * 60 * 1 * 1000L;

    @Autowired
    private JttTokenService jttTokenService;

    @Autowired
    private JsmotSyncConfig jsmotSyncConfig;

    public String getFromDb(String section) {
        JttToken accessToken = jttTokenService.getObjectById(section);
        if (null != accessToken) {
            long createTime = accessToken.getCreateTime().getTime();
            if (System.currentTimeMillis() - createTime <= accessToken.getExpireIn() * 1000L) {
                return accessToken.getAccessToken();
            }
        }
        return null;
    }

    private void saveTokenTodb(String section, String value, Long expiresIn) {
        JttToken accessToken = new JttToken();
        accessToken.setAppId(section);
        accessToken.setAccessToken(value);
        accessToken.setExpireIn(expiresIn);
        accessToken.setExpireTime(new Date((System.currentTimeMillis() + expiresIn * 1000)));
        jttTokenService.saveAccessToke(accessToken);
    }

    public ResponseData getJsmotAccessToken() {
        // 从持久化存储中读取
        String accessToken = getFromDb(jsmotSyncConfig.getCustomKey());
        long expiresIn = 0;
        if (StringUtils.isNotBlank(accessToken)) {
            return ResponseData.makeResponseData(accessToken);
        }
        String retMsg = "";
        long retCode = 1;
        try {
            String uri = jsmotSyncConfig.getJsmotHost() + JsmotConstant.URL_GET_ACCESS_TOKEN + "?customKey=" + jsmotSyncConfig.getCustomKey() + "&customSecret=" + jsmotSyncConfig.getCustomSecret();
            String jsonStr = HttpExecutor.simpleGet(HttpExecutorContext.create(), uri);
            JSONObject jsonObject = JSON.parseObject(jsonStr);
            if (null != jsonObject) {
                retCode = jsonObject.getLong("retCode");
                retMsg = jsonObject.getString("retMsg");
                if (retCode == 0) {
                    accessToken = jsonObject.getJSONObject("bizData").getString("accessToken");
                    expiresIn = jsonObject.getJSONObject("bizData").getLong("validperiod");
                } else {
                    return ResponseData.makeErrorMessage(Integer.valueOf(String.valueOf(retCode)), retMsg);
                }
            } else {
                return ResponseData.makeErrorMessage(1, "调用获取交通云accessToken授权码返回为空");
            }
        } catch (Exception e) {
            log.error("getJsmotAccessToken failed", e);
            return ResponseData.makeErrorMessage(1, e.getMessage());
        }
        saveTokenTodb(jsmotSyncConfig.getCustomKey(), accessToken, expiresIn);
        return ResponseData.makeResponseData(accessToken);
    }

    public ResponseData getSmsAccessToken() {
        // 从持久化存储中读取
        String accessToken = getFromDb(jsmotSyncConfig.getSmsUser());
        long expiresIn = 0;
        if (StringUtils.isNotBlank(accessToken)) {
            return ResponseData.makeResponseData(accessToken);
        }
        String retMsg = "";
        long retCode = 1;
        try {
            String uri = jsmotSyncConfig.getSmsHost() + JsmotConstant.URL_SMS_ACCESS_TOKEN;
            String param = "username=" + jsmotSyncConfig.getSmsUser() + "&password=" + jsmotSyncConfig.getSmsPwd();
            String jsonStr = HttpUtil.httpPostRequest(uri, "form", "", param);
            JSONObject jsonObject = JSON.parseObject(jsonStr);
            if (null != jsonObject) {
                retCode = jsonObject.getLong("code");
                retMsg = jsonObject.getString("msg");
                if (retCode == 200) {
                    accessToken = jsonObject.getJSONObject("data").getString("token");
                    expiresIn = jsonObject.getJSONObject("data").getLong("expiresIn");
                } else {
                    return ResponseData.makeErrorMessage(Integer.valueOf(String.valueOf(retCode)), retMsg);
                }
            } else {
                return ResponseData.makeErrorMessage(1, "调用获取短信平台accessToken授权码返回为空");
            }
        } catch (Exception e) {
            log.error("getSmsAccessToken failed", e);
            return ResponseData.makeErrorMessage(1, e.getMessage());
        }
        saveTokenTodb(jsmotSyncConfig.getSmsUser(), accessToken, expiresIn);
        return ResponseData.makeResponseData(accessToken);
    }
}
