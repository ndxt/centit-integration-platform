package com.centit.framework.ip.service.impl;

import com.centit.framework.ip.po.DatabaseInfo;
import com.centit.support.algorithm.StringBaseOpt;
import com.centit.support.common.CachedObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.*;

public class DatabaseInfoMap implements Map<String, String> {

    private CachedObject<List<DatabaseInfo>> databaseInfoCache;

    public DatabaseInfoMap(CachedObject<List<DatabaseInfo>> databaseInfoCache){
        this.databaseInfoCache = databaseInfoCache;
    }

    @Override
    public int size() {
        List<DatabaseInfo> dbInfos = databaseInfoCache.getCachedTarget();
        if(dbInfos==null){
            return 0;
        }
        return dbInfos.size();
    }


    @Override
    public boolean isEmpty() {
        List<DatabaseInfo> dbInfos = databaseInfoCache.getCachedTarget();
        if(dbInfos==null){
            return true;
        }
        return dbInfos.isEmpty();
    }


    @Override
    public boolean containsKey(Object key) {
        List<DatabaseInfo> dbInfos = databaseInfoCache.getCachedTarget();
        if(dbInfos==null){
            return false;
        }
        for(DatabaseInfo dbi : dbInfos){
            if(StringUtils.equals(dbi.getDatabaseCode(),
                StringBaseOpt.castObjectToString(key))){
                return true;
            }
        }
        return false;
    }


    @Override
    public boolean containsValue(Object value) {
        List<DatabaseInfo> dbInfos = databaseInfoCache.getCachedTarget();
        if(dbInfos==null){
            return false;
        }
        for(DatabaseInfo dbi : dbInfos){
            if(StringUtils.equals(dbi.getDatabaseName(),
                StringBaseOpt.castObjectToString(value))){
                return true;
            }
        }
        return false;
    }


    @Override
    public String get(Object key) {
        List<DatabaseInfo> dbInfos = databaseInfoCache.getCachedTarget();
        if(dbInfos==null){
            return null;
        }
        for(DatabaseInfo dbi : dbInfos){
            if(StringUtils.equals(dbi.getDatabaseCode(),
                StringBaseOpt.castObjectToString(key))){
                return dbi.getDatabaseName();
            }
        }
        return null;
    }


    @Override
    public String put(String key, String value) {
        return null;
    }


    @Override
    public String remove(Object key) {
        return null;
    }


    @Override
    public void putAll(Map<? extends String, ? extends String> m) {

    }


    @Override
    public void clear() {

    }


    @Override
    public Set<String> keySet() {
        List<DatabaseInfo> dbInfos = databaseInfoCache.getCachedTarget();
        if(dbInfos==null){
            return null;
        }
        Set<String> keys = new HashSet<>();
        for(DatabaseInfo dbi : dbInfos){
            keys.add(dbi.getDatabaseCode());
        }
        return keys;
    }


    @Override
    public Collection<String> values() {
        List<DatabaseInfo> dbInfos = databaseInfoCache.getCachedTarget();
        if(dbInfos==null){
            return null;
        }
        List<String> values = new ArrayList<>();
        for(DatabaseInfo dbi : dbInfos){
            values.add(dbi.getDatabaseName());
        }
        return values;
    }


    @Override
    public Set<Entry<String, String>> entrySet() {
        List<DatabaseInfo> dbInfos = databaseInfoCache.getCachedTarget();
        if(dbInfos==null){
            return null;
        }
        Set<Entry<String, String>> entries = new HashSet<>();
        for(DatabaseInfo dbi : dbInfos){
            entries.add(new ImmutablePair<>(
                dbi.getDatabaseCode(), dbi.getDatabaseName()));
        }
        return entries;
    }
}
