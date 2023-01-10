package com.centit.framework.jtt.config;

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

    /**
     * H5单点登录
     */
    public static final String URL_H5_AUTHORIZE = "/authorize/tla/authorize";

    /**
     * H5获取用户的详细信息
     */
    public static final String URL_H5_GETUSERINFO_BYCODE = "/authorize/tla/getuserinfo_bycode";

    /**
     * 短信平台token
     */
    public static final String URL_SMS_ACCESS_TOKEN = "/gettoken";

    /**
     * 单条短信
     */
    public static final String URL_SEND_SINGLE_SMS = "/ttworksheet/sendEmaySinglesms";

    /**
     * 群发短信
     */
    public static final String URL_SEND_BATCHONLY_SMS = "/ttworksheet/sendEmayBatchonlysms";
}
