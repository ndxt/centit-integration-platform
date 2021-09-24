package com.centit.framework.tenan.util;

import com.alibaba.fastjson.JSONObject;
import com.centit.framework.security.model.CentitUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserUtils {

    private UserUtils() {
    }

    /**
     * 从SecurityContext中获取当前登录人的用户信息
     *
     * @return
     */
    public static JSONObject getUserInfoFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        if (principal instanceof CentitUserDetails) {
            return ((CentitUserDetails) principal).getUserInfo();
        }
        return null;
    }

    /**
     * 从SecurityContext中获取当前登录人的userCode
     *
     * @return
     */
    public static String getUserCodeFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        if (principal instanceof CentitUserDetails) {
            return ((CentitUserDetails) principal).getUserCode();
        }
        return null;
    }

}
