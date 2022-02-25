package com.centit.framework.ip.config;

import com.alibaba.nacos.api.annotation.NacosProperties;
import com.alibaba.nacos.spring.context.annotation.config.EnableNacosConfig;
import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource;
import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySources;
import com.centit.framework.components.impl.NotificationCenterImpl;
import com.centit.framework.config.InitialWebRuntimeEnvironment;
import com.centit.framework.config.SpringSecurityCasConfig;
import com.centit.framework.config.SpringSecurityDaoConfig;
import com.centit.framework.jdbc.config.JdbcConfig;
import com.centit.framework.model.adapter.NotificationCenter;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.security.model.StandardPasswordEncoderImpl;
import com.centit.framework.system.config.SystemBeanConfig;
import com.centit.product.oa.EmailMessageSenderImpl;
import com.centit.support.security.AESSecurityUtils;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;

/**
 * Created by codefan on 17-7-18.
 */
@Configuration
@ComponentScan(basePackages = "com.centit",
        excludeFilters = @ComponentScan.Filter(value = org.springframework.stereotype.Controller.class))
@Import({SystemBeanConfig.class,
        SpringSecurityCasConfig.class,
        SpringSecurityDaoConfig.class,
        JdbcConfig.class})
@EnableNacosConfig(globalProperties = @NacosProperties(serverAddr = "${nacos.server-addr}"))
@NacosPropertySources({@NacosPropertySource(dataId = "${nacos.system-dataid}",groupId = "CENTIT", autoRefreshed = true)}
)
public class ServiceConfig {

    @Bean
    @Lazy(value = false)
    public InitialWebRuntimeEnvironment initialEnvironment() {
        InitialWebRuntimeEnvironment initialWebRuntimeEnvironment = new InitialWebRuntimeEnvironment();
        initialWebRuntimeEnvironment.initialEnvironment();
        return initialWebRuntimeEnvironment;
    }
    /**
     * 这个bean必须要有
     * @return CentitPasswordEncoder 密码加密算法
     */
    @Bean("passwordEncoder")
    public StandardPasswordEncoderImpl passwordEncoder() {
        return  new StandardPasswordEncoderImpl();
    }

    @Bean
    public NotificationCenter notificationCenter() {
        NotificationCenterImpl notificationCenter = new NotificationCenterImpl();
        notificationCenter.initDummyMsgSenders();
        //notificationCenter.registerMessageSender("innerMsg",innerMessageManager);
        return notificationCenter;
    }

    @Bean
    public NotificationCenter notificationCenter(@Autowired PlatformEnvironment platformEnvironment) {
        NewEmailMessageSenderImpl messageManager = new NewEmailMessageSenderImpl();
        messageManager.setHostName("mail.centit.com");
        messageManager.setSmtpPort(25);
        messageManager.setUserName("developer@centit.com");
        messageManager.setUserPassword("centitdev.1");
        messageManager.setServerEmail("developer@centit.com");

        NotificationCenterImpl notificationCenter = new NotificationCenterImpl();
        notificationCenter.setPlatformEnvironment(platformEnvironment);
        //禁用发送email
        notificationCenter.registerMessageSender("email", messageManager);
        notificationCenter.appointDefaultSendType("email");

        return notificationCenter;
    }


    /*引入依赖包含这个日志写入bean
      <dependency>
            <groupId>com.centit.product</groupId>
            <artifactId>opt-log-module</artifactId>
            <version>1.1-SNAPSHOT</version>
      </dependency>
    */
    /*@Bean
    @Lazy(value = false)
    public OperationLogWriter operationLogWriter() {
        TextOperationLogWriterImpl operationLog =  new TextOperationLogWriterImpl();
        operationLog.init();
        return operationLog;
    }*/

    @Bean
    public InstantiationServiceBeanPostProcessor instantiationServiceBeanPostProcessor() {
        return new InstantiationServiceBeanPostProcessor();
    }

    @Bean
    public WxMpService wxMpService() {
        return new WxMpServiceImpl();
    }

}
