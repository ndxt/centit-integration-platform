package com.centit.framework.ip.app.config;

import com.centit.framework.ip.app.service.impl.IPClientPlatformEnvironment;
import com.centit.framework.ip.app.service.impl.PlatformEnvironmentProxy;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.security.model.CentitPasswordEncoder;
import com.centit.framework.security.model.CentitUserDetailsService;
import com.centit.framework.staticsystem.service.impl.JdbcPlatformEnvironment;
import com.centit.framework.staticsystem.service.impl.JsonPlatformEnvironment;
import com.centit.framework.staticsystem.service.impl.UserDetailsServiceImpl;
import com.centit.support.algorithm.BooleanBaseOpt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

import java.util.ArrayList;
import java.util.List;

@PropertySource("classpath:system.properties")
public class IPOrStaticAppSystemBeanConfig implements EnvironmentAware{

    private Environment env;

    @Autowired
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

    /*  这bean从框架中移除，由开发人员自行定义，可以配置不同的加密算法
    @Bean("passwordEncoder")
    public StandardPasswordEncoderImpl passwordEncoder() {
            return  new StandardPasswordEncoderImpl();
        }
    */
    /* 这bean从框架中移除，由开发人员自行定义，可以配置不同的session持久化策略
    @Bean
    public SessionRegistry sessionRegistry(){
        return new MemorySessionRegistryImpl();
    }*/

    @Bean
    @Lazy(value = false)
    public PlatformEnvironment platformEnvironment(
        @Autowired CentitPasswordEncoder passwordEncoder) {

        Boolean ipEnable = env.getProperty("centit.ip.system-enable", Boolean.class);// = false
        if(ipEnable == null){
            ipEnable = env.getProperty("centit.ip.enable", Boolean.class);// = false
        }

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
        ipPlatformEnvironment.createPlatAppSession(
            env.getProperty("centit.ip.home"),
            BooleanBaseOpt.castObjectToBoolean(env.getProperty("centit.ip.auth.enable"),false),
            env.getProperty("centit.ip.auth.usercode"),
            env.getProperty("centit.ip.auth.password"));

        List<PlatformEnvironment> evrnMangers = new ArrayList<>();
        evrnMangers.add(ipPlatformEnvironment);
        evrnMangers.add(staticPlatformEnvironment);

        PlatformEnvironmentProxy platformEnvironment = new PlatformEnvironmentProxy();
        platformEnvironment.setEvrnMangers(evrnMangers);

        return platformEnvironment;
    }

    @Bean
    public CentitUserDetailsService centitUserDetailsService(@Autowired PlatformEnvironment platformEnvironment) {
        UserDetailsServiceImpl userDetailsService = new UserDetailsServiceImpl();
        userDetailsService.setPlatformEnvironment(platformEnvironment);
        return userDetailsService;
    }

    @Bean
    public HttpSessionCsrfTokenRepository csrfTokenRepository() {
        return new HttpSessionCsrfTokenRepository();
    }

}
