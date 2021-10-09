package com.centit.framework.ip.app.config;

import com.centit.framework.ip.app.service.impl.IPClientPlatformEnvironment;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.support.algorithm.BooleanBaseOpt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;

public class IPAppSystemBeanConfig {

    @Autowired
    private Environment env;

    @Bean
    @Lazy(value = false)
    public PlatformEnvironment platformEnvironment() {
        IPClientPlatformEnvironment ipPlatformEnvironment = new IPClientPlatformEnvironment();
        ipPlatformEnvironment.setTopOptId(env.getProperty("centit.ip.topoptid"));
        ipPlatformEnvironment.createPlatAppSession(
            env.getProperty("centit.ip.home"),
            BooleanBaseOpt.castObjectToBoolean(env.getProperty("centit.ip.auth.enable"), false),
            env.getProperty("centit.ip.auth.usercode"),
            env.getProperty("centit.ip.auth.password"));
        return ipPlatformEnvironment;
    }

}
