package com.csgo.web.request.blindbox;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author admin
 */
@Getter
@Setter
public class AddBlindBoxByGiftRequest {

    public List<RelateGiftView> relateGiftList;
    public Integer typeId;
}
