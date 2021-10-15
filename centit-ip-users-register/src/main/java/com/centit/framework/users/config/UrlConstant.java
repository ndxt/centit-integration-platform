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

    public static final String URL_GET_USER_BYCODE = HOST + "/sns/getuserinfo_bycode";

    public static final String URL_GET_USER_BYUNIONID = HOST + "/topapi/user/getbyunionid";

    public static final String URL_GET_USER = HOST + "/topapi/v2/user/get";

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

    /**
     * 创建用户
     */
    public static final String USER_CREATE = HOST + "/topapi/v2/user/create?access_token=ACCESS_TOKEN";

    /**
     * 创建部门  接口调用请求地址（请求方式：post）
     **/
    public static final String DEPARTMENT_CREATE = HOST + "/topapi/v2/department/create?access_token=ACCESS_TOKEN";
}
