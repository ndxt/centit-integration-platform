package com.centit.framework.dubbo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource({"classpath:dubbo-ip-client.xml"})
public class IpServerDubboClientConfig {
}
