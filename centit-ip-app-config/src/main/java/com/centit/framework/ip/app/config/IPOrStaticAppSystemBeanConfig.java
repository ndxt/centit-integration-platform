package com.centit.framework.ip.app.config;

import com.centit.framework.ip.app.service.impl.IPClientPlatformEnvironment;
import com.centit.framework.ip.app.service.impl.PlatformEnvironmentProxy;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.security.model.CentitPasswordEncoder;
import com.centit.framework.staticsystem.service.impl.JdbcPlatformEnvironment;
import com.centit.framework.staticsystem.service.impl.JsonPlatformEnvironment;
import com.centit.support.algorithm.BooleanBaseOpt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.List;

public class IPOrStaticAppSystemBeanConfig {

    @Autowired
    private Environment env;

    @Bean
    @Lazy(value = false)
    public PlatformEnvironment platformEnvironment(
        @Autowired CentitPasswordEncoder passwordEncoder) {

        Boolean ipEnable = env.getProperty("centit.ip.system-enable", Boolean.class);// = false
        if (ipEnable == null) {
            ipEnable = env.getProperty("centit.ip.enable", Boolean.class);// = false
        }

        PlatformEnvironment staticPlatformEnvironment = null;
        Boolean jdbcEnable = env.getProperty("centit.jdbcplatform.enable", Boolean.class);// = false
        if (jdbcEnable != null && jdbcEnable) {
            JdbcPlatformEnvironment jdbcPlatformEnvironment = new JdbcPlatformEnvironment();
            jdbcPlatformEnvironment.setDataBaseConnectInfo(
                env.getProperty("centit.jdbcplatform.url"),
                env.getProperty("centit.jdbcplatform.username"),
                env.getProperty("centit.jdbcplatform.password")
            );
            jdbcPlatformEnvironment.setPasswordEncoder(passwordEncoder);
            if (ipEnable == null || !ipEnable) {
                return jdbcPlatformEnvironment;
            } else {
                staticPlatformEnvironment = jdbcPlatformEnvironment;
            }
        } else {
            JsonPlatformEnvironment jsonPlatformEnvironment = new JsonPlatformEnvironment();
            jsonPlatformEnvironment.setAppHome(env.getProperty("app.home"));
            jsonPlatformEnvironment.setPasswordEncoder(passwordEncoder);
            if (ipEnable == null || !ipEnable) {
                return jsonPlatformEnvironment;
            } else {
                staticPlatformEnvironment = jsonPlatformEnvironment;
            }
        }

        IPClientPlatformEnvironment ipPlatformEnvironment = new IPClientPlatformEnvironment();
        ipPlatformEnvironment.setTopOptId(env.getProperty("centit.ip.topoptid"));
        ipPlatformEnvironment.createPlatAppSession(
            env.getProperty("centit.ip.home"),
            BooleanBaseOpt.castObjectToBoolean(env.getProperty("centit.ip.auth.enable"), false),
            env.getProperty("centit.ip.auth.usercode"),
            env.getProperty("centit.ip.auth.password"));

        List<PlatformEnvironment> evrnMangers = new ArrayList<>();
        evrnMangers.add(ipPlatformEnvironment);
        evrnMangers.add(staticPlatformEnvironment);

        PlatformEnvironmentProxy platformEnvironment = new PlatformEnvironmentProxy();
        platformEnvironment.setEvrnMangers(evrnMangers);

        return platformEnvironment;
    }

}
