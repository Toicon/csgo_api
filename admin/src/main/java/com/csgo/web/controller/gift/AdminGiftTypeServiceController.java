package com.csgo.web.controller.gift;

import com.csgo.domain.plus.gift.GiftType;
import com.csgo.mapper.GiftMapper;
import com.csgo.service.GiftTypeService;
import com.csgo.support.Result;
import com.csgo.util.BeanUtilsEx;
import com.csgo.web.controller.BackOfficeController;
import com.csgo.web.request.gift.EditGiftTypeRequest;
import com.csgo.web.response.gift.GiftTypeResponse;
import com.csgo.web.support.Log;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "礼包类型信息")
@RestController
@RequestMapping("/gifttype")
@Slf4j
public class AdminGiftTypeServiceController extends BackOfficeController {

    @Autowired
    private GiftTypeService giftTypeService;
    @Autowired
    private GiftMapper giftMapper;

    /**
     * 编辑礼包类型
     *
     * @return
     */
    @PutMapping(value = "update/{id}")
    @Log(desc = "修改礼包类型")
    public Result update(@Valid @RequestBody EditGiftTypeRequest request, @PathVariable("id") int id) {
        GiftType giftType = giftTypeService.get(id);
        BeanUtilsEx.copyProperties(request, giftType);
        giftTypeService.update(giftType);
        return new Result().result(null);
    }

    /**
     * 根据ID查询到对应的礼包类型
     *
     * @return
     */
    @GetMapping(value = "queryById")
    @Log(desc = "查询礼包类型")
    public Result queryById(int id) {
        return new Result().result(giftTypeService.get(id));
    }

    /**
     * 查询所有对应的礼包类型
     *
     * @return
     */
    @GetMapping(value = "queryAll")
    @Log(desc = "查询所有礼包类型")
    public Result queryAll() {
        List<GiftType> giftTypes = giftTypeService.find();
        return new Result().result(giftTypes.stream().map(type -> {
            GiftTypeResponse response = new GiftTypeResponse();
            BeanUtilsEx.copyProperties(type, response);
            return response;
        }).collect(Collectors.toList()));
    }

    /**
     * 根据ID删除对应的礼包类型
     *
     * @return
     */
    @DeleteMapping(value = "delete")
    @Log(desc = "删除礼包类型")
    public Result delete(int id) {
        giftTypeService.delete(id);
        //根据礼包类型删除对应的礼包
        giftMapper.deleteByPrimaryKeyTypeId(id);
        return new Result().result(null);
    }


}
