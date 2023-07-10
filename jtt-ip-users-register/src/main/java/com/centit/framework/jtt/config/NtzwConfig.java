package com.centit.framework.jtt.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * 南通政务配置
 *
 * @author zfg
 */
@Configuration
@Data
public class NtzwConfig {

    @Value("${ntAppId:}")
    private String ntAppId;

    @Value("${ticketUrl:https://nts.jszwfw.gov.cn:9087/ntzwdt/rest/dhloginauth/ticketValidate}")
    private String ticketUrl;

    @Value("${findUserUrl:https://nts.jszwfw.gov.cn:9087/ntzwdt/rest/dhloginauth/findUserByToken}")
    private String findUserUrl;

    @Value("${findCorpUrl:https://nts.jszwfw.gov.cn:9087/ntzwdt/rest/dhloginauth/findCorporationByToken}")
    private String findCorpUrl;

    @Value("${ntSSL:}")
    private String ntSSL;
}
