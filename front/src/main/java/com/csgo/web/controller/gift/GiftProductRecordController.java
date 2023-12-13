package com.csgo.web.controller.gift;

import com.csgo.domain.GiftProductRecord;
import com.csgo.mapper.GiftProductMapper;
import com.csgo.mapper.GiftProductRecordMapper;
import com.csgo.support.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Api(tags = "商品关联礼包")
@RestController
@RequestMapping("/gift/productRecord")
@Slf4j
public class GiftProductRecordController {

    @Autowired
    private GiftProductRecordMapper giftProductRecordMapper;

    /**
     * 根据ID查询到对应的商品关联礼包
     *
     * @return
     */
    @ApiOperation("根据ID查询到对应的商品关联礼包（后台管理）")
    @RequestMapping(value = "queryById", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id")
    })
    public Result queryById(int id) {
        GiftProductRecord gift = giftProductRecordMapper.selectByPrimaryKey(id);
        return new Result().result(gift);
    }

}
