package com.csgo.web.response.authorize;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class AuthorizeResponse {
    private String code;
    private String description;
    private List<AuthorizeResponse> items;
}
