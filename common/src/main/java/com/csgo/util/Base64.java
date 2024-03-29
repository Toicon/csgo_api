package com.csgo.util;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.nio.charset.StandardCharsets;

/**
 * Base64工具类实现加密解密
 *
 * @author admin
 * @create 2019/11/11
 */
public class Base64 {

    // 加密
    public static String getBase64(String str) {
        byte[] b = null;
        String s = null;
        b = str.getBytes(StandardCharsets.UTF_8);
        if (b != null) {
            s = new BASE64Encoder().encode(b);
        }
        return s;
    }

    // 解密
    public static String getFromBase64(String s) {
        byte[] b = null;
        String result = null;
        if (s != null) {
            BASE64Decoder decoder = new BASE64Decoder();
            try {
                b = decoder.decodeBuffer(s);
                result = new String(b, StandardCharsets.UTF_8);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
