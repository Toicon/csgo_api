package com.csgo.web.controller.gift;

import com.csgo.condition.gift.SearchGiftCondition;
import com.csgo.domain.Gift;
import com.csgo.domain.GiftGrade;
import com.csgo.domain.GiftProduct;
import com.csgo.domain.GiftProductRecord;
import com.csgo.domain.plus.box.TreasureBoxRelate;
import com.csgo.domain.plus.gift.GiftPlus;
import com.csgo.domain.user.UserMessage;
import com.csgo.mapper.GiftGradeMapper;
import com.csgo.mapper.GiftProductRecordMapper;
import com.csgo.mapper.UserMessageMapper;
import com.csgo.service.GiftProductService;
import com.csgo.service.GiftService;
import com.csgo.service.GiftTypeService;
import com.csgo.service.box.TreasureBoxService;
import com.csgo.support.DataConverter;
import com.csgo.support.Result;
import com.csgo.util.BeanUtilsEx;
import com.csgo.web.controller.BackOfficeController;
import com.csgo.web.request.gift.EditGiftRequest;
import com.csgo.web.request.gift.SearchGiftRequest;
import com.csgo.web.response.gift.GiftResponse;
import com.csgo.web.support.Log;
import com.echo.framework.platform.web.response.BaseResponse;
import com.echo.framework.platform.web.response.PageResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@Api(tags = "礼包信息")
@RestController
@RequestMapping("/gift")
@Slf4j
public class AdminGiftController extends BackOfficeController {

    @Autowired
    private GiftService giftService;
    @Autowired
    private GiftProductService giftProductService;
    @Autowired
    private GiftProductRecordMapper giftProductRecordMapper;
    @Autowired
    private UserMessageMapper userMessageMapper;
    @Autowired
    private GiftGradeMapper giftGradeMapper;
    @Autowired
    private GiftTypeService giftTypeService;
    @Autowired
    private TreasureBoxService treasureBoxService;

    @PostMapping("/add")
    @Log(desc = "新增礼包")
    public BaseResponse<Void> add(@Valid @RequestBody EditGiftRequest request) {
        GiftPlus gift = new GiftPlus();
        BeanUtilsEx.copyProperties(request, gift);
        giftService.insert(gift);
        if (null != request.getBoxId()) {
            treasureBoxService.updateRelate(request.getBoxId(), gift.getId(), siteContext.getCurrentUser().getName());
        }
        return BaseResponse.<Void>builder().get();
    }

    @PutMapping("/{id}")
    @Log(desc = "编辑礼包")
    public BaseResponse<Void> update(@PathVariable("id") int id, @Valid @RequestBody EditGiftRequest request) {
        GiftPlus gift = giftService.getPlus(id);
        BeanUtilsEx.copyProperties(request, gift);
        giftService.updatePlus(gift);
        if (request.getBoxId() != null) {
            treasureBoxService.updateRelate(request.getBoxId(), gift.getId(), siteContext.getCurrentUser().getName());
        }
        return BaseResponse.<Void>builder().get();
    }


    @PostMapping("/pagination")
    @Log(desc = "查看礼包列表")
    public PageResponse<GiftResponse> pagination(@Valid @RequestBody SearchGiftRequest request) {
        SearchGiftCondition condition = DataConverter.to(SearchGiftCondition.class, request);
        return DataConverter.to(gift -> {
            GiftResponse response = new GiftResponse();
            BeanUtilsEx.copyProperties(gift, response);
            TreasureBoxRelate relate = treasureBoxService.getRelate(gift.getId());
            if (null != relate) {
                response.setBoxId(relate.getTreasureBoxId());
                response.setBoxImg(treasureBoxService.get(relate.getTreasureBoxId()).getImg());
            }
            if (null != gift.getMembershipGrade()) {
                GiftProductRecord record = new GiftProductRecord();
                record.setGiftId(gift.getId());
                List<GiftProductRecord> productRecordList = giftProductRecordMapper.getList(record);
                response.setGiftNum(productRecordList.size());
            }
            return response;
        }, giftService.pagination(condition));
    }

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
    @Log(desc = "查询礼包详细信息")
    public Result queryGiftId(int id) {
        Gift g = giftService.queryGiftId(id);
        if (g.getTypeId() != null) {
            g.setGiftType(giftTypeService.get(g.getTypeId()));
        }

        if (g.getGrade() != null) {
            GiftGrade grade_b = new GiftGrade();
            grade_b.setGrade(g.getGrade());
            g.setGiftGrade(giftGradeMapper.selectOne(grade_b));
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

    @PutMapping("/{id}/switch")
    public BaseResponse<Void> update(@PathVariable("id") int id) {
        giftService.updateSwitch(id);
        return BaseResponse.<Void>builder().get();
    }

    /**
     * 根据ID删除到对应的礼包
     *
     * @return
     */
    @ApiOperation("根据ID删除到对应的礼包（后台管理）")
    @RequestMapping(value = "delete", method = RequestMethod.DELETE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id")
    })
    @Log(desc = "删除礼包")
    public Result delete(int id) {
        Gift gift = giftService.queryGiftId(id);
        //giftId的查询到对应的商品
        GiftProductRecord record = new GiftProductRecord();
        record.setGiftId(gift.getId());
        List<GiftProductRecord> productRecordList = giftProductRecordMapper.getList(record);
        productRecordList.forEach(giftProductRecord -> {
            UserMessage userMessage = new UserMessage();
            userMessage.setGiftProductId(giftProductRecord.getGiftProductId());
            List<UserMessage> messageList = userMessageMapper.selectList(userMessage);
            messageList.forEach(userMessage1 -> {
                //礼包的状态为1的时候表示礼包删除,物品下架
                userMessage1.setGiftStatus("1");
                userMessageMapper.updateByPrimaryKeySelective(userMessage1);
            });
        });
        int num = giftService.delete(id);
        return new Result().result(num);
    }


}
