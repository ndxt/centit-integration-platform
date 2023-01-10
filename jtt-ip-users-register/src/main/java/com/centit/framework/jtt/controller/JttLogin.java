package com.centit.framework.jtt.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.framework.jtt.config.UniteConfig;
import com.centit.framework.jtt.service.JttAccessTokenService;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.security.model.CentitPasswordEncoder;
import com.centit.framework.security.model.CentitUserDetails;
import com.centit.framework.system.po.UserInfo;
import com.centit.framework.system.service.SysUserManager;
import com.centit.support.network.HttpExecutor;
import com.centit.support.network.HttpExecutorContext;
import com.newland.bi3.security.SM4Utils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

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

    @ApiOperation(value = "统一门户单点登陆", notes = "统一门户单点登陆")
    @GetMapping(value = "/unitelogin")
    public String uniteLogin(HttpServletRequest request) {
        Map<String, Object> filterMap = collectRequestParameters(request);
        logger.info("统一门户单点登陆,参数：{}", filterMap);
        String token = String.valueOf(filterMap.get("token"));
        String returnUrl = String.valueOf(filterMap.get("returnUrl"));
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
                String tokenResult = HttpExecutor.jsonPost(HttpExecutorContext.create(), uniteConfig.getLoginCheckUrl(), params.toJSONString());
                logger.info("调用验证token:{},接口返回信息：{}", params, tokenResult);
                if (StringUtils.isNotEmpty(tokenResult)) {
                    JSONObject tokenJson = JSON.parseObject(tokenResult);
                    if (null != tokenJson && 200 == tokenJson.getInteger("status")) {
                        //获取扩展信息
                        String loginCheckExtend = HttpExecutor.jsonPost(HttpExecutorContext.create(), uniteConfig.getLoginCheckExtendUrl(), params.toJSONString());
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
    public Map<String, Object> checkAppUserValid(@RequestBody String userInfo, HttpServletRequest request) {
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

}