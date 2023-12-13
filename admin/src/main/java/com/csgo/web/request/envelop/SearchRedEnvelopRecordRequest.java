package com.csgo.web.request.envelop;

import com.echo.framework.platform.web.request.PageRequest;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by admin on 2021/4/29
 */
@Setter
@Getter
public class SearchRedEnvelopRecordRequest extends PageRequest {
    private Integer redEnvelopId;
}
