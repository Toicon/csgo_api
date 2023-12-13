package com.csgo.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpsUtil2 {

    public static String getJsonData(String jsonParam, String urls) {
        return sendRequest(urls, jsonParam, "application/json");

    }


    public static String sendFormPost(String path, Map<String, String> params) {
        List<String> keys = new ArrayList<>(params.keySet());

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);
            if (i == keys.size() - 1) {// 拼接时，不包括最后一个&字符
                stringBuilder.append(key).append("=").append(value);
            } else {
                stringBuilder.append(key).append("=").append(value).append("&");
            }
        }

        return sendRequest(path, stringBuilder.toString(), "application/x-www-form-urlencoded");
    }

    private static String sendRequest(String path, String jsonParam, String contentType) {
        StringBuffer sb = new StringBuffer();
        try {
            // 创建url资源
            URL url = new URL(path);
            // 建立http连接
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // 设置允许输出
            conn.setDoOutput(true);
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            // 设置允许输入
            conn.setDoInput(true);
            // 设置不用缓存
            conn.setUseCaches(false);
            // 设置传递方式
            conn.setRequestMethod("POST");
            // 设置维持长连接
            conn.setRequestProperty("Connection", "Keep-Alive");
            // 设置文件字符集:
            conn.setRequestProperty("Charset", "UTF-8");
            // 转换为字节数组
            byte[] data = (jsonParam).getBytes();
            // 设置文件长度
            conn.setRequestProperty("Content-Length", String.valueOf(data.length));
            // 设置文件类型:
            conn.setRequestProperty("Content-Type", contentType);
            // 开始连接请求
            conn.connect();
            OutputStream out = new DataOutputStream(conn.getOutputStream());
            // 写入请求的字符串
            out.write((jsonParam).getBytes(StandardCharsets.UTF_8));
            out.flush();
            out.close();

            // 请求返回的状态
            if (HttpURLConnection.HTTP_OK == conn.getResponseCode()) {
                // 请求返回的数据
                InputStream in1 = conn.getInputStream();
                try {
                    String readLine = "";
                    BufferedReader responseReader = new BufferedReader(new InputStreamReader(in1, StandardCharsets.UTF_8));
                    while ((readLine = responseReader.readLine()) != null) {
                        sb.append(readLine).append("\n");
                    }
                    responseReader.close();
                } catch (Exception e1) {
                    log.error(e1.getMessage(), e1);
                }
            } else {
                log.error("error response code: {}", conn.getResponseCode());
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return sb.toString();
    }

}
