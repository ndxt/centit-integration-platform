package com.centit.framework.users.controller;

import com.centit.framework.common.ResponseData;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.framework.system.dao.UnitInfoDao;
import com.centit.framework.users.config.JsmotSyncConfig;
import com.centit.framework.users.dto.JsmotUnitDTO;
import com.centit.framework.users.dto.JsmotUserDTO;
import com.centit.framework.users.dto.SmsDTO;
import com.centit.framework.users.service.JsmotSyncService;
import com.centit.framework.users.service.TokenService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zfg
 */
@Controller
@RequestMapping("/jsmot")
@Api(value = "交通云对接相关接口", tags = "交通云对接相关接口")
public class JsmotSyncController {

    @Autowired
    private JsmotSyncConfig jsmotSyncConfig;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private JsmotSyncService jsmotSyncService;

    @Autowired
    private UnitInfoDao unitInfoDao;

    private String getAccessToken() {
        String accessToken = "";
        ResponseData accessTokenData = tokenService.getJsmotAccessToken();
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
        ResponseData accessTokenData = tokenService.getSmsAccessToken();
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
}
