package com.csgo.web.controller.gift;

import com.csgo.domain.GiftCount;
import com.csgo.mapper.GiftCountMapper;
import com.csgo.support.Result;
import com.csgo.web.controller.BackOfficeController;
import com.csgo.web.support.Log;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@Api(tags = "散户礼包次数设置")
@RestController
@RequestMapping("/giftcount")
@Slf4j
public class AdminGiftCountController extends BackOfficeController {

    @Autowired
    private GiftCountMapper giftCountMapper;


    /**
     * 新增/修改散户礼包次数设置
     *
     * @return
     */
    @ApiOperation("新增/修改散户礼包次数设置（后台管理）")
    @RequestMapping(value = "addorup", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "gift_id", value = "礼包id"),
            @ApiImplicitParam(name = "two", value = "2级次数"),
            @ApiImplicitParam(name = "three", value = "3级次数"),
            @ApiImplicitParam(name = "four", value = "4级次数"),
            @ApiImplicitParam(name = "five", value = "5级次数"),
            @ApiImplicitParam(name = "two_w", value = "内部2级次数"),
            @ApiImplicitParam(name = "three_w", value = "内部3级次数"),
            @ApiImplicitParam(name = "four_w", value = "内部4级次数"),
            @ApiImplicitParam(name = "five_w", value = "内部5级次数"),
    })
    @Log(desc = "散户礼包次数设置")
    public Result addorup(Integer gift_id, Integer five, Integer two, Integer three, Integer four,
                          Integer five_w, Integer two_w, Integer three_w, Integer four_w) {
        GiftCount gc = new GiftCount();
        gc.setGiftId(gift_id);
        gc = giftCountMapper.getOnt(gc);
        int num = 0;
        if (gc == null) {
            gc = new GiftCount();
            gc.setGiftId(gift_id);
            if (five != null) {
                gc.setCountfive(five);
            }
            if (two != null) {
                gc.setCounttwo(two);
            }
            if (three != null) {
                gc.setCountthree(three);
            }
            if (four != null) {
                gc.setCountfour(four);
            }

            if (five_w != null) {
                gc.setCountfiveWithin(five_w);
            }
            if (two_w != null) {
                gc.setCounttwoWithin(two_w);
            }
            if (three_w != null) {
                gc.setCountthreeWithin(three_w);
            }
            if (four_w != null) {
                gc.setCountfourWithin(four_w);
            }

            gc.setCt(new Date());
            num = giftCountMapper.insertSelective(gc);
        } else {
            if (five != null) {
                gc.setCountfive(five);
            }
            if (two != null) {
                gc.setCounttwo(two);
            }
            if (three != null) {
                gc.setCountthree(three);
            }
            if (four != null) {
                gc.setCountfour(four);
            }

            if (five_w != null) {
                gc.setCountfiveWithin(five_w);
            }
            if (two_w != null) {
                gc.setCounttwoWithin(two_w);
            }
            if (three_w != null) {
                gc.setCountthreeWithin(three_w);
            }
            if (four_w != null) {
                gc.setCountfourWithin(four_w);
            }

            gc.setUt(new Date());
            num = giftCountMapper.updateByPrimaryKeySelective(gc);
        }
        return new Result().result(num);
    }

    /**
     * 根据礼包ID查询到对应的散户礼包次数设置
     *
     * @return
     */
    @ApiOperation("根据礼包ID查询到对应的散户礼包次数设置（后台管理）")
    @RequestMapping(value = "queryByGiftId", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "gift_id", value = "礼包ID")
    })
    @Log(desc = "查询对应的散户礼包次数设置")
    public Result queryByGiftId(Integer gift_id) {
        GiftCount gc = new GiftCount();
        gc.setGiftId(gift_id);
        gc = giftCountMapper.getOnt(gc);
        return new Result().result(gc);
    }

}
