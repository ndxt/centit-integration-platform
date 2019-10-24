package com.centit.framework.ip.app.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(FrameworkProperties.PREFIX)
@Data
public class FrameworkProperties {
    public static final String PREFIX = "framework";

    private AppConfig app;
    private IpConfig ip;

    @Data
    public static class AppConfig{
        private String home;
    }

    @Data
    public static class IpConfig{
        private boolean enable;
        private boolean systemEnable;
        private String home;
        private String topoptid;
        private boolean authEnable;
        private String usercode;
        private String password;
    }

}
