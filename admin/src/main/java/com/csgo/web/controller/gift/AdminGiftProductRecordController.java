package com.csgo.web.controller.gift;

import com.csgo.domain.GiftProductRecord;
import com.csgo.domain.enums.YesOrNoEnum;
import com.csgo.domain.plus.gift.GiftPlus;
import com.csgo.domain.plus.gift.RandomGiftProductDTO;
import com.csgo.mapper.GiftProductMapper;
import com.csgo.mapper.GiftProductRecordMapper;
import com.csgo.mapper.plus.gift.GiftProductPlusMapper;
import com.csgo.service.GiftProductService;
import com.csgo.service.GiftService;
import com.csgo.support.Result;
import com.csgo.web.controller.BackOfficeController;
import com.csgo.web.support.Log;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Api(tags = "商品关联礼包")
@RestController
@RequestMapping("/gift/productRecord")
@Slf4j
public class AdminGiftProductRecordController extends BackOfficeController {

    @Autowired
    private GiftProductService giftProductService;

    @Autowired
    private GiftProductRecordMapper giftProductRecordMapper;
    @Autowired
    private GiftService giftService;
    @Autowired
    private GiftProductMapper giftProductMapper;
    @Autowired
    private GiftProductPlusMapper giftProductPlusMapper;

    /**
     * 商品关联礼包
     *
     * @param
     * @return
     */
    @ApiOperation("商品关联礼包（后台管理）")
    @RequestMapping(value = "add", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "giftId", value = "礼包id"),
            @ApiImplicitParam(name = "giftProductId", value = "礼包商品id"),
            @ApiImplicitParam(name = "withinProbability", value = "内部概率设置"),
            @ApiImplicitParam(name = "outProbability", value = "外部概率设置"),
            @ApiImplicitParam(name = "show_probability", value = "展示概率设置"),
            @ApiImplicitParam(name = "isdefault", value = "设为封面 0 不设  1 设置"),
            @ApiImplicitParam(name = "specialState", value = "是否特殊(1:是,0:否)"),
            @ApiImplicitParam(name = "price", value = "商品价格")
    })
    @Log(desc = "道具与礼包关联")
    public Result add(Integer giftId, Integer giftProductId,
                      String withinProbability, String outProbability, String isdefault,
                      String show_probability, Integer weight, boolean specialState, BigDecimal price) {
        GiftPlus giftPlus = giftService.get(giftId);
        if (giftPlus == null) {
            return new Result().fairResult("礼包信息不存在");
        }
        if (giftPlus.getProbabilityType().equals(YesOrNoEnum.YES.getCode())) {
            if (price == null) {
                return new Result().fairResult("价格不能为空");
            }
            //价格权重:根据价格获取商品id
            RandomGiftProductDTO randomGiftProductDTO = giftProductPlusMapper.getRandomGiftProductByPrice(price);
            if (randomGiftProductDTO == null) {
                return new Result().fairResult("获取不到指定价格商品信息");
            }
            giftProductId = randomGiftProductDTO.getGiftProductId();
        } else {
            //普通权重
            if (giftProductId == null) {
                return new Result().result(0);
            }
        }
        GiftProductRecord gp = new GiftProductRecord();
        gp.setGiftId(giftId);
        gp.setWithinProbability(withinProbability);
        gp.setOutProbability(outProbability);
        gp.setIsdefault(isdefault);
        gp.setSpecialState(specialState);
        gp.setCt(new Date());
        gp.setGiftProductId(giftProductId);
        gp.setProbabilityPrice(price);
        gp.setWeight(null == weight ? 0 : weight);
        if (!StringUtils.isEmpty(show_probability)) {
            gp.setShow_probability(show_probability);
        }
        if (isdefault != null && isdefault.equals("1")) {
            GiftProductRecord giftpp = new GiftProductRecord();
            giftpp.setGiftId(gp.getGiftId());
            List<GiftProductRecord> list = giftProductRecordMapper.getList(giftpp);
            if (list != null && list.size() > 0) {
                for (GiftProductRecord gpp : list) {
                    gpp.setIsdefault(0 + "");
                    giftProductRecordMapper.updateByPrimaryKeySelective(gpp);
                }
            }
        }

        int num = giftProductRecordMapper.insertSelective(gp);
        return new Result().result(num);
    }

    /**
     * 编辑商品关联礼包
     *
     * @return
     */
    @ApiOperation("编辑商品关联礼包（后台管理）")
    @RequestMapping(value = "update", method = RequestMethod.PUT)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id"),
            @ApiImplicitParam(name = "giftId", value = "礼包id"),
            @ApiImplicitParam(name = "giftProductId", value = "礼包商品id"),
            @ApiImplicitParam(name = "show_probability", value = "展示概率设置"),
            @ApiImplicitParam(name = "withinProbability", value = "内部概率设置"),
            @ApiImplicitParam(name = "outProbability", value = "外部概率设置"),
            @ApiImplicitParam(name = "isdefault", value = "设为封面 0 不设  1 设置"),
            @ApiImplicitParam(name = "specialState", value = "是否特殊(1:是,0:否)"),
            @ApiImplicitParam(name = "price", value = "商品价格")
    })
    @Log(desc = "修改道具与礼包关联")
    public Result update(Integer giftId, Integer giftProductId,
                         String withinProbability, String outProbability,
                         String isdefault, int id, String show_probability,
                         Integer weight, boolean specialState, BigDecimal price) {
        GiftPlus giftPlus = giftService.get(giftId);
        if (giftPlus == null) {
            return new Result().fairResult("礼包信息不存在");
        }
        if (giftPlus.getProbabilityType().equals(YesOrNoEnum.YES.getCode())) {
            if (price == null) {
                return new Result().fairResult("价格不能为空");
            }
            //价格权重:根据价格获取商品id
            RandomGiftProductDTO randomGiftProductDTO = giftProductPlusMapper.getRandomGiftProductByPrice(price);
            if (randomGiftProductDTO == null) {
                return new Result().fairResult("获取不到指定价格商品信息");
            }
            giftProductId = randomGiftProductDTO.getGiftProductId();
        }
        GiftProductRecord gp = new GiftProductRecord();
        if (!StringUtils.isEmpty(giftId)) {
            gp.setGiftId(giftId);
        }
        if (!StringUtils.isEmpty(withinProbability)) {
            gp.setWithinProbability(withinProbability);
        }
        if (!StringUtils.isEmpty(outProbability)) {
            gp.setOutProbability(outProbability);
        }
        if (!StringUtils.isEmpty(isdefault)) {
            gp.setIsdefault(isdefault);
        }
        if (!StringUtils.isEmpty(giftProductId)) {
            gp.setGiftProductId(giftProductId);
        }
        gp.setWeight(null == weight ? 0 : weight);
        if (!StringUtils.isEmpty(show_probability)) {
            gp.setShow_probability(show_probability);
        }
        gp.setSpecialState(specialState);
        gp.setId(id);
        gp.setProbabilityPrice(price);
        gp.setCt(new Date());
        if (isdefault != null && isdefault.equals("1")) {
            GiftProductRecord giftpp = new GiftProductRecord();
            giftpp.setGiftId(gp.getGiftId());
            List<GiftProductRecord> list = giftProductRecordMapper.getList(giftpp);
            if (list != null && list.size() > 0) {
                for (GiftProductRecord gpp : list) {
                    gpp.setIsdefault(0 + "");
                    giftProductRecordMapper.updateByPrimaryKeySelective(gpp);
                }
            }
        }
        int num = giftProductRecordMapper.updateByPrimaryKeySelective(gp);
        return new Result().result(num);
    }

    /**
     * 根据ID删除到对应的商品关联礼包
     *
     * @return
     */
    @ApiOperation("根据ID删除到对应的商品关联礼包（后台管理）")
    @RequestMapping(value = "delete", method = RequestMethod.DELETE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id")
    })
    @Log(desc = "删除道具与礼包关联")
    public Result delete(int id) {
        int num = giftProductRecordMapper.deleteByPrimaryKey(id);
        return new Result().result(num);
    }

    /**
     * 根据礼包ID查询到对应的商品关联礼包
     *
     * @return
     */
    @ApiOperation("根据礼包ID查询到对应的商品关联礼包（后台管理）")
    @RequestMapping(value = "queryByGiftId", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "giftId", value = "礼包id")
    })
    public Result queryByGiftId(Integer giftId) {
        if (giftId == null || giftId == 0) {
            return new Result().fairResult("gift_id不能为空");
        }
        GiftProductRecord gift = new GiftProductRecord();
        gift.setGiftId(giftId);
        List<GiftProductRecord> gifts = giftProductRecordMapper.getListOrderPrice(gift);
        List<GiftProductRecord> giftlists = new ArrayList<>();
        if (gifts != null && gifts.size() > 0) {
            for (GiftProductRecord gpr : gifts) {
                gpr.setGiftProduct(giftProductMapper.selectByPrimaryKey(gpr.getGiftProductId()));
                giftlists.add(gpr);
            }
        }
        return new Result().result(gifts);
    }

    /**
     * 获取是否特殊金配置数量
     *
     * @param giftId
     * @return
     */
    @RequestMapping(value = "getCountSpecialStateByGiftId", method = RequestMethod.GET)
    public Result getCountSpecialStateByGiftId(Integer giftId) {
        if (giftId == null || giftId == 0) {
            return new Result().fairResult("gift_id不能为空");
        }
        return new Result().result(giftProductService.getCountSpecialStateByGiftId(giftId));
    }
}
