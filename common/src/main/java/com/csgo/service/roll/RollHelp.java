package com.csgo.service.roll;

import com.csgo.domain.RollInfoView;
import com.csgo.domain.plus.roll.RollCoins;
import com.csgo.domain.plus.roll.RollGiftPlus;
import com.csgo.domain.plus.roll.RollUserPlus;
import com.csgo.domain.response.RollGiftResponse;
import com.csgo.mapper.plus.roll.RollCoinsMapper;
import com.csgo.mapper.plus.roll.RollGiftPlusMapper;
import com.csgo.mapper.plus.roll.RollUserPlusMapper;
import com.csgo.redis.RedisTemplateFacde;
import com.echo.framework.support.jackson.json.JSON;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author admin
 */
@Service
public class RollHelp {
    private static final String ROLL_KEY = "ROLL_INFO_CACHE_";
    @Autowired
    private RedisTemplateFacde redisTemplateFacde;
    @Autowired
    private RollUserPlusMapper rollUserMapper;
    @Autowired
    private RollGiftPlusMapper rollGiftMapper;
    @Autowired
    private RollCoinsMapper rollCoinsMapper;

    public RollInfoView buildRollInfo(int rollId) {
        RollInfoView view = new RollInfoView();
        List<RollUserPlus> rollUsers = rollUserMapper.findByRollId(rollId);
        List<RollGiftPlus> rollGifts = Optional.ofNullable(rollGiftMapper.find(rollId)).orElse(new ArrayList<>());
        view.setGiftResponses(new ArrayList<>());
        view.getGiftResponses().addAll(rollGifts.stream().map(rollGift -> {
            RollGiftResponse rollGiftResponse = new RollGiftResponse();
            BeanUtils.copyProperties(rollGift, rollGiftResponse);
            return rollGiftResponse;
        }).collect(Collectors.toList()));

        List<RollCoins> rollCoins = Optional.ofNullable(rollCoinsMapper.find(rollId)).orElse(new ArrayList<>());
        view.getGiftResponses().addAll(rollCoins.stream().map(coins -> {
            RollGiftResponse rollGiftResponse = new RollGiftResponse();
            rollGiftResponse.setPrice(coins.getAmount());
            rollGiftResponse.setImg(coins.getImg());
            return rollGiftResponse;
        }).collect(Collectors.toList()));
        if (!CollectionUtils.isEmpty(view.getGiftResponses())) {
            BigDecimal diamondTotalPrice = view.getGiftResponses().stream().filter(item -> null != item.getGiftProductId()).map(RollGiftResponse::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal totalPrice = view.getGiftResponses().stream().filter(item -> null == item.getGiftProductId()).map(RollGiftResponse::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            view.setTotalPrice(totalPrice);
            view.setDiamondTotalPrice(diamondTotalPrice);
            view.setTotalGiftNum(view.getGiftResponses().size());
        }
        //根据rollid查询到当前房间参与的人数
        view.setUserNum(CollectionUtils.isEmpty(rollUsers) ? 0 : rollUsers.size());
        if (view.getGiftResponses().size() > 3) {
            view.setGiftResponses(view.getGiftResponses().subList(0, 3));
        }
        return view;
    }

    public void setRollInfo(Integer rollId) {
        if (null == rollId || rollId == 0) {
            return;
        }
        RollInfoView view = buildRollInfo(rollId);
        redisTemplateFacde.set(getKey(rollId), JSON.toJSON(view));
    }

    public void joinRoll(Integer rollId) {
        if (null == rollId || rollId == 0) {
            return;
        }
        RollInfoView view;
        String json = redisTemplateFacde.get(getKey(rollId));
        if (StringUtils.hasText(json)) {
            view = JSON.fromJSON(json, RollInfoView.class);
            view.setUserNum(view.getUserNum() + 1);
        } else {
            view = buildRollInfo(rollId);
        }
        redisTemplateFacde.set(getKey(rollId), JSON.toJSON(view));
    }

    public RollInfoView getRollInfo(int rollId) {
        String json = redisTemplateFacde.get(getKey(rollId));
        if (StringUtils.hasText(json)) {
            return JSON.fromJSON(json, RollInfoView.class);
        }
        RollInfoView view = buildRollInfo(rollId);
        redisTemplateFacde.set(getKey(rollId), JSON.toJSON(view));
        return view;
    }

    private String getKey(int rollId) {
        return ROLL_KEY + rollId;
    }


}
