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
import com.centit.support.common.ObjectException;
import com.centit.support.database.utils.PageDesc;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

    @Value("${app.key}")
    private String appKey;


    private String displayImageUrl = "/api/fileserver/fileserver/download/pfile/displayImageUrl.png";
    private String fullImageUrl = "/api/fileserver/fileserver/download/pfile/fullImageUrl.png";

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
        if (appInfoList != null && appInfoList.size() > 0) {
            return ResponseData.makeErrorMessage("版本号已存在，请勿重复保存!");
        }
        if (StringUtils.isNotBlank(appInfo.getFileId())) {
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
        if (StringUtils.isNotBlank(appInfo.getFileId())) {
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
    public AppInfo getAppInfoById(@PathVariable String appId, HttpServletResponse response) {
        return appInfoService.getObjectById(appId);
    }

    @ApiOperation(value = "删除单个移动端版本信息", notes = "删除单个移动端版本信息。")
    @ApiImplicitParam(
        name = "appId", value = "移动端版本代码",
        required = true, paramType = "path", dataType = "String")
    @RequestMapping(value = "/{appId}", method = {RequestMethod.DELETE})
    public void deleteAppInfo(@PathVariable String appId, HttpServletRequest request, HttpServletResponse response) {
        AppInfo appInfo = appInfoService.getObjectById(appId);
        appInfoService.deleteObjectById(appId);
        JsonResultUtils.writeBlankJson(response);

        OperationLogCenter.logDeleteObject(request, optId, appId, OperationLog.P_OPT_LOG_METHOD_D,
            "删除移动端版本信息", appInfo);
    }

    @ApiOperation(value = "获取最新版的移动端版本", notes = "获取最新版的移动端版本。")
    @RequestMapping(value = "/getLastAppInfo/{appType}", method = {RequestMethod.GET})
    @WrapUpResponseBody
    public JSONObject getLastAppInfo(@PathVariable String appType, HttpServletRequest request) {
        Map<String, Object> filter = new HashMap<>();
        filter.put("appType", appType);
        List<AppInfo> appInfoList = appInfoService.listObjects(filter);
        if (appInfoList == null || appInfoList.size() == 0) {
            throw new ObjectException("未获取到版本号!");
        }
        AppInfo appInfo = new AppInfo();
        if (appInfoList != null && appInfoList.size() > 0) {
            appInfo = appInfoList.get(0);
            if (appInfoList.size() > 1) {
                for (int i = 1; i < appInfoList.size(); i++) {
                    AppInfo appNext = appInfoList.get(i);
                    if (compareAppVersion(appInfo.getVersionId(), appNext.getVersionId()) < 0) {
                        appInfo = appNext;
                    }
                }
            }
        }
        if (appType.equals("IOS")) {
            String serverName = "https://" + request.getServerName() + "/" + appKey;
            String url = "itms-services://?action=download-manifest&url=" +
                serverName + "/api/framework/system/appInfo/manifest.plist";
            appInfo.setFileUrl(url);
        }
        JSONObject params = JSONObject.parseObject(JSONObject.toJSONString(appInfo));
        return params;
    }

    @ApiOperation(value = "获取最新版的移动端下载地址", notes = "获取最新版的移动端下载地址。")
    @RequestMapping(value = "/getLastAppUrl", method = {RequestMethod.GET})
    public void getLastAppUrl(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String serverName = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/" + appKey;
        Map<String, Object> filter = new HashMap<>();
        filter.put("appType", "Android");
        List<AppInfo> appInfoList = appInfoService.listObjects(filter);
        if (appInfoList == null || appInfoList.size() == 0) {
            throw new ObjectException("未获取到版本号!");
        }
        AppInfo appInfo = new AppInfo();
        if (appInfoList != null && appInfoList.size() > 0) {
            appInfo = appInfoList.get(0);
            if (appInfoList.size() > 1) {
                for (int i = 1; i < appInfoList.size(); i++) {
                    AppInfo appNext = appInfoList.get(i);
                    if (compareAppVersion(appInfo.getVersionId(), appNext.getVersionId()) < 0) {
                        appInfo = appNext;
                    }
                }
            }
        }
        String url = serverName + "/" + appInfo.getFileUrl();
        response.sendRedirect(url);
    }

    @ApiOperation(value = "获取最新版的IOS下载地址", notes = "获取最新版的IOS下载地址。")
    @RequestMapping(value = "/manifest.plist", method = {RequestMethod.GET})
    public void getLastIOSUrl(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> filter = new HashMap<>();
        filter.put("appType", "IOS");
        List<AppInfo> appInfoList = appInfoService.listObjects(filter);
        if (appInfoList == null || appInfoList.size() == 0) {
            throw new ObjectException("未获取到版本号!");
        }
        AppInfo appInfo = new AppInfo();
        if (appInfoList != null && appInfoList.size() > 0) {
            appInfo = appInfoList.get(0);
            if (appInfoList.size() > 1) {
                for (int i = 1; i < appInfoList.size(); i++) {
                    AppInfo appNext = appInfoList.get(i);
                    if (compareAppVersion(appInfo.getVersionId(), appNext.getVersionId()) < 0) {
                        appInfo = appNext;
                    }
                }
            }
        }
        String serverName = "https://" + request.getServerName() + "/" + appKey;
        String plist = creatPlist(appInfo, serverName);
        response.reset();
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "inline;fileName=ios.plist");
        response.setContentType("application/xml");
        OutputStream outputStream = response.getOutputStream();
        outputStream.write(plist.getBytes("UTF-8"));
        outputStream.close();
    }


    /**
     * 比较APP版本号的大小
     * <p>
     * 1、前者大则返回一个正数
     * 2、后者大返回一个负数
     * 3、相等则返回0
     *
     * @param version1 app版本号
     * @param version2 app版本号
     * @return int
     */
    private Integer compareAppVersion(String version1, String version2) {
        if (version1 == null || version2 == null) {
            throw new RuntimeException("版本号不能为空");
        }
        // 注意此处为正则匹配，不能用.
        String[] versionArray1 = version1.split("\\.");
        String[] versionArray2 = version2.split("\\.");
        int idx = 0;
        // 取数组最小长度值
        int minLength = Math.min(versionArray1.length, versionArray2.length);
        int diff = 0;
        // 先比较长度，再比较字符
        while (idx < minLength
            && (diff = versionArray1[idx].length() - versionArray2[idx].length()) == 0
            && (diff = versionArray1[idx].compareTo(versionArray2[idx])) == 0) {
            ++idx;
        }
        // 如果已经分出大小，则直接返回，如果未分出大小，则再比较位数，有子版本的为大
        diff = (diff != 0) ? diff : versionArray1.length - versionArray2.length;
        return diff;
    }

    private String creatPlist(AppInfo appInfo, String serverName) {
        String pList = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n"
            + "<plist version=\"1.0\">\n" + "<dict>\n"
            + "<key>items</key>\n"
            + "<array>\n"
            + "<dict>\n"
            + "<key>assets</key>\n"
            + "<array>\n"
            + "<dict>\n"
            + "<key>kind</key>\n"
            + "<string>software-package</string>\n"
            + "<key>url</key>\n"
            //你之前所上传的ipa文件路径
            + "<string>" + serverName + "/" + appInfo.getFileUrl() + "</string>\n"
            + "</dict>\n"
            + "<dict>\n"
            + "<key>kind</key>\n"
            + "<string>display-image</string>\n"
            + "<key>url</key>\n"
            //你之前所上传的ipa文件路径
            + "<string>" + serverName + displayImageUrl + "</string>\n"
            + "</dict>\n"
            + "<dict>\n"
            + "<key>kind</key>\n"
            + "<string>full-size-image</string>\n"
            + "<key>url</key>\n"
            //你之前所上传的ipa文件路径
            + "<string>" + serverName + fullImageUrl + "</string>\n"
            + "</dict>\n"
            + "</array>\n"
            + "<key>metadata</key>\n"
            + "<dict>\n"
            + "<key>bundle-identifier</key>\n"
            + "<string>com.centit.checkWorkApp</string>\n"
            + "<key>bundle-version</key>\n"
            + "<string>" + appInfo.getVersionId() + "</string>\n"
            + "<key>kind</key>\n"
            + "<string>software</string>\n"
            + "<key>platform-identifier</key>\n"
            + "<string>com.apple.platform.iphoneos</string>\n"
            + "<key>title</key>\n"
            + "<string>乐扣开发平台</string>\n"
            + "</dict>\n"
            + "</dict>\n"
            + "</array>\n"
            + "</dict>\n"
            + "</plist>";
        return pList;
    }
}
