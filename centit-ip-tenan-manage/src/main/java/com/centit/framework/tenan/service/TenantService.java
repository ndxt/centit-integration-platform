package com.centit.framework.tenan.service;

import com.centit.framework.common.ResponseData;
import com.centit.framework.core.dao.PageQueryResult;
import com.centit.framework.tenan.po.TenantBusinessLog;
import com.centit.framework.tenan.po.TenantInfo;
import com.centit.framework.system.po.UserInfo;
import com.centit.framework.tenan.po.TenantMember;
import com.centit.framework.tenan.po.TenantMemberApply;
import com.centit.framework.tenan.vo.PageListTenantInfoQo;
import com.centit.framework.tenan.vo.TenantMemberApplyVo;
import com.centit.framework.tenan.vo.TenantMemberQo;
import com.centit.support.database.utils.PageDesc;

import java.util.Map;

public interface TenantService {


    /**
     * 用户注册
     * @param userinfo
     * @return
     */
    ResponseData registerUserAccount(UserInfo userinfo) throws IllegalAccessException;

    /**
     * 用户新建租户
     * 1.新建租户
     * 2.根据租户信息创建租户单位
     * 3.把租户所有人与机构信息绑定
     * 4.给租户所有人创建机构管理的最高权限（最高权限怎么设置）
     * @param tenantInfo
     * @return
     */
    ResponseData applyAddTenant(TenantInfo tenantInfo);

    /**
     * 申请加入租户
     * 可以是用户主动申请，也可以是管理员邀请
     * @param tenantMemberApply
     * @return
     */
    ResponseData applyJoinTenant(TenantMemberApply tenantMemberApply);


    /**
     * 列出申请信息
     * 可以是管理员邀请的信息，也可以是用户主动申请的信息
     * @param parameters 需要包含key
     * code（用户代码或机构代码）
     * codeType（代码类型，1：用户代码2：机构代码）
     * approveType（审批类型， 1待审批 2已审批）
     * @param pageDesc
     * @return
     */
    PageQueryResult listApplyInfo(Map<String,Object> parameters, PageDesc pageDesc);

    /**
     * 平台管理员审核租户
     * @param tenantInfo
     * @return
     */
    ResponseData adminCheckTenant(TenantInfo tenantInfo);

    /**
     * 同意加入
     * 可以是平台管理员审核用户的加入
     * 也可以是普通用户同意管理员的邀请
     * @param tenantMemberApplyVo
     * @return
     */
    ResponseData agreeJoin(TenantMemberApplyVo tenantMemberApplyVo);

    /**
     * 更新用户信息
     * @param userInfo
     * @return
     */
    ResponseData updateUserInfo(UserInfo userInfo);

    /**
     * 退出租户
     * @param topUnit
     * @return
     */
    ResponseData quitTenant(String topUnit,String userCode);

    /**
     * 转让租户
     * @param tenantBusinessLog
     * @return
     */
    ResponseData businessTenant(TenantBusinessLog tenantBusinessLog);


    /**
     * 平台审核租户转让
     * 租户所有者交易租户，不需要平台审核
     * @param tenantBusinessLog
     * @return
     */
    @Deprecated
    ResponseData adminCheckTenantBusiness(TenantBusinessLog tenantBusinessLog);

    /**
     * 分页列出租户列表
     * 1.可以是已经审核通过的
     * 2.也可以是待审核的
     * 3.也可以是审核不通过的
     * @param tenantInfo
     * @return
     */
    PageQueryResult<TenantInfo> pageListTenantApply(PageListTenantInfoQo tenantInfo, PageDesc pageDesc);

    /**
     * 展示该机构下的人员
     * @param tenantMemberQo 机构id
     * @param pageDesc
     * @return
     * 姓名
     * 租户id
     * 职位
     */
    PageQueryResult<TenantMember> pageListTenantMember(TenantMemberQo tenantMemberQo, PageDesc pageDesc);

    /**
     * 租户所有者或平台管理员分配管理员权限
     * @param tenantMemberQo
     * @return
     */
    ResponseData adminAssignRole(TenantMemberQo tenantMemberQo);


    /**
     * 用户所在租户
     * @param userCode
     * @return
     */
    ResponseData userTenants(String userCode);
}
