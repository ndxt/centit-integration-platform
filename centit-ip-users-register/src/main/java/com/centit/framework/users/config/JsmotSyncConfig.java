package com.centit.framework.users.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author zfg
 */
@Configuration
@Data
public class JsmotSyncConfig {

    @Value("${jsmotHost:http://218.2.208.143:10251}")
    private String jsmotHost;

    @Value("${jsmotAppId:85fe34477c954f16ae738452ad80}")
    private String jsmotAppId;

    @Value("${customKey:d868fd0938df4008b8322c903e8d}")
    private String customKey;

    @Value("${customSecret:bdabe60de808493d8b31723cb8c5}")
    private String customSecret;
}
