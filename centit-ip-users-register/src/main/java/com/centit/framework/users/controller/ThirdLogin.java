package com.centit.framework.users.controller;

import com.alibaba.fastjson.JSONObject;
import com.centit.framework.common.ResponseData;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.security.model.CentitUserDetails;
import com.centit.framework.users.config.AppConfig;
import com.centit.framework.users.config.UrlConstant;
import com.centit.framework.users.config.WxAppConfig;
import com.centit.framework.users.po.Platform;
import com.centit.framework.users.po.UserPlat;
import com.centit.framework.users.service.*;
import com.centit.support.common.ObjectException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.util.http.URIUtil;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
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
 * 第三方登录Controller
 */
@Controller
@RequestMapping("/third")
@Api(value = "第三方平台登录相关接口", tags = "第三方平台登录相关接口")
public class ThirdLogin {

    @Autowired
    private WxMpService wxOpenService;

    @Autowired
    private WxAppConfig wxAppConfig;

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private UserPlatService userPlatService;

    @Autowired
    private PlatformEnvironment platformEnvironment;

    @Autowired
    private PlatformService platformService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private DingTalkLoginService dingTalkLoginService;

    @Autowired
    private WeChatService weChatService;

    //微信登录
    private static final String WECHAT_LOGIN = "wx";

    //微信账号绑定
    private static final String WECHAT_BIND = "wxBind";

    //钉钉登录
    private static final String DING_LOGIN = "ding";

    //钉钉账号绑定
    private static final String DING_BIND = "dingBind";

    //QQ登录
    private static final String QQ_LOGIN = "QQ";

    //QQ账号绑定
    private static final String QQ_BIND = "qqBind";

    @ApiOperation(value = "微信二维码登录/绑定", notes = "微信二维码登录/绑定")
    @ApiImplicitParams({
        @ApiImplicitParam(
            name = "type", value = "请求类型;登录:wx;绑定:wxBind;钉钉:ding",
            required = true, paramType = "body", dataType = "String"),
        @ApiImplicitParam(
            name = "userCode", value = "用户名,类型为bind时,不可为空",
            required = true, paramType = "body", dataType = "String")
    })
    @GetMapping(value = "/login")
    public void qrAuthorize(@RequestParam("type") String type,
                            @RequestParam("userCode") String userCode,
                            @RequestParam("returnUrl") String returnUrl,
                            HttpServletResponse response) throws IOException {
        String url = "";
        String authorizeUrl = "";
        if(WECHAT_LOGIN.equals(type)){
            //微信登录
            //url = wxAppConfig.getRedirectLoginUri() + "?returnUrl=" + returnUrl;
            url = wxAppConfig.getRedirectLoginUri() + "?type=" + type + "&returnUrl=" + returnUrl;
            authorizeUrl = wxOpenService.buildQrConnectUrl(url, WxConsts.QRCONNECT_SCOPE_SNSAPI_LOGIN, "");
        }else if(WECHAT_BIND.equals(type)){
            //微信账号绑定
            if(userCode == null || "".equals(userCode)){
                throw new ObjectException("缺少参数userCode;");
            }
            url = wxAppConfig.getRedirectBindUri() + "?type=" + type + "&returnUrl=" + returnUrl + "&userCode=" + userCode;
            authorizeUrl = wxOpenService.buildQrConnectUrl(url, WxConsts.QRCONNECT_SCOPE_SNSAPI_LOGIN, "");
        }else if(DING_LOGIN.equals(type)){
            //钉钉登陆页面
            url = URIUtil.encodeURIComponent(appConfig.getRedirectUri() + "?type=" + type + "&returnUrl=" + returnUrl);
            authorizeUrl = UrlConstant.URL_GET_QRCONNECT + "?appid=" + appConfig.getAppKey() + "&response_type=code" +
                "&scope=snsapi_login&redirect_uri=" + url;
        }else if(DING_BIND.equals(type)){
            //钉钉账号绑定
            if(userCode == null || "".equals(userCode)){
                throw new ObjectException("缺少参数userCode;");
            }
            url = URIUtil.encodeURIComponent(appConfig.getRedirectBindUri() + "?type=" + type + "&returnUrl=" + returnUrl + "&userCode=" + userCode);
            authorizeUrl = UrlConstant.URL_GET_QRCONNECT + "?appid=" + appConfig.getAppKey() + "&response_type=code" +
                "&scope=snsapi_login&redirect_uri=" + url;
        }
        response.sendRedirect(authorizeUrl);
    }

    /**
     * 获取用户信息: 微信/钉钉/QQ
     * @param code
     * @param state
     * @param request
     * @param type  weChat;ding;QQ
     * @return
     */
    @GetMapping("/qrUserInfo")
    public String qrUserInfo(@RequestParam("code") String code,
                             @RequestParam("state") String state,
                             @RequestParam("returnUrl") String returnUrl,
                             @RequestParam("type") String type,
                             HttpServletRequest request) {
        Map<String, Object> paramsMap = new HashMap<>();
        UserPlat userPlat = new UserPlat();
        String msg = "";
        if(WECHAT_LOGIN.equals(type)){
            //微信登录
            msg = "微信";
            WxMpUser wxMpUser = weChatService.getWxUser(code);
            //从token中获取openid
            String openId = wxMpUser.getOpenId();
            String unionId = wxMpUser.getUnionId();
            paramsMap.put("userId", openId);
            paramsMap.put("appKey", wxAppConfig.getAppID());
            paramsMap.put("unionId", unionId);
            userPlat = userPlatService.getUserPlatByProperties(paramsMap);
        }else if(DING_LOGIN.equals(type)){
            //钉钉登录
            msg = "钉钉";
            //获取access_token
            String accessToken = "";
            ResponseData accessTokenData = tokenService.getAccessToken();
            if (accessTokenData.getCode() != 0) {
                throw new ObjectException(accessTokenData.getCode(), accessTokenData.getMessage());
            }
            accessToken = accessTokenData.getData().toString();

            if (StringUtils.isBlank(accessToken)) {
                throw new ObjectException(accessTokenData.getCode(), "获取钉钉access_token失败");
            }

            //获取用户unionid
            ResponseData unionIdData = dingTalkLoginService.getUserByCode(code);
            if (unionIdData.getCode() != 0) {
                throw new ObjectException(unionIdData.getCode(), unionIdData.getMessage());
            }
            String unionid = unionIdData.getData().toString();

            //根据unionid获取userid
            ResponseData userIdData = dingTalkLoginService.getUserByUnionId(accessToken, unionid);
            if (userIdData.getCode() != 0) {
                throw new ObjectException(userIdData.getCode(), userIdData.getMessage());
                //returnUrl = appConfig.getRedirectLoginUrl() + "A/login?accessToken=noUser&type=" + type;
            }
            String userId = userIdData.getData().toString();
            paramsMap.put("userId", userId);
            paramsMap.put("corpId", appConfig.getCorpId());
            paramsMap.put("appKey", appConfig.getAppKey());
            paramsMap.put("appSecret", appConfig.getAppSecret());
            userPlat = userPlatService.getUserPlatByProperties(paramsMap);
        }else if(QQ_LOGIN.equals(type)){
            //QQ登录
            msg = "QQ";
        }
        if(userPlat == null){
            returnUrl = appConfig.getRedirectLoginUrl() + "A/login?accessToken=noUser&type=" + type;
        } else {
            CentitUserDetails ud = platformEnvironment.loadUserDetailsByUserCode(userPlat.getUserCode());
            SecurityContextHolder.getContext().setAuthentication(ud);
            if (returnUrl != null && returnUrl.contains("?")) {
                returnUrl = returnUrl + "&accessToken=" + request.getSession().getId();
            } else {
                returnUrl = returnUrl + "?accessToken=" + request.getSession().getId();
            }
        }

        //占位符 替换成/#/(特殊字符)
        if (returnUrl != null && returnUrl.indexOf("/A/") > -1) {
            returnUrl = returnUrl.replace("/A/", "/#/");
        }
        return "redirect:" + returnUrl;
    }

    /**
     * 获取用户信息: 微信/钉钉/QQ
     * @param code
     * @param state
     * @param request
     * @param type  weChat;ding;QQ
     * @return
     */
    @GetMapping("/bindUserInfo")
    public String bindUserInfo(@RequestParam("code") String code,
                               @RequestParam("state") String state,
                               @RequestParam("userCode") String userCode,
                               @RequestParam("returnUrl") String returnUrl,
                               @RequestParam("type") String type,
                               HttpServletRequest request) {
        if(userCode == null || "".equals(userCode)){
            throw new ObjectException("500", "userCode为空");
        }
        Map<String, Object> paramsMap = new HashMap<>();
        UserPlat userPlat = new UserPlat();
        CentitUserDetails userDetails = platformEnvironment.loadUserDetailsByUserCode(userCode);
        UserPlat newUser = new UserPlat();
        if(WECHAT_BIND.equals(type)){
            WxMpUser wxMpUser = weChatService.getWxUser(code);
            //从token中获取openid(授权用户唯一标识)
            String openId = wxMpUser.getOpenId();
            String unionId = wxMpUser.getUnionId();
            String weChatName = wxMpUser.getNickname();
            paramsMap.put("userCode", userCode);
            paramsMap.put("appKey", wxAppConfig.getAppID());
            paramsMap.put("unionId", unionId);
            userPlat = userPlatService.getUserPlatByProperties(paramsMap);
            if(userPlat != null){
                throw new ObjectException("500", "微信账号已绑定，请勿重复绑定！");
            }else{
                if (null != userDetails) {
                    newUser.setUnionId(unionId);
                    newUser.setUserId(openId);
                    newUser.setUserCode(userDetails.getUserCode());
                    newUser.setPlatId("2");
                    newUser.setCorpId("");
                    newUser.setAppKey(wxAppConfig.getAppID());
                    newUser.setAppSecret(wxAppConfig.getAppSecret());
                    newUser.setUserName(weChatName);
                    userPlatService.saveUserPlat(newUser);
                }
            }
        }else if(DING_BIND.equals(type)){
            //获取access_token
            String accessToken = "";
            ResponseData accessTokenData = tokenService.getAccessToken();
            if (accessTokenData.getCode() != 0) {
                throw new ObjectException(accessTokenData.getCode(), accessTokenData.getMessage());
            }
            accessToken = accessTokenData.getData().toString();

            if (StringUtils.isBlank(accessToken)) {
                throw new ObjectException("500", "获取钉钉access_token失败");
            }

            //获取用户unionid
            ResponseData unionIdData = dingTalkLoginService.getUserByCode(code);
            if (unionIdData.getCode() != 0) {
                throw new ObjectException(unionIdData.getCode(), unionIdData.getMessage());
            }
            String unionid = unionIdData.getData().toString();

            //根据unionid获取userid
            ResponseData userIdData = dingTalkLoginService.getUserByUnionId(accessToken, unionid);
            if (userIdData.getCode() != 0) {
                throw new ObjectException(userIdData.getCode(), userIdData.getMessage());
            }
            String userId = userIdData.getData().toString();

            //根据userId获取用户详情
            ResponseData userInfoData = dingTalkLoginService.getUserInfo(accessToken, userId);
            if (userInfoData.getCode() != 0) {
                throw new ObjectException(userIdData.getCode(), userIdData.getMessage());
            }
            JSONObject jsonObject = JSONObject.parseObject(userInfoData.getData().toString());
            String name = "";
            if (null != jsonObject) {
                JSONObject userObject = JSONObject.parseObject(jsonObject.getString("result"));
                if (null != userObject) {
                    name = userObject.getString("name");
                }
            }
            paramsMap.put("userId", userId);
            paramsMap.put("corpId", appConfig.getCorpId());
            paramsMap.put("appKey", appConfig.getAppKey());
            paramsMap.put("appSecret", appConfig.getAppSecret());
            userPlat = userPlatService.getUserPlatByProperties(paramsMap);
            if(userPlat != null){
                throw new ObjectException("500", "钉钉账号已绑定，请勿重复绑定！");
            }else{
                if (null != userDetails) {
                    newUser.setUserCode(userDetails.getUserCode());
                    Map<String, Object> platMap = new HashMap<>();
                    platMap.put("corpId", appConfig.getCorpId());
                    Platform platform = platformService.getPlatformByProperties(platMap);
                    if (null != platform) {
                        newUser.setPlatId(platform.getPlatId());
                    }
                    newUser.setCorpId(appConfig.getCorpId());
                    newUser.setAppKey(appConfig.getAppKey());
                    newUser.setAppSecret(appConfig.getAppSecret());
                    newUser.setUnionId(unionid);
                    newUser.setUserId(userId);
                    newUser.setUserName(name);
                    userPlatService.saveUserPlat(newUser);
                }
            }
        }else if(QQ_BIND.equals(type)){

        }
        if (returnUrl != null && returnUrl.contains("?")) {
            returnUrl = returnUrl + "&accessToken=" + request.getSession().getId();
        } else {
            returnUrl = returnUrl + "?accessToken=" + request.getSession().getId();
        }
        //占位符 替换成/#/(特殊字符)
        if (returnUrl != null && returnUrl.indexOf("/A/") > -1) {
            returnUrl = returnUrl.replace("/A/", "/#/");
        }
        return "redirect:" + returnUrl;
    }

}
