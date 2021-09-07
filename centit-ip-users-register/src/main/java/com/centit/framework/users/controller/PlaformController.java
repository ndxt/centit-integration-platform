package com.centit.framework.users.controller;

import com.centit.framework.common.ResponseData;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.security.SecurityContextUtils;
import com.centit.framework.security.model.CentitUserDetails;
import com.centit.framework.users.config.AppConfig;
import com.centit.framework.users.service.TokenService;
import com.centit.support.algorithm.StringBaseOpt;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

/**
 * @author zfg
 */
@Controller
@RequestMapping("/plat")
public class PlaformController extends BaseController {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AppConfig appConfig;

    @Autowired
    protected PlatformEnvironment platformEnvironment;

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    @WrapUpResponseBody
    public ResponseData test(@RequestParam("code") String code, @RequestParam("state") String state,
                             HttpServletResponse response) throws IOException {
        //URLEncoder.encode("", "UTF-8");
        return ResponseData.makeResponseData("dingding");
    }

    @RequestMapping(value = "/getCacheToken", method = RequestMethod.GET)
    @WrapUpResponseBody
    public ResponseData test(HttpServletRequest request, HttpServletResponse response) {
        //URLEncoder.encode("", "UTF-8");
        //String accessToken = tokenService.getTokenFromCache();
        String accessToken = tokenService.getFromDb();
        //String accessToken = tokenService.getAccessToken().getResult();
        return ResponseData.makeResponseData(accessToken);
    }

    /**
     * 这个方法是个内部通讯的客户端程序使用的，客户端程序通过用户代码（注意不是用户名）和密码登录，这个密码建议随机生成
     *
     * @param request request
     * @return ResponseData
     */
    @ApiOperation(value = "内部通讯的客户端程序使用接口", notes = "这个方法是个内部通讯的客户端程序使用的，客户端程序通过用户代码（注意不是用户名）和密码登录，这个密码建议随机生成")
    @RequestMapping(value = "/loginasclient", method = RequestMethod.POST)
    @WrapUpResponseBody
    public ResponseData loginAsClient(HttpServletRequest request) {
        Map<String, Object> formValue = BaseController.collectRequestParameters(request);

        String userCode = StringBaseOpt.objectToString(formValue.get("userCode"));
        String userPwd = StringBaseOpt.objectToString(formValue.get("password"));
        boolean bo = platformEnvironment.checkUserPassword(userCode, userPwd);
        if (!bo) {
            return ResponseData.makeErrorMessage("用户名和密码不匹配。");
        }
        CentitUserDetails ud = platformEnvironment.loadUserDetailsByUserCode(userCode);
        SecurityContextHolder.getContext().setAuthentication(ud);
        return ResponseData.makeResponseData(
            SecurityContextUtils.SecurityContextTokenName, request.getSession().getId());
    }

}
