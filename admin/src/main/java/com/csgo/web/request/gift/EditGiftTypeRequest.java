package com.csgo.web.request.gift;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EditGiftTypeRequest {
    private String img;
    private String name;
    private String type;
    private Boolean hidden;
    private Integer sort;
}