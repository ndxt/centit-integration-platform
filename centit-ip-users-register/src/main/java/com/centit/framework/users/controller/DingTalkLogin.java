package com.centit.framework.users.controller;

import com.centit.framework.common.ResponseData;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.security.model.CentitUserDetails;
import com.centit.framework.users.config.AppConfig;
import com.centit.framework.users.config.UrlConstant;
import com.centit.framework.users.po.UserPlat;
import com.centit.framework.users.service.DingTalkLoginService;
import com.centit.framework.users.service.TokenService;
import com.centit.framework.users.service.UserPlatService;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiSnsGetuserinfoBycodeRequest;
import com.dingtalk.api.request.OapiUserGetbyunionidRequest;
import com.dingtalk.api.request.OapiV2UserGetRequest;
import com.dingtalk.api.response.OapiSnsGetuserinfoBycodeResponse;
import com.dingtalk.api.response.OapiUserGetbyunionidResponse;
import com.dingtalk.api.response.OapiV2UserGetResponse;
import com.taobao.api.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zfg
 */
@Controller
@RequestMapping("/ddlogin")
@Api(value = "钉钉平台登录相关接口", tags = "钉钉平台登录相关接口")
public class DingTalkLogin extends BaseController {

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private DingTalkLoginService dingTalkLoginService;

    @Autowired
    private UserPlatService userPlatService;

    @Autowired
    protected PlatformEnvironment platformEnvironment;

    @ApiOperation(value = "钉钉二维码登录", notes = "钉钉二维码登录。")
    @GetMapping(value = "/qrconnect")
    public void qrConnect(HttpServletResponse response) throws IOException {
        String authorizeUrl = UrlConstant.URL_GET_QRCONNECT + "?appid=" + appConfig.getAppKey() + "&response_type=code" +
            "&scope=snsapi_login&redirect_uri=" + appConfig.getRedirectUri();
        response.sendRedirect(authorizeUrl);
    }

    @ApiOperation(value = "钉钉账号登录", notes = "钉钉账号登录。")
    @GetMapping(value = "/snsauthorize")
    public void snsAuthorize(HttpServletResponse response) throws IOException {
        String authorizeUrl = UrlConstant.URL_GET_SNSCONNECT + "?appid=" + appConfig.getAppKey() + "&response_type=code" +
            "&scope=snsapi_login&redirect_uri=" + appConfig.getRedirectUri();
        response.sendRedirect(authorizeUrl);
    }

    /**
     * 登录回调获取授权用户的个人信息
     *
     * @param code  授权码
     * @param state
     * @return
     * @throws ApiException
     */
    @GetMapping(value = "/getUserInfo")
    @WrapUpResponseBody
    public ResponseData getUserInfo(@RequestParam("code") String code, @RequestParam("state") String state,
                                    HttpServletRequest request) throws ApiException {
        // 获取access_token，注意正式代码要有异常流处理
        String accessToken = "";
        ResponseData accessTokenData = tokenService.getAccessToken();
        if (accessTokenData.getCode() != 0) {
            return ResponseData.makeErrorMessage(accessTokenData.getCode(), accessTokenData.getMessage());
        }
        accessToken = accessTokenData.getData().toString();

        if (StringUtils.isBlank(accessToken)) {
            Map<String, Object> map = new HashMap<>();
            map.put("accessToken", "");
            return ResponseData.makeResponseData(map);
        }

        // 获取用户userId
        /*ServiceResult<String> userIdSr = dingTalkLoginService.getUserInfo(accessToken, code);
        if (!userIdSr.isSuccess()) {
            return ServiceResult.failure(userIdSr.getCode(), userIdSr.getMessage());
        }
        // 获取用户详情
        return dingTalkLoginService.getUser(accessToken, userIdSr.getResult());*/

        // 通过临时授权码获取授权用户的个人信息
        DefaultDingTalkClient codeClient = new DefaultDingTalkClient(UrlConstant.URL_GET_USER_BYCODE);
        OapiSnsGetuserinfoBycodeRequest reqBycodeRequest = new OapiSnsGetuserinfoBycodeRequest();
        // 通过扫描二维码，跳转指定的redirect_uri后，向url中追加的code临时授权码
        reqBycodeRequest.setTmpAuthCode(code);
        OapiSnsGetuserinfoBycodeResponse bycodeResponse = codeClient.execute(reqBycodeRequest, appConfig.getAppKey(), appConfig.getAppSecret());

        // 根据unionid获取userid
        String unionid = bycodeResponse.getUserInfo().getUnionid();
        DingTalkClient unionClient = new DefaultDingTalkClient(UrlConstant.URL_GET_USER_BYUNIONID);
        OapiUserGetbyunionidRequest reqGetbyunionidRequest = new OapiUserGetbyunionidRequest();
        reqGetbyunionidRequest.setUnionid(unionid);
        OapiUserGetbyunionidResponse oapiUserGetbyunionidResponse = unionClient.execute(reqGetbyunionidRequest, accessToken);

        // 根据userId获取用户信息
        String userId = oapiUserGetbyunionidResponse.getResult().getUserid();
        DingTalkClient userClient = new DefaultDingTalkClient(UrlConstant.URL_GET_USER);
        OapiV2UserGetRequest reqGetRequest = new OapiV2UserGetRequest();
        reqGetRequest.setUserid(userId);
        reqGetRequest.setLanguage("zh_CN");
        OapiV2UserGetResponse rspGetResponse = userClient.execute(reqGetRequest, accessToken);

        Map<String, Object> map = new HashMap<>();
        map.put("userInfo", rspGetResponse.getBody());

        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("userId", userId);
        paramsMap.put("corpId", appConfig.getCorpId());
        paramsMap.put("appKey", appConfig.getAppKey());
        paramsMap.put("appSecret", appConfig.getAppSecret());
        UserPlat userPlat = userPlatService.getUserPlatByProperties(paramsMap);
        if (null != userPlat) {
            userPlat.setUnionId(unionid);
            userPlatService.updateUserPlat(userPlat);
            CentitUserDetails ud = platformEnvironment.loadUserDetailsByUserCode(userPlat.getUserCode());
            SecurityContextHolder.getContext().setAuthentication(ud);
            //map.put("userInfo", ud);
            map.put("userCode", userPlat.getUserCode());
            map.put("accessToken", request.getSession().getId());
        }
        return ResponseData.makeResponseData(map);
    }

}
