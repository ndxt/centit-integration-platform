package com.centit.product.ip.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.centit.framework.core.common.JsonResultUtils;
import com.centit.framework.core.controller.BaseController;
import com.centit.product.ip.service.UserDirectory;

@Controller
@RequestMapping("/datamanager")
public class SystemDataManagerController  extends BaseController {

	@Resource(name="activeDirectoryUserDirectory")
	@NotNull
	private UserDirectory activeDirectoryUserDirectory;
	
	@RequestMapping(value = "/syncuserdirectory/{directory}", 
			method = RequestMethod.GET)
	public void syncUserDirectory(@PathVariable String directory, 
			HttpServletResponse response) {
		activeDirectoryUserDirectory.synchroniseUserDirectory();
		JsonResultUtils.writeSuccessJson(response);
	}
}
