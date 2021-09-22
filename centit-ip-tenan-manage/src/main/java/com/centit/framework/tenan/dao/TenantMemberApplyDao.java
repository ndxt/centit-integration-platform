package com.centit.framework.tenan.dao;

import com.centit.framework.jdbc.dao.BaseDaoImpl;
import com.centit.framework.tenan.po.TenantMemberApply;
import com.centit.support.database.utils.PageDesc;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Repository
public class TenantMemberApplyDao extends BaseDaoImpl<TenantMemberApply,String> {


    /**
     * 分页查询租户邀请用户的邀请且用户没有处理的信息
     * @param filterMap filterMap中必须存在key  userCode
     * @param pageDesc
     * @return
     */
    @Transactional
    public List<TenantMemberApply> pageListNotApproveApplyByUserCode(Map<String, Object> filterMap, PageDesc pageDesc) {
        filterMap.put("applyType","2");
        filterMap.put("applyState_in",new  String[]{"1","2"});
        return super.listObjects(filterMap, pageDesc);
    }

    /**
     * 分页查询用户申请加入租户且租户没有处理的申请信息
     * @param filterMap filterMap中必须存在key  topUnit
     * @param pageDesc
     * @return
     */
    @Transactional
    public List<TenantMemberApply> pageListNotApproveApplyByUnitCode(Map<String, Object> filterMap, PageDesc pageDesc) {
        filterMap.put("applyType","1");
        filterMap.put("applyState_in",new  String[]{"1","2"});
        return super.listObjects(filterMap, pageDesc);
    }

    /**
     * 分页查询租户邀请用户的邀请且租户已经处理的信息
     * @param filterMap filterMap中必须存在key  userCode
     * @param pageDesc
     * @return
     */
    @Transactional
    public List<TenantMemberApply> pageListHasApproveApplyByUserCode(Map<String, Object> filterMap, PageDesc pageDesc) {
        filterMap.put("applyType","2");
        filterMap.put("applyState_in",new  String[]{"3","4"});
        return super.listObjects(filterMap, pageDesc);
    }

    /**
     * 分页查询用户申请加入租户且租户已经处理的申请信息
     * @param filterMap filterMap中必须存在key  topUnit
     * @param pageDesc
     * @return
     */
    @Transactional
    public List<TenantMemberApply> pageListHasApproveApplyByUnitCode(Map<String, Object> filterMap, PageDesc pageDesc) {
        filterMap.put("applyType","1");
        filterMap.put("applyState_in",new  String[]{"3","4"});
        return super.listObjects(filterMap, pageDesc);
    }

    /**
     * 保存数据前查看数据是否存在，如果存在把applyState修改为重新申请
     * @param tenantMemberApply
     */
    @Transactional
    public void saveTenantMemberApply(TenantMemberApply tenantMemberApply) {
        TenantMemberApply oldTenantMemberApply = super.getObjectById(tenantMemberApply);
        if (null == oldTenantMemberApply){
            tenantMemberApply.setApplyState("1");
            super.saveNewObject(tenantMemberApply);
        }else {
            //更新字段为空的数据
            tenantMemberApply.setApplyState("2");
            String[] fields = new String[]{"inviterUserCode","applyType","applyTime","applyState",
                "applyRemark","approveRemark","unitCode"};
            super.updateObject(fields,tenantMemberApply);
        }
    }


}
