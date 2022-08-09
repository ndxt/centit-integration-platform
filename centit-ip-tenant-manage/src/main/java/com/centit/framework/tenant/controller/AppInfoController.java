package com.centit.framework.tenant.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.framework.common.JsonResultUtils;
import com.centit.framework.common.ResponseData;
import com.centit.framework.common.WebOptUtils;
import com.centit.framework.components.OperationLogCenter;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpContentType;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.framework.core.dao.PageQueryResult;
import com.centit.framework.model.basedata.OperationLog;
import com.centit.framework.tenant.po.AppInfo;
import com.centit.framework.tenant.service.AppInfoService;
import com.centit.support.algorithm.CollectionsOpt;
import com.centit.support.database.utils.PageDesc;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author tian_y
 */
@Controller
@RequestMapping("/appInfo")
@Api(tags = {"移动端版本管理接口"}, value = "移动端版本管理接口")
public class AppInfoController extends BaseController {

    @Autowired
    private AppInfoService appInfoService;

    private String optId = "APPINFO";

    @ApiOperation(value = "移动端版本列表", notes = "移动端版本列表")
    @ApiImplicitParam(
        name = "pageDesc", value = "json格式，分页对象信息",
        paramType = "body", dataTypeClass = PageDesc.class)
    @RequestMapping(method = RequestMethod.GET)
    @WrapUpResponseBody(contentType = WrapUpContentType.MAP_DICT)
    public PageQueryResult<Object> list(PageDesc pageDesc, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> searchColumn = BaseController.collectRequestParameters(request);
        JSONArray listObjects = appInfoService.listObjectsAsJson(searchColumn, pageDesc);
        return PageQueryResult.createJSONArrayResult(listObjects, pageDesc, AppInfo.class);
    }

    @ApiOperation(value = "新增移动端版本信息", notes = "新增移动端版本信息。")
    @ApiImplicitParam(
        name = "appInfo", value = "json格式，移动端版本信息", required = true,
        paramType = "body", dataTypeClass = AppInfo.class)
    @RequestMapping(method = {RequestMethod.POST})
    @WrapUpResponseBody
    public ResponseData saveAppInfo(@RequestBody AppInfo appInfo, HttpServletRequest request, HttpServletResponse response) {
        appInfo.setCreator(WebOptUtils.getCurrentUserCode(request));
         Map<String, Object> filter = new HashMap<>();
        filter.put("versionId", appInfo.getVersionId());
        filter.put("appType", appInfo.getAppType());
        List<AppInfo> appInfoList = appInfoService.listObjectsByProperties(filter);
        if(appInfoList != null && appInfoList.size() > 0){
            return ResponseData.makeErrorMessage("版本号已存在，请勿重复保存!");
        }
        if(StringUtils.isNotBlank(appInfo.getFileId())){
            String fileUrl = "api/fileserver/fileserver/download/pfile/" + appInfo.getFileId();
            appInfo.setFileUrl(fileUrl);
        }
        appInfoService.saveNewObject(appInfo);
        OperationLogCenter.logNewObject(request, optId, appInfo.getId(), OperationLog.P_OPT_LOG_METHOD_C,
            "新增移动端版本信息", appInfo);
        return ResponseData.makeResponseData(appInfo.getId());
    }

    @ApiOperation(value = "修改移动端版本信息", notes = "修改移动端版本信息")
    @ApiImplicitParams({
        @ApiImplicitParam(
            name = "appId", value = "主键Id",
            required = true, paramType = "path", dataType = "String"),
        @ApiImplicitParam(
            name = "appInfo", value = "json格式，移动端版本信息", required = true,
            paramType = "body", dataTypeClass = AppInfo.class)
    })
    @RequestMapping(value = "/{appId}", method = {RequestMethod.PUT})
    public void updateAppInfoById(@PathVariable String appId, @RequestBody AppInfo appInfo,
                                   HttpServletRequest request, HttpServletResponse response) {
        AppInfo temp = appInfoService.getObjectById(appId);
        AppInfo oldApp = new AppInfo();
        BeanUtils.copyProperties(temp, oldApp);
        if(StringUtils.isNotBlank(appInfo.getFileId())){
            String fileUrl = "api/fileserver/fileserver/download/pfile/" + appInfo.getFileId();
            appInfo.setFileUrl(fileUrl);
        }
        appInfoService.updateObject(appInfo);

        JsonResultUtils.writeBlankJson(response);

        OperationLogCenter.logUpdateObject(request, optId, appId, OperationLog.P_OPT_LOG_METHOD_U,
            "修改移动端版本信息", appInfo, oldApp);
    }

    @ApiOperation(value = "获取单个移动端版本信息", notes = "获取单个移动端版本信息。")
    @ApiImplicitParam(
        name = "appId", value = "移动端版本代码",
        required = true, paramType = "path", dataType = "String")
    @RequestMapping(value = "/{appId}", method = {RequestMethod.GET})
    @WrapUpResponseBody
    public AppInfo getAppInfoById(@PathVariable String appId, HttpServletResponse response){
        return appInfoService.getObjectById(appId);
    }

    @ApiOperation(value = "删除单个移动端版本信息", notes = "删除单个移动端版本信息。")
    @ApiImplicitParam(
        name = "appId", value = "移动端版本代码",
        required = true, paramType = "path", dataType = "String")
    @RequestMapping(value = "/{appId}", method = {RequestMethod.DELETE})
    public void deleteAppInfo(@PathVariable String appId, HttpServletRequest request, HttpServletResponse response){
        AppInfo appInfo = appInfoService.getObjectById(appId);
        appInfoService.deleteObjectById(appId);
        JsonResultUtils.writeBlankJson(response);

        OperationLogCenter.logDeleteObject(request, optId, appId, OperationLog.P_OPT_LOG_METHOD_D,
            "删除移动端版本信息", appInfo);
    }

    @ApiOperation(value = "获取最新版的移动端版本", notes = "获取最新版的移动端版本。")
    @RequestMapping(value = "/getLastAppInfo/{appType}", method = {RequestMethod.GET})
    @WrapUpResponseBody
    public JSONObject getLastAppInfo(@PathVariable String appType){
        return appInfoService.getLastAppInfo(appType);
    }

}
