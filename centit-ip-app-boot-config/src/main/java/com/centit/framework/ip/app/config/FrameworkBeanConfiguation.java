package com.centit.framework.ip.app.config;

import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.centit.framework.core.controller.MvcConfigUtil;
import com.centit.framework.ip.app.service.impl.IPClientIntegrationEnvironment;
import com.centit.framework.ip.app.service.impl.IPClientPlatformEnvironment;
import com.centit.framework.ip.app.service.impl.IntegrationEnvironmentProxy;
import com.centit.framework.ip.app.service.impl.PlatformEnvironmentProxy;
import com.centit.framework.ip.service.IntegrationEnvironment;
import com.centit.framework.ip.service.impl.JsonIntegrationEnvironment;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.security.model.CentitPasswordEncoder;
import com.centit.framework.security.model.CentitUserDetailsService;
import com.centit.framework.security.model.StandardPasswordEncoderImpl;
import com.centit.framework.staticsystem.service.impl.JsonPlatformEnvironment;
import com.centit.framework.staticsystem.service.impl.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.util.ArrayList;
import java.util.List;

@EnableConfigurationProperties(FrameworkProperties.class)
@Configuration("frameworkBeanConfiguation")
public class FrameworkBeanConfiguation {

    @Autowired
    private FrameworkProperties frameworkProperties;

    @Bean
    public FastJsonHttpMessageConverter fastJsonHttpMessageConverter(){
        return MvcConfigUtil.fastJsonHttpMessageConverter();
    }

    @Bean({"passwordEncoder"})
    public CentitPasswordEncoder passwordEncoder() {
        return new StandardPasswordEncoderImpl();
    }

    @Bean
    @Lazy(value = false)
    public PlatformEnvironment platformEnvironment(
            @Autowired CentitPasswordEncoder passwordEncoder) {

        boolean ipEnable = frameworkProperties.getIp().isSystemEnable();//.isEnable();

        JsonPlatformEnvironment jsonPlatformEnvironment = new JsonPlatformEnvironment();
        jsonPlatformEnvironment.setAppHome(frameworkProperties.getApp().getHome());
        jsonPlatformEnvironment.setPasswordEncoder(passwordEncoder);
        if(!ipEnable) {
            return jsonPlatformEnvironment;
        }

        IPClientPlatformEnvironment ipPlatformEnvironment = new IPClientPlatformEnvironment();
        ipPlatformEnvironment.setTopOptId(frameworkProperties.getIp().getTopoptid());
        ipPlatformEnvironment.createPlatAppSession(
                frameworkProperties.getIp().getHome(),
                frameworkProperties.getIp().isAuthEnable(),
                frameworkProperties.getIp().getUsercode(),
                frameworkProperties.getIp().getPassword());

        List<PlatformEnvironment> evrnMangers = new ArrayList<>();
        evrnMangers.add(ipPlatformEnvironment);
        evrnMangers.add(jsonPlatformEnvironment);

        PlatformEnvironmentProxy platformEnvironment = new PlatformEnvironmentProxy();
        platformEnvironment.setEvrnMangers(evrnMangers);

        return platformEnvironment;
    }

    @Bean
    @Lazy(value = false)
    public IntegrationEnvironment integrationEnvironment() {

        boolean ipEnable = frameworkProperties.getIp().isEnable();

        JsonIntegrationEnvironment staticIntegrationEnvironment = new JsonIntegrationEnvironment();
        staticIntegrationEnvironment.setAppHome(frameworkProperties.getApp().getHome());

        staticIntegrationEnvironment.reloadIPEnvironmen();
        if(!ipEnable) {
            return staticIntegrationEnvironment;
        }


        IPClientIntegrationEnvironment ipIntegrationEnvironment = new IPClientIntegrationEnvironment();
        ipIntegrationEnvironment.createPlatAppSession(
                frameworkProperties.getIp().getHome(),
                frameworkProperties.getIp().isAuthEnable(),
                frameworkProperties.getIp().getUsercode(),
                frameworkProperties.getIp().getPassword());
        //ipPlatformEnvironment.init();

        List<IntegrationEnvironment> evrnMangers = new ArrayList<>();
        evrnMangers.add(ipIntegrationEnvironment);
        evrnMangers.add(staticIntegrationEnvironment);

        IntegrationEnvironmentProxy integrationEnvironment = new IntegrationEnvironmentProxy();
        integrationEnvironment.setEvrnMangers(evrnMangers);

        return integrationEnvironment;
    }

    @Bean
    public CentitUserDetailsService centitUserDetailsService(@Autowired PlatformEnvironment platformEnvironment) {
        UserDetailsServiceImpl userDetailsService = new UserDetailsServiceImpl();
        userDetailsService.setPlatformEnvironment(platformEnvironment);
        return userDetailsService;
    }


}
