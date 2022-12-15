package com.centit.framework.users.config;

/**
 * @author zfg
 */
public class JsmotConstant {

    /**
     * 测试环境
     */
    private static final String HOST = "http://218.2.208.143:10251";

    /**
     * 获取交通云accessToken授权码
     */
    public static final String URL_GET_ACCESS_TOKEN = HOST + "/authorize/apiLic/getAccessToken";

    /**
     * （行业）获取组织机构详情
     */
    public static final String URL_GET_UNIT_DETAIL = HOST + "/organizeuser/dataSync/f/getUnitDetail/{unitCode}";

    /**
     * （行业）获取用户详情
     */
    public static final String URL_GET_FUSER_DETAIL = HOST + "/organizeuser/dataSync/f/getFUserDetail/{userCode}";
}
