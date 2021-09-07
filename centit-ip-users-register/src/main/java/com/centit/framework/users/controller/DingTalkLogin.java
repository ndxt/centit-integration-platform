package com.centit.framework.users.controller;

import com.alibaba.fastjson.JSONObject;
import com.centit.framework.common.ResponseData;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.security.model.CentitUserDetails;
import com.centit.framework.users.common.ServiceResult;
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
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
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
public class DingTalkLogin extends BaseController {

    @Value("${dingtalk.app_key:}")
    private String appId;

    @Value("${dingtalk.app_secret:}")
    private String appSecret;

    @Value("${dingtalk.pwredirect_uri:}")
        private String pwredirectUri;

    @Value("${dingtalk.redirect_uri:}")
    private String redirectUri;

    @Autowired
    private AppConfig appConfig;

    //@Autowired
    //private TokenService tokenService;
    @Resource
    private TokenService tokenService;

    @Autowired
    private DingTalkLoginService dingTalkLoginService;

    @Autowired
    private UserPlatService userPlatService;

    @Autowired
    protected PlatformEnvironment platformEnvironment;

    @RequestMapping("/qrconnect")
    public void qrConnect(HttpServletResponse response) throws IOException {
        String authorizeUrl = UrlConstant.URL_GET_QRCONNECT + "?appid=" + appId + "&response_type=code" +
            "&scope=snsapi_login&redirect_uri=" + redirectUri;
        response.sendRedirect(authorizeUrl);
    }

    @RequestMapping("/snsauthorize")
    public void snsAuthorize(HttpServletResponse response) throws IOException {
        String authorizeUrl = UrlConstant.URL_GET_SNSCONNECT + "?appid=" + appId + "&response_type=code" +
            "&scope=snsapi_login&redirect_uri=" + pwredirectUri;
        response.sendRedirect(authorizeUrl);
    }

    /**
     * 获取授权用户的个人信息
     *
     * @return
     * @throws Exception ServiceResult<Map<String,Object>> 2020-11-4
     */
    @RequestMapping(value = "/pwscan", method = RequestMethod.GET)
    @WrapUpResponseBody
    //@ResponseBody
    public ResponseData getPwScan(@RequestParam("code") String code) throws Exception {
        //public ServiceResult<Map<String, Object>> getPwScan(@RequestParam("code") String code) throws Exception {
        // 通过临时授权码获取授权用户的个人信息
        DefaultDingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/sns/getuserinfo_bycode");
        OapiSnsGetuserinfoBycodeRequest reqBycodeRequest = new OapiSnsGetuserinfoBycodeRequest();
        // 通过扫描二维码，跳转指定的redirect_uri后，向url中追加的code临时授权码
        reqBycodeRequest.setTmpAuthCode(code);
        OapiSnsGetuserinfoBycodeResponse response = client.execute(reqBycodeRequest, appId, appSecret);
        /*Map<String, Object> returnMap = new HashMap<String, Object>();
        returnMap.put("userInfo", response.getBody());
        return ServiceResult.success(returnMap);*/
        return ResponseData.makeResponseData(response.getUserInfo());
    }

    /**
     * 获取授权用户的个人信息
     *
     * @param code
     * @param state
     * @return
     * @throws ApiException
     */
    @RequestMapping(value = "/getUserInfo", method = RequestMethod.GET)
    @ResponseBody
    public ServiceResult<Map<String, Object>> getUserInfo(@RequestParam("code") String code,
                                                           @RequestParam("state") String state,
                                                           HttpServletRequest request) throws ApiException {
        // 获取access_token，注意正式代码要有异常流处理
        String accessToken = "";
        ServiceResult<String> accessTokenSr = tokenService.getAccessToken();
        //ServiceResult<String> accessTokenSr = tokenService.getCorpAccessToken();
        if (!accessTokenSr.isSuccess()) {
            return ServiceResult.failure(accessTokenSr.getCode(), accessTokenSr.getMessage());
        }
        accessToken = accessTokenSr.getResult();

        // 获取用户userId
        /*ServiceResult<String> userIdSr = dingTalkLoginService.getUserInfo(accessToken, code);
        if (!userIdSr.isSuccess()) {
            return ServiceResult.failure(userIdSr.getCode(), userIdSr.getMessage());
        }

        // 获取用户详情
        return dingTalkLoginService.getUser(accessToken, userIdSr.getResult());*/

        if (StringUtils.isBlank(accessToken)) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("accessToken", "");
            return ServiceResult.success(map);
        }
        // 通过临时授权码获取授权用户的个人信息
        DefaultDingTalkClient client2 = new DefaultDingTalkClient("https://oapi.dingtalk.com/sns/getuserinfo_bycode");
        OapiSnsGetuserinfoBycodeRequest reqBycodeRequest = new OapiSnsGetuserinfoBycodeRequest();
        // 通过扫描二维码，跳转指定的redirect_uri后，向url中追加的code临时授权码
        reqBycodeRequest.setTmpAuthCode(code);
        OapiSnsGetuserinfoBycodeResponse bycodeResponse = client2.execute(reqBycodeRequest, appId, appSecret);

        JSONObject jsonObject = JSONObject.parseObject(bycodeResponse.getBody());
        System.out.println(jsonObject);

        // 根据unionid获取userid
        String unionid = bycodeResponse.getUserInfo().getUnionid();
        DingTalkClient clientDingTalkClient = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/user/getbyunionid");
        OapiUserGetbyunionidRequest reqGetbyunionidRequest = new OapiUserGetbyunionidRequest();
        reqGetbyunionidRequest.setUnionid(unionid);
        OapiUserGetbyunionidResponse oapiUserGetbyunionidResponse = clientDingTalkClient.execute(reqGetbyunionidRequest, accessToken);

        // 根据userId获取用户信息
        String userId = oapiUserGetbyunionidResponse.getResult().getUserid();
        DingTalkClient clientDingTalkClient2 = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/v2/user/get");
        OapiV2UserGetRequest reqGetRequest = new OapiV2UserGetRequest();
        reqGetRequest.setUserid(userId);
        reqGetRequest.setLanguage("zh_CN");
        OapiV2UserGetResponse rspGetResponse = clientDingTalkClient2.execute(reqGetRequest, accessToken);
        System.out.println(rspGetResponse.getBody());
        Map<String, Object> map = new HashMap<>();
        map.put("userInfo", rspGetResponse.getBody());

        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("userId", userId);
        paramsMap.put("corpId", appConfig.getCorpId());
        paramsMap.put("appKey", appConfig.getAppKey());
        paramsMap.put("appSecret", appConfig.getAppSecret());
        UserPlat userPlat = userPlatService.getUserPlatByProperty(paramsMap);
        if (null != userPlat) {
            CentitUserDetails ud = platformEnvironment.loadUserDetailsByUserCode(userPlat.getUserCode());
            SecurityContextHolder.getContext().setAuthentication(ud);
            map.put("userCode", userPlat.getUserCode());
            map.put("accessToken", request.getSession().getId());
        }
        return ServiceResult.success(map);
    }

}
