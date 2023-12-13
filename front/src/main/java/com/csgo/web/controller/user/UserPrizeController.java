package com.csgo.web.controller.user;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;

import com.csgo.condition.user.SearchUserRewardCondition;
import com.csgo.domain.plus.user.UserRewardDTO;
import com.csgo.support.DataConverter;
import com.csgo.web.request.user.SearchUserRewardRequest;
import com.echo.framework.platform.web.response.PageResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.csgo.domain.plus.user.UserPrizeDTO;
import com.csgo.domain.socket.InMessage;
import com.csgo.domain.user.UserPrize;
import com.csgo.service.SimpMessagingTemplateFacde;
import com.csgo.service.user.UserPrizeService;
import com.csgo.support.Result;
import com.csgo.web.request.user.SocketNotifyRequest;
import com.csgo.web.response.user.UserPrizeResponse;
import com.csgo.web.support.SiteContext;
import com.echo.framework.platform.interceptor.session.RequireSession;
import com.echo.framework.platform.web.Api;
import com.echo.framework.platform.web.response.BaseResponse;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Api
@RequireSession
@RequestMapping("/userprize")
@Slf4j
public class UserPrizeController {

    @Autowired
    private UserPrizeService userPrizeService;
    @Autowired
    private SimpMessagingTemplateFacde ws;
    @Autowired
    private SiteContext siteContext;

    /**
     * 查询出所有的用户的抽奖记录
     *
     * @return
     */
    @ApiOperation("查询出所有的用户的抽奖记录（后台管理）")
    @GetMapping("queryAll")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "第几页"),
            @ApiImplicitParam(name = "pageSize", value = "一页多少条数据"),
            @ApiImplicitParam(name = "userName", value = "用户名字"),
            @ApiImplicitParam(name = "giftProductName", value = "奖品名字")
    })
    public Result queryAll(Integer pageNum, Integer pageSize, String userName, String giftProductName) {
        if (pageNum == null || pageNum == 0) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize == 0) {
            pageSize = 10;
        }
        int p = pageNum;
        pageNum = (pageNum - 1) * pageSize;

        UserPrize prize = new UserPrize();
        prize.setPageNum(pageNum);
        prize.setPageSize(pageSize);
        if (userName != null && !userName.equals("")) {
            prize.setUserName(userName);
        }
        if (giftProductName != null && !giftProductName.equals("")) {
            prize.setGiftProductName(giftProductName);
        }
        List<UserPrize> userPrizes = userPrizeService.getListLt(prize);
        if (userPrizes != null && userPrizes.size() > 0) {
            userPrizes.get(0).setTotal(userPrizeService.getList(prize).size());
        }
        return new Result().result(userPrizes);
    }


    /**
     * 查询出所有的用户的抽奖记录
     */
    @GetMapping("/recent")
    public BaseResponse<List<UserPrizeResponse>> find(@RequestParam(value = "limit", required = false) Integer limit) {
        List<UserPrizeResponse> responses = new ArrayList<>();
        List<UserPrizeDTO> dtos = userPrizeService.findRecent(siteContext.getCurrentUser(), limit);
        if (!CollectionUtils.isEmpty(dtos)) {
            responses = dtos.stream().map(userPrizeDTO -> {
                UserPrizeResponse response = new UserPrizeResponse();
                BeanUtils.copyProperties(userPrizeDTO, response);
                return response;
            }).collect(Collectors.toList());
        }
        return BaseResponse.<List<UserPrizeResponse>>builder().data(responses).get();
    }

    @PostMapping("/socket-notify")
    public BaseResponse<Void> socketNotify(@Valid @RequestBody SocketNotifyRequest request) {
        InMessage message = new InMessage();
        List<UserPrizeResponse> responses = userPrizeService.findByIds(request.getUserPrizeId()).stream().map(userPrizeDTO -> {
            UserPrizeResponse response = new UserPrizeResponse();
            BeanUtils.copyProperties(userPrizeDTO, response);
            return response;
        }).collect(Collectors.toList());
        message.setData(responses);
        message.setFrom(request.getFrom());
        message.setStatus(1);
        ws.convertAndSend("/topic/userprize", message);
        return BaseResponse.<Void>builder().get();
    }


    @ApiOperation("查询出所有的用户的抽奖记录（前台抽奖）")
    @GetMapping("queryAllQT")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "第几页"),
            @ApiImplicitParam(name = "pageSize", value = "一页多少条数据")
    })
    public Result queryAllQT(Integer pageNum, Integer pageSize) {
        if (pageNum == 0) {
            pageNum = 1;
        }
        if (pageSize == 0) {
            pageSize = 10;
        }
        pageNum = (pageNum - 1) * pageSize;

        UserPrize prize = new UserPrize();
        prize.setPageNum(pageNum);
        prize.setPageSize(pageSize);
        List<UserPrize> userPrizes = userPrizeService.getListLt(prize);
        if (!CollectionUtils.isEmpty(userPrizes)) {
            userPrizes.get(0).setTotal(userPrizeService.getList(prize).size());
        }
        return new Result().result(userPrizes);
    }


    /**
     * 通过用户id查询出该用户的抽奖记录
     *
     * @return
     */
    @ApiOperation("通过用户id查询出该用户的抽奖记录（后台管理）（前台抽奖）")
    @GetMapping("queryAllByUserid")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userid", value = "用户id"),
            @ApiImplicitParam(name = "pageNum", value = "第几页"),
            @ApiImplicitParam(name = "pageSize", value = "一页多少条数据")
    })
    public Result queryAllByUserid(int userid, int pageNum, int pageSize) {
        if (userid == 0) {
            return new Result().fairResult("uesrid不能为空");
        }
        if (pageNum == 0) {
            pageNum = 1;
        }
        if (pageSize == 0) {
            pageSize = 10;
        }
        int p = pageNum;
        pageNum = (pageNum - 1) * pageSize;

        UserPrize prize = new UserPrize();
        prize.setPageNum(pageNum);
        prize.setPageSize(pageSize);
        prize.setUserId(userid);
        List<UserPrize> userPrizes = userPrizeService.getListLt(prize);
        if (userPrizes != null && userPrizes.size() > 0) {
            userPrizes.get(0).setTotal(userPrizeService.getList(prize).size());
        }
        return new Result().result(userPrizes);
    }
    /**
     * 用户奖励查看
     *
     * @param request
     * @return
     */
    @PostMapping("/rewardPage")
    public PageResponse<UserRewardDTO> rewardPage(@Valid @RequestBody SearchUserRewardRequest request) {
        SearchUserRewardCondition condition = DataConverter.to(SearchUserRewardCondition.class, request);
        condition.setUserId(siteContext.getCurrentUser().getId());
        return DataConverter.to(item -> {
            return item;
        }, userPrizeService.rewardPage(condition));
    }

}
