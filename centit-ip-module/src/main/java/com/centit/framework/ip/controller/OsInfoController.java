package com.centit.framework.ip.controller;

import com.alibaba.fastjson.JSONArray;
import com.centit.framework.common.JsonResultUtils;
import com.centit.framework.common.ResponseMapData;
import com.centit.framework.components.OperationLogCenter;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.ip.po.DatabaseInfo;
import com.centit.framework.ip.po.OsInfo;
import com.centit.framework.ip.service.DatabaseInfoManager;
import com.centit.framework.ip.service.OsInfoManager;
import com.centit.framework.model.basedata.OperationLog;
import com.centit.support.database.utils.PageDesc;
import com.centit.support.json.JsonPropertyUtils;
import com.centit.support.network.HttpExecutor;
import com.centit.support.network.HttpExecutorContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/sys/os")
@Api(tags= "业务系统护接口",value = "业务系统护接口")
public class OsInfoController extends  BaseController {

    @Resource
    private OsInfoManager osInfoMag;

    @Resource
    private DatabaseInfoManager databaseInfoMag;

    private String optId = "OS";
    private String refreshUrl = "/system/environment/reload/refreshall";

    /**
     * 刷新单个系统数据
     * @param osInfo 系统对象
     * @param response HttpServletResponse
     */
    @ApiOperation(value="刷新单个系统数据",notes="刷新单个系统数据。")
    @ApiImplicitParam(
        name = "osInfo", value="json格式，业务系统对象信息",
        paramType = "body", dataTypeClass = OsInfo.class)
    @RequestMapping(value = "/data/refresh/single" ,method = {RequestMethod.POST})
    public void refreshSingle(@RequestBody OsInfo osInfo, HttpServletResponse response){

        if(osInfo == null){
            return;
        }
        //TODO 陈志强，请将这部分业务逻辑代码 迁移到 OsInfoManagerImpl 类中
        boolean flag = true;
        try (CloseableHttpClient httpClient = HttpExecutor.createHttpClient()) {
            HttpExecutor.simpleGet(HttpExecutorContext.create(httpClient),osInfo.getOsUrl() + refreshUrl);
        }catch (IOException e){
            e.printStackTrace();
            flag = false;
        }
        JsonResultUtils.writeSingleDataJson(flag, response);
    }

    /**
     * 刷新查询的所有系统的数据
     * @param response HttpServletResponse
     */
    @ApiOperation(value="刷新所有系统的数据",notes="刷新查询的所有系统的数据。")
    @RequestMapping(value = "/data/refresh/all" ,method = {RequestMethod.GET})
    public void refreshAll(HttpServletResponse response){
        List<OsInfo> osInfos = osInfoMag.listObjects(new HashMap<>());
        if(osInfos.isEmpty()){
            return;
        }

        //TODO 陈志强，请将这部分业务逻辑代码 迁移到 OsInfoManagerImpl 类中
        boolean flag = true;
        for(OsInfo osInfo : osInfos){
            try (CloseableHttpClient httpClient = HttpExecutor.createHttpClient()) {
                HttpExecutor.simpleGet(HttpExecutorContext.create(httpClient),osInfo.getOsUrl() + refreshUrl);
            }catch (IOException e){
                e.printStackTrace();
                flag = false;
                break;
            }
        }
        JsonResultUtils.writeSingleDataJson(flag, response);
    }

    /**
     * 查询所有的业务系统信息
     * @param pageDesc 分页对象
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     */
    @ApiOperation(value="查询所有的业务系统信息",notes="查询所有的业务系统信息。")
    @RequestMapping(method = RequestMethod.GET)
    @ApiImplicitParam(
        name = "pageDesc", value="json格式，分页对象信息",
        paramType = "body", dataTypeClass = PageDesc.class)
    public void list( PageDesc pageDesc, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> searchColumn = convertSearchColumn(request);

        JSONArray listObjects = osInfoMag.listOsInfoAsJson(searchColumn, pageDesc);

        ResponseMapData resData = new ResponseMapData();
        resData.addResponseData(OBJLIST, listObjects);
        resData.addResponseData(PAGE_DESC, pageDesc);

        JsonResultUtils.writeResponseDataAsJson(resData, response);
    }

    /**
     * 添加业务系统
     * @param osinfo 业务系统对象
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     */
    @ApiOperation(value="添加业务系统",notes="添加业务系统。")
    @ApiImplicitParam(
        name = "osInfo", value="json格式，业务系统对象信息",
        paramType = "body", dataTypeClass = OsInfo.class)
    @RequestMapping(method = {RequestMethod.POST})
    public void saveOsInfo(@Valid OsInfo osinfo,HttpServletRequest request, HttpServletResponse response) {
        if (osinfo == null) {
            JsonResultUtils.writeErrorMessageJson("对象不能为空", response);
            return;
        }
        osinfo.setCreated(super.getLoginUserCode(request));
        osinfo.setCreateTime(new Date());
        osInfoMag.saveNewObject(osinfo);

        JsonResultUtils.writeBlankJson(response);

        /**********************log***********************/
        OperationLogCenter.logNewObject(request, optId, osinfo.getOsId(), OperationLog.P_OPT_LOG_METHOD_C,
                "新增业务系统", osinfo);
        /**********************log***********************/
    }

    /**
     * 更新业务系统信息
     * @param osId 业务系统id
     * @param osinfo 业务系统对象
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     */
    @RequestMapping(value = "/{osId}", method = {RequestMethod.PUT})
    @ApiOperation(value="更新业务系统信息",notes="更新业务系统信息。")
    @ApiImplicitParams({
        @ApiImplicitParam(
            name = "osId", value="业务系统id",
            required = true, paramType = "path", dataType= "String"),
        @ApiImplicitParam(
            name = "osinfo", value="json格式，业务系统对象信息", required = true,
            paramType = "body", dataTypeClass = OsInfo.class)
    })
    public void updateOsInfo(@PathVariable String osId, @Valid OsInfo osinfo,
                             HttpServletRequest request, HttpServletResponse response) {

        OsInfo dbOsInfo = osInfoMag.getObjectById(osId);
        OsInfo oldValue = new OsInfo();
        BeanUtils.copyProperties(dbOsInfo, oldValue);
        osInfoMag.mergeObject(osinfo);
        JsonResultUtils.writeBlankJson(response);

        /**********************log*********************/
        OperationLogCenter.logUpdateObject(request, optId, osId, OperationLog.P_OPT_LOG_METHOD_U,
                "更新业务系统信息", osinfo, oldValue);
        /**********************log*********************/
    }

    /**
     * 根据ID查询单个业务系统信息
     * @param osId 业务系统ID
     * @param response HttpServletResponse
     */
    @RequestMapping(value = "/{osId}", method = {RequestMethod.GET})
    @ApiOperation(value="根据ID查询单个业务系统信息",notes="根据ID查询单个业务系统信息。")
    @ApiImplicitParam(
        name = "osId", value="业务系统id",
        required = true, paramType = "path", dataType= "String")
    public void getOsInhfo(@PathVariable String osId, HttpServletResponse response) {
        OsInfo osInfo = osInfoMag.getObjectById(osId);

        JsonResultUtils.writeSingleDataJson(osInfo, response,
                JsonPropertyUtils.getExcludePropPreFilter(OsInfo.class, "osInfo"));
    }

    /**
     * 根据ID删除单个业务系统信息
     * @param osId 业务系统ID
     * @param response HttpServletResponse
     */
    @RequestMapping(value = "/{osId}", method = {RequestMethod.DELETE})
    @ApiOperation(value="根据ID删除单个业务系统信息",notes="根据ID删除单个业务系统信息。")
    @ApiImplicitParam(
        name = "osId", value="业务系统id",
        required = true, paramType = "path", dataType= "String")
    public void deleteOsInfo(@PathVariable String osId,
                             HttpServletRequest request, HttpServletResponse response) {

        OsInfo dbOsInfo = osInfoMag.getObjectById(osId);

        if (databaseInfoMag.listDatabaseByOsId(osId).size() > 0) {
            JsonResultUtils.writeErrorMessageJson("该业务系统被集成数据库关联，不能删除！", response);
            return;
        }

        osInfoMag.deleteObjectById(osId);

        JsonResultUtils.writeBlankJson(response);

        /********************log***********************/

        OperationLogCenter.logDeleteObject(request, optId, osId, OperationLog.P_OPT_LOG_METHOD_D,
                "删除业务系统", dbOsInfo);
        /********************log***********************/
    }
}
