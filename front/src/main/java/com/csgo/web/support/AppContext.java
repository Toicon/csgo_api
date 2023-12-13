package com.csgo.web.support;

import lombok.Getter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Getter
public class AppContext {

    private final HttpServletRequest request;
    private final HttpServletResponse response;

    public AppContext(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }
}