package com.csgo.web.request.zbk;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author admin
 */
@Getter
@Setter
public class FindZBKRequest {

    private List<String> productNames;
}
