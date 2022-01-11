package com.centit.framework.users.service.impl;

import com.centit.framework.common.ResponseData;
import com.centit.framework.users.config.AppConfig;
import com.centit.framework.users.config.UrlConstant;
import com.centit.framework.users.dao.SocialDeptAuthDao;
import com.centit.framework.users.dto.DingUnitDTO;
import com.centit.framework.users.dto.DingUserDTO;
import com.centit.framework.users.po.SocialDeptAuth;
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

    @Autowired
    private SocialDeptAuthDao socialDeptAuthDao;

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
            return ResponseData.makeErrorMessage("Failed to getUserByCode: " + e.getErrMsg());
        }
        if (!response.isSuccess()) {
            return ResponseData.makeErrorMessage(Integer.valueOf(response.getErrorCode()), response.getErrmsg());
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
            return ResponseData.makeErrorMessage("Failed to getUserByUnionId: " + e.getErrMsg());
        }
        if (!response.isSuccess()) {
            return ResponseData.makeErrorMessage(Integer.valueOf(response.getErrorCode()), response.getErrmsg());
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
            return ResponseData.makeErrorMessage("Failed to getUserInfo: " + e.getErrMsg());
        }
        if (!response.isSuccess()) {
            return ResponseData.makeErrorMessage(Integer.valueOf(response.getErrorCode()), response.getErrmsg());
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
    public ResponseData userCreate(String accessToken, DingUserDTO userInfo) {
        DingTalkClient client = new DefaultDingTalkClient(UrlConstant.USER_CREATE);
        OapiV2UserCreateRequest request = new OapiV2UserCreateRequest();
        request.setName(userInfo.getUserName());
        request.setMobile(userInfo.getRegCellPhone());
        SocialDeptAuth socialDeptAuth = socialDeptAuthDao.getObjectById(userInfo.getPrimaryUnit());
        if (null != socialDeptAuth) {
            //需查询获取钉钉部门详情的id
            request.setDeptIdList(socialDeptAuth.getDeptId());
        } else {
            logger.error("Failed to {}", UrlConstant.USER_CREATE, "部门未同步到平台应用");
            return ResponseData.makeErrorMessage(500, "Failed to userCreate: 部门未同步到平台应用");
        }
        OapiV2UserCreateResponse response;
        try {
            response = client.execute(request, accessToken);
        } catch (ApiException e) {
            logger.error("Failed to {}", UrlConstant.USER_CREATE, e);
            return ResponseData.makeErrorMessage("Failed to userCreate: " + e.getErrMsg());
        }
        if (!response.isSuccess()) {
            return ResponseData.makeErrorMessage(Integer.valueOf(response.getErrorCode()), response.getErrmsg());
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
    public ResponseData unitCreate(String accessToken, DingUnitDTO unitInfo) {
        DingTalkClient client = new DefaultDingTalkClient(UrlConstant.DEPARTMENT_CREATE);
        OapiV2DepartmentCreateRequest request = new OapiV2DepartmentCreateRequest();
        request.setName(unitInfo.getUnitName());
        if ("".equals(unitInfo.getParentUnit())) {
            request.setParentId(1L);
        } else {
            //获取部门和平台部门的关联数据
            SocialDeptAuth socialDeptAuth = socialDeptAuthDao.getObjectById(unitInfo.getParentUnit());
            if (null != socialDeptAuth) {
                request.setParentId(Long.valueOf(socialDeptAuth.getDeptId()));
            } else {
                logger.error("Failed to {}", UrlConstant.DEPARTMENT_CREATE, "未查询到父节点机构");
                return ResponseData.makeErrorMessage(500, "Failed to unitCreate: 未查询到父节点机构");
            }
        }
        OapiV2DepartmentCreateResponse response;
        try {
            response = client.execute(request, accessToken);
        } catch (ApiException e) {
            logger.error("Failed to {}", UrlConstant.DEPARTMENT_CREATE, e);
            return ResponseData.makeErrorMessage("Failed to unitCreate: " + e.getErrMsg());
        }
        if (!response.isSuccess()) {
            return ResponseData.makeErrorMessage(Integer.valueOf(response.getErrorCode()), response.getErrmsg());
        }
        //response.getResult().getDeptId();
        SocialDeptAuth socialDeptAuth = new SocialDeptAuth();
        socialDeptAuth.setUnitCode(unitInfo.getUnitCode());
        socialDeptAuth.setDeptId(response.getResult().getDeptId().toString());
        socialDeptAuthDao.saveNewObject(socialDeptAuth);
        return ResponseData.makeResponseData(response.getBody());
    }

    /**
     * 访问/topapi/v2/department/get 获取部门详情
     *
     * @param accessToken
     * @param deptId
     * @return
     */
    @Override
    public ResponseData getUnitInfo(String accessToken, String deptId) {
        OapiV2DepartmentGetResponse response = getDingUnitinfo(accessToken, deptId);
        if (response == null) {
            return ResponseData.makeErrorMessage("Failed to getDingUnitinfo");
        }
        if (!response.isSuccess()) {
            return ResponseData.makeErrorMessage(Integer.valueOf(response.getErrorCode()), response.getErrmsg());
        }
        return ResponseData.makeResponseData(response.getBody());
    }

    private OapiV2DepartmentGetResponse getDingUnitinfo(String accessToken, String deptId) {
        DingTalkClient client = new DefaultDingTalkClient(UrlConstant.URL_DEPARTMENT_GET);
        OapiV2DepartmentGetRequest request = new OapiV2DepartmentGetRequest();
        request.setDeptId(Long.valueOf(deptId));
        request.setLanguage("zh_CN");
        OapiV2DepartmentGetResponse response = null;
        try {
            response = client.execute(request, accessToken);
        } catch (ApiException e) {
            logger.error("Failed to {}", UrlConstant.URL_DEPARTMENT_GET, e);
        }
        return response;
    }

}
