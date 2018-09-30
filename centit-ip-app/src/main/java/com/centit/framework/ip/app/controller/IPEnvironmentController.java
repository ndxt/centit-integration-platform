package com.centit.framework.ip.app.controller;

import com.centit.framework.common.JsonResultUtils;
import com.centit.framework.common.ResponseMapData;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.ip.service.IntegrationEnvironment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/ipenvironment")
public class IPEnvironmentController extends BaseController {

    @Resource
    protected IntegrationEnvironment integrationEnvironment;

    @RequestMapping(value ="/test",method = RequestMethod.GET)
    public void test(
            HttpServletRequest request,HttpServletResponse response) {
		String testSessionString = new String("hello");
		request.getSession().setAttribute("test", (String) testSessionString);
		JsonResultUtils.writeSingleDataJson("test=zouwuyang", response);
    }



    @RequestMapping(value ="/reload/ipenvironment",method = RequestMethod.GET)
    public void reloadIPEnvironment(
            HttpServletRequest request,HttpServletResponse response) {
		if(integrationEnvironment.reloadIPEnvironmen())
			JsonResultUtils.writeSuccessJson(response);
		else
			JsonResultUtils.writeErrorMessageJson("reloadIPEnvironmen failed！", response);
    }

    @RequestMapping(value ="/reload/refreshall",method = RequestMethod.GET)
    public void environmentRefreshAll(
			HttpServletRequest request,HttpServletResponse response) {
		boolean reloadEv = integrationEnvironment.reloadIPEnvironmen();
		if(reloadEv )
			JsonResultUtils.writeSuccessJson(response);
		else
			JsonResultUtils.writeErrorMessageJson("environmentRefreshAll failed！", response);
    }


    @RequestMapping(value ="/osinfos",method = RequestMethod.GET)
    public void listOsInfos(
            HttpServletRequest request,HttpServletResponse response) {

		ResponseMapData resData = new ResponseMapData();
        resData.addResponseData(OBJLIST, integrationEnvironment.listOsInfos());
		JsonResultUtils.writeResponseDataAsJson(resData, response);
    }

    @RequestMapping(value ="/databaseinfos",method = RequestMethod.GET)
    public void listDatabaseInfos(
            HttpServletRequest request,HttpServletResponse response) {

		ResponseMapData resData = new ResponseMapData();
        resData.addResponseData(OBJLIST, integrationEnvironment.listDatabaseInfo());
		JsonResultUtils.writeResponseDataAsJson(resData, response);
    }
}
