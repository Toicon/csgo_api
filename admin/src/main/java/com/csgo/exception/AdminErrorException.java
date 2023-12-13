package com.csgo.exception;


public class AdminErrorException extends RuntimeException {

    private String msg;

    public AdminErrorException(String msg) {
        super(msg);
        this.msg = msg;
    }
}
