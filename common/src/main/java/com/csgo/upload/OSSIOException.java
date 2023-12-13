package com.csgo.upload;

/**
 * @author admin
 * @since 2018/6/18
 */
public class OSSIOException extends RuntimeException {

    public OSSIOException(String message, Throwable cause) {
        super(message, cause);
    }

    public OSSIOException(String message) {
        super(message);
    }
}
