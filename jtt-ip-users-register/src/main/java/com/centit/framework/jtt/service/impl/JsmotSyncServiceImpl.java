package com.centit.framework.jtt.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.centit.framework.common.ResponseData;
import com.centit.framework.jtt.config.JsmotConstant;
import com.centit.framework.jtt.config.JsmotSyncConfig;
import com.centit.framework.jtt.dto.JsmotUnitDTO;
import com.centit.framework.jtt.dto.JsmotUserDTO;
import com.centit.framework.jtt.dto.SmsDTO;
import com.centit.framework.jtt.service.JsmotSyncService;
import com.centit.framework.jtt.utils.HttpUtil;
import com.centit.support.network.HttpExecutor;
import com.centit.support.network.HttpExecutorContext;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author zfg
 */
@Service
public class JsmotSyncServiceImpl implements JsmotSyncService {

    private static final Logger logger = LoggerFactory.getLogger(JsmotSyncServiceImpl.class);

    @Autowired
    private JsmotSyncConfig jsmotSyncConfig;

    /**
     * @param accessToken 授权码
     * @param userInfo    从业人员
     * @return
     */
    @Override
    public ResponseData userCreate(String accessToken, JsmotUserDTO userInfo) {
        String retMsg = "";
        String userCode = "";
        long retCode = 1;
        try {
            String uri = jsmotSyncConfig.getJsmotHost() + JsmotConstant.URL_CREATE_CY + "?accessToken=" + accessToken;
            String result = HttpExecutor.jsonPost(HttpExecutorContext.create(), uri, JSON.parseObject(JSON.toJSONString(userInfo)));
            JSONObject jsonObject = JSON.parseObject(result);
            if (null != jsonObject) {
                retCode = jsonObject.getLong("retCode");
                retMsg = jsonObject.getString("retMsg");
                if (retCode == 0) {
                    userCode = jsonObject.getJSONObject("bizData").getString("userCode");
                } else {
                    return ResponseData.makeErrorMessage(Integer.valueOf(String.valueOf(retCode)), retMsg);
                }
            } else {
                return ResponseData.makeErrorMessage(1, "调用新增从业人员返回为空");
            }
        } catch (Exception e) {
            logger.error("Failed to {}", JsmotConstant.URL_CREATE_CY, e);
            return ResponseData.makeErrorMessage("Failed to userCreate: " + e.getMessage());
        }
        return ResponseData.makeResponseData(userCode);
    }

    /**
     * @param accessToken 授权码
     * @param unitInfo    从业企业
     * @return
     */
    @Override
    public ResponseData unitCreate(String accessToken, JsmotUnitDTO unitInfo) {
        String retMsg = "";
        String id = "";
        long retCode = 1;
        try {
            String uri = jsmotSyncConfig.getJsmotHost() + JsmotConstant.URL_CORP_REGIST + "?accessToken=" + accessToken;
            String result = HttpExecutor.jsonPost(HttpExecutorContext.create(), uri, JSON.parseObject(JSON.toJSONString(unitInfo)));
            JSONObject jsonObject = JSON.parseObject(result);
            if (null != jsonObject) {
                retCode = jsonObject.getLong("retCode");
                retMsg = jsonObject.getString("retMsg");
                if (retCode == 0) {
                    id = jsonObject.getJSONObject("bizData").getString("id");
                } else {
                    return ResponseData.makeErrorMessage(Integer.valueOf(String.valueOf(retCode)), retMsg);
                }
            } else {
                return ResponseData.makeErrorMessage(1, "调用新增从业企业返回为空");
            }
        } catch (Exception e) {
            logger.error("Failed to {}", JsmotConstant.URL_CORP_REGIST, e);
            return ResponseData.makeErrorMessage("Failed to unitCreate: " + e.getMessage());
        }
        return ResponseData.makeResponseData(id);
    }

    /**
     * @param accessToken 授权码
     * @param userCode    用户userCode
     * @param flag        查询数据种类：1仅查询人员信息；2查询人员和关联企业信息
     * @return
     */
    @Override
    public ResponseData getCYUserDetail(String accessToken, String userCode, String flag) {
        String retMsg = "";
        long retCode = 1;
        JSONObject bizData = new JSONObject();
        try {
            String uri = jsmotSyncConfig.getJsmotHost() + JsmotConstant.URL_GET_CYUSER_DETAIL + "/" + userCode + "/" + flag + "?accessToken=" + accessToken;
            String result = HttpExecutor.simpleGet(HttpExecutorContext.create(), uri);
            JSONObject jsonObject = JSON.parseObject(result);
            if (null != jsonObject) {
                retCode = jsonObject.getLong("retCode");
                retMsg = jsonObject.getString("retMsg");
                if (retCode == 0) {
                    bizData = jsonObject.getJSONObject("bizData");
                } else {
                    return ResponseData.makeErrorMessage(Integer.valueOf(String.valueOf(retCode)), retMsg);
                }
            } else {
                return ResponseData.makeErrorMessage(1, "调用获取从业人员详情返回为空");
            }
        } catch (Exception e) {
            logger.error("Failed to {}", JsmotConstant.URL_GET_CYUSER_DETAIL, e);
            return ResponseData.makeErrorMessage("Failed to getCYUserDetail: " + e.getMessage());
        }
        return ResponseData.makeResponseData(bizData);
    }

    /**
     * @param accessToken 授权码
     * @param id          企业id
     * @param flag        查询数据种类：1仅查询企业信息；2查询企业和关联用户信息
     * @return
     */
    @Override
    public ResponseData getCYCorpInfo(String accessToken, String id, String flag) {
        String retMsg = "";
        long retCode = 1;
        JSONObject bizData = new JSONObject();
        try {
            String uri = jsmotSyncConfig.getJsmotHost() + JsmotConstant.URL_GET_CYCORP_INFO + "/" + id + "/" + flag + "?accessToken=" + accessToken;
            String result = HttpExecutor.simpleGet(HttpExecutorContext.create(), uri);
            JSONObject jsonObject = JSON.parseObject(result);
            if (null != jsonObject) {
                retCode = jsonObject.getLong("retCode");
                retMsg = jsonObject.getString("retMsg");
                if (retCode == 0) {
                    bizData = jsonObject.getJSONObject("bizData");
                } else {
                    return ResponseData.makeErrorMessage(Integer.valueOf(String.valueOf(retCode)), retMsg);
                }
            } else {
                return ResponseData.makeErrorMessage(1, "调用获取从业企业详情返回为空");
            }
        } catch (Exception e) {
            logger.error("Failed to {}", JsmotConstant.URL_GET_CYCORP_INFO, e);
            return ResponseData.makeErrorMessage("Failed to getCYUserDetail: " + e.getMessage());
        }
        return ResponseData.makeResponseData(bizData);
    }

    /**
     * @param accessToken 授权码
     * @param smsDTO      短信发送内容
     * @return
     */
    @Override
    public ResponseData sendSms(String accessToken, SmsDTO smsDTO) {
        String retCode = "";
        String data = "";
        String uri = "";
        JSONObject bizData = new JSONObject();
        JSONArray bizArray = new JSONArray();
        String sendType = "0";
        try {
            JSONObject param = new JSONObject();
            param.put("content", smsDTO.getContent());
            if (CollectionUtils.isNotEmpty(smsDTO.getMobiles())) {
                uri = jsmotSyncConfig.getSmsHost() + JsmotConstant.URL_SEND_BATCHONLY_SMS;
                param.put("mobiles", smsDTO.getMobiles());
                sendType = "1";
            } else {
                uri = jsmotSyncConfig.getSmsHost() + JsmotConstant.URL_SEND_SINGLE_SMS;
                param.put("mobile", smsDTO.getMobile());
            }
            String result = HttpUtil.httpPostRequest(uri, "json", accessToken, param.toJSONString());
            JSONObject jsonObject = JSON.parseObject(result);
            if (null != jsonObject) {
                retCode = jsonObject.getString("code");
                if ("SUCCESS".equals(retCode)) {
                    data = jsonObject.getString("result");
                    if ("0".equals(sendType)) {
                        bizData = JSON.parseObject(data);
                    } else {
                        bizArray = JSON.parseArray(data);
                    }
                } else {
                    return ResponseData.makeErrorMessage(retCode);
                }
            } else {
                return ResponseData.makeErrorMessage(1, "调用短信平台发送接口返回为空");
            }
        } catch (Exception e) {
            logger.error("Failed to {}", uri, e);
            return ResponseData.makeErrorMessage("Failed to sendSms: " + e.getMessage());
        }
        if ("0".equals(sendType)) {
            return ResponseData.makeResponseData(bizData);
        } else {
            return ResponseData.makeResponseData(bizArray);
        }
    }
}
