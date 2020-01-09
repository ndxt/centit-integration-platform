package com.centit.framework.ip.controller;

import com.alibaba.fastjson.JSONArray;
import com.centit.framework.common.JsonResultUtils;
import com.centit.framework.common.WebOptUtils;
import com.centit.framework.components.OperationLogCenter;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.framework.core.dao.PageQueryResult;
import com.centit.framework.ip.po.DatabaseInfo;
import com.centit.framework.ip.service.DatabaseInfoManager;
import com.centit.framework.model.basedata.OperationLog;
import com.centit.support.database.utils.DataSourceDescription;
import com.centit.support.database.utils.PageDesc;
import com.centit.support.json.JsonPropertyUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Map;

@Controller
@RequestMapping("/sys/database")
@Api(tags= "数据库维护接口",value = "数据库维护接口")
public class DatabaseInfoController extends BaseController {

    @Autowired
    private DatabaseInfoManager databaseInfoMag;

    private String optId = "DATABASE";

    /**
     * 数据库维护接口
     * @param pageDesc 分页对象信息
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     */
    @ApiOperation(value="所有数据库列表信息",notes="所有数据库列表信息。增加databaseCode")
    @ApiImplicitParam(
        name = "pageDesc", value="json格式，分页对象信息",
        paramType = "body", dataTypeClass = PageDesc.class)
    @RequestMapping(method = RequestMethod.GET)
    @WrapUpResponseBody
    public PageQueryResult<Object> list( PageDesc pageDesc, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> searchColumn = BaseController.collectRequestParameters(request);

//        JSONArray listObjects = databaseInfoMag.queryDatabaseAsJson(
//                StringBaseOpt.objectToString(searchColumn.get("databaseName")), pageDesc);
        JSONArray listObjects = databaseInfoMag.listObjectsAsJson(searchColumn, pageDesc);

        return PageQueryResult.createJSONArrayResult(listObjects,pageDesc,DatabaseInfo.class);
    }

    /**
     * 新增数据库信息
     * @param databaseinfo 数据库对象信息
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     */
    @ApiOperation(value="新增数据库信息",notes="新增数据库信息。")
    @ApiImplicitParam(
        name = "databaseinfo", value="json格式，数据库对象信息", required = true,
        paramType = "body", dataTypeClass = DatabaseInfo.class)
    @RequestMapping(method = {RequestMethod.POST})
    public void saveDatabaseInfo(@Valid DatabaseInfo databaseinfo,
                                 HttpServletRequest request, HttpServletResponse response) {

        DatabaseInfo temp = databaseInfoMag.getObjectById(databaseInfoMag.getNextKey());
        if (temp!=null){
            JsonResultUtils.writeErrorMessageJson("该数据库标识已存在", response);
            return;
        }
        //加密
        databaseinfo.setPassword(databaseinfo.getPassword());
        databaseinfo.setCreated(WebOptUtils.getCurrentUserCode(request));
        databaseInfoMag.saveNewObject(databaseinfo);

        JsonResultUtils.writeBlankJson(response);

        /**********************log************************/
        OperationLogCenter.logNewObject(request, optId, databaseinfo.getDatabaseCode(), OperationLog.P_OPT_LOG_METHOD_C,
                "新增数据库", databaseinfo);
        /**********************log************************/
    }

    /**
     * 连接测试
     * @param databaseInfo 数据库信息
     * @param response 返回
     */
    @ApiOperation(value="数据库连接测试",notes="数据库连接测试。")
    @ApiImplicitParam(
        name = "databaseinfo", value="json格式，数据库对象信息", required = true,
        paramType = "body", dataTypeClass = DatabaseInfo.class)
    @RequestMapping(value = "/testConnect", method = {RequestMethod.POST})
    public void testConnect(@Valid DatabaseInfo databaseInfo, HttpServletResponse response) {


        boolean result = DataSourceDescription.testConntect(new DataSourceDescription(
                databaseInfo.getDatabaseUrl(),
                databaseInfo.getUsername(),
                databaseInfo.getClearPassword()));

        if (result) {
            JsonResultUtils.writeSingleDataJson("连接测试成功",response);
        } else {
            JsonResultUtils.writeErrorMessageJson("数据库连接测试失败！", response);
        }

    }

    /**
     * 修改数据库信息
     * @param databaseCode 数据库代码
     * @param databaseinfo 修改数据库信息
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     */
    @ApiOperation(value="修改数据库信息",notes="修改数据库信息。")
    @ApiImplicitParams({
        @ApiImplicitParam(
            name = "databaseCode", value="数据库代码",
            required = true, paramType = "path", dataType= "String"),
        @ApiImplicitParam(
            name = "databaseinfo", value="json格式，数据库对象信息", required = true,
            paramType = "body", dataTypeClass = DatabaseInfo.class)
    })
    @RequestMapping(value = "/{databaseCode}", method = {RequestMethod.PUT})
    public void updateDatabaseInfo(@PathVariable String databaseCode, @Valid DatabaseInfo databaseinfo,
                                   HttpServletRequest request, HttpServletResponse response) {
        DatabaseInfo temp = databaseInfoMag.getObjectById(databaseCode);
        if (!databaseinfo.getPassword().equals(temp.getPassword())){
            databaseinfo.setPassword(databaseinfo.getPassword());
        }

        DatabaseInfo oldValue = new DatabaseInfo();
        BeanUtils.copyProperties(temp, oldValue);
        databaseInfoMag.mergeObject(databaseinfo);

        JsonResultUtils.writeBlankJson(response);

        /**********************log****************************/
        OperationLogCenter.logUpdateObject(request, optId, databaseCode, OperationLog.P_OPT_LOG_METHOD_U,
                "更新数据库信息", databaseinfo, oldValue);
        /**********************log****************************/
    }

    /**
     * 获取单个数据库信息
     * @param databaseCode 数据库代码
     * @param response HttpServletResponse
     */
    @ApiOperation(value="获取单个数据库信息",notes="获取单个数据库信息。")
    @ApiImplicitParam(
        name = "databaseCode", value="数据库代码",
        required = true, paramType = "path", dataType= "String")
    @RequestMapping(value = "/{databaseCode}", method = {RequestMethod.GET})
    public void getDatabaseInhfo(@PathVariable String databaseCode, HttpServletResponse response) {
        DatabaseInfo databaseInfo = databaseInfoMag.getObjectById(databaseCode);

        JsonResultUtils.writeSingleDataJson(databaseInfo, response,
                JsonPropertyUtils.getExcludePropPreFilter(DatabaseInfo.class, "databaseInfo"));
    }

    /**
     * 删除单个数据库信息
     * @param databaseCode 数据库代码
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     */
    @ApiOperation(value="删除单个数据库信息",notes="删除单个数据库信息。")
    @ApiImplicitParam(
        name = "databaseCode", value="数据库代码",
        required = true, paramType = "path", dataType= "String")
    @RequestMapping(value = "/{databaseCode}", method = {RequestMethod.DELETE})
    public void deleteDatabase(@PathVariable String databaseCode,
                               HttpServletRequest request, HttpServletResponse response) {
        DatabaseInfo databaseInfo = databaseInfoMag.getObjectById(databaseCode);
        databaseInfoMag.deleteObjectById(databaseCode);

        JsonResultUtils.writeBlankJson(response);

        /******************************log********************************/
        OperationLogCenter.logDeleteObject(request, optId, databaseCode, OperationLog.P_OPT_LOG_METHOD_D,
                "删除数据库", databaseInfo);
        /******************************log********************************/
    }

}
