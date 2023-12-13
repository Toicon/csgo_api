package com.csgo.web.controller.box;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.box.SearchTreasureBoxCondition;
import com.csgo.domain.plus.gift.GiftType;
import com.csgo.domain.plus.box.TreasureBox;
import com.csgo.domain.plus.box.TreasureBoxRelate;
import com.csgo.domain.plus.gift.GiftPlus;
import com.csgo.service.GiftService;
import com.csgo.service.GiftTypeService;
import com.csgo.service.box.TreasureBoxService;
import com.csgo.support.DataConverter;
import com.csgo.support.StandardExceptionCode;
import com.csgo.util.BeanUtilsEx;
import com.csgo.web.controller.BackOfficeController;
import com.csgo.web.request.box.OperateTreasureBoxRequest;
import com.csgo.web.request.box.SearchTreasureBoxRequest;
import com.csgo.web.request.box.TreasureBoxRelateRequest;
import com.csgo.web.response.box.TreasureBoxResponse;
import com.csgo.web.response.gift.GiftResponse;
import com.csgo.web.response.gift.GiftTypeResponse;
import com.csgo.web.support.Log;
import com.echo.framework.platform.exception.ApiException;
import com.echo.framework.platform.web.Api;
import com.echo.framework.platform.web.response.BaseResponse;
import com.echo.framework.platform.web.response.PageResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author admin
 */
@Api
@RequestMapping("/treasure-box")
public class TreasureBoxController extends BackOfficeController {
    @Autowired
    private TreasureBoxService treasureBoxService;
    @Autowired
    private GiftService giftService;
    @Autowired
    private GiftTypeService giftTypeService;

    @GetMapping("/{id}/gifttype/all")
    @Log(desc = "查询宝箱关联所有礼包类型")
    public BaseResponse<List<GiftTypeResponse>> findGiftType(@PathVariable("id") int id) {
        List<GiftType> giftTypes = giftTypeService.findAll();
        Set<Integer> existGiftIds = treasureBoxService.findAllRelateExclude(id).stream().map(TreasureBoxRelate::getGiftId).collect(Collectors.toSet());
        return BaseResponse.<List<GiftTypeResponse>>builder().data(giftTypes.stream().map(giftType -> {
            GiftTypeResponse response = new GiftTypeResponse();
            BeanUtils.copyProperties(giftType, response);
            response.setGiftList(giftService.findByTypeId(giftType.getId()).stream().filter(giftPlus -> !existGiftIds.contains(giftPlus.getId())).map(gift -> {
                GiftResponse giftResponse = new GiftResponse();
                BeanUtils.copyProperties(gift, giftResponse);
                return giftResponse;
            }).collect(Collectors.toList()));
            return response;
        }).collect(Collectors.toList())).get();
    }


    @PostMapping
    @Log(desc = "添加宝箱")
    public BaseResponse<Void> insert(@Valid @RequestBody OperateTreasureBoxRequest request) {
        TreasureBox treasureBox = new TreasureBox();
        BeanUtils.copyProperties(request, treasureBox);
        treasureBoxService.insert(treasureBox, siteContext.getCurrentUser().getName());
        return BaseResponse.<Void>builder().get();
    }

    @PostMapping("/pagination")
    @Log(desc = "查询宝箱列表")
    public PageResponse<TreasureBoxResponse> pagination(@Valid @RequestBody SearchTreasureBoxRequest request) {
        Page<TreasureBox> treasureBoxPage = treasureBoxService.pagination(DataConverter.to(SearchTreasureBoxCondition.class, request));
        List<Integer> treasureBoxIds = treasureBoxPage.getRecords().stream().map(TreasureBox::getId).collect(Collectors.toList());
        List<TreasureBoxRelate> relateList = treasureBoxService.findRelateByTreasureBoxIds(treasureBoxIds);
        Map<Integer, List<TreasureBoxRelate>> relateListMap = relateList.stream().collect(Collectors.groupingBy(TreasureBoxRelate::getTreasureBoxId));
        Set<Integer> giftIds = relateList.stream().map(TreasureBoxRelate::getGiftId).collect(Collectors.toSet());
        Map<Integer, GiftPlus> giftMap = giftService.findByIds(giftIds).stream().collect(Collectors.toMap(GiftPlus::getId, giftPlus -> giftPlus));
        return DataConverter.to(treasure -> {
            TreasureBoxResponse response = new TreasureBoxResponse();
            BeanUtils.copyProperties(treasure, response);
            if (relateListMap.containsKey(treasure.getId())) {
                response.setGiftList(relateListMap.get(treasure.getId()).stream().filter(relate -> giftMap.containsKey(relate.getGiftId())).map(relate -> {
                    GiftPlus giftPlus = giftMap.get(relate.getGiftId());
                    GiftResponse giftResponse = new GiftResponse();
                    giftResponse.setId(giftPlus.getId());
                    giftResponse.setName(giftPlus.getName());
                    return giftResponse;
                }).collect(Collectors.toList()));
            }
            return response;
        }, treasureBoxPage);
    }

    @PutMapping("/{id}/relate")
    @Log(desc = "宝箱关联礼包")
    public BaseResponse<Void> relate(@PathVariable("id") int id, @Valid @RequestBody TreasureBoxRelateRequest request) {
        treasureBoxService.relate(id, request.getGiftIds(), siteContext.getCurrentUser().getName());
        return BaseResponse.<Void>builder().get();
    }

    @PutMapping("/{id}")
    @Log(desc = "修改宝箱")
    public BaseResponse<Void> update(@PathVariable("id") int id, @Valid @RequestBody OperateTreasureBoxRequest request) {
        TreasureBox exist = treasureBoxService.get(id);
        if (exist == null) {
            throw new ApiException(StandardExceptionCode.TREASURE_BOX_NOT_EXISTS, "宝箱不存在");
        }
        exist.setImg(request.getImg());
        exist.setHalationImg(request.getHalationImg());
        exist.setBgImg(request.getBgImg());
        exist.setName(request.getName());
        treasureBoxService.update(exist, siteContext.getCurrentUser().getName());
        return BaseResponse.<Void>builder().get();
    }

    @DeleteMapping("/{id}")
    @Log(desc = "删除宝箱")
    public BaseResponse<Void> delete(@PathVariable("id") int id) {
        treasureBoxService.delete(id);
        return BaseResponse.<Void>builder().get();
    }

    @GetMapping("/list")
    @Log(desc = "查询所有宝箱")
    public BaseResponse<List<TreasureBoxResponse>> list() {
        List<TreasureBox> treasureBoxes = treasureBoxService.findList();
        return BaseResponse.<List<TreasureBoxResponse>>builder().data(treasureBoxes.stream().map(treasureBox -> {
            TreasureBoxResponse response = new TreasureBoxResponse();
            BeanUtilsEx.copyProperties(treasureBox, response);
            return response;
        }).collect(Collectors.toList())).get();
    }
}
