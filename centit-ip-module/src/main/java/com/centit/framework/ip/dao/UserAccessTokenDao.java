package com.centit.framework.ip.dao;

import com.centit.framework.core.dao.CodeBook;
import com.centit.framework.ip.po.UserAccessToken;
import com.centit.framework.jdbc.dao.BaseDaoImpl;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class UserAccessTokenDao extends BaseDaoImpl<UserAccessToken,String> {

    public Map<String, String> getFilterField() {
        Map<String, String> filterField = new HashMap<>();
        filterField.put("tokenId", CodeBook.EQUAL_HQL_ID);
        filterField.put("userCode", CodeBook.EQUAL_HQL_ID);
        return filterField;
    }

}
