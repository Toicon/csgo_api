package com.csgo.web.controller.blindbox;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.blind.SearchBlindBoxCondition;
import com.csgo.domain.plus.blind.BlindBoxDTO;
import com.csgo.domain.plus.blind.BlindBoxPlus;
import com.csgo.domain.plus.box.TreasureBoxRelate;
import com.csgo.domain.plus.gift.GiftPlus;
import com.csgo.service.GiftService;
import com.csgo.service.blind.BlindBoxService;
import com.csgo.service.box.TreasureBoxService;
import com.csgo.support.BusinessException;
import com.csgo.support.DataConverter;
import com.csgo.support.ExceptionCode;
import com.csgo.support.StandardExceptionCode;
import com.csgo.util.BeanUtilsEx;
import com.csgo.web.controller.BackOfficeController;
import com.csgo.web.request.blindbox.AddBlindBoxByGiftRequest;
import com.csgo.web.request.blindbox.BlindBoxRequest;
import com.csgo.web.request.blindbox.RelateGiftView;
import com.csgo.web.request.blindbox.SearchBlindBoxRequest;
import com.csgo.web.response.blindbox.BlindBoxResponse;
import com.echo.framework.platform.exception.ApiException;
import com.echo.framework.platform.web.Api;
import com.echo.framework.platform.web.response.BaseResponse;
import com.echo.framework.platform.web.response.PageResponse;

@Api
@RequestMapping("/blindBox/blindBox")
public class AdminBlindBoxController extends BackOfficeController {

    @Autowired
    private BlindBoxService blindBoxService;
    @Autowired
    private GiftService giftService;
    @Autowired
    private TreasureBoxService treasureBoxService;

    @PostMapping("/pagination")
    public PageResponse<BlindBoxResponse> pagination(@Valid @RequestBody SearchBlindBoxRequest request) {
        SearchBlindBoxCondition condition = DataConverter.to(SearchBlindBoxCondition.class, request);
        Page<BlindBoxDTO> page = blindBoxService.pagination(condition);
        return DataConverter.to(blindBoxDTO -> {
            BlindBoxResponse response = new BlindBoxResponse();
            BeanUtils.copyProperties(blindBoxDTO, response);
            return response;
        }, page);
    }

    @PutMapping("/gift/sync")
    public BaseResponse<Void> syncByGift(@RequestParam(value = "boxId", required = false) Integer boxId, @RequestParam(value = "giftId", required = false) Integer giftId) {
        if (boxId != null) {
            BlindBoxPlus blindBox = blindBoxService.get(boxId);
            GiftPlus gift = giftService.getPlus(giftId);
            syncBySingleBox(blindBox, gift);
            return BaseResponse.<Void>builder().get();
        }
        List<BlindBoxPlus> blindBoxList = blindBoxService.findWithGift();
        Map<Integer, GiftPlus> giftMap = giftService.findByIds(blindBoxList.stream().map(BlindBoxPlus::getGiftId).collect(Collectors.toSet())).stream().collect(Collectors.toMap(GiftPlus::getId, giftPlus -> giftPlus));
        for (BlindBoxPlus blindBox : blindBoxList) {
            syncBySingleBox(blindBox, giftMap.get(blindBox.getGiftId()));
        }
        return BaseResponse.<Void>builder().get();
    }

    private void syncBySingleBox(BlindBoxPlus blindBox, GiftPlus gift) {
        TreasureBoxRelate relate = treasureBoxService.getRelate(gift.getId());
        RelateGiftView relateGift = new RelateGiftView();
        BeanUtils.copyProperties(gift, relateGift);
        if (null != relate) {
            relateGift.setBoxImg(treasureBoxService.get(relate.getTreasureBoxId()).getImg());
        }
        blindBoxService.syncByGift(blindBox, relateGift);
    }

    @PostMapping("/gift")
    public BaseResponse<Void> addGiftBox(@RequestBody AddBlindBoxByGiftRequest request) {
        if (CollectionUtils.isEmpty(request.getRelateGiftList())) {
            throw new ApiException(StandardExceptionCode.BLIND_BOX_ADD_BY_GIFT_FAILURE, "请选择礼包");
        }
        if (request.getTypeId() == null) {
            throw new ApiException(StandardExceptionCode.BLIND_BOX_ADD_BY_GIFT_FAILURE, "请选择类型");
        }
        blindBoxService.batchInsert(request.getRelateGiftList(), request.getTypeId());
        return BaseResponse.<Void>builder().get();
    }

    @PostMapping("add")
    public BaseResponse<Void> insert(@Valid @RequestBody BlindBoxRequest request) {
        BlindBoxPlus boxPlus = new BlindBoxPlus();
        BeanUtilsEx.copyProperties(request, boxPlus);
        blindBoxService.insert(boxPlus);
        return BaseResponse.<Void>builder().get();
    }

    @PutMapping("{id}")
    public BaseResponse<Void> update(@PathVariable("id") int id, @Valid @RequestBody BlindBoxRequest request) {
        BlindBoxPlus boxPlus = blindBoxService.get(id);
        BlindBoxPlus blindBoxPlus = blindBoxService.findByName(request.getName());
        if (null != blindBoxPlus && !blindBoxPlus.getId().equals(boxPlus.getId())) {
            throw new BusinessException(ExceptionCode.BIND_BOX_EXISTS);
        }
        BeanUtilsEx.copyProperties(request, boxPlus);
        blindBoxService.update(boxPlus);
        return BaseResponse.<Void>builder().get();
    }

    @DeleteMapping("deleteBath")
    public BaseResponse<Void> deleteBath(@RequestBody List<Integer> ids) {
        blindBoxService.deleteBath(ids);
        return BaseResponse.<Void>builder().get();
    }

}
