package com.centit.product.ip.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.centit.framework.core.service.BaseEntityManager;
import com.centit.product.ip.po.DatabaseInfo;

public interface DatabaseInfoManager extends BaseEntityManager<DatabaseInfo,String> {
    public boolean connectionTest(DatabaseInfo databaseInfo);

    public List<DatabaseInfo> listDatabase();
    
    public Serializable saveNewObject(DatabaseInfo databaseInfo);
    
    public void mergeObject(DatabaseInfo databaseInfo);
    
	public String getNextKey();

	public Map<String, DatabaseInfo> listObjectToDBRepo();
}
