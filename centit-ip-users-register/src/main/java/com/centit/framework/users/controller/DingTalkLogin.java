package com.centit.framework.users.controller;

import com.alibaba.fastjson.JSONObject;
import com.centit.framework.common.ResponseData;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.security.model.CentitUserDetails;
import com.centit.framework.system.po.UnitInfo;
import com.centit.framework.system.po.UserInfo;
import com.centit.framework.users.config.AppConfig;
import com.centit.framework.users.config.UrlConstant;
import com.centit.framework.users.po.UserPlat;
import com.centit.framework.users.service.DingTalkLoginService;
import com.centit.framework.users.service.TokenService;
import com.centit.framework.users.service.UserPlatService;
import com.taobao.api.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zfg
 */
@Controller
@RequestMapping("/ddlogin")
@Api(value = "钉钉平台登录相关接口", tags = "钉钉平台登录相关接口")
public class DingTalkLogin extends BaseController {

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private DingTalkLoginService dingTalkLoginService;

    @Autowired
    private UserPlatService userPlatService;

    @Autowired
    protected PlatformEnvironment platformEnvironment;

    @ApiOperation(value = "钉钉二维码登录", notes = "钉钉二维码登录。")
    @GetMapping(value = "/qrconnect")
    public void qrConnect(HttpServletResponse response) throws IOException {
        String authorizeUrl = UrlConstant.URL_GET_QRCONNECT + "?appid=" + appConfig.getAppKey() + "&response_type=code" +
            "&scope=snsapi_login&redirect_uri=" + appConfig.getRedirectUri();
        response.sendRedirect(authorizeUrl);
    }

    @ApiOperation(value = "钉钉账号登录", notes = "钉钉账号登录。")
    @GetMapping(value = "/snsauthorize")
    public void snsAuthorize(HttpServletResponse response) throws IOException {
        String authorizeUrl = UrlConstant.URL_GET_SNSCONNECT + "?appid=" + appConfig.getAppKey() + "&response_type=code" +
            "&scope=snsapi_login&redirect_uri=" + appConfig.getRedirectUri();
        response.sendRedirect(authorizeUrl);
    }

    /**
     * 登录回调获取授权用户的个人信息
     *
     * @param code  授权码
     * @param state
     * @return
     * @throws ApiException
     */
    @GetMapping(value = "/getUserInfo")
    @WrapUpResponseBody
    public ResponseData getUserInfo(@RequestParam("code") String code, @RequestParam("state") String state,
                                    HttpServletRequest request) throws ApiException {
        //获取access_token
        String accessToken = "";
        ResponseData accessTokenData = tokenService.getAccessToken();
        if (accessTokenData.getCode() != 0) {
            return ResponseData.makeErrorMessage(accessTokenData.getCode(), accessTokenData.getMessage());
        }
        accessToken = accessTokenData.getData().toString();

        if (StringUtils.isBlank(accessToken)) {
            return ResponseData.makeErrorMessage("获取钉钉access_token失败");
        }

        //获取用户unionid
        ResponseData unionIdData = dingTalkLoginService.getUserByCode(code);
        if (unionIdData.getCode() != 0) {
            return ResponseData.makeErrorMessage(unionIdData.getCode(), unionIdData.getMessage());
        }
        String unionid = unionIdData.getData().toString();

        //根据unionid获取userid
        ResponseData userIdData = dingTalkLoginService.getUserByUnionId(accessToken, unionid);
        if (userIdData.getCode() != 0) {
            return ResponseData.makeErrorMessage(userIdData.getCode(), userIdData.getMessage());
        }
        String userId = userIdData.getData().toString();

        //根据userId获取用户详情
        ResponseData userInfoData = dingTalkLoginService.getUserInfo(accessToken, userId);
        if (userInfoData.getCode() != 0) {
            return ResponseData.makeErrorMessage(userIdData.getCode(), userIdData.getMessage());
        }
        JSONObject jsonObject = JSONObject.parseObject(userInfoData.getData().toString());
        String regPhone = "";
        if (null != jsonObject) {
            JSONObject userObject = JSONObject.parseObject(jsonObject.getString("result"));
            if (null != userObject) {
                regPhone = userObject.getString("mobile");
            }
        }
        Map<String, Object> resultMap = new HashMap<>();
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("userId", userId);
        paramsMap.put("corpId", appConfig.getCorpId());
        paramsMap.put("appKey", appConfig.getAppKey());
        paramsMap.put("appSecret", appConfig.getAppSecret());
        UserPlat userPlat = userPlatService.getUserPlatByProperties(paramsMap);
        if (null != userPlat) {
            userPlat.setUnionId(unionid);
            userPlatService.updateUserPlat(userPlat);
            CentitUserDetails ud = platformEnvironment.loadUserDetailsByUserCode(userPlat.getUserCode());
            SecurityContextHolder.getContext().setAuthentication(ud);
            resultMap.put("userInfo", ud);
            resultMap.put("userCode", userPlat.getUserCode());
            resultMap.put("accessToken", request.getSession().getId());
        } else if (userPlat == null && StringUtils.isNotBlank(regPhone)) {
            //通过手机号获取用户信息,保存至userPlat表，下次登录直接查询userPlat
            CentitUserDetails userDetails = platformEnvironment.loadUserDetailsByRegCellPhone(regPhone);
            if (null != userDetails) {
                SecurityContextHolder.getContext().setAuthentication(userDetails);
                UserPlat newUser = new UserPlat();
                newUser.setUserCode(userDetails.getUserCode());
                newUser.setCorpId(appConfig.getCorpId());
                newUser.setAppKey(appConfig.getAppKey());
                newUser.setAppSecret(appConfig.getAppSecret());
                newUser.setUnionId(unionid);
                newUser.setUserId(userId);
                userPlatService.saveUserPlat(newUser);
                resultMap.put("userInfo", userDetails);
                resultMap.put("userCode", userDetails.getUserCode());
                resultMap.put("accessToken", request.getSession().getId());
            }
        }
        return ResponseData.makeResponseData(resultMap);
    }

    private String getAccessToken() {
        String accessToken = "";
        ResponseData accessTokenData = tokenService.getAccessToken();
        if (accessTokenData.getCode() != 0) {
            return "";
        }
        accessToken = accessTokenData.getData().toString();
        return accessToken;
    }

    @ApiOperation(value = "同步钉钉创建用户", notes = "同步钉钉创建用户。")
    @PostMapping(value = "/usercreate")
    @WrapUpResponseBody
    public ResponseData userCreate(UserInfo userInfo, HttpServletRequest request) {
        String accessToken = getAccessToken();
        if (StringUtils.isBlank(accessToken)) {
            return ResponseData.makeErrorMessage("获取钉钉access_token失败");
        }
        return dingTalkLoginService.userCreate(accessToken, userInfo);
    }

    @ApiOperation(value = "同步钉钉创建机构部门", notes = "同步钉钉创建机构部门。")
    @PostMapping(value = "/unitcreate")
    @WrapUpResponseBody
    public ResponseData unitCreate(UnitInfo unitInfo, HttpServletRequest request) {
        String accessToken = getAccessToken();
        if (StringUtils.isBlank(accessToken)) {
            return ResponseData.makeErrorMessage("获取钉钉access_token失败");
        }
        return dingTalkLoginService.unitCreate(accessToken, unitInfo);
    }

    @ApiOperation(value = "根据部门deptId获取钉钉部门详情", notes = "根据部门deptId获取钉钉部门详情。")
    @GetMapping(value = "/{deptId}")
    @WrapUpResponseBody
    public ResponseData getUnitInfo(@PathVariable String deptId, HttpServletResponse response) {
        String accessToken = getAccessToken();
        if (StringUtils.isBlank(accessToken)) {
            return ResponseData.makeErrorMessage("获取钉钉access_token失败");
        }
        return dingTalkLoginService.getUnitInfo(accessToken, deptId);
    }

}
