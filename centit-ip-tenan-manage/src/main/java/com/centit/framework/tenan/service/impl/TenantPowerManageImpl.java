package com.centit.framework.tenan.service.impl;

import com.centit.framework.common.WebOptUtils;
import com.centit.framework.system.dao.UserUnitDao;
import com.centit.framework.tenan.dao.TenantInfoDao;
import com.centit.product.dao.WorkGroupDao;
import com.centit.framework.tenan.po.TenantInfo;
import com.centit.framework.tenan.service.TenantPowerManage;
import com.centit.support.algorithm.CollectionsOpt;
import com.centit.support.common.ObjectException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.centit.framework.tenan.constant.TenantConstant.*;

@Service
public class TenantPowerManageImpl implements TenantPowerManage {

    protected Logger logger = LoggerFactory.getLogger(TenantPowerManage.class);
    @Autowired
    private TenantInfoDao tenantInfoDao;

    @Autowired
    private WorkGroupDao workGroupDao;

    @Autowired
    private UserUnitDao userUnitDao;


    @Override
    public boolean userIsTenantOwner(String userCode, String topUnit) {
        return tenantInfoDao.userIsOwner(topUnit, userCode);
    }

    @Override
    public boolean userIsTenantOwner(String topUnit) throws ObjectException {
        String userCode = WebOptUtils.getCurrentUserCode(
            ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest());
        if (StringUtils.isBlank(userCode)) {
            throw new ObjectException("用户未登录!");
        }
        return userIsTenantOwner(userCode, topUnit);
    }

    @Override
    public boolean userIsTenantAdmin(String userCode, String topUnit) {
        Map<String, Object> filterMap = CollectionsOpt.createHashMap("groupId", topUnit, "userCode", userCode,
            "roleCode", TENANT_ADMIN_ROLE_CODE);
        return workGroupDao.listObjectsByProperties(filterMap).size() > 0;
    }

    @Override
    public boolean userIsTenantAdmin(String topUnit) throws ObjectException {
        String userCode = WebOptUtils.getCurrentUserCode(
            ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest());
        if (StringUtils.isBlank(userCode)) {
            throw new ObjectException("用户未登录!");
        }
        return userIsTenantAdmin(userCode, topUnit);
    }

    @Override
    public boolean userIsTenantMember(String userCode, String topUnit) {
        Map<String, Object> filterMap = CollectionsOpt.createHashMap("userCode", userCode, "topUnit", topUnit);
        return !CollectionUtils.sizeIsEmpty(userUnitDao.listObjects(filterMap));
    }

    @Override
    public boolean userIsTenantMember(String topUnit) throws ObjectException {
        String userCode = WebOptUtils.getCurrentUserCode(
            ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest());
        if (StringUtils.isBlank(userCode)) {
            throw new ObjectException("用户未登录!");
        }
        return userIsTenantMember(userCode, topUnit);
    }

    @Override
    public boolean userIsApplicationAdmin(String userCode, String osId) {
        Map<String, Object> filterMap = CollectionsOpt.createHashMap("groupId", osId, "userCode", userCode, "roleCode",
            APPLICATION_ADMIN_ROLE_CODE);
        return workGroupDao.listObjectsByProperties(filterMap).size() > 0;
    }

    @Override
    public boolean userIsApplicationAdmin(String osId) throws ObjectException {
        String userCode = WebOptUtils.getCurrentUserCode(
            ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest());
        if (StringUtils.isBlank(userCode)) {
            throw new ObjectException("用户未登录!");
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
        String userCode = WebOptUtils.getCurrentUserCode(
            ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest());
        if (StringUtils.isBlank(userCode)) {
            throw new ObjectException("用户未登录!");
        }
        return userIsApplicationMember(userCode, osId);
    }

    @Override
    public TenantInfo tenantResourceLimit(String topUnit) {
        Map<String, Object> filterMap = CollectionsOpt.createHashMap("topUnit", topUnit, "isAvailable", "T");
        List<TenantInfo> tenantInfos = tenantInfoDao.listObjectsByProperties(filterMap);
        if (CollectionUtils.sizeIsEmpty(tenantInfos)) {
            return null;
        }
        return tenantInfos.get(0);
    }

    @Override
    public List<Map<String, Object>> tenantResourceUsed(String topUnit) {
        //屏蔽用户资源功能，查询逻辑待定
        return null;
        // return databaseInfoDao.listHashUsedDatabaseByGroup(topUnit);
    }

    @Override
    public ArrayList<HashMap<String, Object>> tenantResourceDetails(String topUnit) {
        TenantInfo tenantInfo = tenantResourceLimit(topUnit);
        if (null == tenantInfo) {
            return new ArrayList<>();
        }
        List<Map<String, Object>> sourceTypeCount = tenantResourceUsed(topUnit);
        int databaseNumberLimit = tenantInfo.getDatabaseNumberLimit();
        int osNumberLimit = tenantInfo.getOsNumberLimit();
        int dataSpaceLimit = tenantInfo.getDataSpaceLimit();
        int fileSpaceLimit = tenantInfo.getFileSpaceLimit();
        ArrayList<HashMap<String, Object>> resultList = new ArrayList<>();
        resultList.add(getSourceUseDetails(sourceTypeCount, databaseNumberLimit, DATABASE_SOURCE_TYPE));
        resultList.add(getSourceUseDetails(sourceTypeCount, osNumberLimit, OS_SOURCE_TYPE));
        resultList.add(getSourceUseDetails(sourceTypeCount, dataSpaceLimit, FILE_SPACE_SOURCE_TYPE));
        resultList.add(getSourceUseDetails(sourceTypeCount, fileSpaceLimit, DATA_SPACE_SOURCE_TYPE));
        return resultList;
    }


    @Override
    public HashMap<String, Object> specialResourceDetails(String topUnit, String resourceType) {
        TenantInfo tenantInfo = tenantResourceLimit(topUnit);
        if (null == tenantInfo) {
            return new HashMap<>();
        }
        Map<String, Object> map = tenantResourceUsed(topUnit, resourceType);
        ArrayList<Map<String, Object>> maps = new ArrayList<>();
        maps.add(map);
        return getSourceUseDetails(maps, getResourceLimit(tenantInfo, resourceType), resourceType);
    }

    @Override
    public boolean userIsSystemMember(String userCode) {
        return this.userIsTenantMember(userCode, SYSTEM_TENANT_TOP_UNIT_CODE);
    }

    @Override
    public boolean userIsSystemMember() {
        return userIsTenantMember(SYSTEM_TENANT_TOP_UNIT_CODE);
    }

    @Override
    public boolean userIsSystemAdmin(String userCode) {
        return userIsTenantAdmin(userCode, SYSTEM_TENANT_TOP_UNIT_CODE);
    }

    @Override
    public boolean userIsSystemAdmin() {
        return userIsTenantAdmin(SYSTEM_TENANT_TOP_UNIT_CODE);
    }

    /**
     * 获取资源详情
     *
     * @param sourceTypeCount 已用资源集合
     * @param numberLimit     资源的限制个数
     * @param sourceType      资源类型
     * @return sourceType 资源类型 limit 资源限制个数 usedSource 已用资源个数
     * useAble可用资源个数
     * isLimit 是否达到上限 true:达到上限 false：未达到上限
     */
    private HashMap<String, Object> getSourceUseDetails(List<Map<String, Object>> sourceTypeCount, int numberLimit, String sourceType) {
        HashMap<String, Object> resultMap = new HashMap<>();
        int usedSource = usedSourceTypeCount(sourceTypeCount, sourceType);
        resultMap.put("sourceType", sourceType);
        resultMap.put("limit", numberLimit);
        resultMap.put("usedSource", usedSource);
        resultMap.put("useAble", numberLimit - usedSource < 0 ? 0 : numberLimit - usedSource);
        resultMap.put("isLimit", numberLimit - usedSource <= 0);
        return resultMap;
    }

    /**
     * 获取指定资源类型的已用数量
     *
     * @param sourceTypeCount 已用资源集合
     * @param sourceType      资源类型
     * @return 指定资源类型的已用数量
     */
    private int usedSourceTypeCount(List<Map<String, Object>> sourceTypeCount, String sourceType) {
        for (Map<String, Object> sourceMap : sourceTypeCount) {
            if (sourceType.equals(MapUtils.getString(sourceMap, "sourceType"))) {
                return MapUtils.getInteger(sourceMap, "sourceTypeCount");
            }
        }
        return 0;
    }

    /**
     * 获取指定租户下指定资源已使用资源个数
     *
     * @param topUnit
     * @param sourceType
     * @return
     */
    public Map<String, Object> tenantResourceUsed(String topUnit, String sourceType) {
        //屏蔽资源查询公告，查询逻辑待定
        return null;
        //return databaseInfoDao.listHashUsedDatabaseBySourceType(topUnit, sourceType);
    }

    /**
     * 从tenantInfo中获取指定资源类型对应的上限值
     *
     * @param tenantInfo   租户详情
     * @param resourceType 资源类型
     * @return 上限值
     */
    private int getResourceLimit(TenantInfo tenantInfo, String resourceType) {
        switch (resourceType) {
            case DATABASE_SOURCE_TYPE:
                return tenantInfo.getDatabaseNumberLimit();
            case OS_SOURCE_TYPE:
                return tenantInfo.getOsNumberLimit();
            case FILE_SPACE_SOURCE_TYPE:
                return tenantInfo.getFileSpaceLimit();
            case DATA_SPACE_SOURCE_TYPE:
                return tenantInfo.getDataSpaceLimit();
            default:
                logger.warn("未找到资源类型对应的上限值!资源类型code是:{}", resourceType);
                return 0;
        }
    }
}
