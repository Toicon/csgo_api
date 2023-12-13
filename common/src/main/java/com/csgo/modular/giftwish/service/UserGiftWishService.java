package com.csgo.modular.giftwish.service;


import com.csgo.constants.CommonBizCode;
import com.csgo.domain.plus.gift.GiftPlus;
import com.csgo.domain.plus.gift.GiftProductPlus;
import com.csgo.domain.plus.gift.GiftProductRecordPlus;
import com.csgo.domain.plus.user.UserMessagePlus;
import com.csgo.framework.exception.BizClientException;
import com.csgo.framework.exception.BizServerException;
import com.csgo.framework.util.BeanCopyUtil;
import com.csgo.mapper.plus.gift.GiftPlusMapper;
import com.csgo.mapper.plus.gift.GiftProductPlusMapper;
import com.csgo.mapper.plus.gift.GiftProductRecordPlusMapper;
import com.csgo.mapper.plus.user.UserPlusMapper;
import com.csgo.modular.giftwish.domain.UserGiftWishDO;
import com.csgo.modular.giftwish.mapper.UserGiftWishMapper;
import com.csgo.modular.giftwish.model.front.UserGiftWishRewardVO;
import com.csgo.modular.giftwish.model.front.UserGiftWishVM;
import com.csgo.modular.giftwish.model.front.UserGiftWishVO;
import com.csgo.support.ConcurrencyLimit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Set;

/**
 * @author admin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserGiftWishService {

    private static final BigDecimal WITH_TOTAL = BigDecimal.valueOf(10000);
    private static final BigDecimal PER_WISH = BigDecimal.valueOf(100);

    private final UserPlusMapper userPlusMapper;

    private final GiftPlusMapper giftPlusMapper;
    private final GiftProductPlusMapper giftProductPlusMapper;
    private final GiftProductRecordPlusMapper giftProductRecordPlusMapper;

    private final UserGiftWishMapper userGiftWishMapper;

    private final UserGiftWishRewardService userGiftWishRewardService;

    public UserGiftWishVO getWish(Integer userId, Integer giftId) {
        UserGiftWishDO exist = userGiftWishMapper.getExist(userId, giftId);
        if (exist != null) {
            GiftProductPlus product = giftProductPlusMapper.selectById(exist.getGiftProductId());
            return toWishVO(exist, product);
        }
        return null;
    }

    public UserGiftWishVO toWishVO(UserGiftWishDO exist, GiftProductPlus product) {
        UserGiftWishVO vo = BeanCopyUtil.map(exist, UserGiftWishVO.class);
        Integer giftProductWishCount = userGiftWishMapper.countByProductId(exist.getGiftProductId());
        vo.setGiftProductWishCount(giftProductWishCount);
        // 详情
        vo.setGiftProductId(product.getId());
        vo.setGiftProductName(product.getName());
        vo.setGiftProductImg(product.getImg());
        vo.setGiftProductPrice(product.getPrice());
        vo.setGiftProductGrade(product.getGrade());
        return vo;
    }

    public void addWishOrResetWish(UserGiftWishDO wish, BigDecimal pay, Set<Integer> ownerGiftProductIds) {
        if (wish != null) {
            if (ownerGiftProductIds.contains(wish.getGiftProductId())) {
                log.info("[心愿单重置] userId:{} wishId:{} current:{}", wish.getUserId(), wish.getId(), wish.getWishCurrent());
                wish.setWishCurrent(0);
                userGiftWishMapper.updateById(wish);
            } else {
                Integer wishCurrent = wish.getWishCurrent() + pay.multiply(PER_WISH).intValue();
                wish.setWishCurrent(wishCurrent);
                userGiftWishMapper.updateById(wish);
            }
        }
    }

    public UserGiftWishDO getExist(Integer userId, Integer giftId) {
        return userGiftWishMapper.getExist(userId, giftId);
    }

    @ConcurrencyLimit
    public UserGiftWishVO create(Integer userId, UserGiftWishVM vm) {
        Integer giftId = vm.getGiftId();
        Integer giftProductId = vm.getGiftProductId();

        checkGift(giftId);
        checkNotExist(userId, giftId);
        checkProductInGiftPackage(giftId, giftProductId);

        GiftProductPlus product = giftProductPlusMapper.selectById(giftProductId);
        if (product == null) {
            throw BizServerException.of(CommonBizCode.PRODUCT_NOT_FOUND);
        }

        BigDecimal price = product.getPrice();

        int total = price.multiply(WITH_TOTAL).intValue();

        UserGiftWishDO entity = new UserGiftWishDO();
        entity.setUserId(userId);
        entity.setGiftId(giftId);
        entity.setGiftProductId(giftProductId);
        entity.setWishTotal(total);
        entity.setWishCurrent(0);
        entity.setState(UserGiftWishDO.STATE_ING);

        userGiftWishMapper.insert(entity);

        return toWishVO(entity, product);
    }

    private void checkGift(Integer giftId) {
        GiftPlus gift = giftPlusMapper.selectById(giftId);
        if (gift == null) {
            log.error("[心愿宝箱] giftId:{} 不存在", giftId);
            throw BizClientException.of(CommonBizCode.COMMON_PARAM_ILLEGAL);
        }
        if (!gift.getWishSwitch()) {
            log.error("[心愿宝箱]  giftId:{} 心愿开关未开启", gift);
            throw BizClientException.of(CommonBizCode.COMMON_PARAM_ILLEGAL);
        }
    }

    private void checkNotExist(Integer userId, Integer giftId) {
        UserGiftWishDO exist = userGiftWishMapper.getExist(userId, giftId);
        if (exist != null) {
            throw BizClientException.of(CommonBizCode.GIFT_WISH_EXIST);
        }
    }

    private void checkProductInGiftPackage(Integer giftId, Integer giftProductId) {
        GiftProductRecordPlus record = giftProductRecordPlusMapper.findByGiftIdAndProductId(giftId, giftProductId);
        if (record == null) {
            log.error("饰品不属于该箱子 giftId:{} giftProductId:{}", giftId, giftProductId);
            throw new BizClientException(CommonBizCode.COMMON_PARAM_ILLEGAL);
        }
    }

    @ConcurrencyLimit
    public void cancelWish(Integer userId, Integer id) {
        UserGiftWishDO wish = loadWish(userId, id);
        if (!UserGiftWishDO.STATE_ING.equals(wish.getState())) {
            throw BizClientException.of(CommonBizCode.COMMON_STATUS_INCORRECT);
        }
        wish.setState(UserGiftWishDO.STATE_CANCEL);
        userGiftWishMapper.updateById(wish);
    }

    @ConcurrencyLimit
    public UserGiftWishRewardVO receiveWish(Integer userId, Integer id) {
        UserGiftWishDO wish = loadWish(userId, id);
        if (UserGiftWishDO.STATE_RECEIVE.equals(wish.getState())) {
            throw new BizClientException(CommonBizCode.COMMON_CAN_NOT_REPEAT);
        }
        if (!UserGiftWishDO.STATE_ING.equals(wish.getState())) {
            throw BizClientException.of(CommonBizCode.COMMON_STATUS_INCORRECT);
        }
        if (wish.getWishCurrent() < wish.getWishTotal()) {
            throw BizClientException.of(CommonBizCode.GIFT_WISH_UN_COMPLETE);
        }
        wish.setState(UserGiftWishDO.STATE_RECEIVE);
        userGiftWishMapper.updateById(wish);

        GiftProductPlus product = giftProductPlusMapper.selectById(wish.getGiftProductId());
        if (product == null) {
            throw BizServerException.of(CommonBizCode.PRODUCT_NOT_FOUND);
        }

        UserMessagePlus message = userGiftWishRewardService.handleReward(wish, product);

        UserGiftWishRewardVO vo = new UserGiftWishRewardVO();
        vo.setGiftProductId(product.getId());
        vo.setGiftProductName(product.getName());
        vo.setGiftProductImg(product.getImg());
        vo.setGiftProductPrice(product.getPrice());
        vo.setGiftProductGrade(product.getGrade());
        vo.setUserMessageId(message.getId());

        return vo;
    }

    private UserGiftWishDO loadWish(Integer userId, Integer id) {
        UserGiftWishDO wish = userGiftWishMapper.selectById(id);
        if (wish == null) {
            throw BizServerException.of(CommonBizCode.COMMON_DATA_NOT_FOUND);
        }
        if (!wish.getUserId().equals(userId)) {
            log.error("wish:{} not belong to user:{}", wish.getId(), userId);
            throw BizServerException.of(CommonBizCode.COMMON_DATA_NOT_FOUND);
        }
        return wish;
    }

}

