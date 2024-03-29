package com.centit.framework.tenant.service.impl;

import com.centit.framework.common.ResponseData;
import com.centit.framework.common.WebOptUtils;
import com.centit.framework.filter.RequestThreadLocal;
import com.centit.framework.system.dao.UnitInfoDao;
import com.centit.framework.system.dao.UserUnitDao;
import com.centit.framework.system.dao.WorkGroupDao;
import com.centit.framework.tenant.constant.TenantConstant;
import com.centit.framework.tenant.dao.TenantInfoDao;
import com.centit.framework.tenant.po.TenantInfo;
import com.centit.framework.tenant.service.TenantPowerManage;
import com.centit.support.algorithm.CollectionsOpt;
import com.centit.support.common.ObjectException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class TenantPowerManageImpl implements TenantPowerManage {

    protected Logger logger = LoggerFactory.getLogger(TenantPowerManage.class);
    @Autowired
    private TenantInfoDao tenantInfoDao;

    @Autowired
    private WorkGroupDao workGroupDao;

    @Autowired
    private UserUnitDao userUnitDao;

    @Autowired
    private UnitInfoDao unitInfoDao;

    @Override
    public boolean userIsTenantOwner(String userCode, String topUnit) {
        return tenantInfoDao.userIsOwner(topUnit, userCode);
    }

    @Override
    public boolean userIsTenantOwner(String topUnit) throws ObjectException {
        String userCode = WebOptUtils.getCurrentUserCode(RequestThreadLocal.getLocalThreadWrapperRequest());
        if (StringUtils.isBlank(userCode)) {
            throw new ObjectException(ResponseData.ERROR_USER_NOT_LOGIN, "用户未登录!");
        }
        return userIsTenantOwner(userCode, topUnit);
    }

    @Override
    public boolean userIsTenantAdmin(String userCode, String topUnit) {
        Map<String, Object> filterMap = CollectionsOpt.createHashMap("groupId", topUnit, "userCode", userCode,
            "roleCode", TenantConstant.TENANT_ADMIN_ROLE_CODE);
        return workGroupDao.listObjectsByProperties(filterMap).size() > 0;
    }


    @Override
    public String userTenantRole(String topUnit) {
        if (StringUtils.isBlank(topUnit)) {
            throw new ObjectException(ResponseData.ERROR_USER_NOT_LOGIN, "topUnit不能为空");
        }
        if (this.userIsTenantOwner(topUnit)) {
            return TenantConstant.TENANT_OWNE_ROLE_CODE;
        }
        if (this.userIsTenantAdmin(topUnit)) {
            return TenantConstant.TENANT_ADMIN_ROLE_CODE;
        }
        if (this.userIsTenantMember(topUnit)) {
            return TenantConstant.TENANT_NORMAL_MEMBER_ROLE_CODE;
        }
        return "";
    }

    @Override
    public boolean userIsTenantAdmin(String topUnit) throws ObjectException {
        String userCode = WebOptUtils.getCurrentUserCode(RequestThreadLocal.getLocalThreadWrapperRequest());
        if (StringUtils.isBlank(userCode)) {
            throw new ObjectException(ResponseData.ERROR_USER_NOT_LOGIN, "用户未登录!");
        }
        return userIsTenantAdmin(userCode, topUnit);
    }

    @Override
    public boolean userIsTenantMember(String userCode, String topUnit) {
        Map<String, Object> filterMap = CollectionsOpt.createHashMap("userCode", userCode, "topUnit", topUnit);
        return userUnitDao.countObjectByProperties(filterMap)>0;
    }

    @Override
    public boolean userIsTenantMember(String topUnit) throws ObjectException {
        String userCode = WebOptUtils.getCurrentUserCode(RequestThreadLocal.getLocalThreadWrapperRequest());
        if (StringUtils.isBlank(userCode)) {
            throw new ObjectException(ResponseData.ERROR_USER_NOT_LOGIN, "用户未登录!");
        }
        return userIsTenantMember(userCode, topUnit);
    }

    @Override
    public boolean userIsApplicationAdmin(String userCode, String osId) {
        Map<String, Object> filterMap = CollectionsOpt.createHashMap("groupId", osId, "userCode", userCode, "roleCode",
            TenantConstant.APPLICATION_ADMIN_ROLE_CODE);
        return workGroupDao.listObjectsByProperties(filterMap).size() > 0;
    }

    @Override
    public boolean userIsApplicationAdmin(String osId) throws ObjectException {
        String userCode = WebOptUtils.getCurrentUserCode(RequestThreadLocal.getLocalThreadWrapperRequest());
        if (StringUtils.isBlank(userCode)) {
            throw new ObjectException(ResponseData.ERROR_USER_NOT_LOGIN, "用户未登录!");
        }
        return userIsApplicationAdmin(userCode, osId);
    }

    @Override
    public boolean userIsApplicationMember(String userCode, String osId) throws ObjectException {
        Map<String, Object> filterMap = CollectionsOpt.createHashMap("groupId", osId, "userCode", userCode);
        return workGroupDao.listObjectsByProperties(filterMap).size() > 0;
    }

    @Override
    public boolean userIsApplicationMember(String osId) throws ObjectException {
        String userCode = WebOptUtils.getCurrentUserCode(RequestThreadLocal.getLocalThreadWrapperRequest());
        if (StringUtils.isBlank(userCode)) {
            throw new ObjectException(ResponseData.ERROR_USER_NOT_LOGIN, "用户未登录!");
        }
        return userIsApplicationMember(userCode, osId);
    }

    @Override
    public boolean userIsSystemMember(String userCode) {
        return this.userIsTenantMember(userCode, TenantConstant.SYSTEM_TENANT_TOP_UNIT_CODE);
    }

    @Override
    public boolean userIsSystemMember() {
        return userIsTenantMember(TenantConstant.SYSTEM_TENANT_TOP_UNIT_CODE);
    }

    @Override
    public boolean userIsSystemAdmin(String userCode) {
        return !CollectionUtils.sizeIsEmpty(userUnitDao.listUserUnitsByUserCode(TenantConstant.SYSTEM_TENANT_TOP_UNIT_CODE, userCode));
    }

    @Override
    public boolean userIsSystemAdmin() {
        String userCode = WebOptUtils.getCurrentUserCode(RequestThreadLocal.getLocalThreadWrapperRequest());
        if (StringUtils.isBlank(userCode)) {
            throw new ObjectException(ResponseData.ERROR_USER_NOT_LOGIN, "用户未登录!");
        }

        return !CollectionUtils.sizeIsEmpty(userUnitDao.listUserUnitsByUserCode(TenantConstant.SYSTEM_TENANT_TOP_UNIT_CODE, userCode));
    }

    @Override
    public boolean userNumberLimitIsOver(String topUnit) {
        TenantInfo tenantInfo = tenantInfoDao.getObjectById(topUnit);
        if (null == tenantInfo || null == tenantInfo.getUserNumberLimit()) {
            return true;
        }
        return userUnitDao.countUserByTopUnit(topUnit) >= tenantInfo.getUserNumberLimit();
    }

    @Override
    public boolean unitNumberLimitIsOver(String topUnit) {
        TenantInfo tenantInfo = tenantInfoDao.getObjectById(topUnit);
        if (null == tenantInfo || null == tenantInfo.getUnitNumberLimit()) {
            return true;
        }
        return unitInfoDao.countUnitByTopUnit(topUnit) >= tenantInfo.getUnitNumberLimit();
    }

}
