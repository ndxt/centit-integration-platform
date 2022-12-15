package com.centit.framework.users.service;

import com.centit.framework.common.ResponseData;
import com.centit.framework.users.dto.JsmotUnitDTO;
import com.centit.framework.users.dto.JsmotUserDTO;

/**
 * @author zfg
 */
public interface JsmotSyncService {

    ResponseData userCreate(String accessToken, JsmotUserDTO userInfo);

    ResponseData unitCreate(String accessToken, JsmotUnitDTO unitInfo);

    ResponseData getCYUserDetail(String accessToken, String userCode, String flag);

    ResponseData getCYCorpInfo(String accessToken, String userCode, String flag);
}
