package com.csgo.web.controller.gift;

import cn.hutool.core.util.StrUtil;
import com.csgo.condition.gift.SearchGiftProductDTOCondition;
import com.csgo.domain.GiftProduct;
import com.csgo.domain.GiftProductRecord;
import com.csgo.domain.ProductFilterCategoryDO;
import com.csgo.domain.enums.GiftProductStatusEnum;
import com.csgo.domain.plus.gift.GiftProductPlus;
import com.csgo.domain.plus.user.UserMessagePlus;
import com.csgo.domain.user.User;
import com.csgo.domain.user.UserMessageGift;
import com.csgo.mapper.GiftProductRecordMapper;
import com.csgo.mapper.UserMessageGiftMapper;
import com.csgo.modular.backpack.logic.UserMessageLogic;
import com.csgo.service.*;
import com.csgo.service.blind.BlindBoxProductService;
import com.csgo.support.DataConverter;
import com.csgo.support.GlobalConstants;
import com.csgo.support.Result;
import com.csgo.web.controller.BackOfficeController;
import com.csgo.web.request.gift.EditGiftProductRelaRequest;
import com.csgo.web.request.gift.SearchGiftProductRequest;
import com.csgo.web.response.product.GiftProductResponse;
import com.csgo.web.support.Log;
import com.echo.framework.platform.web.response.BaseResponse;
import com.echo.framework.platform.web.response.PageResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "礼包商品信息")
@RestController
@RequestMapping("/gift/product")
@Slf4j
public class AdminGiftProductController extends BackOfficeController {
    @Autowired
    private GiftProductService service;
    @Autowired
    private UserService userService;
    @Autowired
    private UserMessageService userMessageService;
    @Autowired
    private UserMessageRecordService userMessageRecordService;
    @Autowired
    private UserMessageItemRecordService userMessageItemRecordService;
    @Autowired
    private UserMessageGiftMapper messageMapper;
    @Autowired
    private GiftProductRecordMapper giftProductRecordMapper;
    @Autowired
    private ZbtProductFiltersService zbtProductFiltersService;
    @Autowired
    private BlindBoxProductService blindBoxProductService;
    @Autowired
    private UserMessageLogic userMessageLogic;

    @PostMapping("/summary/pagination")
    @Log(desc = "查询道具汇总列表")
    public PageResponse<GiftProductResponse> pagination(@Valid @RequestBody SearchGiftProductRequest request) {
        SearchGiftProductDTOCondition condition = DataConverter.to(SearchGiftProductDTOCondition.class, request);
        return DataConverter.to(giftProductDTO -> {
            GiftProductResponse response = new GiftProductResponse();
            BeanUtils.copyProperties(giftProductDTO, response);
            String blindName = blindBoxProductService.findBoxNames(giftProductDTO.getId());
            if (StringUtils.hasText(blindName)) {
                response.setBlindName(blindName);
            }
            return response;
        }, service.paginationSummary(condition));
    }

    /**
     * 软删除道具
     *
     * @return
     */
    @PutMapping("/update/status/{id}")
    @Log(desc = "删除道具")
    public BaseResponse<Void> updateStatus(@PathVariable("id") int id) {
        service.updateStatus(id, GiftProductStatusEnum.DELETE);
        return BaseResponse.<Void>builder().get();
    }

    /**
     * 下架道具
     *
     * @return
     */
    @PutMapping("/update/off/{id}")
    @Log(desc = "下架道具")
    public BaseResponse<Void> updateOff(@PathVariable("id") int id) {
        service.updateOff(id);
        return BaseResponse.<Void>builder().get();
    }

    /**
     * 更新道具关联宝箱
     *
     * @return
     */
    @PutMapping("/update/relate")
    @Log(desc = "更新道具关联宝箱、饰品")
    public BaseResponse<Void> updateStatus(@Validated @RequestBody EditGiftProductRelaRequest request) {
        service.updateRelate(request.getOriginalId(), request.getName());
        return BaseResponse.<Void>builder().get();
    }

    @GetMapping("/by/name")
    public BaseResponse<List<GiftProductResponse>> findAllRollUser(@RequestParam("name") String name) {
        return BaseResponse.<List<GiftProductResponse>>builder().data(service.findByName(name).stream().map(adminUserPlus -> {
            GiftProductResponse response = new GiftProductResponse();
            BeanUtils.copyProperties(adminUserPlus, response);
            return response;
        }).collect(Collectors.toList())).get();
    }

    /**
     * 全局更新道具相关信息
     *
     * @return
     */
    @PutMapping("/update/props")
    @Log(desc = "全局更新道具")
    public BaseResponse<Void> updateProps() {
        service.updateProps();
        return BaseResponse.<Void>builder().get();
    }

    /**
     * 全局更新价格权重礼包物品
     *
     * @return
     */
    @PutMapping("/update/giftProduct")
    @Log(desc = "全局更新价格权重礼包物品")
    public BaseResponse<Void> updateGiftProductByPrice() {
        service.updateGiftProductByPrice();
        return BaseResponse.<Void>builder().get();
    }

    /**
     * 增加礼包商品信息
     *
     * @param
     * @return
     */
    @ApiOperation("增加礼包商品信息（后台管理）")
    @RequestMapping(value = "add", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "price", value = "价值"),
            @ApiImplicitParam(name = "name", value = "商品名称"),
            @ApiImplicitParam(name = "img", value = "商品图片"),
            @ApiImplicitParam(name = "gameName", value = "游戏名称"),
            @ApiImplicitParam(name = "content", value = "商品介绍"),
            @ApiImplicitParam(name = "grade", value = "物品等级")

    })
    @Log(desc = "新增礼包道具")
    public Result add(BigDecimal price, String name,
                      String img,
                      String gameName, String content, String grade, String csgoType) {
        GiftProductPlus gp = new GiftProductPlus();
        gp.setPrice(price);
        gp.setCsgoType(csgoType);
        gp.setName(name);
        gp.setImg(img);
        //gp.setGameName("CSGO");
        gp.setGameName(gameName);
        gp.setCreatedAt(new Date());
        gp.setContent(content);
        gp.setGrade(grade);
        int num = service.addPlus(gp);
        return new Result().result(num);
    }

    @GetMapping("getAllZbtProductFilters")
    @ApiOperation("获取所有分类")
    @Log(desc = "获取所有分类")
    public Result getAllZbtProductFilters() {
        List<ProductFilterCategoryDO> productFilterCategoryDOS = zbtProductFiltersService.getAllZbtProductFilters();
        return new Result().result(productFilterCategoryDOS);
    }

    /**
     * 编辑礼包商品信息
     *
     * @return
     */
    @ApiOperation("编辑礼包商品信息（后台管理）")
    @RequestMapping(value = "update", method = RequestMethod.PUT)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "礼包商品id"),
            @ApiImplicitParam(name = "price", value = "价值"),
            @ApiImplicitParam(name = "name", value = "商品名称"),
            @ApiImplicitParam(name = "img", value = "商品图片"),
            @ApiImplicitParam(name = "gameName", value = "游戏名称"),
            @ApiImplicitParam(name = "content", value = "商品介绍")
    })
    @Log(desc = "更新礼包道具")
    public Result update(BigDecimal price, String name,
                         String img,
                         String gameName, int id, String content, String grade, String csgoType) {
        GiftProduct gp = service.queryById(id);
        if (!StringUtils.isEmpty(price)) {
            gp.setPrice(price);
        }
        if (!StringUtils.isEmpty(csgoType)) {
            gp.setCsgoType(csgoType);
        }
        if (!StringUtils.isEmpty(grade)) {
            gp.setGrade(grade);
        }
        if (!StringUtils.isEmpty(name)) {
            gp.setName(name);
        }
        if (!StringUtils.isEmpty(img)) {
            gp.setImg(img);
        }
        if (!StringUtils.isEmpty(gameName)) {
            gp.setGameName(gameName);
        }
        if (!StringUtils.isEmpty(content)) {
            gp.setContent(content);
        }
        gp.setCreatedAt(new Date());
        int num = service.update(gp, id);
        return new Result().result(num);
    }

    /**
     * 查询出所有的商品信息
     *
     * @return
     */
    @ApiOperation("查询出所有的商品信息（后台管理）")
    @RequestMapping(value = "queryAll", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "第几页"),
            @ApiImplicitParam(name = "pageSize", value = "一页多少条数据"),
            @ApiImplicitParam(name = "name", value = "商品名称（模糊查询）")
    })
    @Log(desc = "查询礼包道具列表")
    public Result queryAll(Integer pageNum, Integer pageSize, String name, String csgoType) {
        if (pageSize == null || pageSize == 0) {
            pageSize = 10;
        }
        if (pageNum == null || pageNum == 0) {
            pageNum = 1;
        }
        int p = pageNum;
        pageNum = (pageNum - 1) * pageSize;

        GiftProduct giftProduct = new GiftProduct();
        giftProduct.setPageNum(pageNum);
        giftProduct.setPageSize(pageSize);
        if (name != null && !("").equals(name)) {
            giftProduct.setName(name);
        }
        if (StrUtil.isNotBlank(csgoType)) {
            giftProduct.setCsgoType(csgoType);
        }
        //参数名称默认取：pageNum，pageSize
        List<GiftProduct> gift = service.getListLt(giftProduct);
        if (gift != null && gift.size() > 0) {
            gift.get(0).setTotal(service.totalCount(name));
        }
        gift.forEach(items -> {
            items.setCsgoTypeName(zbtProductFiltersService.getNameByCsgoType(items.getCsgoType()));
        });
        return new Result().result(gift);
    }

    /**
     * 根据ID删除商品信息
     *
     * @return
     */
    @ApiOperation("根据ID删除商品信息（后台管理）")
    @RequestMapping(value = "delete", method = RequestMethod.DELETE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id")
    })
    @Log(desc = "删除礼包道具")
    public Result delete(int id) {
        int num = service.delete(id);
        return new Result().result(num);
    }


    /**
     * 根据礼包ID查询到对应所有的礼包商品信息
     *
     * @return
     */
    @ApiOperation("根据礼包ID查询到对应所有的礼包商品信息（前台抽奖）")
    @RequestMapping(value = "queryByGiftId", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "giftId", value = "礼包id")
    })
    public Result queryByGiftId(Integer giftId) {
        if (giftId == null || giftId == 0) {
            return new Result().fairResult("gift_id不能为空");
        }

        GiftProductRecord giftProductr = new GiftProductRecord();
        giftProductr.setGiftId(giftId);
        List<GiftProductRecord> s = giftProductRecordMapper.getListOrderPrice(giftProductr);
        if (s == null || s.size() < 1) {
            return new Result().fairResult("没有找到该礼包中的商品，请确认后再试");
        }

        List<GiftProduct> giftProduct1 = new ArrayList<>();
//        List<GiftProduct> giftProduct2 = new ArrayList<>();
//        List<GiftProduct> giftProduct3 = new ArrayList<>();


        for (GiftProductRecord gfs : s) {
            GiftProduct giftProduct = new GiftProduct();
            giftProduct.setId(gfs.getGiftProductId());
            List<GiftProduct> giftProducts2s = service.getList(giftProduct);
            if (giftProducts2s != null && giftProducts2s.size() > 0) {
                GiftProduct sss = giftProducts2s.get(0);
                sss.setIsdefault(gfs.getIsdefault());
                sss.setOutProbability(gfs.getOutProbability());
                sss.setWithinProbability(gfs.getWithinProbability());
                sss.setShow_probability(gfs.getShow_probability());
                giftProduct1.add(sss);
            }
        }
        //List<GiftProduct> giftProduct = giftProduct1.stream().sorted(Comparator.comparing(GiftProduct::getGrade)).collect(Collectors.toList());
        return new Result().result(giftProduct1);
    }

    /**
     * 根据道具id给用户发枪
     *
     * @return
     */
    @ApiOperation("根据道具id给用户发枪")
    @RequestMapping(value = "distributionByUid", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "giftProductid", value = "道具id"),
            @ApiImplicitParam(name = "phone_num", value = "手机号"),
    })
    @Log(desc = "根据道具ID给用户发枪")
    public Result distributionByUid(Integer giftProductid, String phone_num) {
        if (giftProductid == null || giftProductid == 0) {
            return new Result().fairResult("道具id不能为空");
        }
        if (phone_num == null || phone_num.equals("")) {
            return new Result().fairResult("手机号不能为空");
        }
        GiftProduct s = service.queryById(giftProductid);
        if (s == null) {
            return new Result().fairResult("道具为空");
        }
        User user = new User();
        user.setPhone(phone_num);
        List<User> list = userService.selectList(user);
        if (CollectionUtils.isEmpty(list)) {
            return new Result().fairResult("用户查询不到，请确认后再试！");
        }
        user = list.get(0);
        addGiftProduct(s, user);
        return new Result().result("成功");
    }

    private void addGiftProduct(GiftProduct giftP, User user) {

        //增加用户背包信息
        UserMessagePlus message = new UserMessagePlus();
        message.setGameName(giftP.getGameName());
        message.setGiftProductId(giftP.getId());
        message.setMoney(giftP.getPrice());
        message.setDrawDare(new Date());
        message.setState(GlobalConstants.USER_MESSAGE_INVENTORY);
        message.setGameName("CSGO");
        message.setImg(giftP.getImg());
        message.setGiftType("系统赠送");
        message.setUserId(user.getId());
        message.setProductName(giftP.getName());
        message.setGiftStatus("0");
        userMessageLogic.processGiftKeyProduct(message,giftP.getCsgoType());

        userMessageService.insert(message);

        //背包流水记录
        int recordId = userMessageRecordService.add(user.getId(), "系统赠送", "IN");

        //背包流水详情记录
        userMessageItemRecordService.add(recordId, message.getId(), message.getImg());

        //抽奖入库记录
        UserMessageGift userMessageGift = new UserMessageGift();
        userMessageGift.setCt(new Date());
        userMessageGift.setUserMessageId(message.getId());
        userMessageGift.setGiftProductId(message.getGiftProductId());
        userMessageGift.setUserId(user.getId());
        userMessageGift.setImg(message.getImg());
        userMessageGift.setPhone(user.getUserName());
        userMessageGift.setState(0);
        userMessageGift.setMoney(message.getMoney());
        userMessageGift.setUt(new Date());
        userMessageGift.setGameName(giftP.getGameName());
        userMessageGift.setGiftProductName(giftP.getName());
        userMessageGift.setGiftType("系统赠送");
        messageMapper.insert(userMessageGift);
    }


}
