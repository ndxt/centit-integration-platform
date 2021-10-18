package com.centit.framework.users.service;

import com.centit.framework.common.ResponseData;
import com.centit.framework.system.po.UnitInfo;
import com.centit.framework.system.po.UserInfo;

/**
 * @author zfg
 */
public interface DingTalkLoginService {

    ResponseData getUserByCode(String authCode);

    ResponseData getUserByUnionId(String accessToken, String unionId);

    ResponseData getUserInfo(String accessToken, String userId);

    ResponseData userCreate(String accessToken, UserInfo userInfo);

    ResponseData unitCreate(String accessToken, UnitInfo unitInfo);

    ResponseData getUnitInfo(String accessToken, String deptId);
}
