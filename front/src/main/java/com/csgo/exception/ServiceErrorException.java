package com.csgo.exception;


public class ServiceErrorException extends RuntimeException {

    private String msg;

    public ServiceErrorException(String msg) {
        super(msg);
        this.msg = msg;
    }
}
