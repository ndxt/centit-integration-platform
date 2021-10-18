package com.centit.framework.users.service.impl;

import com.centit.framework.common.ResponseData;
import com.centit.framework.system.po.UnitInfo;
import com.centit.framework.system.po.UserInfo;
import com.centit.framework.users.config.AppConfig;
import com.centit.framework.users.config.UrlConstant;
import com.centit.framework.users.service.DingTalkLoginService;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.*;
import com.dingtalk.api.response.*;
import com.taobao.api.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author zfg
 */
@Service("dingTalkLoginService")
public class DingTalkLoginServiceImpl implements DingTalkLoginService {

    private static final Logger logger = LoggerFactory.getLogger(DingTalkLoginServiceImpl.class);

    @Autowired
    private AppConfig appConfig;

    /**
     * 访问/sns/getuserinfo_bycode接口获取用户unionid
     *
     * @param authCode 临时授权码
     * @return 用户unionId或错误信息
     */
    @Override
    public ResponseData getUserByCode(String authCode) {
        DingTalkClient client = new DefaultDingTalkClient(UrlConstant.URL_GET_USER_BYCODE);
        OapiSnsGetuserinfoBycodeRequest request = new OapiSnsGetuserinfoBycodeRequest();
        request.setTmpAuthCode(authCode);
        OapiSnsGetuserinfoBycodeResponse response;
        try {
            response = client.execute(request, appConfig.getAppKey(), appConfig.getAppSecret());
        } catch (ApiException e) {
            logger.error("Failed to {}", UrlConstant.URL_GET_USER_BYCODE, e);
            return ResponseData.makeErrorMessage(Integer.valueOf(e.getErrCode()), "Failed to getUserByCode: " + e.getErrMsg());
        }
        return ResponseData.makeResponseData(response.getUserInfo().getUnionid());
    }

    /**
     * 访问/topapi/user/getbyunionid 获取userid
     *
     * @param accessToken
     * @param unionId
     * @return
     */
    @Override
    public ResponseData getUserByUnionId(String accessToken, String unionId) {
        DingTalkClient client = new DefaultDingTalkClient(UrlConstant.URL_GET_USER_BYUNIONID);
        OapiUserGetbyunionidRequest request = new OapiUserGetbyunionidRequest();
        request.setUnionid(unionId);
        OapiUserGetbyunionidResponse response;
        try {
            response = client.execute(request, accessToken);
        } catch (ApiException e) {
            logger.error("Failed to {}", UrlConstant.URL_USER_GET, e);
            return ResponseData.makeErrorMessage(Integer.valueOf(e.getErrCode()), "Failed to getUserByUnionId: " + e.getErrMsg());
        }
        return ResponseData.makeResponseData(response.getResult().getUserid());
    }

    /**
     * 访问/topapi/v2/user/get 获取用户详情
     *
     * @param accessToken access_token
     * @param userId      用户userId
     * @return 用户详情或错误信息
     */
    @Override
    public ResponseData getUserInfo(String accessToken, String userId) {
        DingTalkClient client = new DefaultDingTalkClient(UrlConstant.URL_GET_USER);
        OapiV2UserGetRequest request = new OapiV2UserGetRequest();
        request.setUserid(userId);
        request.setLanguage("zh_CN");
        OapiV2UserGetResponse response;
        try {
            response = client.execute(request, accessToken);
        } catch (ApiException e) {
            logger.error("Failed to {}", UrlConstant.URL_GET_USER, e);
            return ResponseData.makeErrorMessage(Integer.valueOf(e.getErrCode()), "Failed to getUserInfo: " + e.getErrMsg());
        }
        return ResponseData.makeResponseData(response.getBody());
    }

    /**
     * 访问/topapi/v2/user/create 创建用户
     *
     * @param accessToken
     * @param userInfo
     * @return
     */
    @Override
    public ResponseData userCreate(String accessToken, UserInfo userInfo) {
        DingTalkClient client = new DefaultDingTalkClient(UrlConstant.USER_CREATE);
        OapiV2UserCreateRequest request = new OapiV2UserCreateRequest();
        request.setUserid(userInfo.getUserCode());
        request.setName(userInfo.getUserName());
        request.setMobile(userInfo.getRegCellPhone());
        //需查询获取钉钉部门详情的id
        request.setDeptIdList(userInfo.getPrimaryUnit());
        OapiV2UserCreateResponse response;
        try {
            response = client.execute(request, accessToken);
        } catch (ApiException e) {
            logger.error("Failed to {}", UrlConstant.USER_CREATE, e);
            return ResponseData.makeErrorMessage(Integer.valueOf(e.getErrCode()), "Failed to userCreate: " + e.getErrMsg());
        }
        //response.getResult().getUserid();
        return ResponseData.makeResponseData(response.getBody());
    }

    /**
     * 访问/topapi/v2/department/create 创建机构部门
     *
     * @param accessToken
     * @param unitInfo
     * @return
     */
    @Override
    public ResponseData unitCreate(String accessToken, UnitInfo unitInfo) {
        DingTalkClient client = new DefaultDingTalkClient(UrlConstant.DEPARTMENT_CREATE);
        OapiV2DepartmentCreateRequest request = new OapiV2DepartmentCreateRequest();
        request.setName(unitInfo.getUnitName());
        //需查询获取钉钉上级部门详情的id
        request.setParentId(1L);
        OapiV2DepartmentCreateResponse response;
        try {
            response = client.execute(request, accessToken);
        } catch (ApiException e) {
            logger.error("Failed to {}", UrlConstant.DEPARTMENT_CREATE, e);
            return ResponseData.makeErrorMessage(Integer.valueOf(e.getErrCode()), "Failed to unitCreate: " + e.getErrMsg());
        }
        //response.getResult().getDeptId();
        return ResponseData.makeResponseData(response.getBody());
    }

    @Override
    public ResponseData getUnitInfo(String accessToken, String deptId) {
        DingTalkClient client = new DefaultDingTalkClient(UrlConstant.URL_DEPARTMENT_GET);
        OapiV2DepartmentGetRequest request = new OapiV2DepartmentGetRequest();
        request.setDeptId(Long.valueOf(deptId));
        request.setLanguage("zh_CN");
        OapiV2DepartmentGetResponse response;
        try {
            response = client.execute(request, accessToken);
        } catch (ApiException e) {
            logger.error("Failed to {}", UrlConstant.URL_DEPARTMENT_GET, e);
            return ResponseData.makeErrorMessage(Integer.valueOf(e.getErrCode()), "Failed to getUnitInfo: " + e.getErrMsg());
        }
        //response.getResult().getParentId();
        return ResponseData.makeResponseData(response.getBody());
    }
}
