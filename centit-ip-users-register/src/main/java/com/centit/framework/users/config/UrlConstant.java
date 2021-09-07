package com.centit.framework.users.config;

/**
 * 钉钉开放接口网关常量
 */
public class UrlConstant {
    private static final String HOST = "https://oapi.dingtalk.com";

    public static final String URL_GET_QRCONNECT= HOST + "/connect/qrconnect";

    public static final String URL_GET_SNSCONNECT= HOST + "/connect/oauth2/sns_authorize";

    /**
     * 获取access_token url
     */
    public static final String URL_GET_TOKEN = HOST + "/gettoken";

    public static final String URL_GET_TOKEN_NEW = HOST + "/service/get_corp_token";

    /**
     * 获取jsapi_ticket url
     */
    public static final String URL_GET_JSTICKET = HOST + "/get_jsapi_ticket";

    /**
     * 通过免登授权码获取用户信息 url
     */
    public static final String URL_GET_USER_INFO = HOST + "/user/getuserinfo";

    /**
     * 根据用户id获取用户详情 url
     */
    public static final String URL_USER_GET = HOST + "/user/get";

    /**
     * 获取部门列表 url
     */
    public static final String URL_DEPARTMENT_LIST = HOST + "/department/list";

    /**
     * 获取部门用户 url
     */
    public static final String URL_USER_SIMPLELIST = HOST + "/user/simplelist";

}
