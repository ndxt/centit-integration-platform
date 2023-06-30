package com.centit.framework.jtt.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author zfg
 */
@Configuration
@Data
public class UniteConfig {

    @Value("${appId:}")
    private String appId;

    @Value("${loginCheckUrl:}")
    private String loginCheckUrl;

    @Value("${loginCheckExtendUrl:}")
    private String loginCheckExtendUrl;

    @Value("${uniteAppSecret:}")
    private String uniteAppSecret;

    @Value("${useSSL:}")
    private String useSSL;
}
