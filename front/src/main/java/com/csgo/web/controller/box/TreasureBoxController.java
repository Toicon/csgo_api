package com.csgo.web.controller.box;

import com.csgo.domain.Gift;
import com.csgo.domain.GiftProduct;
import com.csgo.domain.GiftProductRecord;
import com.csgo.domain.plus.box.TreasureBox;
import com.csgo.domain.plus.box.TreasureBoxRelate;
import com.csgo.domain.plus.gift.GiftProductPlus;
import com.csgo.mapper.plus.gift.GiftProductPlusMapper;
import com.csgo.mapper.plus.user.UserMessagePlusMapper;
import com.csgo.service.GiftProductService;
import com.csgo.service.box.TreasureBoxService;
import com.csgo.service.gift.GiftProductRecordService;
import com.csgo.service.gift.GiftService;
import com.csgo.web.response.box.TreasureBoxGiftResponse;
import com.csgo.web.support.SiteContext;
import com.csgo.web.support.UserInfo;
import com.echo.framework.platform.interceptor.session.RequireSession;
import com.echo.framework.platform.web.Api;
import com.echo.framework.platform.web.response.BaseResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author admin
 */
@Api
@RequireSession
@RequestMapping("/treasure-box")
public class TreasureBoxController {

    @Autowired
    private SiteContext siteContext;
    @Autowired
    private GiftProductPlusMapper giftProductPlusMapper;
    @Autowired
    private UserMessagePlusMapper userMessagePlusMapper;

    @Autowired
    private TreasureBoxService treasureBoxService;
    @Autowired
    private GiftService giftService;
    @Autowired
    private GiftProductRecordService giftProductRecordService;
    @Autowired
    private GiftProductService giftProductService;

    @GetMapping("/all")
    public BaseResponse<Map<Integer, TreasureBoxGiftResponse>> findAll() {
        List<TreasureBox> boxList = treasureBoxService.findAll();
        Map<Integer, TreasureBox> boxMap = boxList.stream().collect(Collectors.toMap(TreasureBox::getId, treasureBox -> treasureBox));
        List<TreasureBoxRelate> relateList = treasureBoxService.findRelateByTreasureBoxIds(boxMap.keySet());
        return BaseResponse.<Map<Integer, TreasureBoxGiftResponse>>builder().data(
                relateList.stream().collect(Collectors.toMap(TreasureBoxRelate::getGiftId, relate -> {
                    TreasureBoxGiftResponse response = new TreasureBoxGiftResponse();
                    int treasureBoxId = relate.getTreasureBoxId();
                    response.setTreasureBoxImg(boxMap.get(treasureBoxId).getImg());
                    response.setTreasureBoxHalationImg(boxMap.get(treasureBoxId).getHalationImg());
                    response.setTreasureBoxBgImg(boxMap.get(treasureBoxId).getBgImg());
                    return response;
                }, (value1, value2) -> value1))).get();
    }

    @GetMapping("/gift/{id}")
    public BaseResponse<TreasureBoxGiftResponse> getGift(@PathVariable("id") int id) {
        UserInfo currentUser = siteContext.getCurrentUser();

        TreasureBoxGiftResponse response = new TreasureBoxGiftResponse();
        Gift gift = giftService.queryGiftId(id);
        BeanUtils.copyProperties(gift, response);
        TreasureBox box = treasureBoxService.getByGiftId(id);
        if (null == box) {
            return BaseResponse.<TreasureBoxGiftResponse>builder().data(response).get();
        }
        List<GiftProductRecord> list = giftProductRecordService.find(id, "1");
        if (!CollectionUtils.isEmpty(list)) {
            GiftProduct giftProduct = giftProductService.queryById(list.get(0).getGiftProductId());
            response.setImg(giftProduct.getImg());
        }

        if (gift.getKeyProductId() != null) {
            GiftProductPlus keyProduct = giftProductPlusMapper.selectById(gift.getKeyProductId());
            if (keyProduct != null) {
                response.setKeyProductName(keyProduct.getName());
            }
        }

        if (currentUser != null && gift.getKeyProductId() != null) {
            Integer userKeyNum = userMessagePlusMapper.countKeyByUserIdAndGiftProductId(currentUser.getId(), gift.getKeyProductId());
            response.setUserKeyNum(userKeyNum);
        }

        response.setTreasureBoxName(box.getName());
        response.setTreasureBoxImg(box.getImg());
        response.setTreasureBoxHalationImg(box.getHalationImg());
        response.setTreasureBoxBgImg(box.getBgImg());
        if (StringUtils.hasText(gift.getShowProbability())) {
            response.setProbabilities(Arrays.asList(gift.getShowProbability().split(",")));
        }
        return BaseResponse.<TreasureBoxGiftResponse>builder().data(response).get();
    }
}
