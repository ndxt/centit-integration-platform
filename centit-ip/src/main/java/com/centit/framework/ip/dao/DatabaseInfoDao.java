package com.centit.framework.ip.dao;

import com.centit.framework.core.dao.CodeBook;
import com.centit.framework.hibernate.dao.BaseDaoImpl;
import com.centit.framework.hibernate.dao.DatabaseOptUtils;
import com.centit.framework.ip.po.DatabaseInfo;
import com.centit.support.database.DataSourceDescription;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class DatabaseInfoDao extends BaseDaoImpl<DatabaseInfo,String> {

    public Map<String, String> getFilterField() {
        if (filterField == null) {
            filterField = new HashMap<>();

            filterField.put("databaseName", CodeBook.LIKE_HQL_ID);

            filterField.put("databaseNames", CodeBook.EQUAL_HQL_ID);

            filterField.put("databaseType", CodeBook.LIKE_HQL_ID);

            filterField.put("hostPort", CodeBook.LIKE_HQL_ID);

            filterField.put("databaseUrl", CodeBook.LIKE_HQL_ID);

            filterField.put("username", CodeBook.LIKE_HQL_ID);

            filterField.put("password", CodeBook.LIKE_HQL_ID);

            filterField.put("dataDesc", CodeBook.LIKE_HQL_ID);

            filterField.put("createTime", CodeBook.LIKE_HQL_ID);

            filterField.put("created", CodeBook.LIKE_HQL_ID);

        }
        return filterField;
    }

    public boolean connectionTest(DatabaseInfo databaseInfo) {
        return DataSourceDescription.testConntect(new DataSourceDescription(
                databaseInfo.getDatabaseUrl(),
                databaseInfo.getUsername(),
                databaseInfo.getPassword()));
    }
    
	public List<DatabaseInfo> listDatabase() {
        return this.listObjects();
    }

	public DatabaseInfo getDatabaseInfoById(String databaseCode) {
		DatabaseInfo dbi=this.getObjectById(databaseCode);
		return dbi;
	}
	
	public String getNextKey() {
		return DatabaseOptUtils.getNextKeyBySequence(this, "S_DATABASECODE", 10);
	}
}
