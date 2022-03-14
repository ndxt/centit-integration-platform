package com.centit.framework.tenant.controller;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.tea.TeaConverter;
import com.aliyun.tea.TeaModel;
import com.aliyun.tea.TeaPair;
import com.centit.framework.common.ResponseData;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.framework.model.adapter.NotificationCenter;
import com.centit.framework.model.basedata.NoticeMessage;
import com.centit.framework.system.dao.UserInfoDao;
import com.centit.framework.system.po.UserInfo;
import com.centit.support.common.ObjectException;
import com.centit.support.security.AESSecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.aliyun.dysmsapi20170525.models.*;
import com.aliyun.teaopenapi.models.*;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
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

    private static Pattern pattern = Pattern.compile("[0-9]*");

    @ApiOperation(
        value = "验证唯一性",
        notes = "验证唯一性"
    )
    @RequestMapping(value = "/checkOnly", method = RequestMethod.POST)
    @WrapUpResponseBody
    public ResponseData checkOnly(@RequestParam(value = "loginname") String loginname,
                                  HttpServletRequest request) throws Exception{
        UserInfo userInfo = new UserInfo();
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
    public ResponseData getEmailCode(@RequestParam(value = "userCode", required = false) String userCode,
                                     @RequestParam("email") String email,
                                     HttpServletRequest request) {
        UserInfo userInfo = userInfoDao.getUserByRegEmail(email);
        if (userInfo != null) {
            return ResponseData.makeErrorMessage("此邮箱已被使用！");
        }
        return sendEmail(email, email, request);
    }

    @ApiOperation(
        value = "获取手机验证码",
        notes = "获取手机验证码"
    )
    @RequestMapping(value = "/getPhoneCode", method = RequestMethod.POST)
    @ResponseBody
    public SendSmsResponseBody getPhoneCode(@RequestParam(value = "userCode", required = false) String userCode,
                                        @RequestParam("phone") String phone,
                                        HttpServletRequest request) throws Exception {
        if(phone != null && !phone.equals("")){
            UserInfo userInfo = userInfoDao.getUserByRegCellPhone(phone);
            if (userInfo != null) {
                //throw new Exception("此手机号已被使用！");
                Map<String, Object> map = new HashMap<String, Object>();
                Map<String, Object> bodyMap = new HashMap<String, Object>();
                bodyMap.put("Message", "此手机号已被使用");
                bodyMap.put("Code", 500);
                map.put("body", bodyMap);
                return TeaModel.toModel(map, new SendSmsResponse()).getBody();
            }
        }
        SendSmsResponseBody s = sendPhone(phone, phone, userCode, request);
        if(s != null && s.getCode() != null && s.getCode().equals("OK")){
            s.setCode("0");
        }
        return s;
    }

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
            JSONObject json = JSONObject.parseObject(request.getSession().getAttribute(key) + "");
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
            if(userCode != null && !userCode.equals("")){
                UserInfo user = userInfoDao.getUserByCode(userCode);
                if (user != null) {
                    if(email != null && !email.equals("")){
                        user.setRegEmail(email);
                        logger.info("用户:{}修改用户信息邮箱",userCode);
                    }else if(phone != null && !phone.equals("")){
                        user.setRegCellPhone(phone);
                        logger.info("用户:{}修改用户信息手机",userCode);
                    }
                    userInfoDao.updateUser(user);
                }
            }
            request.getSession().removeAttribute(key);
            return ResponseData.makeSuccessResponse();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseData.errorResponse;
    }

    @ApiOperation(
        value = "找回密码(发送验证码 手机/邮箱)",
        notes = "找回密码(发送验证码 手机/邮箱)"
    )
    @RequestMapping(value = "/findPwd", method = RequestMethod.POST)
    @WrapUpResponseBody
    public ResponseData findPwd(@RequestParam(value = "loginname") String loginname,
                                HttpServletRequest request) throws Exception {
        try {
            UserInfo userInfo = new UserInfo();
            if(loginname.indexOf('@')>0){
                userInfo = userInfoDao.getUserByRegEmail(loginname);
                if(userInfo == null){
                    return ResponseData.makeErrorMessage("用户不存在");
                }
                sendEmail(loginname, loginname, request);
            }else{
                userInfo = userInfoDao.getUserByRegCellPhone(loginname);
                if(userInfo == null){
                    return ResponseData.makeErrorMessage("用户不存在");
                }
                SendSmsResponseBody s = sendPhone(loginname, loginname, "", request);
                //System.out.println(s.body);
            }
            return ResponseData.successResponse;
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
            JSONObject json = JSONObject.parseObject(request.getSession().getAttribute(key) + "");
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
            if(email != null && !email.equals("")){
                userInfo = userInfoDao.getUserByRegEmail(email);
            }else if(phone != null && !phone.equals("")){
                userInfo = userInfoDao.getUserByRegCellPhone(phone);
            }
            request.getSession().removeAttribute(key);
            return ResponseData.makeResponseData(userInfo);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseData.errorResponse;
    }


    public ResponseData sendEmail(String email, String key, HttpServletRequest request){
        String verifyCode = String.valueOf(new Random().nextInt(899999) + 100000);
        String message = "您的验证码为:" + verifyCode + "，该码有效期为5分钟，该码只能使用一次!";
        List<String> sendMessageUser = new ArrayList<>();
        sendMessageUser.add(email);
        JSONObject json = new JSONObject();
        json.put("email", email);
        json.put("verifyCode", verifyCode);
        json.put("createTime", System.currentTimeMillis());
        request.getSession().setAttribute(key, json);
        return notificationCenter.sendMessage("system", sendMessageUser,
            NoticeMessage.create().operation("email").method("post").subject("您有新邮件")
                .content(message));
    }

    public SendSmsResponseBody sendPhone(String phone, String key, String userCode, HttpServletRequest request)
            throws Exception{
        String verifyCode = String.valueOf(new Random().nextInt(899999) + 100000);
        JSONObject jSONObject = new JSONObject();
        jSONObject.put("code", verifyCode);
        if(userCode != null && !userCode.equals("")){
            UserInfo userInfo = new UserInfo();
            userInfo = userInfoDao.getUserByCode(userCode);
            if(userInfo != null){
                jSONObject.put("product", "用户"+userInfo.getUserName());
            }else{
                jSONObject.put("product", "用户");
            }
        }else{
            jSONObject.put("product", "用户");
        }
        //+GrP3D07U/aR2WDtm9iTSUeJ0F00X0f75Byebbcw8fc=
        //String accessKeyId = AESSecurityUtils.encryptAndBase64("LTAI5tEa6fT8PoidN8PkQNnN", "0123456789abcdefghijklmnopqrstuvwxyzABCDEF");
        //gqdjhi7JEasb2uiOW/riueAXA4vvOxsgYfmdRbAqwIU=
        //String accessKeySecret = AESSecurityUtils.encryptAndBase64("SeirpGApf75fAow1rT1qVJ7v0zqCRy", "0123456789abcdefghijklmnopqrstuvwxyzABCDEF");
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
        request.getSession().setAttribute(key, json);
        // 复制代码运行请自行打印 API 的返回值
        return client.sendSms(sendSmsRequest).getBody();
    }

    /**
     * 使用AK&SK初始化账号Client
     * @return Client
     * @throws Exception
     */
    public static com.aliyun.dysmsapi20170525.Client createClient() throws Exception {

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

}
