package com.csgo.upload;

import com.aliyun.oss.OSSClient;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author admin
 * @since 2018/6/19
 */
public class AutoCloseOSSClient extends OSSClient implements Closeable {

    public AutoCloseOSSClient(String endpoint, String accessKeyId, String secretAccessKey) {
        super(endpoint, accessKeyId, secretAccessKey);
    }

    @Override
    public void close() throws IOException {
        super.shutdown();
    }
}
