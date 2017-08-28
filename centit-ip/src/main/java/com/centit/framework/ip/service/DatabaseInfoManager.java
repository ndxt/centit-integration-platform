package com.centit.framework.ip.service;
import com.centit.framework.hibernate.service.BaseEntityManager;
import com.centit.framework.ip.po.DatabaseInfo;

import java.util.List;
import java.util.Map;

public interface DatabaseInfoManager extends BaseEntityManager<DatabaseInfo,String> {
    boolean connectionTest(DatabaseInfo databaseInfo);

    List<DatabaseInfo> listDatabase();

    void saveNewObject(DatabaseInfo databaseInfo);
    
    void mergeObject(DatabaseInfo databaseInfo);
    
	String getNextKey();

	Map<String, DatabaseInfo> listObjectToDBRepo();
}
