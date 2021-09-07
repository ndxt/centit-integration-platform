package com.centit.framework.users.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * 应用凭证配置
 */
@Configuration
public class AppConfig {
    @Value("${dingtalk.app_key}")
    private String appKey;

    @Value("${dingtalk.app_secret}")
    private String appSecret;

    @Value("${dingtalk.agent_id}")
    private String agentId;

    @Value("${dingtalk.corp_id}")
    private String corpId;

    private String redirectUri;

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getCorpId() {
        return corpId;
    }

    public void setCorpId(String corpId) {
        this.corpId = corpId;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }
}
