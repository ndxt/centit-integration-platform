package com.centit.framework.jtt.service;

import com.centit.framework.common.ResponseData;
import com.centit.framework.jtt.dto.JsmotUnitDTO;
import com.centit.framework.jtt.dto.JsmotUserDTO;
import com.centit.framework.jtt.dto.SmsDTO;

/**
 * @author zfg
 */
public interface JsmotSyncService {

    ResponseData userCreate(String accessToken, JsmotUserDTO userInfo);

    ResponseData unitCreate(String accessToken, JsmotUnitDTO unitInfo);

    ResponseData getCYUserDetail(String accessToken, String userCode, String flag);

    ResponseData getCYCorpInfo(String accessToken, String userCode, String flag);

    ResponseData sendSms(String accessToken, SmsDTO smsDTO);
}
