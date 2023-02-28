package com.centit.tenant.dubbo.adapter;

import com.alibaba.fastjson2.JSONObject;

public interface TenantManageService {

    /**
     * 根据topUnit获取租户基本信息
     * @param topUnit 租户code
     * @return
     */
    JSONObject getTenantInfoByTopUnit(String topUnit);
}
