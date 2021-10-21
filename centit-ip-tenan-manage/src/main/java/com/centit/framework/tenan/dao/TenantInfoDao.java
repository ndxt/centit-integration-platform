package com.centit.framework.tenan.dao;

import com.centit.framework.jdbc.dao.BaseDaoImpl;
import com.centit.framework.tenan.po.TenantInfo;
import com.centit.framework.tenan.vo.PageListTenantInfoQo;
import com.centit.support.algorithm.CollectionsOpt;
import com.centit.support.database.utils.PageDesc;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class TenantInfoDao extends BaseDaoImpl<TenantInfo,String> {


    public List<TenantInfo> listObjectsByProperties(PageListTenantInfoQo pageListTenantInfoQo, PageDesc pageDesc) {

        HashMap<String, Object> filterMap = getQoFilterMap(pageListTenantInfoQo);
        return super.listObjectsByProperties(filterMap,pageDesc);
    }

    /**
     *把请求参数转换为过滤参数
     * @param pageListTenantInfoQo
     * @return
     */
    private HashMap<String, Object> getQoFilterMap(PageListTenantInfoQo pageListTenantInfoQo) {
        HashMap<String, Object> filterMap = new HashMap<>();

        if (StringUtils.isNotBlank(pageListTenantInfoQo.getUnitName())){
            filterMap.put("unitName_lk",StringUtils.join("%",pageListTenantInfoQo.getUnitName(),"%"));
        }

        if (StringUtils.isNotBlank(pageListTenantInfoQo.getCheckState()) && pageListTenantInfoQo.getCheckState().equals("1")){
            //passTime_nv is null
            filterMap.put("passTime_nv","");
        }
        if (StringUtils.isNotBlank(pageListTenantInfoQo.getCheckState()) && pageListTenantInfoQo.getCheckState().equals("2")){
            //passTime is not null
            filterMap.put("passTime_nn","");
        }

        if (null != pageListTenantInfoQo.getStartApplyTime()){
            filterMap.put("applyTime_ge",pageListTenantInfoQo.getStartApplyTime());
        }
        if (null != pageListTenantInfoQo.getEndApplyTime()){
            filterMap.put("applyTime_le",pageListTenantInfoQo.getEndApplyTime());
        }

        if (null != pageListTenantInfoQo.getStartUseLimittime()){
            filterMap.put("useLimittime_ge",pageListTenantInfoQo.getStartUseLimittime());
        }
        if (null != pageListTenantInfoQo.getEndUseLimittime()){
            filterMap.put("useLimittime_le",pageListTenantInfoQo.getEndUseLimittime());
        }

        if (null != pageListTenantInfoQo.getStartPassTime()){
            filterMap.put("passTime_ge",pageListTenantInfoQo.getStartPassTime());
        }
        if (null != pageListTenantInfoQo.getEndPassTime()){
            filterMap.put("passTime_le",pageListTenantInfoQo.getEndPassTime());
        }

        if (StringUtils.isNotBlank(pageListTenantInfoQo.getIsAvailable())){
            filterMap.put("isAvailable",pageListTenantInfoQo.getIsAvailable());
        }

        if (StringUtils.isNotBlank(pageListTenantInfoQo.getOwnUser())){
            filterMap.put("ownUser",pageListTenantInfoQo.getOwnUser());
        }
        return filterMap;
    }

    /**
     * 校验用户是否为租户所有者
     * @param topUnit
     * @param userCode
     * @return
     */
    public boolean userIsOwner(String topUnit,String userCode) {

        Map<String, Object> filterMap = CollectionsOpt.createHashMap("topUnit", topUnit, "ownUser", userCode);
        return super.listObjectsByProperties(filterMap).size() > 0;
    }
}
