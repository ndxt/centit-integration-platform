package com.centit.framework.users.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WxAppConfig {

    @Value("${wechat.appid}")
    private String appID;

    @Value("${wechat.appsecret}")
    private String appSecret;

    @Value("${wechat.redirectloginuri}")
    private String redirectLoginUri;

    @Value("${wechat.redirectbinduri}")
    private String redirectBindUri;

    public String getAppID() {
        return appID;
    }

    public void setAppID(String appID) {
        this.appID = appID;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public String getRedirectLoginUri() {
        return redirectLoginUri;
    }

    public void setRedirectLoginUri(String redirectLoginUri) {
        this.redirectLoginUri = redirectLoginUri;
    }

    public String getRedirectBindUri() {
        return redirectBindUri;
    }

    public void setRedirectBindUri(String redirectBindUri) {
        this.redirectBindUri = redirectBindUri;
    }
}
