package com.centit.framework.tenan.dao;

import com.centit.framework.jdbc.dao.BaseDaoImpl;
import com.centit.framework.tenan.po.WorkGroup;
import com.centit.framework.tenan.vo.TenantMemberQo;
import com.centit.support.algorithm.CollectionsOpt;
import com.centit.support.database.utils.DatabaseAccess;
import com.centit.support.database.utils.QueryAndNamedParams;
import com.centit.support.database.utils.QueryAndParams;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.lang.model.element.NestingKind;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

import static com.centit.framework.tenan.constant.TenantConstant.APPLICATION_ADMIN_ROLE_CODE;
import static com.centit.framework.tenan.constant.TenantConstant.TENANT_ADMIN_ROLE_CODE;

@Repository
public class WorkGroupDao extends BaseDaoImpl<WorkGroup, String> {
    /**
     * 校验用户是否为租户管理员
     *
     * @param topUnit 租户d
     * @param userCode 用户code
     * @return
     */
    public boolean userIsTenantAdmin(String topUnit, String userCode) {

        Map<String, Object> filterMap = CollectionsOpt.createHashMap("groupId", topUnit, "userCode", userCode, "roleCode", TENANT_ADMIN_ROLE_CODE);
        return super.listObjectsByProperties(filterMap).size() > 0;
    }

    /**
     *校验用户是否为应用开发组长（管理员）
     * @param osId 应用id
     * @param userCode 用户code
     * @return true：是 false：否
     */
    public boolean userIsApplicationAdmin(String osId, String userCode) {

        Map<String, Object> filterMap = CollectionsOpt.createHashMap("groupId", osId, "userCode", userCode, "roleCode", APPLICATION_ADMIN_ROLE_CODE);
        return super.listObjectsByProperties(filterMap).size() > 0;
    }
    /**
     * 校验用户是否为应用开发组成员
     * @param topUnit 应用id
     * @param userCode 用户id
     * @return
     */
    public boolean userIsMember(String topUnit, String userCode) {
        Map<String, Object> filterMap = CollectionsOpt.createHashMap("groupId", topUnit, "userCode", userCode);
        return super.listObjectsByProperties(filterMap).size() > 0;
    }

    /**
     * 根据userCode和topUnit查询数据
     * @param userCode
     * @param topUnits
     * @return
     */
    public List<WorkGroup> listWorkGroupByUserCodeAndTopUnit(String userCode, String... topUnits) {
        HashMap<String, Object> map = new HashMap<>();
        if (topUnits.length>0){
            map.put("groupId_in",topUnits);
        }
        map.put("userCode",userCode);
        return super.listObjects(map);
    }


    /**
     * 根据groupId userCode更新字段 updator roleCode
     * @param filterMap
     * roleCode require
     * updator  require
     * groupId  require
     * userCode  require
     * @return 更新个数
     */
    @Transactional
    public int updateByProperties(Map<String, Object> filterMap) {

        String sql = " UPDATE work_group SET role_code = :roleCode, updator = :updator, update_date = :updateDate WHERE group_id = :groupId AND user_code = :userCode ";
        filterMap.put("updateDate", new Date(System.currentTimeMillis()));
        QueryAndParams qap = QueryAndParams.createFromQueryAndNamedParams(new QueryAndNamedParams(sql, filterMap));
        return super.jdbcTemplate.execute((Connection conn) -> DatabaseAccess.doExecuteSql(conn, qap.getQuery(), qap.getParams()));
    }

}
