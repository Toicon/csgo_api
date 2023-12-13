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
 * 企业产品团队       2016-10-12       证书工具类.
 * =============================================================================
 */
package com.csgo.util.sand;
//银联聚合码

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @version 2.0.0
 * @ClassName: CertUtil
 * @Description: sdk证书工具类，主要用于对证书的加载和使用
 */
@Slf4j
public class CertUtil {

    private static final String PUBLIC_KEY = "public_key";
    private static final String PRIVATE_KEY = "private_key";
    private static final String CLASSPATH = "classpath:";

    private static final ConcurrentHashMap<String, Object> keys = new ConcurrentHashMap<>();

    public static void init(String publicKeyPath, String privateKeyPath, String keyPassword) {
        // 加载公钥
        if (StringUtils.hasText(publicKeyPath) && null == keys.get(PUBLIC_KEY)) {
            if (publicKeyPath.startsWith(CLASSPATH)) {
                try (InputStream inputStream = CertUtil.class.getClassLoader()
                        .getResourceAsStream(publicKeyPath.substring(CLASSPATH.length()))) {
                    PublicKey publicKey = getPublicKey(inputStream);
                    keys.put(PUBLIC_KEY, publicKey);
                } catch (Exception e) {
                    log.info("init public cert error:{}", e.getMessage());
                }
            } else {
                try (InputStream inputStream = new FileInputStream(publicKeyPath)) {
                    PublicKey publicKey = getPublicKey(inputStream);
                    keys.put(PUBLIC_KEY, publicKey);
                } catch (Exception e) {
                    log.info("init public cert error:{}", e.getMessage());
                }
            }
        }
        // 加载私钥
        if (StringUtils.hasText(privateKeyPath) && StringUtils.hasText(keyPassword) && null == keys.get(PRIVATE_KEY)) {
            if (privateKeyPath.startsWith(CLASSPATH)) {
                try (InputStream inputStream = CertUtil.class.getClassLoader()
                        .getResourceAsStream(privateKeyPath.substring(CLASSPATH.length()))) {
                    PrivateKey privateKey = CertUtil.getPrivateKey(inputStream, keyPassword);
                    keys.put(PRIVATE_KEY, privateKey);
                } catch (Exception e) {
                    log.info("init private cert error:{}", e.getMessage());
                }
            } else {
                try (InputStream inputStream = new FileInputStream(privateKeyPath)) {
                    PrivateKey privateKey = CertUtil.getPrivateKey(inputStream, keyPassword);
                    keys.put(PRIVATE_KEY, privateKey);
                } catch (Exception e) {
                    log.info("init private cert error:{}", e.getMessage());
                }
            }
        }
    }

    public static PublicKey getPublicKey() {
        return (PublicKey) keys.get(PUBLIC_KEY);
    }

    public static PrivateKey getPrivateKey() {
        return (PrivateKey) keys.get(PRIVATE_KEY);
    }


    private static PublicKey getPublicKey(InputStream inputStream) throws Exception {
        try (InputStream ignored = inputStream) {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate oCert = (X509Certificate) cf.generateCertificate(inputStream);
            return oCert.getPublicKey();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("读取公钥异常");
        }
    }


    /**
     * 获取私钥对象
     *
     * @param inputStream 私钥输入流
     * @return 私钥对象
     * @throws Exception
     */
    private static PrivateKey getPrivateKey(InputStream inputStream, String password) throws Exception {
        try (InputStream ignored = inputStream) {
            KeyStore ks = KeyStore.getInstance("PKCS12");
            char[] nPassword = null;
            if (StringUtils.hasText(password)) {
                nPassword = password.toCharArray();
            }
            ks.load(inputStream, nPassword);
            Enumeration<String> enumas = ks.aliases();
            String keyAlias = null;
            if (enumas.hasMoreElements()) {
                keyAlias = enumas.nextElement();
            }
            return (PrivateKey) ks.getKey(keyAlias, nPassword);
        } catch (FileNotFoundException e) {
            throw new Exception("私钥路径文件不存在");
        } catch (IOException e) {
            throw new Exception(e);
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("生成私钥对象异常");
        }
    }

    public static PrivateKey getPrivateKeyFromPKCS8(String algorithm, byte[] encodedKey) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(encodedKey));
    }

    public static PublicKey getPublicKeyFromX509(String algorithm, byte[] encodedKey) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        return keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
    }

}
