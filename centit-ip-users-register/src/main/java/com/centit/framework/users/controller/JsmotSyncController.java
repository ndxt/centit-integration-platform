package com.centit.framework.users.controller;

import com.centit.framework.common.ResponseData;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.framework.system.dao.UnitInfoDao;
import com.centit.framework.system.po.UnitInfo;
import com.centit.framework.users.config.JsmotSyncConfig;
import com.centit.framework.users.dto.JsmotUnitDTO;
import com.centit.framework.users.dto.JsmotUserDTO;
import com.centit.framework.users.po.SocialDeptAuth;
import com.centit.framework.users.service.JsmotSyncService;
import com.centit.framework.users.service.SocialDeptAuthService;
import com.centit.framework.users.service.TokenService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author zfg
 */
@Controller
@RequestMapping("/uipmp")
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

    @Autowired
    private SocialDeptAuthService socialDeptAuthService;

    private String getAccessToken() {
        String accessToken = "";
        ResponseData accessTokenData = tokenService.getJsmotAccessToken();
        if (accessTokenData.getCode() != 0) {
            return "";
        }
        accessToken = accessTokenData.getData().toString();
        return accessToken;
    }

    @ApiOperation(value = "同步交通云创建用户", notes = "同步交通云创建用户。")
    @PostMapping(value = "/usercreate")
    @WrapUpResponseBody
    public ResponseData userCreate(JsmotUserDTO userInfo, HttpServletRequest request) {
        String accessToken = getAccessToken();
        if (StringUtils.isBlank(accessToken)) {
            return ResponseData.makeErrorMessage("获取交通云access_token失败");
        }
        String unitCode = userInfo.getPrimaryUnit();
        SocialDeptAuth socialDeptAuth = socialDeptAuthService.getObjectById(unitCode);
        if (null == socialDeptAuth) {
            UnitInfo unitInfo = unitInfoDao.getObjectById(unitCode);
            JsmotUnitDTO jsmotUnitDTO = new JsmotUnitDTO();
            jsmotUnitDTO.setUnitCode(unitCode);
            jsmotUnitDTO.setParentUnit(unitInfo.getParentUnit());
            jsmotUnitDTO.setUnitName(unitInfo.getUnitName());
            jsmotSyncService.unitCreate(accessToken, jsmotUnitDTO);
        }
        return jsmotSyncService.userCreate(accessToken, userInfo);
    }

    @ApiOperation(value = "同步交通云创建机构部门", notes = "同步交通云创建机构部门。")
    @PostMapping(value = "/unitcreate")
    @WrapUpResponseBody
    public ResponseData unitCreate(JsmotUnitDTO unitInfo, HttpServletRequest request) {
        String accessToken = getAccessToken();
        if (StringUtils.isBlank(accessToken)) {
            return ResponseData.makeErrorMessage("获取交通云access_token失败");
        }
        return jsmotSyncService.unitCreate(accessToken, unitInfo);
    }

    @ApiOperation(value = "根据部门deptId获取交通云部门详情", notes = "根据部门deptId获取交通云部门详情。")
    @GetMapping(value = "/{deptId}")
    @WrapUpResponseBody
    public ResponseData getUnitInfo(@PathVariable String deptId, HttpServletResponse response) {
        String accessToken = getAccessToken();
        if (StringUtils.isBlank(accessToken)) {
            return ResponseData.makeErrorMessage("获取交通云access_token失败");
        }
        return jsmotSyncService.getUnitInfo(accessToken, deptId);
    }
}
