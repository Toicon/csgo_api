package com.csgo.web.request.role;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by Admin on 2021/11/4
 */
@Setter
@Getter
public class EditRoleRequest {
    private String name;
    private String content;
    private String dataScope;
    private List<String> codes;
}
