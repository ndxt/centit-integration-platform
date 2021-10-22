package com.centit.framework.users.service;

import com.centit.framework.common.ResponseData;
import com.centit.framework.users.dto.DingUnitDTO;
import com.centit.framework.users.dto.DingUserDTO;

/**
 * @author zfg
 */
public interface DingTalkLoginService {

    ResponseData getUserByCode(String authCode);

    ResponseData getUserByUnionId(String accessToken, String unionId);

    ResponseData getUserInfo(String accessToken, String userId);

    ResponseData userCreate(String accessToken, DingUserDTO userInfo);

    ResponseData unitCreate(String accessToken, DingUnitDTO unitInfo);

    ResponseData getUnitInfo(String accessToken, String deptId);
}
