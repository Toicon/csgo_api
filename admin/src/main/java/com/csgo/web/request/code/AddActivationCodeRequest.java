package com.csgo.web.request.code;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * @author admin
 */
@Getter
@Setter
public class AddActivationCodeRequest {

    private List<AddActivationCodeView> views;

}
