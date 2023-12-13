package com.csgo.sms;

import com.csgo.util.FormDateReportConvertor;
import com.csgo.util.EncryDecryUtils;
import com.csgo.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class SendShortMessage {

    public boolean senddx(String phone, String code) {

        try {
            String appId = SmsConfig.appId;
            String des3Key = SmsConfig.des3Key;
            String md5Key = SmsConfig.md5Key;
            String url = SmsConfig.url;
            String notifyUrl = SmsConfig.notifyUrl;
            // biz_content Json
            PostMethod post = new PostMethod(url);
            post.addRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

            Map<String, String> requestMap = new HashMap<String, String>();
            String mhtOrderNo = StringUtil.dateToStr(new Date(), "MMddhhmmss") + StringUtil.randomString(10);
            requestMap.put("funcode", "INDUSTRY_TEMP");
            requestMap.put("mhtOrderNo", mhtOrderNo);
            requestMap.put("appId", appId);

            requestMap.put("mobile", phone);
            //parm是变量值，如验证码等（如果没有变量可以去掉）
            requestMap.put("templateInfo", "{\"tempCode\":\"4000000820212882\",\"param\":{\"code\":\"" + code + "\"}}");
            requestMap.put("notifyUrl", URLEncoder.encode(notifyUrl, "utf-8"));

            String toRSAStr = FormDateReportConvertor.postFormLinkReport(requestMap);
            //message=base64(appId=xxx)| base64(3DES(报文原文))|base64(MD5(报文原文+&+ md5Key))
            String message1 = "appId=" + appId + "";
            message1 = EncryDecryUtils.base64Encrypt(message1);//base64(appId=xxx)

            String message2 = toRSAStr;
            message2 = EncryDecryUtils.encryptFromDESBase64(des3Key, message2);// base64(3DES(报文原文)
            String message3 = EncryDecryUtils.base64Encrypt(EncryDecryUtils.md5(toRSAStr.trim() + "&" + md5Key));//base64(MD5(报文原文+&+ md5Key))
            String message = message1 + "|" + message2 + "|" + message3 + "";

            log.info("==================");

            /**
             *发送的message  一定要做URLEncoder，否则会请求失败
             **/
            message = URLEncoder.encode(message, "UTF-8");
            log.info(message);

            log.info("发送报文原文:" + toRSAStr);

            NameValuePair[] param = {
                    new NameValuePair("funcode", "INDUSTRY_TEMP"),
                    new NameValuePair("message", message)
            };

            HttpClient client = new HttpClient();
            post.setRequestBody(param);
            client.executeMethod(post);
            InputStream responseBody = post.getResponseBodyAsStream();
            InputStreamReader in = new InputStreamReader(responseBody, StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(in);
            StringBuffer rets = new StringBuffer();
            String inputLine = "";
            while ((inputLine = br.readLine()) != null) {
                rets.append(inputLine);
            }
            in.close();

            log.info("SMS返回报文" + rets.toString().trim());
            String res = rets.toString().trim();

            //解包失败 或者 验签失败的时候res.split("\\|").length==2
            ///////////////////////////////////////////////////////////////////////////////////////////////////
            if (res.split("\\|").length == 2) {
                String return2 = res.split("\\|")[1];
                //错误原因
                log.info(EncryDecryUtils.base64Decrypt(return2));
                return false;
            }
            //正常返回res.split("\\|").length==3
            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

            String return1 = res.split("\\|")[0];
            String return2 = res.split("\\|")[1];
            String return3 = res.split("\\|")[2];
            //解析第二部分，获取原始报文 先解密base再解密3des
            String originalMsg = EncryDecryUtils.decryptFromBase64DES(des3Key, return2);
            log.info("返回的报文原文是:" + originalMsg);

            //解析第三部分，获取原始签名
            String originalSign = EncryDecryUtils.base64Decrypt(return3);
            log.info("返回的报文原始签名是:" + originalSign);


            //商户自己生成签名

            //appId=1433921064606182&funcode=EP01&responseCode=00&responseMsg=success&responseTime=20150610054045&status=00&userId=123456

            //商户需要对返回的原始报文进行key排序
            //商户需要对返回的原始报文进行key排序
            //商户需要对返回的原始报文进行key排序     现在支付返回的时候已经排序，建议商户再进行一次排序


            /**
             * 获取到原始报文以后建议做一次trim去掉后面的空格，否则会验签不通过
             **/
            String mySign = EncryDecryUtils.md5(originalMsg.trim() + "&" + md5Key);//.trim() 很重要

            log.info("商户生成的签名是:" + mySign);
            //对比签名是否验证通过
            if (originalSign.equals(mySign)) {
                //签名验证通过
                log.info("验证签名正确");
            } else {
                log.info("验证签名不正确");
            }

            /**
             * 正常情况下返回的报文
             **/
            //appId=1433921064606182&funcode=EP01&responseCode=00&responseMsg=success&responseTime=20150611093639&status=00&userId=123456
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }
}
