package com.centit.framework.ip.service;

import com.centit.support.database.utils.PageDesc;
import com.centit.framework.ip.po.DatabaseInfo;
import com.centit.framework.jdbc.service.BaseEntityManager;

import java.util.List;
import java.util.Map;

public interface DatabaseInfoManager extends BaseEntityManager<DatabaseInfo,String> {
    boolean connectionTest(DatabaseInfo databaseInfo);

    List<DatabaseInfo> listDatabase();

    void saveNewObject(DatabaseInfo databaseInfo);
    
    void mergeObject(DatabaseInfo databaseInfo);
    
	String getNextKey();

	Map<String, DatabaseInfo> listObjectToDBRepo();

    List<DatabaseInfo> listObjects(Map<String, Object> map);
    List<DatabaseInfo> listObjects(Map<String, Object> map, PageDesc pageDesc);
}
