package com.centit.framework.users.controller;

import com.centit.framework.core.controller.BaseController;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.security.model.CentitUserDetails;
import com.centit.framework.users.config.WxAppConfig;
import com.centit.framework.users.po.UserPlat;
import com.centit.framework.users.service.UserPlatService;
import com.centit.support.common.ObjectException;
import io.swagger.annotations.Api;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信登录controller
 * @author tian_y
 */
@Controller
@RequestMapping("/wxlogin")
@Api(value = "微信登录相关接口", tags = "微信登录相关接口")
public class WeChatLogin extends BaseController {

    @Autowired
    private WxMpService wxOpenService;

    @Autowired
    private WxAppConfig wxAppConfig;

    @Autowired
    private UserPlatService userPlatService;

    @Autowired
    private PlatformEnvironment platformEnvironment;

    @GetMapping(value = "/login")
    public ModelAndView qrAuthorize(HttpServletResponse response) {
        return new ModelAndView("/sys/wxLogin");
    }

    /**
     * 通过扫码获取微信用户信息
     * @param code
     * @return
     */
    private WxMpUser getWxUser(String code){
        WxMpUser wxMpUser = new WxMpUser();
        try {
            WxMpOAuth2AccessToken wxMpOAuth2AccessToken = this.getAccessToken(code);
            wxMpUser = wxOpenService.oauth2getUserInfo(wxMpOAuth2AccessToken, null);
        } catch (WxErrorException e) {
            e.printStackTrace();
        }
        return wxMpUser;
    }

    /**
     * 获取access_token
     * @param code
     * @return
     */
    private WxMpOAuth2AccessToken getAccessToken(String code){
        WxMpOAuth2AccessToken wxMpOAuth2AccessToken = new WxMpOAuth2AccessToken();
        try {
            //通过code获取access_token
            wxMpOAuth2AccessToken = wxOpenService.oauth2getAccessToken(code);
        } catch (WxErrorException e) {
            e.printStackTrace();
        }
        return wxMpOAuth2AccessToken;
    }

    /**
     * 获取用户信息
     * @param code
     * @param state
     * @param request
     * @return
     */
    @GetMapping("/qrUserInfo")
    public String qrUserInfo(@RequestParam("code") String code,
                             @RequestParam("state") String state,
                             @RequestParam("returnUrl") String returnUrl,
                             HttpServletRequest request) {
        WxMpUser wxMpUser = this.getWxUser(code);
        //从token中获取openid
        String openId = wxMpUser.getOpenId();
        String unionId = wxMpUser.getUnionId();
        logger.info("openid={}", openId);
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("userId", openId);
        paramsMap.put("appKey", wxAppConfig.getAppID());
        paramsMap.put("unionId", unionId);
        UserPlat userPlat = userPlatService.getUserPlatByProperties(paramsMap);
        if(userPlat == null){
            throw new ObjectException("500", "未绑定微信，暂时无法登录！");
        }else {
            if (userPlat != null) {
                CentitUserDetails ud = platformEnvironment.loadUserDetailsByUserCode(userPlat.getUserCode());
                SecurityContextHolder.getContext().setAuthentication(ud);
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
        }
        return "redirect:" + returnUrl;
    }

    /**
     * 绑定微信用户信息
     * @param code
     * @param request
     */
    @RequestMapping("/bindUserInfo")
    //@WrapUpResponseBody
    public String bindUserInfo(@RequestParam("code") String code,
                               @RequestParam("userCode") String userCode,
                               @RequestParam("returnUrl") String returnUrl,
                               HttpServletRequest request) {
        WxMpUser wxMpUser = this.getWxUser(code);
        //从token中获取openid(授权用户唯一标识)
        String openId = wxMpUser.getOpenId();
        String unionId = wxMpUser.getUnionId();
        String weChatName = wxMpUser.getNickname();
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("userCode", userCode);
        paramsMap.put("appKey", wxAppConfig.getAppID());
        UserPlat userPlat = userPlatService.getUserPlatByProperties(paramsMap);
        if(userPlat != null){
            throw new ObjectException("500", "微信账号已绑定，请勿重复绑定！");
        }else{
            CentitUserDetails userDetails = platformEnvironment.loadUserDetailsByUserCode(userCode);
            if (null != userDetails) {
                UserPlat newUser = new UserPlat();
                newUser.setUnionId(unionId);
                newUser.setUserId(openId);
                newUser.setUserCode(userDetails.getUserCode());
                newUser.setPlatId("2");
                newUser.setCorpId("");
                newUser.setAppKey(wxAppConfig.getAppID());
                newUser.setAppSecret(wxAppConfig.getAppSecret());
                newUser.setWeChatName(weChatName);
                userPlatService.saveUserPlat(newUser);
            }
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
