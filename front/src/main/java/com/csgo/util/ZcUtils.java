package com.csgo.util;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.green.model.v20180509.ImageSyncScanRequest;
import com.aliyuncs.green.model.v20180509.TextScanRequest;
import com.aliyuncs.http.FormatType;
import com.aliyuncs.http.HttpResponse;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.http.ProtocolType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.csgo.exception.ServiceErrorException;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class ZcUtils {

    /**
     * 图片安全信息检测接口
     *
     * @param accessKeyId
     * @param secret
     * @param imageUrl
     * @throws Exception
     */
    public static boolean checkImage(String accessKeyId, String secret, String imageUrl) throws Exception {
        boolean isOk = true;
        IClientProfile profile = DefaultProfile.getProfile("cn-shenzhen", accessKeyId, secret);
        DefaultProfile.addEndpoint("", "cn-shenzhen", "Green", "green.cn-shenzhen.aliyuncs.com");
        // 注意：此处实例化的client尽可能重复使用，提升检测性能。避免重复建立连接。
        IAcsClient client = new DefaultAcsClient(profile);
        ImageSyncScanRequest imageSyncScanRequest = new ImageSyncScanRequest();
        // 指定API返回格式。
        imageSyncScanRequest.setAcceptFormat(FormatType.JSON);
        // 指定请求方法。
        imageSyncScanRequest.setMethod(MethodType.POST);
        imageSyncScanRequest.setEncoding("utf-8");
        // 支持HTTP和HTTPS。
        imageSyncScanRequest.setProtocol(ProtocolType.HTTPS);
        List<Map<String, Object>> tasks = new ArrayList<Map<String, Object>>();
        Map<String, Object> task = new LinkedHashMap<String, Object>();
        task.put("dataId", UUID.randomUUID().toString());
        task.put("url", imageUrl);
        task.put("time", new Date());
        tasks.add(task);
        JSONObject data = new JSONObject();
        data.put("bizType", "default");
        data.put("scenes", Arrays.asList("porn", "terrorism"));
        data.put("tasks", tasks);
        log.info("图片安全信息核验请求参数>>{}", JSON.toJSONString(data, true));
        imageSyncScanRequest.setHttpContent(org.apache.commons.codec.binary.StringUtils.getBytesUtf8(data.toJSONString()),
                "UTF-8", FormatType.JSON);
        /**
         * 请设置超时时间。服务端全链路处理超时时间为10秒，请做相应设置。
         * 如果您设置的ReadTimeout小于服务端处理的时间，程序中会获得一个ReadTimeout异常。
         */
        imageSyncScanRequest.setConnectTimeout(3000);
        imageSyncScanRequest.setReadTimeout(10000);
        try {
            HttpResponse httpResponse = client.doAction(imageSyncScanRequest);
            if (!httpResponse.isSuccess()) {
                log.error("response not success. status:" + httpResponse.getStatus());
                throw new ServiceErrorException("请求接口异常");
            }
            JSONObject scrResponse = JSON.parseObject(new String(httpResponse.getHttpContent(), "UTF-8"));
            log.info("图片安全信息核验返回参数>>{}", JSON.toJSONString(scrResponse, true));
            if (200 != scrResponse.getInteger("code")) {
                log.error("detect not success. code:" + scrResponse.getInteger("code"));
                throw new ServiceErrorException("请求接口异常");
            }
            JSONArray taskResults = scrResponse.getJSONArray("data");
            for (Object taskResult : taskResults) {
                if (200 != ((JSONObject) taskResult).getInteger("code")) {
                    log.error("task process fail:" + ((JSONObject) taskResult).getInteger("code"));
                    throw new ServiceErrorException("请求接口异常");
                }
                JSONArray sceneResults = ((JSONObject) taskResult).getJSONArray("results");
                for (Object sceneResult : sceneResults) {
                    String suggestion = ((JSONObject) sceneResult).getString("suggestion");
                    // suggestion为pass表示未命中垃圾。suggestion为block表示命中了垃圾，可以通过label字段查看命中的垃圾分类。
                    if ("block".equals(suggestion)) {
                        isOk = false;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceErrorException("图片安全信息核验异常:" + e.getMessage());
        }
        return isOk;
    }

    /**
     * 文本安全信息检测接口
     *
     * @param accessKeyId
     * @param secret
     * @param content
     * @throws Exception
     */
    public static boolean checkText(String accessKeyId, String secret, String content) throws Exception {
        boolean isOk = true;
        IClientProfile profile = DefaultProfile.getProfile("cn-shenzhen", accessKeyId, secret);
        DefaultProfile.addEndpoint("", "cn-shenzhen", "Green", "green.cn-shenzhen.aliyuncs.com");
        // 注意：此处实例化的client尽可能重复使用，提升检测性能。避免重复建立连接。
        IAcsClient client = new DefaultAcsClient(profile);
        TextScanRequest textScanRequest = new TextScanRequest();
        textScanRequest.setAcceptFormat(FormatType.JSON); // 指定API返回格式。
        textScanRequest.setHttpContentType(FormatType.JSON);
        textScanRequest.setMethod(com.aliyuncs.http.MethodType.POST); // 指定请求方法。
        textScanRequest.setEncoding("UTF-8");
        textScanRequest.setRegionId("cn-shenzhen");
        List<Map<String, Object>> tasks = new ArrayList<Map<String, Object>>();
        Map<String, Object> task1 = new LinkedHashMap<String, Object>();
        task1.put("dataId", UUID.randomUUID().toString());
        /**
         * 待检测的文本，长度不超过10000个字符。
         */
        task1.put("content", content);
        tasks.add(task1);
        JSONObject data = new JSONObject();
        data.put("bizType", "default");
        data.put("scenes", Arrays.asList("antispam"));
        data.put("tasks", tasks);
        log.info("文本安全信息核验请求参数>>{}", JSON.toJSONString(data, true));
        textScanRequest.setHttpContent(data.toJSONString().getBytes("UTF-8"), "UTF-8", FormatType.JSON);
        // 请务必设置超时时间。
        textScanRequest.setConnectTimeout(3000);
        textScanRequest.setReadTimeout(6000);
        try {
            HttpResponse httpResponse = client.doAction(textScanRequest);
            if (!httpResponse.isSuccess()) {
                log.error("response not success. status:" + httpResponse.getStatus());
                throw new ServiceErrorException("请求接口异常");
            }
            JSONObject scrResponse = JSON.parseObject(new String(httpResponse.getHttpContent(), "UTF-8"));
            log.info("文本安全信息核验返回参数>>{}", JSON.toJSONString(scrResponse, true));
            if (200 != scrResponse.getInteger("code")) {
                log.error("detect not success. code:" + scrResponse.getInteger("code"));
                throw new ServiceErrorException("请求接口异常");
            }
            JSONArray taskResults = scrResponse.getJSONArray("data");
            for (Object taskResult : taskResults) {
                if (200 != ((JSONObject) taskResult).getInteger("code")) {
                    log.error("task process fail:" + ((JSONObject) taskResult).getInteger("code"));
                    throw new ServiceErrorException("请求接口异常");
                }
                JSONArray sceneResults = ((JSONObject) taskResult).getJSONArray("results");
                for (Object sceneResult : sceneResults) {
                    String suggestion = ((JSONObject) sceneResult).getString("suggestion");
                    // suggestion为pass表示未命中垃圾。suggestion为block表示命中了垃圾，可以通过label字段查看命中的垃圾分类。
                    if ("block".equals(suggestion)) {
                        isOk = false;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceErrorException("文本安全信息核验异常:" + e.getMessage());
        }
        return isOk;
    }

}