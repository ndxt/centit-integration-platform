package com.centit.framework.ip.config;

import com.centit.framework.common.WebOptUtils;
import com.centit.framework.components.CodeRepositoryCache;
import com.centit.framework.components.OperationLogCenter;
import com.centit.framework.model.adapter.NotificationCenter;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.system.service.OperationLogManager;
import com.centit.framework.system.service.impl.DBPlatformEnvironment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import javax.annotation.Resource;

/**
 * Created by codefan on 17-7-6.
 */
public class InstantiationServiceBeanPostProcessor implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    protected NotificationCenter notificationCenter;

    @Resource
    private OperationLogManager operationLogManager;

    @Autowired
    private PlatformEnvironment platformEnvironment;

    @Value("${http.exception.notAsHttpError:false}")
    protected boolean httpExceptionNotAsHttpError;

    @Value("${app.support.tenant:false}")
    protected boolean supportTenant;

    @Autowired
    protected CodeRepositoryCache.EvictCacheExtOpt osInfoManager;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        CodeRepositoryCache.setAllCacheFreshPeriod(CodeRepositoryCache.CACHE_FRESH_PERIOD_SECONDS);
        WebOptUtils.setExceptionNotAsHttpError(httpExceptionNotAsHttpError);
        WebOptUtils.setIsTenant(supportTenant);
        if (operationLogManager != null) {
            OperationLogCenter.registerOperationLogWriter(operationLogManager);
        }
        DBPlatformEnvironment dbPlatformEnvironment = event.getApplicationContext().getBean("dbPlatformEnvironment",
            DBPlatformEnvironment.class);
        dbPlatformEnvironment.setSupportTenant(supportTenant);
        CodeRepositoryCache.setPlatformEnvironment(platformEnvironment);
    }

}
