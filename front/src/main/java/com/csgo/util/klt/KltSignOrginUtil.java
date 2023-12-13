package com.csgo.util.klt;

import cfca.sadk.algorithm.common.PKIException;
import cfca.sadk.algorithm.sm2.SM2PrivateKey;
import cfca.sadk.util.KeyUtil;
import cn.hutool.core.util.HexUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.crypto.engines.SM2Engine;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.BufferedInputStream;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * @Author: hjw
 * @createTime: 2023年05月25日 17:58:11
 * @version:
 * @Description:
 */
@Slf4j
public class KltSignOrginUtil {

    /**
     * 对字符串进行加签.
     *
     * @param signString 用于签名的字符串
     * @return 签名结果
     */
    public static String sign(String signString, String merchantId,String sm2FilePath,String password) throws PKIException {
        // 证书路径
        BufferedInputStream inputStream = (BufferedInputStream) ClassLoader.getSystemResourceAsStream(sm2FilePath);
        //读取文件获取私钥
        SM2PrivateKey privateKeyFromSM2 = KeyUtil.getPrivateKeyFromSM2(inputStream, password);

        //加签
        SM2 sm2 = new SM2(HexUtil.encodeHexStr(privateKeyFromSM2.getD_Bytes()), null);
        sm2.usePlainEncoding();
        byte[] sign = sm2.sign(signString.getBytes(StandardCharsets.UTF_8), merchantId.getBytes(StandardCharsets.UTF_8));
        return HexUtil.encodeHexStr(sign);
    }

    /**
     * 验证签名.
     * responseData 返回参数
     * sign 返回签名
     */
    public static boolean verifySign(String responseData, String sign, String merchantId,String sm2FilePath) throws CertificateException, NoSuchProviderException {

        //签名数据
        String data = KltSignUtil.getSignBody(JSONObject.parseObject(responseData));

        //读取公钥
        X509Certificate x509Certificate = null;
        Security.addProvider(new BouncyCastleProvider());
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509", "BC");
        BufferedInputStream fileInputStream = (BufferedInputStream) ClassLoader.getSystemResourceAsStream(sm2FilePath);
        x509Certificate = (X509Certificate) certificateFactory.generateCertificate(fileInputStream);
        PublicKey publicKey = x509Certificate.getPublicKey();
        //验签
        SM2 sm2 = new SM2(null, publicKey);
        sm2.usePlainEncoding();
        log.info("[开联通] 验签原串:{}", data);
        return sm2.verify(data.getBytes(StandardCharsets.UTF_8), HexUtil.decodeHex(sign), merchantId.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 对请求体中的content进行加密.
     *
     * @param content 待加密内容
     * @return 加密后的字符串
     * 使用开联通公钥对content中的内容进行加密
     */
    public static String encryptContent(String content,String sm2FilePath) throws CertificateException, NoSuchProviderException {
        //读取公钥
        X509Certificate x509Certificate = null;
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509", "BC");
        BufferedInputStream fileInputStream = (BufferedInputStream) ClassLoader.getSystemResourceAsStream(sm2FilePath);

        x509Certificate = (X509Certificate) certificateFactory.generateCertificate(fileInputStream);
        PublicKey publicKey = x509Certificate.getPublicKey();

        //加密
        SM2 sm2 = new SM2(null, publicKey);
        sm2.setMode(SM2Engine.Mode.C1C2C3);
        return sm2.encryptHex(content, KeyType.PublicKey);
    }

    /**
     * 获取待签名的字符串.
     *
     * @param body 请求体
     * @return 用于签名的字符串
     */
    public static String getToSignString(JSONObject body) {
        if (null == body) {
            return "";
        }
        JSONObject header = body.getJSONObject("head");
        Object content = body.get("content");
        Map param = new TreeMap();
        if (content instanceof JSONArray) {
            String con = ((JSONArray) content).toJSONString();
            param.put("content", con);
        } else if (content instanceof JSONObject) {
            JSONObject contentObject = body.getJSONObject("content");
            // 国密无需对fileContent加签
            contentObject.remove("fileContent");
            Set<String> keysetContent = ((JSONObject) content).keySet();
            keysetContent.stream().forEach(e -> param.put(e, contentObject.get(e)));
        }
        //sign 不参与签名
        header.remove("sign");
        Set<String> keysetHeader = header.keySet();
        keysetHeader.stream().forEach(e -> param.put(e, header.get(e)));
        StringBuilder signbody = new StringBuilder();
        param.keySet().stream().forEach(e -> {
            //去除空字符串和null
            String value = (param.get(e) == null ? "" : param.get(e).toString());
            if (!StringUtils.isEmpty(value)) {
                signbody.append(e).append("=").append(value).append("&");
            }
        });

        //去除末尾&
        return signbody.substring(0, signbody.length() - 1);
    }

}
