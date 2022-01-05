package com.centit.framework.users.controller;

import com.centit.framework.users.config.AppConfig;
import com.centit.framework.users.config.UrlConstant;
import com.centit.framework.users.config.WxAppConfig;
import com.centit.support.common.ObjectException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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

    private static final String WECHAT_LOGIN = "wx";

    private static final String WECHAT_BIND = "wxBind";

    private static final String DING_LOGIN = "ding";

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
                            HttpServletResponse response) throws IOException {
        String url = "";
        String authorizeUrl = "";
        if(WECHAT_LOGIN.equals(type)){
            //微信登录
            url = wxAppConfig.getRedirectLoginUri();
            authorizeUrl = wxOpenService.buildQrConnectUrl(url, WxConsts.QRCONNECT_SCOPE_SNSAPI_LOGIN, "");
        }else if(WECHAT_BIND.equals(type)){
            //微信账号绑定
            if(userCode == null || "".equals(userCode)){
                throw new ObjectException("缺少参数userCode;");
            }
            url = wxAppConfig.getRedirectBindUri() + "?userCode=" + userCode;
            authorizeUrl = wxOpenService.buildQrConnectUrl(url, WxConsts.QRCONNECT_SCOPE_SNSAPI_LOGIN, "");
        }else if(DING_LOGIN.equals(type)){
            //钉钉登陆页面
            authorizeUrl = UrlConstant.URL_GET_QRCONNECT + "?appid=" + appConfig.getAppKey() + "&response_type=code" +
                "&scope=snsapi_login&redirect_uri=" + appConfig.getRedirectUri();
        }
        response.sendRedirect(authorizeUrl);
    }

}
