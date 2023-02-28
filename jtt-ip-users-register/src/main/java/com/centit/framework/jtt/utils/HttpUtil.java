package com.centit.framework.jtt.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpUtil {
    private static final Logger logger = LoggerFactory.getLogger(HttpUtil.class);
    private static final int TIMEOUT = 20 * 1000;//超时时间
    private static final String CHARSET_UTF_8 = "UTF-8";

    /**
     * 接口调用 GET
     */
    public static String httpGetRequest(String getUrl) {
        StringBuffer buffer = new StringBuffer();
        try {
            URL url = new URL(getUrl); // 把字符串转换为URL请求地址
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();// 打开连接
            connection.connect();// 连接会话
            // 获取输入流
            try (InputStream inputStream = connection.getInputStream();
                 InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                 BufferedReader br = new BufferedReader(inputStreamReader)) {
                String line;

                while ((line = br.readLine()) != null) {// 循环读取流
                    buffer.append(line);
                }
            }
            connection.disconnect();// 断开连接
        } catch (Exception e) {
            logger.error("GET请求失败!请求地址：{}", getUrl);
            return null;
        }
        return buffer.toString();
    }

    /**
     * 接口调用 POST
     */
    public static String httpPostRequest(String postUrl, String contentType, String accessToken, String params) {
        StringBuffer buffer = new StringBuffer(); // 用来存储响应数据
        try {

			CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));

            URL url = new URL(postUrl);
            // 将url 以 open方法返回的urlConnection 连接强转为HttpURLConnection连接
            // (标识一个url所引用的远程对象连接)
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();// 此时cnnection只是为一个连接对象,待连接中
            // 设置连接输出流为true,默认false (post 请求是以流的方式隐式的传递参数)
            connection.setDoOutput(true);
            // 设置连接输入流为true
            connection.setDoInput(true);
            // 设置请求方式为post
            connection.setRequestMethod("POST");
            // post请求缓存设为false
            connection.setUseCaches(false);
            // 设置请求头
            if ("json".equals(contentType)) {
                connection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            } else if ("form".equals(contentType)) {
                connection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
            }
            // 密钥验证
            if (StringUtils.isNotBlank(accessToken)) {
                connection.setRequestProperty("Authorization", "Bearer " + accessToken);
            }
            //传入参数
            if (StringUtils.isNotBlank(params)) {
                try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8);
                     BufferedWriter writer = new BufferedWriter(outputStreamWriter)) {
                    writer.write(params);
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                    return null;
                }
            }
            // 建立连接
            // (请求未开始,直到connection.getInputStream()方法调用时才发起,以上各个参数设置需在此方法之前进行)
            connection.connect();
            // 连接发起请求,处理服务器响应 (从连接获取到输入流并包装为bufferedReader)
            //获得输入
            try (InputStream inputStream = connection.getInputStream();
                 InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                 BufferedReader bf = new BufferedReader(inputStreamReader)) {
                String line;
                // 循环读取流,若不到结尾处
                while ((line = bf.readLine()) != null) {
                    buffer.append(line);
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                return null;
            }
            connection.disconnect(); // 销毁连接
        } catch (Exception e) {
            logger.error("POST请求失败,this error", e);
            logger.error("POST请求失败!请求地址：{}", postUrl);
            return null;
        }
        return buffer.toString();
    }

	/**
	* HTTP POST表单
	* @param url
	* @param params
	* @return
	* @throws Exception
	*/
	public static String postJsonData(String url, Map<String, String> params) {
		return httpPost(url, null, params);
	}

	/**
	 * HTTP POST表单
	 * @param url
	 * @param headers
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static String httpPost(String url, Map<String, String> headers, Object params) {
		String result = "";
		CloseableHttpClient httpClient = createSSLInsecureClient();
		HttpPost post = new HttpPost(url);
		post.setProtocolVersion(HttpVersion.HTTP_1_0);
		post.addHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_CLOSE);
		try {
			//设置超时时间
			RequestConfig requestConfig = getRequestConfig();
			post.setConfig(requestConfig);

			// 构造消息头
			if (headers != null) {
				for (Map.Entry<String, String> e : headers.entrySet()) {
					post.setHeader(e.getKey(), e.getValue());
				}
			}
			HttpEntity entity = null;
			// 构造消息体
			if (params instanceof String) {
				String jsonStr = (String) params;
				post.setHeader("Content-Type", "application/json");
				entity = new StringEntity(jsonStr, CHARSET_UTF_8);
			} else if (params instanceof Map) {
				Map<String, String> formParam = (Map<String, String>) params;
				entity = buildFormEntity(formParam);
			}
			if (entity != null) {
				post.setEntity(entity);
			}
			CloseableHttpResponse response = httpClient.execute(post);

			result = getResponseResult(response);
            if (null != response) {
                response.close();
            }
		} catch (IOException e) {
			result = "";
			logger.error("【postJsonData】请求失败,error ", e);
			logger.error("【postJsonData】请求失败,请求地址：{} ", url);
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
                logger.error(e.getMessage(), e);
			}
		}
		return result;
	}

	private static RequestConfig getRequestConfig() {
		return RequestConfig.custom().setConnectTimeout(TIMEOUT) //设置连接超时时间
				.setConnectionRequestTimeout(TIMEOUT) // 设置请求超时时间
				.setSocketTimeout(TIMEOUT).setRedirectsEnabled(true)//默认允许自动重定向
				.build();
	}

	private static String getResponseResult(HttpResponse response) {
		String result = "";
		if (response != null) {
			// 检验返回码
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				HttpEntity resEntity = response.getEntity();
				if (resEntity != null) {
					try {
						result = EntityUtils.toString(resEntity, CHARSET_UTF_8);
						EntityUtils.consume(resEntity); //按照官方文档的说法：二者都释放了才可以正常的释放链接
					} catch (ParseException | IOException e) {
                        logger.error(e.getMessage(), e);
					}
				}
			}
		}
		return result;
	}

	/**
	* 构建FormEntity
	*
	* @param formParam
	* @return
	* @throws UnsupportedEncodingException
	*/
	private static UrlEncodedFormEntity buildFormEntity(Map<String, String> formParam) {
		UrlEncodedFormEntity formEntity = null;
		try {
			if (formParam != null) {
				List<NameValuePair> nameValuePairList = new ArrayList<>();

				for (Map.Entry<String, String> entry : formParam.entrySet()) {
					nameValuePairList.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
				}
				formEntity = new UrlEncodedFormEntity(nameValuePairList, CHARSET_UTF_8);
			}
		} catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage(), e);
		}
		return formEntity;
	}

	public static JSONObject convertResponseBytes2JsonObj(HttpResponse response) {
		JSONObject jsonObject = null;
		try {
			InputStream inputStream = response.getEntity().getContent();
			byte[] respBytes = IOUtils.toByteArray(inputStream);
			String result = new String(respBytes, StandardCharsets.UTF_8);
			if (StringUtils.isBlank(result)) {
				logger.info("请求无响应==============================");
			} else {
				if (result.startsWith("{") && result.endsWith("}")) {
					jsonObject = JSON.parseObject(result);
				} else {
					logger.info("请求不能转成JSON对象==============================");
				}
			}
		} catch (Exception e) {
            logger.error(e.getMessage(), e);
		}
		return jsonObject;
	}

	@SuppressWarnings("deprecation")
    public static CloseableHttpClient createSSLInsecureClient() {
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext,
                    SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            return HttpClients.custom().setMaxConnTotal(100).setMaxConnPerRoute(40).setSSLSocketFactory(sslsf).build();
        } catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
            logger.error(e.getMessage(), e);
        }
        return HttpClients.createDefault();
    }
}
