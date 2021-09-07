package com.centit.framework.users.dao;

import com.centit.framework.core.dao.CodeBook;
import com.centit.framework.jdbc.dao.BaseDaoImpl;
import com.centit.framework.users.po.UserPlat;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zfg
 */
@Repository
public class UserPlatDao extends BaseDaoImpl<UserPlat, String> {

    public Map<String, String> getFilterField() {
        Map<String, String> filterField = new HashMap<>();
        filterField.put("userPlatId", CodeBook.EQUAL_HQL_ID);
        filterField.put("userCode", CodeBook.EQUAL_HQL_ID);
        filterField.put("corpId", CodeBook.EQUAL_HQL_ID);
        filterField.put("appKey", CodeBook.EQUAL_HQL_ID);
        filterField.put("appSecret", CodeBook.EQUAL_HQL_ID);
        filterField.put("unionId", CodeBook.EQUAL_HQL_ID);
        filterField.put("userId", CodeBook.EQUAL_HQL_ID);

        return filterField;
    }
}
