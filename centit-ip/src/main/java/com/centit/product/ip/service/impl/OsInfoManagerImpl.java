package com.centit.product.ip.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.centit.framework.hibernate.service.BaseEntityManagerImpl;
import com.centit.product.ip.dao.OsInfoDao;
import com.centit.product.ip.po.OsInfo;
import com.centit.product.ip.service.OsInfoManager;

@Service
@Transactional
public class OsInfoManagerImpl extends BaseEntityManagerImpl<OsInfo,String,OsInfoDao>
        implements OsInfoManager {
 
    //private static final SysOptLog sysOptLog = SysOptLogFactoryImpl.getSysOptLog();

    @Override
    @Resource(name = "osInfoDao")
    public void setBaseDao(OsInfoDao baseDao) {
        super.baseDao = baseDao;
    }

}

