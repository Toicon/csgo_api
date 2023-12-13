package com.csgo.util;

import java.security.MessageDigest;

public class MD5 {
    /**
     * MD5方法
     *
     * @param text    明文
     * @param charset 密钥
     * @return 密文
     * @throws Exception
     */
    public static String md5(String text, String charset) throws Exception {
        if (charset == null || charset.length() == 0) {
            charset = "UTF-8";
        }
        byte[] bytes = text.getBytes(charset);

        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        messageDigest.update(bytes);
        bytes = messageDigest.digest();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            if ((bytes[i] & 0xff) < 0x10) {
                sb.append("0");
            }

            sb.append(Long.toString(bytes[i] & 0xff, 16));
        }

        return sb.toString().toLowerCase();
    }

    /**
     * 32位MD5加密的大写字符串
     *
     * @param s
     * @return
     */
    public final static String MD5(String s) {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F'};
        try {
            byte[] btInput = s.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * MD5验证方法
     *
     * @param text    明文
     * @param charset 字符编码
     * @param md5     密文
     * @return true/false
     * @throws Exception
     */
    public static boolean verify(String text, String charset, String md5) throws Exception {
        String md5Text = md5(text, charset);
        if (md5Text.equalsIgnoreCase(md5)) {
            return true;
        }

        return false;
    }


}