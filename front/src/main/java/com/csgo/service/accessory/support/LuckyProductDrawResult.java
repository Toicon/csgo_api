package com.csgo.service.accessory.support;

import com.csgo.domain.plus.config.LuckyProductDrawRecord;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * @author admin
 */
@Getter
@AllArgsConstructor
public class LuckyProductDrawResult {

    private final boolean hit;
    private final LuckyProductDrawRecord record;
}
