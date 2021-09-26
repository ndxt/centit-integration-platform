package com.centit.framework.tenan.service;

import com.centit.framework.tenan.po.TenantInfo;
import com.centit.support.common.ObjectException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 判断用户在租户中具有的权限与角色
 * 如果用户登录后，且判断的是当前人员不需要传入参数userCode
 */
public interface TenantPowerManage {

    /**
     * 判断用户是否为租户所有者
     *
     * @param userCode 用户code
     * @param topUnit  租户id
     * @return true:是所有者
     */
    boolean userIsTenantOwner(String userCode, String topUnit);


    /**
     * 判断当前用户是否为租户所有者
     *
     * @param topUnit topUnit 租户id
     * @return true:是所有者
     * @throws ObjectException 如果当前用户未登录抛出异常
     */
    boolean userIsTenantOwner(String topUnit) throws ObjectException;

    /**
     * 判断用户是否为租户管理员
     *
     * @param userCode 用户code
     * @param topUnit  租户id
     * @return true:是管理员
     */
    boolean userIsTenantAdmin(String userCode, String topUnit);

    /**
     * 判断当前用户是否为租户管理员
     *
     * @param topUnit topUnit 租户id
     * @return true:是管理员
     * @throws ObjectException 如果当前用户未登录抛出异常
     */
    boolean userIsTenantAdmin(String topUnit) throws ObjectException;

    /**
     * 判断用户是否为租户成员
     *
     * @param userCode 用户code
     * @param topUnit  租户id
     * @return true:是管理员
     */
    boolean userIsTenantMember(String userCode, String topUnit);

    /**
     * 判断当前用户是否为租户成员
     *
     * @param topUnit 租户id
     * @return true:是成员
     * @throws ObjectException 如果当前用户未登录抛出异常
     */
    boolean userIsTenantMember(String topUnit) throws ObjectException;

    /**
     * 判断用户是否为应用管理员
     *
     * @param userCode 用户code
     * @param osId     应用id
     * @return true:是管理员
     */
    boolean userIsApplicationAdmin(String userCode, String osId);

    /**
     * 判断当前用户是否为应用管理员
     *
     * @param osId 应用id
     * @return true:是管理员
     * @throws ObjectException 如果当前用户未登录抛出异常
     */
    boolean userIsApplicationAdmin(String osId) throws ObjectException;

    /**
     * 判断用户是否为应用成员
     *
     * @param userCode 用户code
     * @param osId     应用id
     * @return true:是成员
     * @throws ObjectException 如果当前用户未登录抛出异常
     */
    boolean userIsApplicationMember(String userCode, String osId) throws ObjectException;

    /**
     * 判断当前用户是否为应用成员
     *
     * @param osId 应用id
     * @return true:是成员
     * @throws ObjectException 如果当前用户未登录抛出异常
     */
    boolean userIsApplicationMember(String osId) throws ObjectException;

    /**
     * 获取租户资源上限
     * @param topUnit 租户id
     * @return 租户详情
     */
    TenantInfo tenantResourceLimit(String topUnit);

    /**
     * 租户已用资源
     * @param topUnit 租户id
     * @return map中对应的key：
     *       SOURCE_TYPE：资源code
     *      SOURCE_TYPE_COUNT：已用个数
     */
    List<Map<String, Object>> tenantResourceUsed(String topUnit);

    /**
     * 租户资源详情
     * @param topUnit 租户id
     * @return map中对应的key：
     *      sourceType 资源类型
     *      limit 资源限制个数
     *      usedSource 已用资源个数
     *      useAble可用资源个数
     *      isLimit 是否达到上限 true:达到上限 false：未达到上限
     */
    ArrayList<HashMap<String, Object>> tenantResourceDetails(String topUnit);


    /**
     * 租户指定资源详情
     * @param topUnit 租户id
     * @param resourceType 租户类型
     * @return map中对应的key：
     *      sourceType 资源类型
     *      limit 资源限制个数
     *      usedSource 已用资源个数
     *      useAble可用资源个数
     *      isLimit 是否达到上限 true:达到上限 false：未达到上限
     */
    HashMap<String, Object> specialResourceDetails(String topUnit, String resourceType);
}
