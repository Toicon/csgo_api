package com.csgo.web.request.shop;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author admin
 */
@Getter
@Setter
public class BatchDeleteShopRequest {

    private List<Integer> ids;
}
