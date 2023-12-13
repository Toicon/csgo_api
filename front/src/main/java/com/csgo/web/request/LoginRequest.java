package com.csgo.web.request;

import com.csgo.modular.verify.model.BehaviorValidateVM;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author admin
 */
@Getter
@Setter
public class LoginRequest {

    private String username;
    private String password;

    @ApiModelProperty(value = "验证版本 0图形验证码 1行为验证")
    private String v = "0";

    private String imageCode;

    @ApiModelProperty(value = "行为验证参数")
    private BehaviorValidateVM behavior;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImageCode() {
        return imageCode;
    }

    public void setImageCode(String imageCode) {
        this.imageCode = imageCode;
    }
}
