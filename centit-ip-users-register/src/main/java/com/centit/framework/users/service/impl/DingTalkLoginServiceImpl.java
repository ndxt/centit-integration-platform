package com.centit.framework.users.service.impl;

import com.centit.framework.users.common.ServiceResult;
import com.centit.framework.users.config.UrlConstant;
import com.centit.framework.users.po.UserPlat;
import com.centit.framework.users.service.DingTalkLoginService;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiUserGetRequest;
import com.dingtalk.api.request.OapiUserGetuserinfoRequest;
import com.dingtalk.api.response.OapiUserGetResponse;
import com.dingtalk.api.response.OapiUserGetuserinfoResponse;
import com.taobao.api.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author zfg
 */
@Service("dingTalkLoginService")
public class DingTalkLoginServiceImpl implements DingTalkLoginService {

    private static final Logger logger = LoggerFactory.getLogger(DingTalkLoginServiceImpl.class);

    /**
     * 访问/user/getuserinfo接口获取用户userId
     *
     * @param accessToken access_token
     * @param authCode    临时授权码
     * @return 用户userId或错误信息
     */
    @Override
    public ServiceResult<String> getUserByUnionId(String accessToken, String authCode) {
        DingTalkClient client = new DefaultDingTalkClient(UrlConstant.URL_GET_USER_INFO);
        OapiUserGetuserinfoRequest request = new OapiUserGetuserinfoRequest();
        request.setCode(authCode);
        request.setHttpMethod("GET");

        OapiUserGetuserinfoResponse response;
        try {
            response = client.execute(request, accessToken);
        } catch (ApiException e) {
            logger.error("Failed to {}", UrlConstant.URL_GET_USER_INFO, e);
            return ServiceResult.failure(e.getErrCode(), "Failed to getUserInfo: " + e.getErrMsg());
        }
        if (!response.isSuccess()) {
            return ServiceResult.failure(response.getErrorCode(), response.getErrmsg());
        }

        return ServiceResult.success(response.getUserid());
    }

    /**
     * 访问/user/get 获取用户名称
     *
     * @param accessToken access_token
     * @param userId      用户userId
     * @return 用户名称或错误信息
     */
    @Override
    public ServiceResult<UserPlat> getUserInfo(String accessToken, String userId) {
        DingTalkClient client = new DefaultDingTalkClient(UrlConstant.URL_USER_GET);
        OapiUserGetRequest request = new OapiUserGetRequest();
        request.setUserid(userId);
        request.setHttpMethod("GET");

        OapiUserGetResponse response;
        try {
            response = client.execute(request, accessToken);
        } catch (ApiException e) {
            logger.error("Failed to {}", UrlConstant.URL_USER_GET, e);
            return ServiceResult.failure(e.getErrCode(), "Failed to getUserName: " + e.getErrMsg());
        }

        UserPlat userPlat = new UserPlat();
        userPlat.setUnionId(response.getUnionid());
        userPlat.setUserId(response.getUserid());
        userPlat.setUserName(response.getName());

        return ServiceResult.success(userPlat);
    }
}
