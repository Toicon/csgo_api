package com.csgo.util.klt;

import cfca.sadk.algorithm.sm2.SM2PrivateKey;
import cfca.sadk.util.KeyUtil;
import com.csgo.framework.exception.BizException;
import com.csgo.util.sand.CertUtil;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.util.StringUtils;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author admin
 */
@Slf4j
public class KltCertUtil {

    private static final String PUBLIC_KEY = "public_key";
    private static final String PRIVATE_KEY = "private_key";

    private static final ConcurrentHashMap<String, Object> KEYS = new ConcurrentHashMap<>();

    public static void init(String publicKeyPath, String privateKeyPath, String keyPassword) {
        // 加载公钥
        if (StringUtils.hasText(publicKeyPath) && null == KEYS.get(PUBLIC_KEY)) {
            try (InputStream inputStream = KltCertUtil.class.getClassLoader().getResourceAsStream(publicKeyPath)) {
                PublicKey publicKey = getPublicKey(inputStream);
                KEYS.put(PUBLIC_KEY, publicKey);
            } catch (Exception e) {
                log.error("[开联通] init public cert error:" + e.getMessage(), e);
            }
        }
        // 加载私钥
        if (StringUtils.hasText(privateKeyPath) && StringUtils.hasText(keyPassword) && null == KEYS.get(PRIVATE_KEY)) {
            try (InputStream inputStream = KltCertUtil.class.getClassLoader().getResourceAsStream(privateKeyPath)) {
                //读取文件获取私钥
                SM2PrivateKey privateKey = KeyUtil.getPrivateKeyFromSM2(inputStream, keyPassword);
                KEYS.put(PRIVATE_KEY, privateKey);
            } catch (Exception e) {
                log.error("[开联通] init private cert error:" + e.getMessage(), e);
            }
        }
    }

    public static PublicKey getPublicKey() {
        PublicKey publicKey = (PublicKey) KEYS.get(PUBLIC_KEY);
        if (publicKey == null) {
            log.error("[开联通] 公钥证书配置错误");
            throw BizException.of("支付异常：证书错误");
        }
        return publicKey;
    }

    public static SM2PrivateKey getPrivateKey() {
        SM2PrivateKey privateKey = (SM2PrivateKey) KEYS.get(PRIVATE_KEY);
        if (privateKey == null) {
            log.error("[开联通] 私钥证书配置错误");
            throw BizException.of("支付异常：证书错误");
        }
        return privateKey;
    }

    private static PublicKey getPublicKey(InputStream inputStream) throws Exception {
        try (InputStream ignored = inputStream) {
            Security.addProvider(new BouncyCastleProvider());
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509", "BC");
            X509Certificate x509Certificate = (X509Certificate) certificateFactory.generateCertificate(inputStream);
            return x509Certificate.getPublicKey();
        } catch (Exception e) {
            throw new Exception("读取公钥异常", e);
        }
    }

}
