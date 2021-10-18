package com.centit.framework.users.utils;

import com.alibaba.fastjson.JSONObject;
import com.centit.framework.appclient.HttpReceiveJSON;
import com.centit.framework.users.config.UrlConstant;
import com.centit.support.compiler.Pretreatment;
import com.centit.support.network.HttpExecutor;
import com.centit.support.network.HttpExecutorContext;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author zfg
 */
public class DingTalkUtil {

    private static final Logger logger = LoggerFactory.getLogger(DingTalkUtil.class);

    /**
     * get请求
     *
     * @param url 接口地址参数
     * @return
     */
    public static JSONObject doGetStr(String url) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response = null;
        JSONObject jsonObject = null;//接收结果
        try {
            response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();//从消息体里获取结果
            if (entity != null) {
                String result = EntityUtils.toString(entity, "UTF-8");
                jsonObject = JSONObject.parseObject(result);
            }
            EntityUtils.consume(entity);
        } catch (ClientProtocolException e) {
            logger.error("", e);
        } catch (IOException e) {
            logger.error("", e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                logger.error("", e);
            }
        }
        return jsonObject;
    }

    /**
     * post请求
     *
     * @param url    接口地址参数
     * @param outStr
     * @return
     */
    public static JSONObject doPostStr(String url, String outStr) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        //DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        JSONObject jsonObject = null;
        try {
            httpPost.setEntity(new StringEntity(outStr, "UTF-8"));
            HttpResponse response = httpClient.execute(httpPost);
            String result = EntityUtils.toString(response.getEntity(), "UTF-8");
            jsonObject = JSONObject.parseObject(result);
        } catch (Exception e) {
            logger.error("异常", e);
        }
        return jsonObject;
    }

    /**
     * 创建人员
     *
     * @param accessToken
     * @param userJson
     * @return
     */
    public static JSONObject userCreate(String accessToken, JSONObject userJson) throws IOException{
        String url = UrlConstant.USER_CREATE.replace("ACCESS_TOKEN", accessToken);
        String jsonStr = HttpExecutor.jsonPost(HttpExecutorContext.create(), url, userJson);
        //HttpReceiveJSON resJson = HttpReceiveJSON.valueOfJson(jsonStr);
        JSONObject jsonObject = doPostStr(url, userJson.toString());
        return jsonObject;
    }

    /**
     * 创建部门
     *
     * @param accessToken
     * @param deptJson    部门信息
     * @return
     */
    public static JSONObject departmentCreate(String accessToken, JSONObject deptJson) {
        String url = UrlConstant.DEPARTMENT_CREATE.replace("ACCESS_TOKEN", accessToken);
        JSONObject jsonObject = doPostStr(url, deptJson.toString());
        return jsonObject;
    }
}
