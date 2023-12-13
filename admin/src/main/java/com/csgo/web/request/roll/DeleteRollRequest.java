package com.csgo.web.request.roll;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author admin
 */
@Getter
@Setter
public class DeleteRollRequest {

    private List<Integer> ids;
}
