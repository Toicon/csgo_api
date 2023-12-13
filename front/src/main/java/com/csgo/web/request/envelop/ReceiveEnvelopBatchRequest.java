package com.csgo.web.request.envelop;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author admin
 */
@Data
public class ReceiveEnvelopBatchRequest {

    @NotNull(message = "红包ID不能为空")
    private List<Integer> envelopIdList;

}
