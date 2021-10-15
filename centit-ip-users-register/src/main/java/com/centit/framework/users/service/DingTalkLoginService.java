package com.centit.framework.users.service;

import com.centit.framework.common.ResponseData;
import com.centit.framework.users.common.ServiceResult;
import com.centit.framework.users.po.UserPlat;

/**
 * @author zfg
 */
public interface DingTalkLoginService {

    ResponseData getUserByCode(String accessToken, String authCode);

    ResponseData getUserByUnionId(String accessToken, String unionId);

    ResponseData getUserInfo(String accessToken, String userId);
}
