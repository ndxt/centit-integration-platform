package com.centit.framework.tenant.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.centit.framework.tenant.dao.TenantInfoDao;
import com.centit.framework.tenant.po.TenantInfo;
import com.centit.tenant.dubbo.adapter.TenantManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TenantManageServiceImpl implements TenantManageService {
    @Autowired
    private TenantInfoDao tenantInfoDao;
    @Override
    public JSONObject getTenantInfoByTopUnit(String topUnit) {
        TenantInfo tenantInfo = tenantInfoDao.getObjectById(topUnit);
        if (null == tenantInfo){
            return null;
        }
        return JSONObject.parseObject(JSONObject.toJSONString(tenantInfo));
    }
}
