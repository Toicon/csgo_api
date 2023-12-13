package com.csgo.web.controller.gift;

import com.csgo.domain.Gift;
import com.csgo.domain.GiftGrade;
import com.csgo.domain.GiftProduct;
import com.csgo.domain.plus.gift.GiftGradePlus;
import com.csgo.domain.plus.gift.GiftProductPlus;
import com.csgo.domain.plus.gift.GiftProductRecordPlus;
import com.csgo.domain.plus.gift.GiftType;
import com.csgo.mapper.plus.user.UserMessagePlusMapper;
import com.csgo.modular.product.logic.GiftProductQueryLogic;
import com.csgo.modular.product.logic.GiftProductRecordLogic;
import com.csgo.modular.product.model.dto.UserMessageKeyStatisticsDTO;
import com.csgo.service.GiftProductService;
import com.csgo.service.gift.GiftGradeService;
import com.csgo.service.gift.GiftProductRecordService;
import com.csgo.service.gift.GiftService;
import com.csgo.service.gift.GiftTypeService;
import com.csgo.support.Result;
import com.csgo.web.support.SiteContext;
import com.csgo.web.support.UserInfo;
import com.echo.framework.platform.interceptor.session.RequireSession;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Api(tags = "礼包信息")
@RestController
@RequestMapping("/gift")
@Slf4j
public class GiftController {

    @Autowired
    private GiftService giftService;
    @Autowired
    private GiftProductService giftProductService;
    @Autowired
    private GiftProductRecordService giftProductRecordService;
    @Autowired
    private GiftGradeService gradeService;
    @Autowired
    private GiftTypeService giftTypeService;

    @Autowired
    private SiteContext siteContext;
    @Autowired
    private GiftProductQueryLogic giftProductQueryLogic;
    @Autowired
    private UserMessagePlusMapper userMessagePlusMapper;
    @Autowired
    private GiftProductRecordLogic giftProductRecordLogic;

    /**
     * 根据礼包ID查询到对应的礼包信息
     *
     * @return
     */
    @ApiOperation("根据礼包ID查询到对应的礼包信息（后台管理）")
    @RequestMapping(value = "id", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "礼包id")
    })
    public Result queryGiftId(int id) {
        Gift g = giftService.queryGiftId(id);
        if (g.getTypeId() != null) {
            g.setGiftType(giftTypeService.get(g.getTypeId()));
        }

        if (g.getGrade() != null) {
            g.setGiftGrade(gradeService.getByGrade(g.getGrade()));
        }

        GiftProduct gp = new GiftProduct();
        gp.setGiftId(g.getId());
        gp.setIsdefault(1 + "");
        List<GiftProduct> list = giftProductService.getList(gp);
        if (list != null && list.size() > 0) {
            g.setImg(list.get(0).getImg());
        }
        return new Result().result(g);
    }

    /**
     * 查询所有礼包信息
     *
     * @return
     */
    @ApiOperation("查询所有礼包信息[礼包分类]（后台管理）")
    @GetMapping("/all")
    @RequireSession
    public Result findAll() {
        UserInfo currentUser = siteContext.getCurrentUser();

        //查询所有的礼包类别信息
        List<GiftType> typeList = giftTypeService.findAll();
        Map<String, GiftGrade> gradeMap = gradeService.findAll().stream().collect(Collectors.toMap(GiftGradePlus::getGrade, giftGradePlus -> {
            GiftGrade giftGrade = new GiftGrade();
            BeanUtils.copyProperties(giftGradePlus, giftGrade);
            return giftGrade;
        }));

        Set<Integer> existGiftKeyGiftId = giftProductRecordLogic.findExistGiftKeyGiftId();

        typeList.forEach(giftType -> {
            int id = giftType.getId();
            //根据礼包类别查询到对应的礼包信息
            List<Gift> giftList = giftService.getTypeList(id);
            List<Integer> giftIds = giftList.stream().map(Gift::getId).collect(Collectors.toList());
            List<GiftProductRecordPlus> giftProductRecords = giftProductRecordService.findByGiftIds(giftIds, "1");
            Map<Integer, List<GiftProductRecordPlus>> giftProductRecordListMap = giftProductRecords.stream().collect(Collectors.groupingBy(GiftProductRecordPlus::getGiftId));
            Set<Integer> giftProductIds = giftProductRecords.stream().map(GiftProductRecordPlus::getGiftProductId).collect(Collectors.toSet());
            Map<Integer, GiftProductPlus> giftProductMap = giftProductService.findByGiftProductIds(giftProductIds).stream().collect(Collectors.toMap(GiftProductPlus::getId, giftProductPlus -> giftProductPlus));

            // 密钥专区
            Map<Integer, Integer> keyOwnMap = Maps.newHashMap();
            Map<Integer, GiftProductPlus> productIdMap = Maps.newHashMap();
            if ("碎片专区".equals(giftType.getType())) {
                if (currentUser != null) {
                    List<UserMessageKeyStatisticsDTO> list = userMessagePlusMapper.getKeyStatistics(currentUser.getId());
                    keyOwnMap = list.stream()
                            .collect(Collectors.toMap(UserMessageKeyStatisticsDTO::getProductId, UserMessageKeyStatisticsDTO::getProductNum));
                }
                List<Integer> keyProductIds = giftList.stream().map(Gift::getKeyProductId).filter(Objects::nonNull).collect(Collectors.toList());
                productIdMap = giftProductQueryLogic.mapByIds(keyProductIds);
            }

            for (Gift g : giftList) {
                GiftType copy = new GiftType();
                BeanUtils.copyProperties(giftType, copy);
                g.setGiftType(copy);
                if (gradeMap.containsKey(g.getGrade())) {
                    g.setGiftGrade(gradeMap.get(g.getGrade()));
                }

                // 密钥专区
                if (productIdMap.containsKey(g.getKeyProductId())) {
                    GiftProductPlus product = productIdMap.get(g.getKeyProductId());
                    g.setKeyProductName(product.getName());
                }
                if (currentUser != null && g.getKeyProductId() != null) {
                    Integer userKeyNum = keyOwnMap.getOrDefault(g.getKeyProductId(), 0);
                    g.setUserKeyNum(userKeyNum);
                }
                g.setKeyGenerator(existGiftKeyGiftId.contains(g.getId()));

                if (!StringUtils.hasText(g.getImg())) {
                    if (giftProductRecordListMap.containsKey(g.getId()) && giftProductMap.containsKey(giftProductRecordListMap.get(g.getId()).get(0).getGiftProductId())) {
                        g.setImg(giftProductMap.get(giftProductRecordListMap.get(g.getId()).get(0).getGiftProductId()).getImg());
                    }
                }
            }
            giftType.setGiftList(giftList);
        });
        return new Result().result(typeList);
    }

}
