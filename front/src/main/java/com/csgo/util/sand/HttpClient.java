/**
 * Licensed Property to Sand Co., Ltd.
 * <p>
 * (C) Copyright of Sand Co., Ltd. 2010
 * All Rights Reserved.
 * <p>
 * <p>
 * Modification History:
 * =============================================================================
 * Author           Date           Description
 * ------------ ---------- ---------------------------------------------------
 * 企业产品团队       2016-10-12       Http通讯工具类.
 * =============================================================================
 */
package com.csgo.util.sand;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


/**
 * @version 2.0.0
 * @ClassName: HttpClient
 * @Description: 主要用于Http通讯
 */
@Slf4j
@SuppressWarnings("deprecation")
public class HttpClient {


    private static final String DEFAULT_CHARSET = "UTF-8";

    private static SSLConnectionSocketFactory sslsf;

    static {
        try {
            SSLContext sslcontext = SSLContext.getInstance("TLSv1.2");
            sslcontext.init(new KeyManager[0], new TrustManager[]{new DefaultTrustManager()}, new SecureRandom());
            sslsf = new SSLConnectionSocketFactory(sslcontext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static String doPost(String url, Map<String, String> params, int connectTimeout, int readTimeout)
            throws IOException {
        return doPost(url, params, DEFAULT_CHARSET, connectTimeout, readTimeout);
    }

    public static String doPost(String url, Map<String, String> params, String charset, int connectTimeout,
                                int readTimeout) throws IOException {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-type", "application/x-www-form-urlencoded;charset=" + charset);
        return doPost(url, headers, params, charset, connectTimeout, readTimeout);
    }

    public static String doPost(String url, Map<String, String> headers, Map<String, String> params,
                                final String charset, int connectTimeout, int readTimeout) throws IOException {

        URL targetUrl = new URL(url);
        HttpHost httpHost = new HttpHost(targetUrl.getHost(), targetUrl.getPort(), targetUrl.getProtocol());
        log.info("host:" + targetUrl.getHost() + ",port:" + targetUrl.getPort() + ",protocol:"
                + targetUrl.getProtocol() + ",path:" + targetUrl.getPath());

        try (CloseableHttpClient httpclient = getHttpClient(targetUrl);) {
            HttpPost httpPost = getHttpPost(targetUrl, headers, params, charset, connectTimeout, readTimeout);

            return httpclient.execute(httpHost, httpPost, response -> {

                int status = response.getStatusLine().getStatusCode();

                log.info("status:[" + status + "]");
                if (status == 200) {
                    return EntityUtils.toString(response.getEntity(), charset);
                } else {
                    return "";
                }
            });
        }
    }

    /**
     * @param targetUrl @param headers @param params @param charset @param
     *                  connectTimeout @param readTimeout @return @throws IOException @throws
     */
    private static HttpPost getHttpPost(URL targetUrl, Map<String, String> headers, Map<String, String> params,
                                        String charset, int connectTimeout, int readTimeout) throws IOException {

        HttpPost httpPost = new HttpPost(targetUrl.getPath());

        for (Entry<String, String> entry : headers.entrySet()) {
            httpPost.setHeader(entry.getKey(), entry.getValue());
        }

        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(readTimeout)
                .setConnectTimeout(connectTimeout) // Connection timeout is the timeout until a connection with the
                // server is established.
                .build();
        httpPost.setConfig(requestConfig);

        StringEntity entity = new StringEntity(buildQuery(params, charset), charset);
        httpPost.setEntity(entity);

        return httpPost;
    }

    /**
     * @param targetUrl
     * @return
     */
    private static CloseableHttpClient getHttpClient(URL targetUrl) {

        CloseableHttpClient httpClient = null;
        if ("https".equals(targetUrl.getProtocol())) {
            httpClient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
        } else {
            httpClient = HttpClients.createDefault();
        }
        return httpClient;
    }

    private static class DefaultTrustManager implements X509TrustManager {
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public void checkClientTrusted(X509Certificate[] chain, String authType) {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType) {
        }
    }

    private static String buildQuery(Map<String, String> params, String charset) {

        List<NameValuePair> nvps = new LinkedList<NameValuePair>();

        Set<Entry<String, String>> entries = params.entrySet();
        for (Entry<String, String> entry : entries) {
            nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }

        return URLEncodedUtils.format(nvps, charset);
    }

}
