package com.centit.framework.tenan.dao;

import com.centit.framework.jdbc.dao.BaseDaoImpl;
import com.centit.framework.tenan.po.DatabaseInfo;
import com.centit.support.algorithm.CollectionsOpt;
import com.centit.support.algorithm.StringBaseOpt;
import com.google.common.base.CaseFormat;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class DatabaseInfoDao extends BaseDaoImpl<DatabaseInfo, String> {

    /**
     * 获取指定租户下已使用资源个数
     *
     * @param topUnit 租户id
     * @return key：SOURCE_TYPE：资源code SOURCE_TYPE_COUNT：已用个数
     */

    public List<Map<String, Object>> listHashUsedDatabaseByGroup(String topUnit) {
        String sql = "SELECT SOURCE_TYPE, COUNT(SOURCE_TYPE) SOURCE_TYPE_COUNT FROM f_database_info WHERE TOP_UNIT = ? GROUP BY SOURCE_TYPE ";
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql, new String[]{topUnit});
        if (CollectionUtils.sizeIsEmpty(maps)) {
            return new ArrayList<>();
        }

        ArrayList<Map<String, Object>> mapList = new ArrayList<>();
        maps.forEach(map->mapList.add(mapKeyUpperUnderScoreToLowerCamel(map)));
        return mapList;
    }

    /**
     * 获取指定租户下指定资源已使用资源个数
     * @param topUnit 租户id
     * @param sourceType 资源类型
     * @return SOURCE_TYPE  SOURCE_TYPE_COUNT
     */
    public Map<String, Object> listHashUsedDatabaseBySourceType(String topUnit,String sourceType) {
        String sql = "SELECT SOURCE_TYPE, COUNT(SOURCE_TYPE) SOURCE_TYPE_COUNT FROM f_database_info WHERE TOP_UNIT = ? AND SOURCE_TYPE = ? ";
        Map<String, Object> maps = jdbcTemplate.queryForMap(sql, new String[]{topUnit,sourceType});
        if (CollectionUtils.sizeIsEmpty(maps)) {
            return new HashMap<>();
        }
        return mapKeyUpperUnderScoreToLowerCamel(maps);
    }

    /**
     * 把map中key的字符串转换为小驼峰命名法
     * @param map 参数  key --> SOURCE_TYPE
     * @return  Map<String, Object> key --> sourceType
     */
    public Map<String, Object> mapKeyUpperUnderScoreToLowerCamel(Map<String, Object> map) {
        HashMap<String, Object> resultMap = new HashMap<>();
        if (CollectionUtils.sizeIsEmpty(map)) {
            return map;
        }
        map.forEach((key, value) -> resultMap.put( CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL,key),value));
        return resultMap;
    }
}
