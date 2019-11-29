package com.centit.framework.ip.app.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(FrameworkProperties.PREFIX)
@Data
public class FrameworkProperties {
    public static final String PREFIX = "framework";

    private AppConfig app;
    private JdbcPlatform jdbcplatform;

    @Data
    public static class AppConfig{
        private String home;
    }

    @Data
    public static class JdbcPlatform{
        private String url;
        private String username;
        private String password;
    }
}
