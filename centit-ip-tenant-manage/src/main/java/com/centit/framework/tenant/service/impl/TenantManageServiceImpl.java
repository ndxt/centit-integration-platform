package com.centit.framework.tenant.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.centit.framework.components.CodeRepositoryUtil;
import com.centit.framework.model.basedata.UserInfo;
import com.centit.framework.tenant.dao.TenantInfoDao;
import com.centit.framework.tenant.po.TenantInfo;
import com.centit.tenant.dubbo.adapter.TenantManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service("tenantManageServiceImpl")
public class TenantManageServiceImpl implements TenantManageService {
    @Autowired
    private TenantInfoDao tenantInfoDao;

    @Override
    public JSONObject getTenantInfoByTopUnit(String topUnit) {
        TenantInfo tenantInfo = tenantInfoDao.getObjectById(topUnit);
        if (null == tenantInfo) {
            return null;
        }
        //单独翻译租户所有者姓名
        Map<String, UserInfo> userRepo = CodeRepositoryUtil.getUserRepo(topUnit);
        UserInfo ownUserInfo = userRepo.get(tenantInfo.getOwnUser());
        JSONObject jsonObject = JSONObject.from(tenantInfo);
        jsonObject.put("ownUserName", null == ownUserInfo ? "" : ownUserInfo.getUserName());
        return jsonObject;
    }
}
