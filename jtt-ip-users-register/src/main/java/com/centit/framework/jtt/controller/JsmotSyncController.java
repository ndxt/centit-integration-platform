package com.centit.framework.jtt.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.centit.framework.common.ResponseData;
import com.centit.framework.components.CodeRepositoryCache;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.framework.jtt.config.JsmotSyncConfig;
import com.centit.framework.jtt.dto.JsmotUnitDTO;
import com.centit.framework.jtt.dto.JsmotUserDTO;
import com.centit.framework.jtt.dto.SmsDTO;
import com.centit.framework.jtt.service.JsmotSyncService;
import com.centit.framework.jtt.service.JttAccessTokenService;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.security.model.CentitUserDetails;
import com.centit.framework.security.model.JsonCentitUserDetails;
import com.centit.framework.system.dao.UserInfoDao;
import com.centit.framework.system.po.UserInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author zfg
 */
@Controller
@RequestMapping("/jsmot")
@Api(value = "交通云对接相关接口", tags = "交通云对接相关接口")
public class JsmotSyncController extends BaseController {

    @Autowired
    private JsmotSyncConfig jsmotSyncConfig;

    @Autowired
    private JttAccessTokenService jttAccessTokenService;

    @Autowired
    private JsmotSyncService jsmotSyncService;

    @Autowired
    private UserInfoDao userInfoDao;

    @Autowired
    private PlatformEnvironment platformEnvironment;

    @Autowired(required = false)
    private RedisTemplate<String, JSONObject> redisTemplate;

    private String getAccessToken() {
        String accessToken = "";
        ResponseData accessTokenData = jttAccessTokenService.getJsmotAccessToken();
        if (accessTokenData.getCode() != 0) {
            return "";
        }
        accessToken = accessTokenData.getData().toString();
        return accessToken;
    }

    @ApiOperation(value = "交通云新增从业人员", notes = "交通云新增从业人员")
    @PostMapping(value = "/usercreate")
    @WrapUpResponseBody
    public ResponseData userCreate(@RequestBody JsmotUserDTO userInfo) {
        String accessToken = getAccessToken();
        if (StringUtils.isBlank(accessToken)) {
            return ResponseData.makeErrorMessage("获取交通云accessToken失败");
        }
        return jsmotSyncService.userCreate(accessToken, userInfo);
    }

    @ApiOperation(value = "交通云新增从业企业", notes = "交通云新增从业企业")
    @PostMapping(value = "/unitcreate")
    @WrapUpResponseBody
    public ResponseData unitCreate(@RequestBody JsmotUnitDTO unitInfo) {
        String accessToken = getAccessToken();
        if (StringUtils.isBlank(accessToken)) {
            return ResponseData.makeErrorMessage("获取交通云accessToken失败");
        }
        return jsmotSyncService.unitCreate(accessToken, unitInfo);
    }

    @ApiOperation(value = "根据用户userCode获取从业人员详情", notes = "根据用户userCode获取从业人员详情。")
    @GetMapping(value = "/{userCode}/{flag}")
    @WrapUpResponseBody
    public ResponseData getCYUserDetail(@PathVariable String userCode, @PathVariable String flag) {
        String accessToken = getAccessToken();
        if (StringUtils.isBlank(accessToken)) {
            return ResponseData.makeErrorMessage("获取交通云accessToken失败");
        }
        return jsmotSyncService.getCYUserDetail(accessToken, userCode, flag);
    }

    @ApiOperation(value = "根据企业id获取从业企业详情", notes = "根据企业id获取从业企业详情。")
    @GetMapping(value = "/{id}/{flag}")
    @WrapUpResponseBody
    public ResponseData getUnitInfo(@PathVariable String id, @PathVariable String flag) {
        String accessToken = getAccessToken();
        if (StringUtils.isBlank(accessToken)) {
            return ResponseData.makeErrorMessage("获取交通云accessToken失败");
        }
        return jsmotSyncService.getCYCorpInfo(accessToken, id, flag);
    }

    private String getSmsAccessToken() {
        String accessToken = "";
        ResponseData accessTokenData = jttAccessTokenService.getSmsAccessToken();
        if (accessTokenData.getCode() != 0) {
            return "";
        }
        accessToken = accessTokenData.getData().toString();
        return accessToken;
    }

    @ApiOperation(value = "交通厅短信发送", notes = "交通厅短信发送")
    @PostMapping(value = "/sendsms")
    @WrapUpResponseBody
    public ResponseData sendSms(@RequestBody SmsDTO smsDTO, HttpServletRequest request) {
        String accessToken = getSmsAccessToken();
        if (StringUtils.isBlank(accessToken)) {
            return ResponseData.makeErrorMessage("获取短信平台accessToken失败");
        }
        return jsmotSyncService.sendSms(accessToken, smsDTO);
    }

    @ApiOperation(value = "注册获取手机验证码", notes = "注册获取手机验证码")
    @PostMapping(value = "/getphonecode")
    @ResponseBody
    public Map<String, Object> getPhoneCode(@RequestParam(value = "userCode", required = false) String userCode,
                                            @RequestParam("phone") String phone, HttpServletRequest request,
                                            HttpServletResponse response) {
        JSONObject jsonObject = redisTemplate.boundValueOps(phone).get();
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> bodyMap = new HashMap<>();
        if(jsonObject != null){
            Long createTime = jsonObject.getLong("createTime");
            if ((System.currentTimeMillis() - createTime) < 1000 * 60) {
                //验证发送时间，防止多次发送验证码
                bodyMap.put("Message", "验证码发送时间小于1分钟，请稍后再试。");
                bodyMap.put("Code", 500);
                map.put("body", bodyMap);
                return bodyMap;
            }else{
                //重新发送则删除之前存入redis中的数据
                redisTemplate.delete(phone);
            }
        }
        if (StringUtils.isNotBlank(phone)) {
            UserInfo userInfo = userInfoDao.getUserByRegCellPhone(phone);
            if (userInfo != null) {
                bodyMap.put("Message", "此手机号已被使用");
                bodyMap.put("Code", 500);
                map.put("body", bodyMap);
                return bodyMap;
            }
        }
        Map<String, Object> result = new HashMap<>();
        String accessToken = getSmsAccessToken();
        if (StringUtils.isBlank(accessToken)) {
            result.put("code", "-1");
            result.put("message", "获取短信平台accessToken失败");
            return result;
        }
        ResponseData sendData = sendPhone(accessToken, phone, userCode, request);
        if (sendData.getCode() != 0) {
            result.put("code", "-1");
            result.put("message", sendData.getMessage());
            return result;
        }
        result.put("code", sendData.getCode());
        result.put("message", sendData.getMessage());
        result.put("data", sendData.getData());
        result.put("x-auth-token", request.getSession().getId());
        return result;
    }

    @ApiOperation(value = "验证码校验和用户信息更新", notes = "验证码校验和用户信息更新")
    @PostMapping(value = "/checkcode")
    @WrapUpResponseBody
    public ResponseData checkCode(@RequestParam(value = "userCode", required = false) String userCode,
                                  @RequestParam("key") String key, @RequestParam("code") String code,
                                  HttpServletRequest request) {
        try {
            if (StringUtils.isBlank(code)) {
                return ResponseData.makeErrorMessage(500, "请输入验证码！");
            }
            //从Redis中获取验证码和部分信息
            JSONObject json = redisTemplate.boundValueOps(key).get();
            if (json == null) {
                json = JSON.parseObject(request.getHeader("verifyCode"));
            }
            if (json == null) {
                return ResponseData.makeErrorMessage(500, "未发送验证码！");
            }
            String verifyCode = json.getString("verifyCode");
            Long createTime = json.getLong("createTime");
            String email = json.getString("email");
            String phone = json.getString("phone");
            if (!verifyCode.equals(code)) {
                return ResponseData.makeErrorMessage(500, "验证码错误！");
            }
            if ((System.currentTimeMillis() - createTime) > 1000 * 60 * 5) {
                return ResponseData.makeErrorMessage(500, "验证码已过期！");
            }
            if (StringUtils.isNotBlank(userCode)) {
                UserInfo user = userInfoDao.getUserByCode(userCode);
                if (null != user) {
                    if (StringUtils.isNotBlank(email)) {
                        user.setRegEmail(email);
                        logger.info("用户:{}修改用户信息邮箱", userCode);
                    } else if (StringUtils.isNotBlank(phone)) {
                        user.setRegCellPhone(phone);
                        logger.info("用户:{}修改用户信息手机", userCode);
                    }
                    userInfoDao.updateUser(user);
                    //刷新缓存中的人员信息
                    reloadAuthentication(user.getUserCode());
                    //人员新增更新成功后刷新缓存
                    CodeRepositoryCache.evictCache("UserInfo");
                }
            }
            redisTemplate.delete(key);
            return ResponseData.makeSuccessResponse();
        } catch (Exception e) {
            logger.error("验证码校验和用户信息更新异常:{}", e.getMessage());
            return ResponseData.errorResponse;
        }
    }

    private ResponseData sendPhone(String accessToken, String phone, String userCode, HttpServletRequest request) {
        String verifyCode = String.valueOf(new Random().nextInt(899999) + 100000);
        JSONObject jSONObject = new JSONObject();
        jSONObject.put("code", verifyCode);
        if (StringUtils.isNotBlank(userCode)) {
            UserInfo userInfo = userInfoDao.getUserByCode(userCode);
            if (null != userInfo) {
                jSONObject.put("product", "用户" + userInfo.getUserName());
            } else {
                jSONObject.put("product", "用户");
            }
        } else {
            jSONObject.put("product", "用户");
        }
        SmsDTO smsDTO = new SmsDTO();
        JSONObject json = new JSONObject();
        json.put("phone", phone);
        json.put("verifyCode", verifyCode);
        json.put("createTime", System.currentTimeMillis());
        String conten = "【江苏交通】验证码" + verifyCode + "，有效期5分钟";
        smsDTO.setContent(conten);
        smsDTO.setMobile(phone);
        ResponseData result = jsmotSyncService.sendSms(accessToken, smsDTO);
        if(result.getCode() == 0){
            redisTemplate.boundValueOps(phone).set(json);
        }
        return result;
    }

    private void reloadAuthentication(String userCode) {
        CentitUserDetails centitUserDetails = platformEnvironment.loadUserDetailsByUserCode(userCode);
        centitUserDetails.setLoginIp(getUserIp());
        SecurityContextHolder.getContext().setAuthentication(centitUserDetails);
    }

    private String getUserIp() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof JsonCentitUserDetails) {
            JsonCentitUserDetails userDetails = (JsonCentitUserDetails) principal;
            return userDetails.getLoginIp();
        }
        return "";
    }
}
