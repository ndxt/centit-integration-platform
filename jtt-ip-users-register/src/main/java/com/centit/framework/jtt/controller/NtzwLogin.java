package com.centit.framework.jtt.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.centit.framework.common.ResponseData;
import com.centit.framework.components.CodeRepositoryCache;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.framework.jtt.config.NtzwConfig;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.model.security.CentitUserDetails;
import com.centit.support.algorithm.BooleanBaseOpt;
import com.centit.support.network.HttpExecutor;
import com.centit.support.network.HttpExecutorContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 南通政务登录
 *
 * @author zfg
 */
@Controller
@RequestMapping("/ntzw")
@Api(value = "南通政务登录相关接口", tags = "南通政务登录相关接口")
public class NtzwLogin extends BaseController {

    @Autowired
    private PlatformEnvironment platformEnvironment;

    @Autowired
    private NtzwConfig ntzwConfig;

    @ApiOperation(value = "南通政务单点登陆", notes = "南通政务单点登陆")
    @GetMapping(value = "/login")
    @ResponseBody
    public Map<String, Object> login(HttpServletRequest request) {
        Map<String,Object> result = new HashMap<>();
        Map<String, Object> filterMap = collectRequestParameters(request);
        logger.info("南通政务单点登陆,参数：{}", filterMap);
        String ticket = request.getParameter("ticket");
        String returnUrl = request.getParameter("returnUrl");
        if (StringUtils.isBlank(returnUrl)) {
            returnUrl = ntzwConfig.getNtReturnUrl();
        }
        logger.info("returnUrl值:{}", returnUrl);
        //ticket = ticket.replace(" ", "+");
        String errorMsg = "";
        String accessToken = "";
        try {
            logger.info("ticket值：{}", ticket);
            JSONObject params = new JSONObject();
            params.put("appid", ntzwConfig.getNtAppId());
            params.put("ticket", ticket);
            // params.put("token", URLEncoder.encode(ticket, "utf-8"));
            if (StringUtils.isBlank(accessToken) && StringUtils.isNotBlank(ticket)) {
                //验证ticket是否有效
                Boolean useSSL = BooleanBaseOpt.castObjectToBoolean(ntzwConfig.getNtSSL(), false);
                HttpClientContext context = HttpClientContext.create();
                CloseableHttpClient httpClient = null;
                if (useSSL) {
                    httpClient = HttpExecutor.createKeepSessionHttpsClient();
                } else {
                    httpClient = HttpExecutor.createKeepSessionHttpClient();
                }
                HttpExecutorContext executorContext = HttpExecutorContext.create(httpClient).context(context);
                String ticketResult = HttpExecutor.jsonPost(executorContext, ntzwConfig.getTicketUrl(), params.toJSONString());
                logger.info("调用验证ticket:{},接口返回信息：{}", params, ticketResult);
                if (StringUtils.isNotEmpty(ticketResult)) {
                    JSONObject ticketJson = JSON.parseObject(ticketResult);
                    if (null != ticketJson) {
                        //获取token信息
                        String token = ticketJson.getString("token");
                        params.remove("ticket");
                        params.put("token",token);
                        String loginUser = HttpExecutor.jsonPost(executorContext, ntzwConfig.getFindUserUrl(), params.toJSONString());
                        logger.info("调用获取用户:{},接口返回信息：{}", params, loginUser);
                        //返回用户转json 对象
                        JSONObject loginUserJson = JSON.parseObject(loginUser);
                        if (null != loginUserJson && StringUtils.isBlank(loginUserJson.getString("errormsg"))) {
                            String loginName = loginUserJson.getString("loginname");
                            String mobile = loginUserJson.getString("mobile");
                            logger.info("loginName:{},mobile:{}", loginName, mobile);
                            CentitUserDetails ud = platformEnvironment.loadUserDetailsByLoginName(loginName);
                            if (null == ud) {
                                ud = platformEnvironment.loadUserDetailsByRegCellPhone(mobile);
                            }
                            if (null != ud) {
                                SecurityContextHolder.getContext().setAuthentication(ud);
                                accessToken = request.getSession().getId();
                                logger.info("用户名：{}登录成功", loginName);
                            } else {
                                String corpJsonStr = HttpExecutor.jsonPost(executorContext, ntzwConfig.getFindCorpUrl(), params.toJSONString());
                                JSONObject corpJson = JSON.parseObject(corpJsonStr);
                                Map<String,Object> data = new HashMap<>();
                                data.put("userInfo",loginUserJson);
                                data.put("unitInfo",corpJson);
                                result.put("status", 300);
                                result.put("data",data);
                                return result;
                            }
                        } else {
                            if (null != loginUserJson) {
                                errorMsg = ticketJson.getString("errormsg");
                            } else {
                                errorMsg = "获取用户接口返回为空！";
                            }
                        }
                    } else {
                        if (null != ticketJson) {
                            errorMsg = ticketJson.getString("errormsg");
                        } else {
                            errorMsg = "南通政务ticket验证接口返回为空！";
                        }
                    }
                } else {
                    errorMsg = "南通政务ticket验证接口返回为空！";
                }
            } else {
                errorMsg = "南通政务ticket为空！";
            }
        } catch (Exception e) {
            logger.error("南通政务单点登录异常：{}", e.getMessage());
            errorMsg = "南通政务单点登录异常:" + e.getMessage();
        }
        if (StringUtils.isNotBlank(errorMsg)) {
//            String errorUrl = "redirect:redirecterror";
//            try {
//                errorUrl = errorUrl + "?msg=" + URLEncoder.encode(errorMsg, "UTF-8");
//            } catch (UnsupportedEncodingException e) {
//                logger.error("URLEncoder异常", e);
//            }
            result.put("status", 500);
            result.put("msg",errorMsg);
        } else{
            result.put("status", 200);
            result.put("msg", "登陆成功");
            Map<String,Object> data = new HashMap<>();
            data.put("accessToken",accessToken);
            result.put("data",data);
        }
        return result;
    }

    /**
     * 用户注册之后更新缓存
     */
    @GetMapping("/updateCache")
    @WrapUpResponseBody
    public ResponseData updateCache(HttpServletRequest request) {
        Map<String, Object> filterMap = collectRequestParameters(request);
        logger.info("用户注册之后更新缓存,参数：{}", filterMap);
        String cacheName = request.getParameter("cacheName");
        CodeRepositoryCache.evictCache(cacheName);
        return ResponseData.makeResponseData("更新成功");
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
