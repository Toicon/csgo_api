package com.csgo.web.controller.roll;

import com.csgo.condition.roll.SearchRollGiftPlusCondition;
import com.csgo.domain.GiftProduct;
import com.csgo.domain.plus.roll.RollGiftPlus;
import com.csgo.domain.plus.roll.RollGiftType;
import com.csgo.domain.plus.roll.RollUserPlus;
import com.csgo.service.GiftProductService;
import com.csgo.service.UserService;
import com.csgo.service.roll.RollGiftService;
import com.csgo.service.roll.RollHelp;
import com.csgo.service.roll.RollUserService;
import com.csgo.support.DataConverter;
import com.csgo.support.StandardExceptionCode;
import com.csgo.web.controller.BackOfficeController;
import com.csgo.web.request.roll.InsertRollGiftPlusRequest;
import com.csgo.web.request.roll.SearchRollGiftPlusRequest;
import com.csgo.web.response.roll.RollGiftPlusResponse;
import com.echo.framework.platform.exception.ApiException;
import com.echo.framework.platform.web.response.BaseResponse;
import com.echo.framework.platform.web.response.PageResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/roll/gift")
@Slf4j
public class AdminRollGiftPlusController extends BackOfficeController {

    @Autowired
    private RollGiftService rollGiftService;
    @Autowired
    private RollUserService rollUserService;
    @Autowired
    private UserService userService;
    @Autowired
    private GiftProductService giftProductService;
    @Autowired
    private RollHelp rollHelp;

    @PostMapping
    public BaseResponse<Void> insert(@Valid @RequestBody InsertRollGiftPlusRequest request) {
        if (CollectionUtils.isEmpty(request.getGiftProductIds())) {
            throw new ApiException(StandardExceptionCode.ROLL_GIFT_NULL, "请选择道具");
        }
        if (null != request.getNumber() && request.getNumber() > 1) {
            for (int i = 0; i < request.getNumber(); i++) {
                insertGift(request);
            }
        } else {
            insertGift(request);
        }
        rollHelp.setRollInfo(request.getRollId());
        return BaseResponse.<Void>builder().get();
    }

    private void insertGift(InsertRollGiftPlusRequest request) {
        request.getGiftProductIds().forEach(giftProductId -> {
            //根据房间id以及商品id查询房间是否存在这个商品信息
            GiftProduct giftProduct = giftProductService.queryById(giftProductId);
            RollGiftPlus rollGift = new RollGiftPlus();
            rollGift.setGiftProductId(giftProductId);
            rollGift.setRollId(request.getRollId());
            rollGift.setGrade(giftProduct.getGrade());
            rollGift.setImg(giftProduct.getImg());
            rollGift.setProductname(giftProduct.getName());
            rollGift.setPrice(giftProduct.getPrice());
            rollGift.setCt(new Date());
            rollGiftService.insert(rollGift);
        });
    }

    /**
     * 查询Roll金币信息
     *
     * @return
     */
    @PostMapping("/pagination")
    public PageResponse<RollGiftPlusResponse> pagination(@Valid @RequestBody SearchRollGiftPlusRequest request) {
        PageResponse<RollGiftPlusResponse> pageResponse = DataConverter.to(rollGiftPlus -> {
            RollGiftPlusResponse response = new RollGiftPlusResponse();
            BeanUtils.copyProperties(rollGiftPlus, response);
            RollUserPlus rollUserPlus = rollUserService.getAppoint(rollGiftPlus.getRollId(), rollGiftPlus.getId());
            if (null != rollUserPlus) {
                response.setUserName(userService.getUserPlus(rollUserPlus.getUserId()).getUserName());
            }
            if (RollGiftType.CANCEL.equals(rollGiftPlus.getType())) {
                response.setUserName("不开");
            }
            if (RollGiftType.INNER.equals(rollGiftPlus.getType())) {
                response.setUserName("内部");
            }
            return response;
        }, rollGiftService.pagination(DataConverter.to(SearchRollGiftPlusCondition.class, request)));
        if (pageResponse.getData() != null && !CollectionUtils.isEmpty(pageResponse.getData().getRows()) && request.isCalculateAmount()) {
            List<RollGiftPlus> rollGiftPluses = rollGiftService.findByRollId(request.getRollId());
            pageResponse.getData().getRows().get(0).setTotalAmount(rollGiftPluses.stream().map(RollGiftPlus::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add));
        }
        return pageResponse;
    }

    @DeleteMapping("/{id}")
    public BaseResponse<Void> delete(@PathVariable("id") int id) {
        RollGiftPlus giftPlus = rollGiftService.get(id);
        rollGiftService.delete(giftPlus.getId());
        rollHelp.setRollInfo(giftPlus.getRollId());
        return BaseResponse.<Void>builder().get();
    }
}
