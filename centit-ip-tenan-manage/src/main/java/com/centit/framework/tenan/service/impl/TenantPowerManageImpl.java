package com.centit.framework.tenan.service.impl;

import com.centit.framework.common.ResponseData;
import com.centit.framework.system.dao.UserUnitDao;
import com.centit.framework.system.po.UserUnit;
import com.centit.framework.tenan.dao.TenantInfoDao;
import com.centit.framework.tenan.dao.WorkGroupDao;
import com.centit.framework.tenan.service.TenantPowerManage;
import com.centit.support.algorithm.CollectionsOpt;
import com.centit.support.common.ObjectException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.centit.framework.tenan.util.UserUtils.getUserCodeFromSecurityContext;

@Service
public class TenantPowerManageImpl implements TenantPowerManage {

    @Autowired
    private TenantInfoDao tenantInfoDao;

    @Autowired
    private WorkGroupDao workGroupDao;

    @Autowired
    private UserUnitDao userUnitDao;

    @Override
    public boolean userIsTenantOwner(String userCode, String topUnit) {
        return tenantInfoDao.userIsOwner(userCode, topUnit);
    }

    @Override
    public boolean userIsTenantOwner(String topUnit) throws ObjectException {
        String userCode = getUserCodeFromSecurityContext();
        if (StringUtils.isBlank(userCode)) {
            throw new ObjectException("用户未登录!");
        }
        return userIsTenantOwner(userCode, topUnit);
    }

    @Override
    public boolean userIsTenantAdmin(String userCode, String topUnit) {
        return workGroupDao.userIsTenantAdmin(userCode, topUnit);
    }

    @Override
    public boolean userIsTenantAdmin(String topUnit) throws ObjectException {
        String userCode = getUserCodeFromSecurityContext();
        if (StringUtils.isBlank(userCode)) {
            throw new ObjectException("用户未登录!");
        }
        return workGroupDao.userIsTenantAdmin(userCode, topUnit);
    }

    @Override
    public boolean userIsTenantMember(String userCode, String topUnit) {
        Map<String, Object> filterMap = CollectionsOpt.createHashMap("userCode", userCode, "topUnit", topUnit);
        return CollectionUtils.sizeIsEmpty(userUnitDao.listObjects(filterMap));
    }

    @Override
    public boolean userIsTenantMember(String topUnit) throws ObjectException {
        String userCode = getUserCodeFromSecurityContext();
        if (StringUtils.isBlank(userCode)) {
            throw new ObjectException("用户未登录!");
        }
        return userIsTenantMember(userCode, topUnit);
    }

    @Override
    public boolean userIsApplicationAdmin(String userCode, String osId) {
        return false;
    }

    @Override
    public boolean userIsApplicationAdmin(String osId) throws ObjectException {
        return false;
    }

    @Override
    public boolean userIsApplicationMember(String userCode, String osId) throws ObjectException {
        return false;
    }

    @Override
    public boolean userIsApplicationMember(String osId) throws ObjectException {
        return false;
    }

    @Override
    public ResponseData tenantResourceLimit(String topUnit) {
        return null;
    }

    @Override
    public ResponseData tenantResourceUsed(String topUnit) {
        return null;
    }

    @Override
    public boolean teanatResourceUseable(String topUnit) {
        return false;
    }

    @Override
    public boolean specialResourceUseable(String topUnit, String resourceType) {
        return false;
    }
}
