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

@Repository
public class WorkGroupDao extends BaseDaoImpl<WorkGroup, String> {
    /**
     * 校验用户是否为租户管理员
     *
     * @param topUnit
     * @param userCode
     * @return
     */
    public boolean userIsAdmin(String topUnit, String userCode) {

        Map<String, Object> filterMap = CollectionsOpt.createHashMap("groupId", topUnit, "userCode", userCode, "roleCode", "ZHGLY");
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
     * @return
     */
    @Transactional
    public int updateByProperties(Map<String, Object> filterMap) {

        String sql = " UPDATE work_group SET role_code = :roleCode, updator = :updator, update_date = :updateDate WHERE group_id = :groupId AND user_code = :userCode ";
        filterMap.put("updateDate", new Date(System.currentTimeMillis()));
        QueryAndParams qap = QueryAndParams.createFromQueryAndNamedParams(new QueryAndNamedParams(sql, filterMap));
        return super.jdbcTemplate.execute((Connection conn) -> DatabaseAccess.doExecuteSql(conn, qap.getQuery(), qap.getParams()));
    }

}
