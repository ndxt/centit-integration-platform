package com.centit.framework.tenant.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.centit.framework.components.CodeRepositoryUtil;
import com.centit.framework.model.basedata.IUserInfo;
import com.centit.framework.tenant.dao.TenantInfoDao;
import com.centit.framework.tenant.po.TenantInfo;
import com.centit.tenant.dubbo.adapter.TenantManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
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
        Map<String, ? extends IUserInfo> userRepo = CodeRepositoryUtil.getUserRepo(topUnit);
        IUserInfo ownUserInfo = userRepo.get(tenantInfo.getOwnUser());
        JSONObject jsonObject = (JSONObject) JSONObject.toJSON(tenantInfo);
        jsonObject.put("ownUserName", null == ownUserInfo ? "" : ownUserInfo.getUserName());
        return jsonObject;
    }
}
