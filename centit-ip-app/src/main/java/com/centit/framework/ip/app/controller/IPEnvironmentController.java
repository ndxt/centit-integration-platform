package com.centit.framework.ip.app.controller;

import com.centit.framework.common.JsonResultUtils;
import com.centit.framework.common.ResponseMapData;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.ip.service.IntegrationEnvironment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/ipenvironment")
public class IPEnvironmentController extends BaseController {

    @Resource
    protected IntegrationEnvironment integrationEnvironment;


    @RequestMapping(value ="/reload/ipenvironment",method = RequestMethod.GET)
    public void reloadIPEnvironment(HttpServletResponse response) {
        if(integrationEnvironment.reloadIPEnvironmen())
            JsonResultUtils.writeSuccessJson(response);
        else
            JsonResultUtils.writeErrorMessageJson("reloadIPEnvironmen failed！", response);
    }

    @RequestMapping(value ="/reload/refreshall",method = RequestMethod.GET)
    public void environmentRefreshAll(HttpServletResponse response) {
        boolean reloadEv = integrationEnvironment.reloadIPEnvironmen();
        if(reloadEv )
            JsonResultUtils.writeSuccessJson(response);
        else
            JsonResultUtils.writeErrorMessageJson("environmentRefreshAll failed！", response);
    }


    @RequestMapping(value ="/osinfos",method = RequestMethod.GET)
    public void listOsInfos(HttpServletResponse response) {
        ResponseMapData resData = new ResponseMapData();
        resData.addResponseData(OBJLIST, integrationEnvironment.listOsInfos());
        JsonResultUtils.writeResponseDataAsJson(resData, response);
    }

    @RequestMapping(value ="/databaseinfos",method = RequestMethod.GET)
    public void listDatabaseInfos(HttpServletResponse response) {
        ResponseMapData resData = new ResponseMapData();
        resData.addResponseData(OBJLIST, integrationEnvironment.listDatabaseInfo());
        JsonResultUtils.writeResponseDataAsJson(resData, response);
    }
}
