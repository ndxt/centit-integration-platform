package com.centit.framework.users.service.impl;

import com.centit.framework.common.ResponseData;
import com.centit.framework.users.config.UrlConstant;
import com.centit.framework.users.po.UserPlat;
import com.centit.framework.users.service.DingTalkLoginService;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiUserGetRequest;
import com.dingtalk.api.request.OapiUserGetUseridByUnionidRequest;
import com.dingtalk.api.request.OapiUserGetuserinfoRequest;
import com.dingtalk.api.response.OapiUserGetResponse;
import com.dingtalk.api.response.OapiUserGetUseridByUnionidResponse;
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
    public ResponseData getUserByCode(String accessToken, String authCode) {
        DingTalkClient client = new DefaultDingTalkClient(UrlConstant.URL_GET_USER_BYCODE);
        OapiUserGetuserinfoRequest request = new OapiUserGetuserinfoRequest();
        request.setCode(authCode);
        request.setHttpMethod("POST");

        OapiUserGetuserinfoResponse response;
        try {
            response = client.execute(request, accessToken);
        } catch (ApiException e) {
            logger.error("Failed to {}", UrlConstant.URL_GET_USER_BYCODE, e);
            return ResponseData.makeErrorMessage(Integer.valueOf(e.getErrCode()), "Failed to getUserInfo: " + e.getErrMsg());
        }
        if (!response.isSuccess()) {
            return ResponseData.makeErrorMessage(Integer.valueOf(response.getErrorCode()), response.getErrmsg());
        }

        return ResponseData.makeResponseData(response.getUserid());
    }

    @Override
    public ResponseData getUserByUnionId(String accessToken, String unionId) {
        DingTalkClient client = new DefaultDingTalkClient(UrlConstant.URL_GET_USER_BYUNIONID);
        OapiUserGetUseridByUnionidRequest request = new OapiUserGetUseridByUnionidRequest();
        request.setUnionid(unionId);
        request.setHttpMethod("POST");
        OapiUserGetUseridByUnionidResponse response;
        try {
            response = client.execute(request, accessToken);
        } catch (ApiException e) {
            logger.error("Failed to {}", UrlConstant.URL_USER_GET, e);
            return ResponseData.makeErrorMessage(Integer.valueOf(e.getErrCode()), "Failed to getUserName: " + e.getErrMsg());
        }
        return ResponseData.makeResponseData(response.getUserid());
    }

    /**
     * 访问/user/get 获取用户名称
     *
     * @param accessToken access_token
     * @param userId      用户userId
     * @return 用户名称或错误信息
     */
    @Override
    public ResponseData getUserInfo(String accessToken, String userId) {
        DingTalkClient client = new DefaultDingTalkClient(UrlConstant.URL_GET_USER);
        OapiUserGetRequest request = new OapiUserGetRequest();
        request.setUserid(userId);
        request.setHttpMethod("POST");
        OapiUserGetResponse response;
        try {
            response = client.execute(request, accessToken);
        } catch (ApiException e) {
            logger.error("Failed to {}", UrlConstant.URL_USER_GET, e);
            return ResponseData.makeErrorMessage(Integer.valueOf(e.getErrCode()), "Failed to getUserName: " + e.getErrMsg());
        }
        UserPlat userPlat = new UserPlat();
        userPlat.setUnionId(response.getUnionid());
        userPlat.setUserId(response.getUserid());
        userPlat.setUserName(response.getName());
        return ResponseData.makeResponseData(response.getUserid());
    }
}
