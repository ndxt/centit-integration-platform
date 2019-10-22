package com.centit.framework.ip.controller;

import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.framework.ip.service.UserDirectory;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;

@Controller
@RequestMapping("/datamanager")
@Api(tags= "ldap同步接口",value = "ldap同步接口")
public class SystemDataManagerController  extends BaseController {

    @Resource(name="activeDirectoryUserDirectory")
    @NotNull
    private UserDirectory activeDirectoryUserDirectory;
    @ApiOperation(value="ldap同步",notes="ldap同步")
    @WrapUpResponseBody
    @RequestMapping(value = "/syncuserdirectory/{directory}",
            method = RequestMethod.GET)
    public void syncUserDirectory(@PathVariable String directory) {
        activeDirectoryUserDirectory.synchroniseUserDirectory();
    }
}
