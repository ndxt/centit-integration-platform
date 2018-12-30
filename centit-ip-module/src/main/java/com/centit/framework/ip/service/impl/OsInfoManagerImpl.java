package com.centit.framework.ip.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.centit.framework.components.CodeRepositoryCache;
import com.centit.support.database.utils.PageDesc;
import com.centit.framework.ip.dao.OsInfoDao;
import com.centit.framework.ip.po.OsInfo;
import com.centit.framework.ip.service.OsInfoManager;
import com.centit.framework.jdbc.service.BaseEntityManagerImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;


@Service("osInfoManager")
@Transactional
public class OsInfoManagerImpl extends BaseEntityManagerImpl<OsInfo,String,OsInfoDao>
        implements OsInfoManager, CodeRepositoryCache.EvictCacheExtOpt {

    //private static final SysOptLog sysOptLog = SysOptLogFactoryImpl.getSysOptLog();

    @Override
    @Resource(name = "osInfoDao")
    public void setBaseDao(OsInfoDao baseDao) {
        super.baseDao = baseDao;
    }

    @Override
    public List<OsInfo> listObjects(Map<String, Object> map){
        return baseDao.listObjects(map);
    }

    @Override
    public List<OsInfo> listObjects(Map<String, Object> map, PageDesc pageDesc){
        return baseDao.listObjectsByProperties(map, pageDesc);
    }

    @Override
    public JSONArray listOsInfoAsJson(Map<String, Object> filterMap, PageDesc pageDesc){
        return baseDao.listObjectsAsJson(filterMap, pageDesc);
    }

    /**
     * 刷新某个缓存
     * @param cacheName 缓存名
     * @param mapKey 对应额key
     */
    @Override
    public void evictCache(String cacheName, String mapKey) {

    }

    /**
     * 刷新某个缓存
     * @param cacheName 缓存名
     */
    @Override
    public void evictCache(String cacheName) {

    }

    /**
     * 刷新所有缓存
     */
    @Override
    public void evictAllCache() {

    }
}

