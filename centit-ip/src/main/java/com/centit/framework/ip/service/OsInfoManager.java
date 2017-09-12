package com.centit.framework.ip.service;


import com.centit.framework.core.dao.PageDesc;
import com.centit.framework.ip.po.OsInfo;
import com.centit.framework.jdbc.service.BaseEntityManager;

import java.util.List;
import java.util.Map;

public interface OsInfoManager extends BaseEntityManager<OsInfo,String> {

    List<OsInfo> listObjects(Map<String, Object> map);
    List<OsInfo> listObjects(Map<String, Object> map, PageDesc pageDesc);

}
