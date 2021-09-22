package com.centit.framework.tenan.dao;

import com.centit.framework.jdbc.dao.BaseDaoImpl;
import com.centit.framework.tenan.po.TenantMember;
import com.centit.framework.tenan.vo.TenantMemberQo;
import com.centit.support.database.orm.OrmDaoUtils;
import com.centit.support.database.utils.PageDesc;
import com.centit.support.database.utils.QueryAndNamedParams;
import com.centit.support.database.utils.QueryUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;

@Repository
public class TenantMemberDao extends BaseDaoImpl<TenantMember, String> {

    /**
     * 分页查询租户内成员信息
     * @param tenantMemberQo
     * @param pageDesc
     * @return
     */
   public List<TenantMember> pageListTenantMember(TenantMemberQo tenantMemberQo, PageDesc pageDesc) {
        String selectFieldsSql = "SELECT A.USER_CODE, A.LOGIN_NAME, A.USER_NAME, A.ENGLISH_NAME, A.TOP_UNIT, A.REG_EMAIL, A.REG_CELL_PHONE, ROLE_CODE ";
        String fromAndFilterSql = "  FROM f_userinfo A  JOIN work_group B ON ( A.USER_CODE = B.user_code AND A.TOP_UNIT = B.group_id  )   WHERE [ (isNotEmpty(topUnit))| B.group_id = :topUnit ] [ (isNotEmpty(loginName)) | AND A.LOGIN_NAME LIKE :loginName ] [ (isNotEmpty(roleCode)) | AND B.ROLE_CODE = :roleCode ] ";
        String limitSql = "LIMIT :offset ,:size  ";

        String querySql = selectFieldsSql + fromAndFilterSql  + limitSql;
        String countSql = " SELECT COUNT(1) " + fromAndFilterSql ;

        HashMap<String, Object> filterMap = new HashMap<>();
        filterMap.put("topUnit", tenantMemberQo.getTopUnit());
        if (StringUtils.isNotBlank(tenantMemberQo.getUserName())) {
            filterMap.put("loginName", StringUtils.join("%", tenantMemberQo.getUserName(), "%"));
        }
        if (StringUtils.isNotBlank(tenantMemberQo.getRoleCode())) {
            filterMap.put("roleCode", tenantMemberQo.getRoleCode());
        }
        filterMap.put("offset", pageDesc.getRowStart());
        filterMap.put("size", pageDesc.getPageSize());
        QueryAndNamedParams qapSql = QueryUtils.translateQuery(querySql, filterMap);
       qapSql.addAllParams(filterMap);

       QueryAndNamedParams qapCount = QueryUtils.translateQuery(countSql, filterMap);
        return this.getJdbcTemplate().execute((Connection conn) ->{
            pageDesc.setTotalRows(OrmDaoUtils.fetchObjectsCount(conn,qapCount.getQuery(), qapSql.getParams()));
           return OrmDaoUtils.queryObjectsByNamedParamsSql(conn, qapSql.getQuery(), qapSql.getParams(), TenantMember.class);}
        );

    }
}
