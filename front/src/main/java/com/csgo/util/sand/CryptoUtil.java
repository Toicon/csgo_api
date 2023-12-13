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
 * 企业产品团队       2016-10-12       加解密工具类.
 * =============================================================================
 */
package com.csgo.util.sand;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;


/**
 * @version 2.0.0
 * @ClassName: CryptoUtil
 * @Description: sdk加解密工具类，主要用于签名、验证、RSA加解密等
 */

@Slf4j
public class CryptoUtil {

    /**
     * 数字签名函数入口
     *
     * @param plainBytes    待签名明文字节数组
     * @param privateKey    签名使用私钥
     * @param signAlgorithm 签名算法
     * @return 签名后的字节数组
     * @throws Exception
     */
    public static byte[] digitalSign(byte[] plainBytes, PrivateKey privateKey, String signAlgorithm) throws Exception {
        try {
            Signature signature = Signature.getInstance(signAlgorithm);
            signature.initSign(privateKey);
            signature.update(plainBytes);

            return signature.sign();
        } catch (NoSuchAlgorithmException e) {
            throw new Exception(String.format("数字签名时没有[%s]此类算法", signAlgorithm));
        } catch (InvalidKeyException e) {
            throw new Exception("数字签名时私钥无效", e);
        } catch (SignatureException e) {
            throw new Exception("数字签名时出现异常", e);
        }
    }

    /**
     * 验证数字签名函数入口
     *
     * @param plainBytes    待验签明文字节数组
     * @param signBytes     待验签签名后字节数组
     * @param publicKey     验签使用公钥
     * @param signAlgorithm 签名算法
     * @return 验签是否通过
     * @throws Exception
     */
    public static boolean verifyDigitalSign(byte[] plainBytes, byte[] signBytes, PublicKey publicKey, String signAlgorithm) throws Exception {
        boolean isValid = false;
        try {
            Signature signature = Signature.getInstance(signAlgorithm);
            signature.initVerify(publicKey);
            signature.update(plainBytes);
            isValid = signature.verify(signBytes);
            return isValid;
        } catch (NoSuchAlgorithmException e) {
            throw new Exception(String.format("验证数字签名时没有[%s]此类算法", signAlgorithm), e);
        } catch (InvalidKeyException e) {
            throw new Exception("验证数字签名时公钥无效", e);
        } catch (SignatureException e) {
            throw new Exception("验证数字签名时出现异常", e);
        }
    }


    public static String rsaSign(String content, PrivateKey priKey, String charset) throws Exception {
        Signature signature = java.security.Signature.getInstance("SHA1WithRSA");
        signature.initSign(priKey);
        if (charset == null || "".equals(charset)) {
            signature.update(content.getBytes());
        } else {
            signature.update(content.getBytes(charset));
        }
        byte[] signed = signature.sign();
        return new String(Base64.encodeBase64(signed));
    }

    public static boolean rsaVerify(String content, String sign, PublicKey pubKey, String charset) throws Exception {
        try {
            Signature signature = Signature.getInstance("SHA1WithRSA");
            signature.initVerify(pubKey);
            if (charset == null || "".equals(charset)) {
                signature.update(content.getBytes());
            } else {
                signature.update(content.getBytes(charset));
            }
            return signature.verify(Base64.decodeBase64(sign.getBytes()));
        } catch (Exception e) {
            log.info(e.getMessage());
            throw e;
        }
    }
}
