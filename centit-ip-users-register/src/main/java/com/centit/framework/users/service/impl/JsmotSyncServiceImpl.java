package com.centit.framework.users.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.centit.framework.common.ResponseData;
import com.centit.framework.users.config.JsmotConstant;
import com.centit.framework.users.config.JsmotSyncConfig;
import com.centit.framework.users.dto.JsmotUnitDTO;
import com.centit.framework.users.dto.JsmotUserDTO;
import com.centit.framework.users.service.JsmotSyncService;
import com.centit.support.network.HttpExecutor;
import com.centit.support.network.HttpExecutorContext;
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

}
