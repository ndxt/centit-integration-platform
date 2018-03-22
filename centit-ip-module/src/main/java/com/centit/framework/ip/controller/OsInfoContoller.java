package com.centit.framework.ip.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.centit.framework.common.JsonResultUtils;
import com.centit.framework.common.ResponseMapData;
import com.centit.framework.components.OperationLogCenter;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.ip.service.DatabaseInfoManager;
import com.centit.support.algorithm.StringBaseOpt;
import com.centit.support.database.utils.PageDesc;
import com.centit.framework.ip.po.OsInfo;
import com.centit.framework.ip.service.OsInfoManager;
import com.centit.framework.model.basedata.OperationLog;
import com.centit.framework.security.model.CentitUserDetails;
import com.centit.support.json.JsonPropertyUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/sys/os")
public class OsInfoContoller extends  BaseController {

    @Resource
    private OsInfoManager osInfoMag;

    @Resource
    private DatabaseInfoManager databaseInfoMag;

    private String optId = "OS";

    @RequestMapping(method = RequestMethod.GET)
    public void list( PageDesc pageDesc, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> searchColumn = convertSearchColumn(request);

        JSONArray listObjects = osInfoMag.listOsInfoAsJson(searchColumn, pageDesc);

        ResponseMapData resData = new ResponseMapData();
        resData.addResponseData(OBJLIST, listObjects);
        resData.addResponseData(PAGE_DESC, pageDesc);

        JsonResultUtils.writeResponseDataAsJson(resData, response);
    }
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

    @RequestMapping(value = "/{osId}", method = {RequestMethod.PUT})
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
    @RequestMapping(value = "/{osId}", method = {RequestMethod.GET})
    public void getOsInhfo(@PathVariable String osId, HttpServletResponse response) {
        OsInfo osInfo = osInfoMag.getObjectById(osId);

        JsonResultUtils.writeSingleDataJson(osInfo, response,
                JsonPropertyUtils.getExcludePropPreFilter(OsInfo.class, "osInfo"));
    }
    @RequestMapping(value = "/{osId}", method = {RequestMethod.DELETE})
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
