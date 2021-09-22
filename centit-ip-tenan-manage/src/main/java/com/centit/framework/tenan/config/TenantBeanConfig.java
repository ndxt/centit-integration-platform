package com.centit.framework.tenan.config;

import com.centit.framework.security.model.StandardPasswordEncoderImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TenantBeanConfig {

    @Bean
    public StandardPasswordEncoderImpl centitPasswordEncoder(){
        return new StandardPasswordEncoderImpl();
    }
}
