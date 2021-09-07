package com.centit.framework.users.service;

import com.centit.framework.users.common.ServiceResult;
import com.centit.framework.users.po.UserPlat;

/**
 * @author zfg
 */
public interface DingTalkLoginService {

    ServiceResult<String> getUserByUnionId(String accessToken, String authCode);

    ServiceResult<UserPlat> getUserInfo(String accessToken, String userId);
}
