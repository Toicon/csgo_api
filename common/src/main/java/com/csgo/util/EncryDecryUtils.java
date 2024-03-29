package com.csgo.util;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 报文加解密工具类
 */
public class EncryDecryUtils {


    // 对数据进行base64加密
    public static String base64Encrypt(String str) {
        byte[] b = null;
        String s = null;
        b = str.getBytes(StandardCharsets.UTF_8);
        if (b != null) {
            s = new BASE64Encoder().encode(b);
        }
        return s;
    }


    // 对数据进行base64解密
    public static String base64Decrypt(String str) {
        byte[] b = null;
        String result = null;
        if (str != null) {
            BASE64Decoder decoder = new BASE64Decoder();
            try {
                b = decoder.decodeBuffer(str);
                result = new String(b, StandardCharsets.UTF_8);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    //对返回的数据 先用base64解密 再用3Des解密
    public static String decryptFromBase64DES(String key, String data) {
        String result = null;
        byte[] keyByte = key.getBytes();
        byte[] base64Byte = EncryDecryUtils.getFromBase64byte(data);
        try {
            result = new String(DESUtils.Union3DesDecrypt(keyByte, base64Byte), StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    // 把一个base64的加密字符串转成数组
    public static byte[] getFromBase64byte(String s) {
        byte[] b = null;
        String result = null;
        if (s != null) {
            BASE64Decoder decoder = new BASE64Decoder();
            try {
                b = decoder.decodeBuffer(s);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return b;
    }

    //把数据 先用3des加密 再用base64加密
    public static String encryptFromDESBase64(String key, String data) {
        String result = null;
        byte[] b = DESUtils.Union3DesEncrypt(key.getBytes(), data.getBytes());
        result = EncryDecryUtils.base64Encrypt(b);
        return result;
    }

    // 把传入的数组进行base64加密
    public static String base64Encrypt(byte[] b) {
        String s = null;
        if (b != null) {
            s = new BASE64Encoder().encode(b);
        }
        return s;
    }

    public static String md5(String str) {
        if (str == null) {
            return null;
        }
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(str.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            return str;
        }
        byte[] byteArray = messageDigest.digest();
        StringBuffer md5StrBuff = new StringBuffer();
        int aa;

        for (int i = 0; i < byteArray.length; i++) {
            aa = byteArray[i];
            aa = aa & 0xff;
            if (Integer.toHexString(aa).length() == 1)
                md5StrBuff.append("0").append(Integer.toHexString(aa));
            else
                md5StrBuff.append(Integer.toHexString(aa));
        }
        return md5StrBuff.toString();
    }


}
