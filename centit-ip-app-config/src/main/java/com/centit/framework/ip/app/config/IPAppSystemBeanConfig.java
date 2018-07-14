package com.centit.framework.ip.app.config;

import com.centit.framework.ip.app.service.impl.IPClientIntegrationEnvironment;
import com.centit.framework.ip.app.service.impl.IPClientPlatformEnvironment;
import com.centit.framework.ip.app.service.impl.IntegrationEnvironmentProxy;
import com.centit.framework.ip.app.service.impl.PlatformEnvironmentProxy;
import com.centit.framework.ip.service.IntegrationEnvironment;
import com.centit.framework.ip.service.impl.JdbcIntegrationEnvironment;
import com.centit.framework.ip.service.impl.JsonIntegrationEnvironment;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.security.model.*;
import com.centit.framework.staticsystem.service.impl.JdbcPlatformEnvironment;
import com.centit.framework.staticsystem.service.impl.JsonPlatformEnvironment;
import com.centit.framework.staticsystem.service.impl.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@PropertySource("classpath:system.properties")
public class IPAppSystemBeanConfig  implements EnvironmentAware{

    private Environment env;

    @Resource
    @Override
    public void setEnvironment(final Environment environment) {
        if(environment!=null) {
            this.env = environment;
        }
    }

    @Bean
    public AutowiredAnnotationBeanPostProcessor autowiredAnnotationBeanPostProcessor() {
        return new AutowiredAnnotationBeanPostProcessor();
    }

    @Bean
    public StandardPasswordEncoderImpl passwordEncoder() {
        return new StandardPasswordEncoderImpl();
    }

    @Bean
    @Lazy(value = false)
    public PlatformEnvironment platformEnvironment(
            CentitPasswordEncoder passwordEncoder) {

        Boolean ipEnable = env.getProperty("centit.ip.enable",Boolean.class);// = false
        PlatformEnvironment staticPlatformEnvironment=null;

        Boolean jdbcEnable = env.getProperty("centit.jdbcplatform.enable", Boolean.class);// = false
        if (jdbcEnable != null && jdbcEnable) {
            JdbcPlatformEnvironment jdbcPlatformEnvironment = new JdbcPlatformEnvironment();
            jdbcPlatformEnvironment.setDataBaseConnectInfo(
                 env.getProperty("centit.jdbcplatform.url"),
                    env.getProperty("centit.jdbcplatform.username"),
                    env.getProperty("centit.jdbcplatform.password")
            );
            jdbcPlatformEnvironment.setPasswordEncoder(passwordEncoder);
            if(ipEnable==null || !ipEnable) {
                return jdbcPlatformEnvironment;
            }else {
                staticPlatformEnvironment = jdbcPlatformEnvironment;
            }
        } else{
            JsonPlatformEnvironment jsonPlatformEnvironment = new JsonPlatformEnvironment();
            jsonPlatformEnvironment.setAppHome(env.getProperty("app.home"));
            jsonPlatformEnvironment.setPasswordEncoder(passwordEncoder);
            if(ipEnable==null || !ipEnable) {
                return jsonPlatformEnvironment;
            }else {
                staticPlatformEnvironment = jsonPlatformEnvironment;
            }
        }

        IPClientPlatformEnvironment ipPlatformEnvironment = new IPClientPlatformEnvironment();
        ipPlatformEnvironment.setTopOptId(env.getProperty("centit.ip.topoptid"));
        ipPlatformEnvironment.setPlatServerUrl(env.getProperty("centit.ip.home"));

        List<PlatformEnvironment> evrnMangers = new ArrayList<>();
        evrnMangers.add(ipPlatformEnvironment);
        evrnMangers.add(staticPlatformEnvironment);

        PlatformEnvironmentProxy platformEnvironment = new PlatformEnvironmentProxy();
        platformEnvironment.setEvrnMangers(evrnMangers);

        return platformEnvironment;
    }

    @Bean
    @Lazy(value = false)
    public IntegrationEnvironment integrationEnvironment() {

        Boolean ipEnable = env.getProperty("centit.ip.enable",Boolean.class);// = false

        IntegrationEnvironment staticIntegrationEnvironment=null;

        Boolean jdbcEnable = env.getProperty("centit.jdbcplatform.ip.enable", Boolean.class);// = false
        if (jdbcEnable != null && jdbcEnable) {
            JdbcIntegrationEnvironment jdbcIntegrationEnvironment = new JdbcIntegrationEnvironment();
            jdbcIntegrationEnvironment.setDataBaseConnectInfo(
                    env.getProperty("centit.jdbcplatform.url"),
                    env.getProperty("centit.jdbcplatform.username"),
                    env.getProperty("centit.jdbcplatform.password")
            );
            jdbcIntegrationEnvironment.reloadIPEnvironmen();
            if(ipEnable==null || !ipEnable) {
                return jdbcIntegrationEnvironment;
            }else {
                staticIntegrationEnvironment = jdbcIntegrationEnvironment;
            }
        } else{
            JsonIntegrationEnvironment jsonIntegrationEnvironment = new JsonIntegrationEnvironment();
            jsonIntegrationEnvironment.setAppHome(env.getProperty("app.home"));

            jsonIntegrationEnvironment.reloadIPEnvironmen();
            if(ipEnable==null || !ipEnable) {
                return jsonIntegrationEnvironment;
            }else {
                staticIntegrationEnvironment = jsonIntegrationEnvironment;
            }
        }


        IPClientIntegrationEnvironment ipIntegrationEnvironment = new IPClientIntegrationEnvironment();
        ipIntegrationEnvironment.setPlatServerUrl(env.getProperty("centit.ip.home"));
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

    @Bean
    public CentitSessionRegistry centitSessionRegistry(){
        return new MemorySessionRegistryImpl();
    }


    @Bean
    public HttpSessionCsrfTokenRepository csrfTokenRepository() {
        return new HttpSessionCsrfTokenRepository();
    }


}
