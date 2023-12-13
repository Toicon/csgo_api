package com.csgo.service.accessory.support;

import com.csgo.domain.plus.accessory.LuckyProductDTO;
import com.csgo.domain.plus.config.LuckyProductDrawRecord;
import com.csgo.domain.plus.config.SystemConfigFacade;
import com.csgo.domain.plus.user.UserPlus;
import com.csgo.service.jackpot.JackpotService;
import com.csgo.service.jackpot.UpgradeJackpotService;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author admin
 */
@Slf4j
public class LuckyProductDrawer {

    private final UserPlus player;
    private final BigDecimal pay;
    private final LuckyProductDTO luckyProduct;
    private final BigDecimal luckyValue;
    private final JackpotService jackpotService;
    private final UpgradeJackpotService upgradeJackpotService;
    private final SystemConfigFacade config;
    private final List<String> messageList;
    private final LuckyProductDrawRecord record;

    public LuckyProductDrawer(LuckyProductDrawerCondition condition, JackpotService jackpotService, UpgradeJackpotService upgradeJackpotService, SystemConfigFacade config) {
        this.player = condition.getPlayer();
        this.pay = condition.getPay();
        this.luckyProduct = condition.getLuckyProduct();
        this.luckyValue = condition.getLuckyValue();
        this.jackpotService = jackpotService;
        this.config = config;
        this.messageList = new ArrayList<>();
        this.record = this.record(condition.getLuckyId());
        this.upgradeJackpotService = upgradeJackpotService;

    }

    private LuckyProductDrawRecord record(int luckyId) {
        LuckyProductDrawRecord record = new LuckyProductDrawRecord();
        record.setUserId(player.getId());
        record.setLuckyId(luckyId);
        record.setLucky(player.getAccessoryLucky());
        record.setProductName(luckyProduct.getProductName());
        record.setPrice(luckyProduct.getPrice());
        record.setRate(luckyValue);
        return record;
    }


    public LuckyProductDrawResult draw() {
        boolean isHit = hit(luckyValue);
        boolean realHit = upgradeJackpotService.hit(luckyProduct.getPrice(), player, isHit);
        return result(realHit);
    }

    private LuckyProductDrawResult result(boolean hit) {
        record.setHit(hit);
        record.setCt(new Date());
        record.setMessage(String.join("->", messageList));
        return new LuckyProductDrawResult(hit, record);
    }


    private void log(String message) {
        log.info(message);
        messageList.add(message);
    }

    private boolean hit(BigDecimal probability) {
        int random = (int) (Math.random() * 10001);
        return BigDecimal.valueOf(random).compareTo(probability.multiply(new BigDecimal(100))) <= 0;
    }


}
