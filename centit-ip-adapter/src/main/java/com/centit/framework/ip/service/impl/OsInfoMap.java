package com.centit.framework.ip.service.impl;

import com.centit.framework.ip.po.OsInfo;
import com.centit.support.algorithm.StringBaseOpt;
import com.centit.support.common.CachedObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.*;

public class OsInfoMap implements Map<String, String> {

    private CachedObject<List<OsInfo>> osInfoCache;

    public OsInfoMap(CachedObject<List<OsInfo>> osInfoCache){
        this.osInfoCache = osInfoCache;
    }

    @Override
    public int size() {
        List<OsInfo> osInfos = osInfoCache.getCachedTarget();
        if(osInfos==null){
            return 0;
        }
        return osInfos.size();
    }


    @Override
    public boolean isEmpty() {
        List<OsInfo> osInfos = osInfoCache.getCachedTarget();
        if(osInfos==null){
            return true;
        }
        return osInfos.isEmpty();
    }


    @Override
    public boolean containsKey(Object key) {
        List<OsInfo> osInfos = osInfoCache.getCachedTarget();
        if(osInfos==null){
            return false;
        }
        for(OsInfo oi : osInfos){
            if(StringUtils.equals(oi.getOsId(),
                StringBaseOpt.castObjectToString(key))){
                return true;
            }
        }
        return false;
    }


    @Override
    public boolean containsValue(Object value) {
        List<OsInfo> osInfos = osInfoCache.getCachedTarget();
        if(osInfos==null){
            return false;
        }
        for(OsInfo oi : osInfos){
            if(StringUtils.equals(oi.getOsName(),
                StringBaseOpt.castObjectToString(value))){
                return true;
            }
        }
        return false;
    }


    @Override
    public String get(Object key) {
        List<OsInfo> osInfos = osInfoCache.getCachedTarget();
        if(osInfos==null){
            return null;
        }
        for(OsInfo oi : osInfos){
            if(StringUtils.equals(oi.getOsId(),
                StringBaseOpt.castObjectToString(key))){
                return oi.getOsName();
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
        List<OsInfo> osInfos = osInfoCache.getCachedTarget();
        if(osInfos==null){
            return null;
        }
        Set<String> keys = new HashSet<>();
        for(OsInfo oi : osInfos){
            keys.add(oi.getOsId());
        }
        return keys;
    }


    @Override
    public Collection<String> values() {
        List<OsInfo> osInfos = osInfoCache.getCachedTarget();
        if(osInfos==null){
            return null;
        }
        List<String> values = new ArrayList<>();
        for(OsInfo oi : osInfos){
            values.add(oi.getOsName());
        }
        return values;
    }


    @Override
    public Set<Entry<String, String>> entrySet() {
        List<OsInfo> osInfos = osInfoCache.getCachedTarget();
        if(osInfos==null){
            return null;
        }
        Set<Entry<String, String>> entries = new HashSet<>();
        for(OsInfo oi : osInfos){
            entries.add(new ImmutablePair<>(
                oi.getOsId(), oi.getOsName()));
        }
        return entries;
    }
}
