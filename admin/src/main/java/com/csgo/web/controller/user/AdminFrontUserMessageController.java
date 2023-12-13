package com.csgo.web.controller.user;

import com.csgo.domain.user.UserMessage;
import com.csgo.domain.user.UserMessageGift;
import com.csgo.mapper.UserMessageGiftMapper;
import com.csgo.service.UserMessageService;
import com.csgo.support.Result;
import com.csgo.web.controller.BackOfficeController;
import com.csgo.web.support.Log;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "用户个人背包信息")
@RestController
@RequestMapping("/user/message")
@Slf4j
public class AdminFrontUserMessageController extends BackOfficeController {

    @Autowired
    private UserMessageService service;

    @Autowired
    private UserMessageGiftMapper messageMapper;

    /**
     * 编辑个人背包信息
     *
     * @return
     */
    @ApiOperation("编辑个人背包信息（后台管理）")
    @RequestMapping(value = "update", method = RequestMethod.PUT)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "个人背包信息id"),
            @ApiImplicitParam(name = "state", value = "物品状态 0-入库,1-已出售,2-已提取"),
    })
    @Log(desc = "修改用户背包")
    public Result update(Integer state, int id) {
        UserMessage um = new UserMessage();
        um.setState(state.toString());
        int num = service.update(um, id);

        UserMessageGift userMessageGift = new UserMessageGift();
        userMessageGift.setUserMessageId(um.getId());
        List<UserMessageGift> a = messageMapper.selectListBys(userMessageGift);
        if (!CollectionUtils.isEmpty(a)) {
            userMessageGift = a.get(0);
            userMessageGift.setState(state);
            num = messageMapper.updateByPrimaryKeySelective(userMessageGift);
        }
        return new Result().result(num);
    }

    /**
     * 根据个人信息ID查询到对应的个人背包信息
     *
     * @return
     */
    @ApiOperation("根据个人信息ID查询到对应的个人背包信息（后台管理）")
    @RequestMapping(value = "id", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "个人背包信息ID")
    })
    public Result queryById(int id) {
        UserMessage userMessage = service.queryById(id);
        return new Result().result(userMessage);
    }


    /**
     * 根据用户ID查询到对应的个人背包信息
     *
     * @return
     */
    @ApiOperation("根据用户ID查询到对应的个人背包信息（后台管理）")
    @RequestMapping(value = "userId", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户ID"),
            @ApiImplicitParam(name = "state", value = "商品状态0-入库1出售2-提取"),
            @ApiImplicitParam(name = "name", value = "模糊搜索字段"),
            @ApiImplicitParam(name = "pageNum", value = "第几页"),
            @ApiImplicitParam(name = "pageSize", value = "一页多少条数据"),
    })
    @Log(desc = "查询个人用户背包列表")
    public Result queryByuserId(Integer pageNum, Integer pageSize, Integer userId, String state, String name) {
        if (pageNum == null || pageNum == 0) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize == 0) {
            pageSize = 100;
        }
        pageNum = (pageNum - 1) * pageSize;

        UserMessage userMessage = new UserMessage();
        userMessage.setUserId(userId);
        if (state != null && !state.equals("")) {
            userMessage.setState(state);
        }
        if (name != null && !name.equals("")) {
            userMessage.setProductName(name);
        }
        userMessage.setPageNum(pageNum);
        userMessage.setPageSize(pageSize);
        List<UserMessage> userMessages = service.selectListLm(userMessage);
        if (userMessages != null && userMessages.size() > 0) {
            userMessages.get(0).setTotal(service.selectList(userMessage).size());
        }
        return new Result().result(userMessages);
    }


    /**
     * 根个人信息ID删除到对应的个人包信息
     *
     * @return
     */
    @ApiOperation("根个人信息ID删除到对应的个人包信息（后台管理）（前台抽奖）")
    @RequestMapping(value = "delete", method = RequestMethod.DELETE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户id")
    })
    @Log(desc = "删除用户背包")
    public Result delete(int id) {
        int num = service.delete(id);
        if (num == 0) {
            return new Result().fairResult("删除失败");
        }
        return new Result().result(num);
    }


}
