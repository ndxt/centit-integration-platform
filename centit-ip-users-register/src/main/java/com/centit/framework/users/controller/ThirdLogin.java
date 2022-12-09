package com.centit.framework.users.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.centit.framework.common.ResponseData;
import com.centit.framework.common.WebOptUtils;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.security.model.CentitPasswordEncoder;
import com.centit.framework.security.model.CentitUserDetails;
import com.centit.framework.users.config.AppConfig;
import com.centit.framework.users.config.UniteConfig;
import com.centit.framework.users.config.UrlConstant;
import com.centit.framework.users.config.WxAppConfig;
import com.centit.framework.users.dto.DingUserDTO;
import com.centit.framework.users.po.Platform;
import com.centit.framework.users.po.UserPlat;
import com.centit.framework.users.service.*;
import com.centit.support.common.ObjectException;
import com.centit.support.network.HttpExecutor;
import com.centit.support.network.HttpExecutorContext;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 第三方登录Controller
 */
@Controller
@RequestMapping("/third")
@Api(value = "第三方平台登录相关接口", tags = "第三方平台登录相关接口")
public class ThirdLogin extends BaseController {

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

    @Autowired
    private UniteConfig uniteConfig;

    @Autowired
    private CentitPasswordEncoder passwordEncoder;

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
        if (WECHAT_LOGIN.equals(type)) {
            //微信登录
            //url = wxAppConfig.getRedirectLoginUri() + "?returnUrl=" + returnUrl;
            url = wxAppConfig.getRedirectLoginUri() + "?type=" + type + "&returnUrl=" + returnUrl;
            authorizeUrl = wxOpenService.buildQrConnectUrl(url, WxConsts.QRCONNECT_SCOPE_SNSAPI_LOGIN, "");
        } else if (WECHAT_BIND.equals(type)) {
            //微信账号绑定
            if (userCode == null || "".equals(userCode)) {
                throw new ObjectException("缺少参数userCode;");
            }
            url = wxAppConfig.getRedirectBindUri() + "?type=" + type + "&returnUrl=" + returnUrl + "&userCode=" + userCode;
            authorizeUrl = wxOpenService.buildQrConnectUrl(url, WxConsts.QRCONNECT_SCOPE_SNSAPI_LOGIN, "");
        } else if (DING_LOGIN.equals(type)) {
            //钉钉登陆页面
            url = URIUtil.encodeURIComponent(appConfig.getRedirectUri() + "?type=" + type + "&returnUrl=" + returnUrl);
            authorizeUrl = UrlConstant.URL_GET_QRCONNECT + "?appid=" + appConfig.getAppKey() + "&response_type=code" +
                "&scope=snsapi_login&redirect_uri=" + url;
        } else if (DING_BIND.equals(type)) {
            //钉钉账号绑定
            if (userCode == null || "".equals(userCode)) {
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
     *
     * @param code
     * @param state
     * @param request
     * @param type    weChat;ding;QQ
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
        if (WECHAT_LOGIN.equals(type)) {
            //微信登录
            WxMpUser wxMpUser = weChatService.getWxUser(code);
            //从token中获取openid
            //String openId = wxMpUser.getOpenId();
            String unionId = wxMpUser.getUnionId();
            paramsMap.put("platId", "2");
            //paramsMap.put("userId", openId);
            //paramsMap.put("appKey", wxAppConfig.getAppID());
            paramsMap.put("unionId", unionId);
            userPlat = userPlatService.getUserPlatByProperties(paramsMap);
        } else if (DING_LOGIN.equals(type)) {
            //钉钉登录
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
        } else if (QQ_LOGIN.equals(type)) {
            //QQ登录
        }
        if (null == userPlat) {
            returnUrl = appConfig.getRedirectLoginUrl() + "A/login?accessToken=noUser&type=" + type;
        } else {
            CentitUserDetails ud = platformEnvironment.loadUserDetailsByUserCode(userPlat.getUserCode());
            ud.setLoginIp(WebOptUtils.getRequestAddr(request));
            SecurityContextHolder.getContext().setAuthentication(ud);
            if (StringUtils.isNotBlank(returnUrl) && returnUrl.contains("?")) {
                returnUrl = returnUrl + "&accessToken=" + request.getSession().getId();
            } else {
                returnUrl = returnUrl + "?accessToken=" + request.getSession().getId();
            }
        }

        //占位符 替换成/#/(特殊字符)
        if (StringUtils.isNotBlank(returnUrl) && returnUrl.indexOf("/A/") > -1) {
            returnUrl = returnUrl.replace("/A/", "/#/");
        }
        return "redirect:" + returnUrl;
    }

    /**
     * 获取用户信息: 微信/钉钉/QQ
     *
     * @param code
     * @param state
     * @param request
     * @param type    weChat;ding;QQ
     * @return
     */
    @GetMapping("/bindUserInfo")
    public String bindUserInfo(@RequestParam("code") String code,
                               @RequestParam("state") String state,
                               @RequestParam("userCode") String userCode,
                               @RequestParam("returnUrl") String returnUrl,
                               @RequestParam("type") String type,
                               HttpServletRequest request) {
        if (userCode == null || "".equals(userCode)) {
            throw new ObjectException("500", "userCode为空");
        }
        Map<String, Object> paramsMap = new HashMap<>();
        UserPlat userPlat = new UserPlat();
        CentitUserDetails userDetails = platformEnvironment.loadUserDetailsByUserCode(userCode);
        if (null == userDetails) {
            throw new ObjectException("500", "根据userCode获取用户信息为空");
        }
        String regCellPhone = userDetails.getUserInfo().getString("regCellPhone");
        UserPlat newUser = new UserPlat();
        if (WECHAT_BIND.equals(type)) {
            WxMpUser wxMpUser = weChatService.getWxUser(code);
            //从token中获取openid(授权用户唯一标识)
            String openId = wxMpUser.getOpenId();
            String unionId = wxMpUser.getUnionId();
            logger.info("unionId：{}", unionId);
            String weChatName = wxMpUser.getNickname();
            //paramsMap.put("appKey", wxAppConfig.getAppID());
            paramsMap.put("platId", "2");
            paramsMap.put("unionId", unionId);
            userPlat = userPlatService.getUserPlatByProperties(paramsMap);
            if (null != userPlat) {
                returnUrl = returnUrl + "?accessToken=noBind&type=" + type;
            } else {
                newUser.setUnionId(unionId);
                newUser.setUserId(openId);
                newUser.setUserCode(userDetails.getUserCode());
                newUser.setPlatId("2");
                newUser.setCorpId("PC");
                newUser.setAppKey(wxAppConfig.getAppID());
                newUser.setAppSecret(wxAppConfig.getAppSecret());
                newUser.setUserName(weChatName);
                userPlatService.saveUserPlat(newUser);
            }
        } else if (DING_BIND.equals(type)) {
            //获取access_token
            String accessToken = "";
            ResponseData accessTokenData = tokenService.getAccessToken();
            if (accessTokenData.getCode() != 0) {
                throw new ObjectException(accessTokenData.getCode(), accessTokenData.getMessage());
            }
            accessToken = accessTokenData.getData().toString();

            DingUserDTO dingUserDTO = new DingUserDTO();
            dingUserDTO.setUserName(userDetails.getUsername());
            dingUserDTO.setRegCellPhone(regCellPhone);
            dingUserDTO.setPrimaryUnit(userDetails.getCurrentUnitCode());
            dingTalkLoginService.userCreate(accessToken, dingUserDTO);

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
            JSONObject jsonObject = JSON.parseObject(userInfoData.getData().toString());
            String name = "";
            if (null != jsonObject) {
                JSONObject userObject = JSON.parseObject(jsonObject.getString("result"));
                if (null != userObject) {
                    name = userObject.getString("name");
                }
            }
            paramsMap.put("userId", userId);
            paramsMap.put("corpId", appConfig.getCorpId());
            paramsMap.put("appKey", appConfig.getAppKey());
            paramsMap.put("appSecret", appConfig.getAppSecret());
            userPlat = userPlatService.getUserPlatByProperties(paramsMap);
            if (null != userPlat) {
                returnUrl = returnUrl + "?accessToken=noBind&type=" + type;
            } else {
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
        } else if (QQ_BIND.equals(type)) {
            // QQ
        }
        if (null == userPlat) {
            if (StringUtils.isNotBlank(returnUrl) && returnUrl.contains("?")) {
                returnUrl = returnUrl + "&accessToken=" + request.getSession().getId();
            } else {
                returnUrl = returnUrl + "?accessToken=" + request.getSession().getId();
            }
        }
        //占位符 替换成/#/(特殊字符)
        if (StringUtils.isNotBlank(returnUrl) && returnUrl.indexOf("/A/") > -1) {
            returnUrl = returnUrl.replace("/A/", "/#/");
        }
        return "redirect:" + returnUrl;
    }


    @ApiOperation(value = "移动端微信登录", notes = "移动端微信登录")
    @PostMapping(value = "/mobile/weChat/login")
    @WrapUpResponseBody
    public ResponseData weChatLogin(@RequestParam("unionId") String unionId,
                                    @RequestParam("nickName") String nickName,
                                    HttpServletRequest request) {
        Map<String, Object> sessionMap = new HashMap<>();
        try {
            Map<String, Object> paramsMap = new HashMap<>();
            paramsMap.put("platId", "2");
            paramsMap.put("unionId", unionId);
            UserPlat userPlat = userPlatService.getUserPlatByProperties(paramsMap);
            if (userPlat == null) {
                return ResponseData.makeErrorMessageWithData(sessionMap, 500, "请在登陆后绑定微信。");
            }
            CentitUserDetails ud = platformEnvironment.loadUserDetailsByUserCode(userPlat.getUserCode());
            ud.setLoginIp(WebOptUtils.getRequestAddr(request));
            SecurityContextHolder.getContext().setAuthentication(ud);
            sessionMap.put("accessToken", request.getSession().getId());
            sessionMap.put("userInfo", ud);
        } catch (Exception e) {
            return ResponseData.makeErrorMessageWithData(new HashMap<>(), 500, "系统错误。");
        }
        return ResponseData.makeResponseData(sessionMap);
    }

    @ApiOperation(value = "移动端微信绑定", notes = "移动端微信绑定")
    @PostMapping(value = "/mobile/weChat/bind")
    @WrapUpResponseBody
    public ResponseData weChatBind(@RequestParam("unionId") String unionId,
                                   @RequestParam("nickName") String nickName,
                                   @RequestParam("userCode") String userCode) {
        try {
            CentitUserDetails userDetails = platformEnvironment.loadUserDetailsByUserCode(userCode);
            if (null == userDetails) {
                return ResponseData.makeErrorMessageWithData("", 500, "未查询到用户。");
            }
            Map<String, Object> paramsMap = new HashMap<>();
            paramsMap.put("platId", "2");
            paramsMap.put("unionId", unionId);
            UserPlat userPlat = userPlatService.getUserPlatByProperties(paramsMap);
            if (null != userPlat) {
                return ResponseData.makeErrorMessageWithData("", 500, "该微信号已绑定，请勿重复绑定。");
            }
            UserPlat newUser = new UserPlat();
            newUser.setUnionId(unionId);
            newUser.setUserCode(userDetails.getUserCode());
            newUser.setPlatId("2");
            newUser.setCorpId("Moblie");
            newUser.setAppKey(wxAppConfig.getMoblieAppId());
            //58c5cb02d53e3586dd4f4ea27fede53d
            newUser.setAppSecret(wxAppConfig.getMoblieAppSecret());
            newUser.setUserName(nickName);
            userPlatService.saveUserPlat(newUser);
        } catch (Exception e) {
            return ResponseData.makeErrorMessageWithData("", 500, "系统错误。");
        }
        return ResponseData.makeResponseData(nickName);
    }

    @ApiOperation(value = "统一门户单点登陆", notes = "统一门户单点登陆")
    @GetMapping(value = "/unitelogin")
    public String uniteLogin(HttpServletRequest request) {
        Map<String, Object> filterMap = collectRequestParameters(request);
        request.changeSessionId();
        logger.info("验证统一门户单点登陆接口,参数：{}", filterMap);
        String token = String.valueOf(filterMap.get("token"));
        String returnUrl = String.valueOf(filterMap.get("returnUrl"));
        token = token.replace(" ", "+");
        String errorMsg = "";
        String accessToken = "";
        try {
            JSONObject params = new JSONObject();
            params.put("appId", uniteConfig.getAppId());
            params.put("token", URLEncoder.encode(token, "utf-8"));
            //todo token持久化redis，根据token对应关系获取accessToken

            if (StringUtils.isBlank(accessToken)) {
                //验证token是否有效
                String tokenResult = HttpExecutor.jsonPost(HttpExecutorContext.create(), uniteConfig.getLoginCheckUrl(), params.toJSONString());
                logger.info("调用验证token:{},接口返回信息：{}", params, tokenResult);
                if (StringUtils.isNotEmpty(tokenResult)) {
                    JSONObject tokenJson = JSON.parseObject(tokenResult);
                    if (null != tokenJson && 200 == tokenJson.getInteger("status")) {
                        //获取扩展信息
                        String loginCheckExtend = HttpExecutor.jsonPost(HttpExecutorContext.create(), uniteConfig.getLoginCheckExtendUrl(), params.toJSONString());
                        JSONObject loginExtendJson = JSON.parseObject(loginCheckExtend);
                        if (null != loginExtendJson) {
                            JSONObject userInfo = loginExtendJson.getJSONObject("data").getJSONObject("userInfo");
                            String loginName = userInfo.getString("loginName");
                            userInfo.put("userAccount", userInfo.getString("loginName"));
                            CentitUserDetails ud = platformEnvironment.loadUserDetailsByLoginName(loginName);
                            if (null != ud) {
                                //todo 国密sm4或sm3 解密
                                String password = "";
                                //String password = SM4dDecrypt(userInfo.getString("userPwd"), uniteConfig.getUniteAppSecret());
                                if (passwordEncoder.isPasswordValid(ud.getUserInfo().getString("userPin"), password, ud.getUserCode())) {
                                    SecurityContextHolder.getContext().setAuthentication(ud);
                                    accessToken = request.getSession().getId();
                                    logger.info("用户名：{}登录成功", userInfo.getString("loginName"));
                                    //todo token-accessToken redis 持久化
                                } else {
                                    logger.error("用户名：{}验证不通过", userInfo.getString("loginName"));
                                    errorMsg = "登录名" + userInfo.getString("loginName") + "密码错误！";
                                }
                            } else {
                                errorMsg = "登录名" + userInfo.getString("loginName") + "不存在！";
                            }
                        } else {
                            errorMsg = "统一门户扩展验证接口返回为空！";
                        }
                    } else {
                        if (null != tokenJson) {
                            errorMsg = tokenJson.getString("msg");
                        } else {
                            errorMsg = "统一门户token验证接口返回为空！";
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("统一门户单点登录异常：{}", e.getMessage());
            errorMsg = "统一门户单点登录异常:" + e.getMessage();
        }
        if (StringUtils.isNotBlank(errorMsg)) {
            String errorUrl = "redirect:redirecterror";
            try {
                errorUrl = errorUrl + "?msg=" + URLEncoder.encode(errorMsg, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                logger.error("URLEncoder异常", e);
            }
            return errorUrl;
        } else {
            if (StringUtils.isNotBlank(returnUrl) && returnUrl.contains("?")) {
                returnUrl = returnUrl + "&accessToken=" + accessToken;
            } else {
                returnUrl = returnUrl + "?accessToken=" + accessToken;
            }
            //占位符 替换成/#/(特殊字符)
            if (StringUtils.isNotBlank(returnUrl) && returnUrl.indexOf("/A/") > -1) {
                returnUrl = returnUrl.replace("/A/", "/#/");
            }
        }
        return "redirect:" + returnUrl;
    }

    /**
     * redirect返回失败信息
     */
    @GetMapping("/redirecterror")
    @WrapUpResponseBody
    public String redirectError(HttpServletRequest request) {
        String errorMsg = request.getParameter("msg");
        if (StringUtils.isBlank(errorMsg)) {
            errorMsg = "error";
        }
        return errorMsg;
    }

}
