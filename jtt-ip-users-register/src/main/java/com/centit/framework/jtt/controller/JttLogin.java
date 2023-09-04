package com.centit.framework.jtt.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.centit.framework.common.ResponseData;
import com.centit.framework.common.WebOptUtils;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.framework.jtt.config.JsmotConstant;
import com.centit.framework.jtt.config.JsmotSyncConfig;
import com.centit.framework.jtt.config.UniteConfig;
import com.centit.framework.jtt.service.JttAccessTokenService;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.model.basedata.UserInfo;
import com.centit.framework.model.security.CentitPasswordEncoder;
import com.centit.framework.model.security.CentitUserDetails;
import com.centit.framework.system.service.SysUserManager;
import com.centit.support.algorithm.BooleanBaseOpt;
import com.centit.support.network.HttpExecutor;
import com.centit.support.network.HttpExecutorContext;
import com.newland.bi3.security.SM4Utils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 第三方登录Controller
 */
@Controller
@RequestMapping("/jtt")
@Api(value = "第三方平台登录相关接口", tags = "第三方平台登录相关接口")
public class JttLogin extends BaseController {

    @Autowired
    private PlatformEnvironment platformEnvironment;

    @Autowired
    private JttAccessTokenService jttAccessTokenService;

    @Autowired
    private UniteConfig uniteConfig;

    @Autowired
    private CentitPasswordEncoder passwordEncoder;

    @Autowired
    private SysUserManager sysUserManager;

    @Autowired
    private JsmotSyncConfig jsmotSyncConfig;

    @Autowired(required = false)
    private RedisTemplate<String, JSONObject> redisTemplate;

    @ApiOperation(value = "水务集团单点登陆", notes = "水务集团单点登陆")
    @GetMapping(value = "/waterlogin")
    public String waterLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        CentitUserDetails userDetails = WebOptUtils.getCurrentUserDetails(request);
        String returnUrl = request.getParameter("returnUrl");
        if (null == userDetails) {
            String loginName = request.getHeader("oam_remote_user");
            if (null == loginName) {
                loginName = request.getParameter("testUserCode");
            }
            String errorMsg = "";
            CentitUserDetails ud = platformEnvironment.loadUserDetailsByLoginName(loginName);
            if (null != ud) {
                SecurityContextHolder.getContext().setAuthentication(ud);
            } else {
                errorMsg = "登录名" + loginName + "不存在！";
            }
            if (StringUtils.isNotBlank(errorMsg)) {
                String errorUrl = "redirect:redirecterror";
                try {
                    errorUrl = errorUrl + "?msg=" + URLEncoder.encode(errorMsg, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    logger.error("URLEncoder异常", e);
                }
                return errorUrl;
            }
        }
        if (StringUtils.isNotBlank(returnUrl) && returnUrl.indexOf("/A/") > -1) {
            returnUrl = returnUrl.replace("/A/", "/#/");
        }
        response.setHeader("x-auth-token", request.getSession().getId());
        return "redirect:" + returnUrl;
    }

    @ApiOperation(value = "统一门户单点登陆", notes = "统一门户单点登陆")
    @GetMapping(value = "/unitelogin")
    public String uniteLogin(HttpServletRequest request) {
        Map<String, Object> filterMap = collectRequestParameters(request);
        logger.info("统一门户单点登陆,参数：{}", filterMap);
        String token = request.getParameter("token");
        String returnUrl = request.getParameter("returnUrl");
        logger.info("returnUrl值:{}", returnUrl);
        token = token.replace(" ", "+");
        String errorMsg = "";
        String accessToken = "";
        try {
            logger.info("token值：{}", token);
            JSONObject params = new JSONObject();
            params.put("appId", uniteConfig.getAppId());
            params.put("token", URLEncoder.encode(token, "utf-8"));
            //todo token持久化redis，根据token对应关系获取accessToken

            if (StringUtils.isBlank(accessToken) && StringUtils.isNotBlank(token)) {
                //验证token是否有效
                Boolean useSSL = BooleanBaseOpt.castObjectToBoolean(uniteConfig.getUseSSL(), false);
                HttpClientContext context = HttpClientContext.create();
                CloseableHttpClient httpClient = null;
                if (useSSL) {
                    httpClient = HttpExecutor.createKeepSessionHttpsClient();
                } else {
                    httpClient = HttpExecutor.createKeepSessionHttpClient();
                }
                HttpExecutorContext executorContext = HttpExecutorContext.create(httpClient).context(context);
                String tokenResult = HttpExecutor.jsonPost(executorContext, uniteConfig.getLoginCheckUrl(), params.toJSONString());
                logger.info("调用验证token:{},接口返回信息：{}", params, tokenResult);
                if (StringUtils.isNotEmpty(tokenResult)) {
                    JSONObject tokenJson = JSON.parseObject(tokenResult);
                    if (null != tokenJson && 200 == tokenJson.getInteger("status")) {
                        //获取扩展信息
                        String loginCheckExtend = HttpExecutor.jsonPost(executorContext, uniteConfig.getLoginCheckExtendUrl(), params.toJSONString());
                        logger.info("调用扩展验证:{},接口返回信息：{}", params, loginCheckExtend);
                        JSONObject loginExtendJson = JSON.parseObject(loginCheckExtend);
                        if (null != loginExtendJson) {
                            JSONObject userInfo = loginExtendJson.getJSONObject("data").getJSONObject("userInfo");
                            String loginName = userInfo.getString("loginName");
                            logger.info("loginName:{}", loginName);
                            CentitUserDetails ud = platformEnvironment.loadUserDetailsByLoginName(loginName);
                            if (null != ud) {
                                SecurityContextHolder.getContext().setAuthentication(ud);
                                accessToken = request.getSession().getId();
                                logger.info("用户名：{}登录成功", loginName);
                            } else {
                                errorMsg = "登录名" + loginName + "不存在！";
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
            } else {
                errorMsg = "统一门户token为空！";
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

    @ApiOperation(value = "移动端单点登陆", notes = "移动端单点登陆")
    @GetMapping(value = "/applogin")
    public String appLogin(HttpServletRequest request) {
        Map<String, Object> filterMap = collectRequestParameters(request);
        logger.info("移动端单点登陆,参数：{}", filterMap);
        String tmpAuthCode = request.getParameter("tmp_auth_code");
        String returnUrl = request.getParameter("returnUrl");
        logger.info("returnUrl值:{}", returnUrl);
        if (StringUtils.isBlank(returnUrl)) {
            returnUrl = jsmotSyncConfig.getAppReturnUrl();
        }
        String errorMsg = "";
        String accessToken = "";
        try {
            logger.info("临时授权码code值：{}", tmpAuthCode);
            if (StringUtils.isNotBlank(tmpAuthCode)) {
                //获取token
                String token = getAccessToken();
                if (StringUtils.isBlank(token)) {
                    errorMsg = "获取交通云accessToken失败";
                }
                if (StringUtils.isBlank(errorMsg)) {
                    String uri = jsmotSyncConfig.getJsmotHost() + JsmotConstant.URL_H5_GETUSERINFO_BYCODE + "/" + tmpAuthCode + "?accessToken=" + token;
                    String userResult = HttpExecutor.simpleGet(HttpExecutorContext.create(), uri);
                    logger.info("调用获取用户的详细信息token:{},接口返回信息：{}", token, userResult);
                    if (StringUtils.isNotEmpty(userResult)) {
                        JSONObject userJson = JSON.parseObject(userResult);
                        if (null != userJson) {
                            long retCode = userJson.getLong("retCode");
                            String retMsg = userJson.getString("retMsg");
                            if (retCode == 0) {
                                String loginName = userJson.getJSONObject("bizData").getString("loginName");
                                logger.info("loginName:{}", loginName);
                                CentitUserDetails ud = platformEnvironment.loadUserDetailsByLoginName(loginName);
                                if (null != ud) {
                                    SecurityContextHolder.getContext().setAuthentication(ud);
                                    accessToken = request.getSession().getId();
                                    logger.info("用户名：{}登录成功", loginName);
                                } else {
                                    errorMsg = "登录名" + loginName + "不存在！";
                                }
                            } else {
                                errorMsg = retMsg;
                            }
                        } else {
                            errorMsg = "移动端获取用户的详细信息接口返回为空！";
                        }
                    } else {
                        errorMsg = "移动端获取用户的详细信息接口返回为空！";
                    }
                }
            } else {
                errorMsg = "临时授权码为空！";
            }
        } catch (Exception e) {
            logger.error("移动端单点登录异常：{}", e.getMessage());
            errorMsg = "移动端单点登录异常:" + e.getMessage();
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

    @ApiOperation(value = "统一门户账号验证", notes = "统一门户账号验证")
    @ResponseBody
    @PostMapping(value = "/checkAppUserValid")
    public Map<String, Object> checkAppUserValid(@RequestBody String userInfo) {
        logger.info("统一门户账号验证；{}", userInfo);
        Map<String, Object> resMap = new HashMap<>();
        JSONObject userInfoJson = JSON.parseObject(userInfo);
        if (null != userInfoJson) {
            String password = sm4dDecrypt(userInfoJson.getString("userPwd"), uniteConfig.getUniteAppSecret());
            UserInfo user = sysUserManager.loadUserByLoginname(userInfoJson.getString("userAccount"));
            if (null != user && passwordEncoder.isPasswordValid(user.getUserPin(), password, user.getUserCode())) {
                resMap.put("status", 200);
                resMap.put("msg", "OK");
            } else {
                resMap.put("status", 601);
                resMap.put("msg", "登录名不存在或密码错误！");
            }
        } else {
            resMap.put("status", 500);
            resMap.put("msg", "登入账号和密码信息为空！");
        }
        return resMap;
    }

    @ApiOperation(value = "短信登陆", notes = "短信登陆")
    @PostMapping(value = "/smslogin")
    @WrapUpResponseBody
    public ResponseData smslogin(@RequestParam("phone") String phone,
                                 @RequestParam("code") String code,
                                 HttpServletRequest request) throws Exception {
        if (StringUtils.isBlank(phone)) {
            return ResponseData.makeErrorMessage(500, "请输入手机号！");
        }
        if (StringUtils.isBlank(code)) {
            return ResponseData.makeErrorMessage(500, "请输入验证码！");
        }
        //从Redis中获取验证码和部分信息
        JSONObject json = redisTemplate.boundValueOps(phone).get();
        if (null == json) {
            json = JSON.parseObject(request.getHeader("verifyCode"));
        }
        if (null == json) {
            return ResponseData.makeErrorMessage(500, "未发送验证码！");
        }
        String verifyCode = json.getString("verifyCode");
        Long createTime = json.getLong("createTime");
        if (!verifyCode.equals(code)) {
            return ResponseData.makeErrorMessage(500, "验证码错误！");
        }
        if ((System.currentTimeMillis() - createTime) > 1000 * 60 * 5) {
            redisTemplate.delete(phone);
            return ResponseData.makeErrorMessage(500, "验证码已过期！");
        }
        CentitUserDetails ud = platformEnvironment.loadUserDetailsByRegCellPhone(phone);
        if (null != ud) {
            redisTemplate.delete(phone);
            ud.setLoginIp(WebOptUtils.getRequestAddr(request));
            SecurityContextHolder.getContext().setAuthentication(ud);
            Map<String, Object> sessionMap = new HashMap<>();
            sessionMap.put("accessToken", request.getSession().getId());
            sessionMap.put("userInfo", ud);
            return ResponseData.makeResponseData(sessionMap);
        } else {
            redisTemplate.boundValueOps(phone).expire(60L * 5, TimeUnit.SECONDS);
            return ResponseData.makeErrorMessage("未查询到" + phone + "对应手机号用户");
        }
    }

    private static String sm4dDecrypt(String encryptData, String key) {
        String decryptData = null;
        try {
            SM4Utils sm4 = new SM4Utils();
            sm4.secretKey = key;
            decryptData = sm4.decryptData_ECB(encryptData);
        } catch (Exception e) {
            return null;
        }
        return decryptData;
    }

    private String getAccessToken() {
        String accessToken = "";
        ResponseData accessTokenData = jttAccessTokenService.getJsmotAccessToken();
        if (accessTokenData.getCode() != 0) {
            return "";
        }
        accessToken = accessTokenData.getData().toString();
        return accessToken;
    }

}
