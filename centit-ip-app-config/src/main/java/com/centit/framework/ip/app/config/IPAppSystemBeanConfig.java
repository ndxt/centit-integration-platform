package com.centit.framework.ip.app.config;

import com.centit.framework.ip.app.service.impl.IPClientIntegrationEnvironment;
import com.centit.framework.ip.app.service.impl.IPClientPlatformEnvironment;
import com.centit.framework.ip.service.IntegrationEnvironment;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.security.model.CentitUserDetailsService;
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
    public PlatformEnvironment platformEnvironment() {
        IPClientPlatformEnvironment ipPlatformEnvironment = new IPClientPlatformEnvironment();
        ipPlatformEnvironment.setTopOptId(env.getProperty("centit.ip.topoptid"));
        ipPlatformEnvironment.setPlatServerUrl(env.getProperty("centit.ip.home"));
        return ipPlatformEnvironment;
    }

    @Bean
    @Lazy(value = false)
    public IntegrationEnvironment integrationEnvironment() {
        IPClientIntegrationEnvironment ipIntegrationEnvironment = new IPClientIntegrationEnvironment();
        ipIntegrationEnvironment.setPlatServerUrl(env.getProperty("centit.ip.home"));
        //ipPlatformEnvironment.init();
        return ipIntegrationEnvironment;
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
