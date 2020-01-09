package com.centit.framework.ip.service.impl;

import com.centit.framework.ip.dao.DatabaseInfoDao;
import com.centit.framework.ip.dao.OsInfoDao;
import com.centit.framework.ip.po.DatabaseInfo;
import com.centit.framework.ip.po.OsInfo;
import com.centit.framework.ip.service.IntegrationEnvironment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by codefan on 17-7-3.
 */
@Service
public class DBIntegrationEnvironmentImpl implements IntegrationEnvironment {

    @Autowired
    private OsInfoDao osInfoDao;

    @Autowired
    private DatabaseInfoDao databaseInfoDao;
    /**
     * 刷新集成环境相关信息
     * 包括：业务系统、数据库信息
     *
     * @return boolean 刷新集成环境相关信息
     */
    @Override
    public boolean reloadIPEnvironmen() {
        return true;
    }

    /**
     * 获取框架中注册的业务系统
     *
     * @param osId osId
     * @return 框架中注册的业务系统
     */
    @Override
    public OsInfo getOsInfo(String osId) {
        return osInfoDao.getObjectById(osId);
    }

    /**
     * 获取框架中注册的数据库
     *
     * @param databaseCode databaseCode
     * @return 框架中注册的数据库
     */
    @Override
    public DatabaseInfo getDatabaseInfo(String databaseCode) {
        return databaseInfoDao.getDatabaseInfoById(databaseCode);
    }

    /**
     * 获取所有注册的业务系统
     *
     * @return 所有注册的业务系统
     */
    @Override
    public List<OsInfo> listOsInfos() {
        return osInfoDao.listObjects();
    }

    /**
     * 获取所有注册的数据库
     *
     * @return 所有注册的数据库
     */
    @Override
    public List<DatabaseInfo> listDatabaseInfo() {
        return databaseInfoDao.listObjects();
    }

    @Override
    public String checkAccessToken(String tokenId, String accessKey) {
        return null;
    }
}
