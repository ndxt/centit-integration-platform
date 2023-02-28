package com.centit.framework.users.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * <p>HTTP 请求工具类<p>
 */
public class HttpSendUtil {

    private static final Logger logger = LoggerFactory.getLogger(HttpSendUtil.class);

    private static PoolingHttpClientConnectionManager connMgr;
    private static RequestConfig requestConfig;
    private static final int MAX_TIMEOUT = 600000;

    static {
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
            .register("http", PlainConnectionSocketFactory.INSTANCE)
            .register("https", createSSLConnSocketFactory())
            .build();
        // 设置连接池
        connMgr = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        // 设置连接池大小
        connMgr.setMaxTotal(100);
        connMgr.setDefaultMaxPerRoute(connMgr.getMaxTotal());
        RequestConfig.Builder configBuilder = RequestConfig.custom();
        // 设置连接超时
        configBuilder.setConnectTimeout(MAX_TIMEOUT);
        // 设置读取超时
        configBuilder.setSocketTimeout(MAX_TIMEOUT);
        // 设置从连接池获取连接实例的超时
        configBuilder.setConnectionRequestTimeout(MAX_TIMEOUT);
        // 在提交请求之前 测试连接是否可用
        configBuilder.setStaleConnectionCheckEnabled(true);
        requestConfig = configBuilder.build();
    }

    /**
     * 发送 GET 请求（HTTP）
     *
     * @param url
     * @return
     */
    public static JSONObject doGet(String url) {
        CloseableHttpClient httpclient = HttpClients.custom().setConnectionManager(connMgr).setDefaultRequestConfig(requestConfig).build();
        HttpGet httpGet = new HttpGet(url);
        HttpResponse response = null;
        String out = null;
        JSONObject jsonObject = null;//接收结果
        try {
            response = httpclient.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) { //请求出错
                String result = EntityUtils.toString(response.getEntity(), "utf-8");
                logger.info("{} url:{}", result, url);//打印错误信息
                return null;
            }
            out = EntityUtils.toString(response.getEntity(), "utf-8");
            jsonObject = JSON.parseObject(out);
        } catch (IOException e) {
            logger.error("doGet异常", e);
        } finally {
            if (httpGet != null) {
                httpGet.releaseConnection();
            }
        }
        return jsonObject;
    }

    /**
     * 发送 POST 请求
     *
     * @param url    API接口URL
     * @param params 参数map
     * @return
     */
    public static JSONObject doPost(String url, String params) {
        CloseableHttpClient httpclient = HttpClients.custom().setConnectionManager(connMgr).setDefaultRequestConfig(requestConfig).build();
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpResponse response = null;
        String out = null;
        JSONObject jsonObject = null;//接收结果
        try {
            httpPost.setConfig(requestConfig);
            StringEntity stringEntity = new StringEntity(params, "UTF-8");
            stringEntity.setContentEncoding("UTF-8");
            stringEntity.setContentType("application/json");
            httpPost.setEntity(stringEntity);

            response = httpclient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                String result = EntityUtils.toString(response.getEntity(), "utf-8");
                logger.info("{} url:{} parms {}", result, url, params);//打印错误信息
                return null;
            }
            out = EntityUtils.toString(response.getEntity(), "utf-8");
            jsonObject = JSON.parseObject(out);
        } catch (Exception e) {
            logger.error("doPost异常", e);
        } finally {
            if (httpPost != null) {
                httpPost.releaseConnection();
            }
        }
        return jsonObject;
    }

    /**
     * 创建SSL安全连接
     *
     * @return
     */
    private static SSLConnectionSocketFactory createSSLConnSocketFactory() {
        SSLConnectionSocketFactory sslsf = null;
        try {
            SSLContext ctx = SSLContext.getInstance("SSL");
            X509TrustManager tm = new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[]{};
                }
            };
            ctx.init(null, new TrustManager[]{tm}, null);
            sslsf = new SSLConnectionSocketFactory(ctx, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        } catch (GeneralSecurityException e) {
            logger.error("创建SSL安全连接异常", e);
        }
        return sslsf;
    }

}
