package com.centit.framework.users.config;

/**
 * @author zfg
 */
public class JsmotConstant {

    /**
     * 获取交通云accessToken授权码
     */
    public static final String URL_GET_ACCESS_TOKEN = "/authorize/apiLic/getAccessToken";

    /**
     * 新增从业人员
     */
    public static final String URL_CREATE_CY = "/organizeuser/dataSync/g/createCY";

    /**
     * 获取从业人员详情
     */
    public static final String URL_GET_CYUSER_DETAIL = "/organizeuser/dataSync/g/getCYUserDetail";

    /**
     * 新增从业企业
     */
    public static final String URL_CORP_REGIST = "/organizeuser/dataSync/c/corpRegist";

    /**
     * 获取从业企业详情
     */
    public static final String URL_GET_CYCORP_INFO = "/organizeuser/dataSync/c/getCYCorpInfo";
}
