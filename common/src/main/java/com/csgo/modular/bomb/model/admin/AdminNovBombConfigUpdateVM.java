package com.csgo.modular.bomb.model.admin;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 * @author admin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class AdminNovBombConfigUpdateVM extends AdminNovBombConfigCreateVM {

    @NotNull(message = "ID不能为空")
    private Integer id;

}
