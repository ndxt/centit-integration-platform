package com.centit.framework.ip.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.centit.framework.components.CodeRepositoryCache;
import com.centit.framework.ip.po.DatabaseInfo;
import com.centit.framework.ip.po.OsInfo;
import com.centit.framework.ip.po.UserAccessToken;
import com.centit.framework.ip.service.IntegrationEnvironment;
import com.centit.support.common.CachedObject;
import com.centit.support.file.FileIOOpt;
import com.centit.support.file.FileSystemOpt;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.List;

/**
 * Created by codefan on 17-7-3.
 */
public class JsonIntegrationEnvironment extends AbstractIntegrationEnvironment {

    private Logger logger = LoggerFactory.getLogger(JsonIntegrationEnvironment.class);

    protected String appHome;

    public void setAppHome(String appHome) {
        this.appHome = appHome;
    }

    private String loadJsonStringFormConfigFile(String fileName) throws IOException {
        String jsonFile = appHome +"/config" +  fileName;
        if(FileSystemOpt.existFile(jsonFile)) {
            return FileIOOpt.readStringFromFile(jsonFile,"UTF-8");
        }else{

            return FileIOOpt.readStringFromInputStream(
                    new ClassPathResource(fileName).getInputStream(),"UTF-8");

        }
    }

    public JsonIntegrationEnvironment(){
        super();
    }

    @Override
    public List<OsInfo> reloadOsInfos() {
        try {
            String jsonStr = loadJsonStringFormConfigFile("/ip_environmen.json");
            JSONObject json = JSON.parseObject(jsonStr);
            return JSON.parseArray(json.getString("osInfos"), OsInfo.class);

        } catch (IOException e) {

            logger.error("加载集成数据出错",e);
            return null;
        }
    }

    @Override
    public List<DatabaseInfo> reloadDatabaseInfos() {
        try {
            String jsonStr = loadJsonStringFormConfigFile("/ip_environmen.json");
            JSONObject json = JSON.parseObject(jsonStr);
            return JSON.parseArray(json.getString("databaseInfos"),
                DatabaseInfo.class);
        } catch (IOException e) {
            logger.error("加载集成数据出错",e);
            return null;
        }
    }

    @Override
    public List<UserAccessToken> reloadAccessTokens() {
        try {
            String jsonStr = loadJsonStringFormConfigFile("/ip_environmen.json");
            JSONObject json = JSON.parseObject(jsonStr);
            return JSON.parseArray(json.getString("userAccessTokens"),
                    UserAccessToken.class);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("加载集成数据出错",e);
            return null;
        }
    }
}
