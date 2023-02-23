package com.centit.framework.tenant.controller;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponseBody;
import com.aliyun.teaopenapi.models.Config;
import com.centit.framework.common.ResponseData;
import com.centit.framework.components.CodeRepositoryCache;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.framework.model.adapter.NotificationCenter;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.model.basedata.NoticeMessage;
import com.centit.framework.security.model.CentitUserDetails;
import com.centit.framework.security.model.JsonCentitUserDetails;
import com.centit.framework.system.dao.UserInfoDao;
import com.centit.framework.system.po.UserInfo;
import com.centit.support.security.AESSecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.InetAddress;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 验证码接口
 * @author tian_y
 */
@Controller
@RequestMapping("/vateCode")
@Api(value = "邮箱、手机号验证码接口", tags = "邮箱、手机号验证码接口")
public class VateCodeController extends BaseController {

    @Autowired
    private NotificationCenter notificationCenter;

    @Autowired
    private UserInfoDao userInfoDao;

    @Autowired
    private PlatformEnvironment platformEnvironment;

    @Autowired(required = false)
    private RedisTemplate<String, JSONObject> redisTemplate;

    private static Pattern pattern = Pattern.compile("[0-9]*");

    @ApiOperation(
        value = "验证唯一性",
        notes = "验证唯一性"
    )
    @RequestMapping(value = "/checkOnly", method = RequestMethod.POST)
    @WrapUpResponseBody
    public ResponseData checkOnly(@RequestParam(value = "loginname") String loginname,
                                  HttpServletRequest request) throws Exception{
        UserInfo userInfo;
        String msg = "";
        Matcher isNum = pattern.matcher(loginname);
        if(loginname.indexOf('@')>0){
            msg = "邮件";
            userInfo = userInfoDao.getUserByRegEmail(loginname);
        }else if(loginname.length() == 11 && isNum.matches()){
            msg = "手机号";
            userInfo = userInfoDao.getUserByRegCellPhone(loginname);
        }else{
            msg = "账号";
            userInfo = userInfoDao.getUserByLoginName(loginname);
        }
        if(userInfo != null){
            return ResponseData.makeErrorMessage("此" + msg + "已被使用！");
        }
        return ResponseData.successResponse;
    }

    @ApiOperation(
        value = "获取Email验证码",
        notes = "获取Email验证码"
    )
    @RequestMapping(value = "/getEmailCode", method = RequestMethod.POST)
    @WrapUpResponseBody
    public ResponseData getEmailCode(@RequestParam("email") String email,
                                     HttpServletRequest request) {
        JSONObject jsonObject = redisTemplate.boundValueOps(email).get();
        Map<String, Object> bodyMap = new HashMap<>();
        if(jsonObject != null){
            Long createTime = jsonObject.getLong("createTime");
            if ((System.currentTimeMillis() - createTime) < 1000 * 60) {
                bodyMap.put("Message", "验证码发送时间小于1分钟，请稍后再试。");
                bodyMap.put("Code", 500);
                return ResponseData.makeResponseData(bodyMap);
            }else{
                //重新发送则删除之前存入redis中的数据
                redisTemplate.delete(email);
            }
        }
        UserInfo userInfo = userInfoDao.getUserByRegEmail(email);
        if (userInfo != null) {
            return ResponseData.makeErrorMessage("此邮箱已被使用！");
        }
        return sendEmail(email, request);
    }

    @ApiOperation(
        value = "获取手机验证码",
        notes = "获取手机验证码"
    )
    @RequestMapping(value = "/getPhoneCode", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> getPhoneCode(@RequestParam(value = "userCode", required = false) String userCode,
                                            @RequestParam("phone") String phone,
                                            HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> result = new HashMap<>();
        JSONObject jsonObject = redisTemplate.boundValueOps(phone).get();
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> bodyMap = new HashMap<>();
        if(jsonObject != null){
            Long createTime = jsonObject.getLong("createTime");
            if ((System.currentTimeMillis() - createTime) < 1000 * 60) {
                bodyMap.put("Message", "验证码发送时间小于1分钟，请稍后再试。");
                bodyMap.put("Code", 500);
                map.put("body", bodyMap);
                return bodyMap;
            }else{
                //重新发送则删除之前存入redis中的数据
                redisTemplate.delete(phone);
            }
        }
        if(phone != null && !phone.equals("")){
            UserInfo userInfo = userInfoDao.getUserByRegCellPhone(phone);
            if (userInfo != null) {
                bodyMap.put("Message", "此手机号已被使用");
                bodyMap.put("Code", 500);
                map.put("body", bodyMap);
                return bodyMap;
            }
        }
        SendSmsResponseBody s = sendPhone(phone, userCode, request, result);
        if(s != null && s.getCode() != null && s.getCode().equals("OK")){
            s.setCode("0");
        }
        result.put("code", s.getCode());
        result.put("message", s.getMessage());
        result.put("x-auth-token", request.getSession().getId());
        return result;
    }

    /**
     * 校验验证码并且更新用户信息
     * @param userCode 用户编码
     * @param key 手机号或者邮箱
     * @param code 验证码
     * @param request
     * @return
     */
    @ApiOperation(
        value = "校验和更新",
        notes = "校验和更新"
    )
    @RequestMapping(value = "/checkCode", method = RequestMethod.POST)
    @WrapUpResponseBody
    public ResponseData checkCode(@RequestParam(value = "userCode", required=false) String userCode,
                                       @RequestParam("key") String key,
                                       @RequestParam("code") String code,
                                       HttpServletRequest request){
        try {
            if (code == null) {
                return ResponseData.makeErrorMessage(500, "请输入验证码！");
            }
            //从Redis中获取验证码和部分信息
            JSONObject json = redisTemplate.boundValueOps(key).get();
            if(json == null){
                json = JSONObject.parseObject(request.getHeader("verifyCode"));
            }
            if(json == null){
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
            if(StringUtils.isNotBlank(userCode)){
                UserInfo user = userInfoDao.getUserByCode(userCode);
                if (user != null) {
                    if(StringUtils.isNotBlank(email)){
                        user.setRegEmail(email);
                        logger.info("用户:{}修改用户信息邮箱",userCode);
                    }else if(StringUtils.isNotBlank(phone)){
                        user.setRegCellPhone(phone);
                        logger.info("用户:{}修改用户信息手机",userCode);
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
        }catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseData.errorResponse;
    }

    /**
     *
     * @param loginname  手机号或者邮箱
     * @param request
     * @return
     * @throws Exception
     */
    @ApiOperation(
        value = "找回密码(发送验证码 手机/邮箱)",
        notes = "找回密码(发送验证码 手机/邮箱)"
    )
    @RequestMapping(value = "/findPwd", method = RequestMethod.POST)
    @WrapUpResponseBody
    public ResponseData findPwd(@RequestParam(value = "loginname") String loginname,
                                HttpServletRequest request) throws Exception {
        Map<String, Object> result = new HashMap<>();
        try {
            UserInfo userInfo;
            if(loginname.indexOf('@')>0){
                userInfo = userInfoDao.getUserByRegEmail(loginname);
                if(userInfo == null){
                    return ResponseData.makeErrorMessage("用户不存在");
                }
                sendEmail(loginname, request);
            }else{
                userInfo = userInfoDao.getUserByRegCellPhone(loginname);
                if(userInfo == null){
                    return ResponseData.makeErrorMessage("用户不存在");
                }
                sendPhone(loginname, "", request, result);
            }
            result.put("x-auth-token", request.getSession().getId());
            return ResponseData.makeResponseData(result);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseData.errorResponse;
        }


    }

    @ApiOperation(
        value = "校验并返回用户信息",
        notes = "校验并返回用户信息"
    )
    @RequestMapping(value = "/checkCodeUser", method = RequestMethod.POST)
    @WrapUpResponseBody
    public ResponseData checkCodeUser(@RequestParam("key") String key,
                                       @RequestParam("code") String code,
                                       HttpServletRequest request){
        try {
            if (code == null) {
                return ResponseData.makeErrorMessage(500, "请输入验证码！");
            }
            //从Redis中获取验证码和部分信息
            JSONObject json = redisTemplate.boundValueOps(key).get();
            if(json == null){
                json = JSONObject.parseObject(request.getHeader("verifyCode"));
            }
            if(json == null){
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
            UserInfo userInfo = new UserInfo();
            if(StringUtils.isNotBlank(email)){
                userInfo = userInfoDao.getUserByRegEmail(email);
            }else if(StringUtils.isNotBlank(phone)){
                userInfo = userInfoDao.getUserByRegCellPhone(phone);
            }
            redisTemplate.delete(key);
            return ResponseData.makeResponseData(userInfo);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseData.errorResponse;
    }


    public ResponseData sendEmail(String email, HttpServletRequest request){
        String verifyCode = String.valueOf(new Random().nextInt(899999) + 100000);
        String message = "您的验证码为:" + verifyCode + "，该码有效期为5分钟，该码只能使用一次!";
        List<String> sendMessageUser = new ArrayList<>();
        sendMessageUser.add(email);
        JSONObject json = new JSONObject();
        json.put("email", email);
        json.put("verifyCode", verifyCode);
        json.put("createTime", System.currentTimeMillis());
        ResponseData result = notificationCenter.sendMessage("system", sendMessageUser,
            NoticeMessage.create().operation("email").method("post").subject("您有新邮件")
                .content(message));
        if(result.getCode() == 0){
            //发送成功则将JSON保存到session和Redis中
            redisTemplate.boundValueOps(email).set(json);
        }
        return result;
    }

    public SendSmsResponseBody sendPhone(String phone, String userCode, HttpServletRequest request, Map<String, Object> map)
            throws Exception{
        String verifyCode = String.valueOf(new Random().nextInt(899999) + 100000);
        JSONObject jSONObject = new JSONObject();
        jSONObject.put("code", verifyCode);
        if(userCode != null && !userCode.equals("")){
            UserInfo userInfo = userInfoDao.getUserByCode(userCode);
            if(userInfo != null){
                jSONObject.put("product", "用户"+userInfo.getUserName());
            }else{
                jSONObject.put("product", "用户");
            }
        }else{
            jSONObject.put("product", "用户");
        }
        com.aliyun.dysmsapi20170525.Client client = VateCodeController.createClient();
        SendSmsRequest sendSmsRequest = new SendSmsRequest()
            .setSignName("身份验证")
            .setTemplateCode("SMS_65920066")
            .setPhoneNumbers(phone)
            .setTemplateParam(jSONObject.toString());
        JSONObject json = new JSONObject();
        json.put("phone", phone);
        json.put("verifyCode", verifyCode);
        json.put("createTime", System.currentTimeMillis());
        json.put("IP", InetAddress.getLocalHost().getHostAddress());
        // 复制代码运行请自行打印 API 的返回值
        SendSmsResponseBody result = client.sendSms(sendSmsRequest).getBody();
        if(result.getCode().equals("OK")){
            //发送成功
            //将验证码存入到Redis中
            redisTemplate.boundValueOps(phone).set(json);
        }
        return result;
    }

    /**
     * 使用AK&SK初始化账号Client
     * @return Client
     * @throws Exception 异常
     */
    private static com.aliyun.dysmsapi20170525.Client createClient() throws Exception {

        String accessKeyId = "+GrP3D07U/aR2WDtm9iTSUeJ0F00X0f75Byebbcw8fc=";
        String accessKeySecret = "gqdjhi7JEasb2uiOW/riueAXA4vvOxsgYfmdRbAqwIU=";
        Config config = new Config()
            // 您的AccessKey ID
            .setAccessKeyId(AESSecurityUtils.decryptBase64String(accessKeyId, "0123456789abcdefghijklmnopqrstuvwxyzABCDEF"))
            // 您的AccessKey Secret
            .setAccessKeySecret(AESSecurityUtils.decryptBase64String(accessKeySecret, "0123456789abcdefghijklmnopqrstuvwxyzABCDEF"));
        // 访问的域名
        config.endpoint = "dysmsapi.aliyuncs.com";
        return new com.aliyun.dysmsapi20170525.Client(config);
    }

    private void reloadAuthentication(String userCode) {
        CentitUserDetails centitUserDetails = platformEnvironment.loadUserDetailsByUserCode(userCode);
        centitUserDetails.setLoginIp(getUserIp());
        SecurityContextHolder.getContext().setAuthentication(centitUserDetails);
    }
    /**
     * 获取用户ip地址
     * @return
     */
    private String getUserIp(){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(principal instanceof JsonCentitUserDetails){
            JsonCentitUserDetails userDetails = (JsonCentitUserDetails) principal;
            return userDetails.getLoginIp();
        }
        return "";
    }

}
