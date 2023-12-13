package com.csgo.web.controller.roll;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.roll.SearchRollCondition;
import com.csgo.domain.plus.membership.MembershipLevelConfig;
import com.csgo.domain.plus.roll.RollCoins;
import com.csgo.domain.plus.roll.RollGiftPlus;
import com.csgo.domain.plus.roll.RollPlus;
import com.csgo.domain.plus.roll.RollType;
import com.csgo.domain.user.User;
import com.csgo.service.UserService;
import com.csgo.service.membership.MembershipLevelConfigService;
import com.csgo.service.roll.RollCoinsService;
import com.csgo.service.roll.RollGiftService;
import com.csgo.service.roll.RollService;
import com.csgo.support.DataConverter;
import com.csgo.web.controller.BackOfficeController;
import com.csgo.web.request.roll.DeleteRollRequest;
import com.csgo.web.request.roll.InsertRollRequest;
import com.csgo.web.request.roll.SearchRollRequest;
import com.csgo.web.response.roll.RollResponse;
import com.echo.framework.platform.exception.ApiException;
import com.echo.framework.platform.web.Api;
import com.echo.framework.platform.web.response.BaseResponse;
import com.echo.framework.platform.web.response.PageResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Api
@RequestMapping("/roll")
@Slf4j
public class AdminRollController extends BackOfficeController {

    @Autowired
    private RollService rollService;
    @Autowired
    private UserService userService;
    @Autowired
    private RollGiftService rollGiftService;
    @Autowired
    private RollCoinsService rollCoinsService;
    @Autowired
    private MembershipLevelConfigService membershipLevelConfigService;

    @PostMapping
    public BaseResponse<Void> insert(@Valid @RequestBody InsertRollRequest request) {
        RollPlus roll = new RollPlus();
        BeanUtils.copyProperties(request, roll);
        roll.setType("1");
        roll.setStatus("0");
        roll.setRollType(RollType.valueOf(request.getRollType()));
        User user = userService.getUserByUserName(request.getAnchorUserName());
        if (user != null) {
            roll.setAnchorUserId(user.getId());
        }
        if (null != request.getMinGrade() && null != request.getMaxGrade() && request.getMinGrade() >= request.getMaxGrade()) {
            throw new ApiException(HttpStatus.SC_BAD_REQUEST, "VIP等级筛选错误");
        }
        roll.setRollNumber((System.currentTimeMillis()) + "" + ((int) ((Math.random() * 9 + 1) * 10000)));
        rollService.insert(roll);
        return BaseResponse.<Void>builder().get();
    }

    @PutMapping("/{id}")
    public BaseResponse<Void> update(@PathVariable("id") int id, @Valid @RequestBody InsertRollRequest request) {
        RollPlus roll = rollService.get(id);
        BeanUtils.copyProperties(request, roll);
        roll.setRollType(RollType.valueOf(request.getRollType()));
        if (StringUtils.hasText(request.getAnchorUserName())) {
            User user = userService.getUserByUserName(request.getAnchorUserName());
            if (user != null) {
                roll.setAnchorUserId(user.getId());
            }
        }

        if (null != request.getMinGrade() && null != request.getMaxGrade() && request.getMinGrade() >= request.getMaxGrade()) {
            throw new ApiException(HttpStatus.SC_BAD_REQUEST, "VIP等级筛选错误");
        }
        rollService.update(roll);
        return BaseResponse.<Void>builder().get();
    }

    /**
     * 查询Roll福利房间的状态信息
     *
     * @return
     */
    @PostMapping("/pagination")
    public PageResponse<RollResponse> pagination(@Valid @RequestBody SearchRollRequest request) {
        Page<RollPlus> page = rollService.search(DataConverter.to(SearchRollCondition.class, request));
        Set<Integer> rollIds = page.getRecords().stream().map(RollPlus::getId).collect(Collectors.toSet());
        Map<Integer, Integer> rollUserMap = rollService.getRollUserMap(rollIds);
        Map<Integer, List<RollGiftPlus>> rollGiftGroupMap = rollGiftService.findByRollIds(rollIds).stream().collect(Collectors.groupingBy(RollGiftPlus::getRollId));
        Map<Integer, List<RollCoins>> rollCoinsGroupMap = rollCoinsService.findByRollIds(rollIds).stream().collect(Collectors.groupingBy(RollCoins::getRollId));
        return DataConverter.to(roll -> {
            RollResponse response = new RollResponse();
            BeanUtils.copyProperties(roll, response);
            if (null != roll.getAnchorUserId()) {
                User anchor = userService.getUserById(roll.getAnchorUserId());
                if (anchor != null) {
                    response.setAnchorUserName(anchor.getUserName());
                }
            }
            response.setRollType(roll.getRollType().name());
            //银币件数
            int totalGiftNum = 0;
            //银币奖励
            BigDecimal totalPrice = BigDecimal.ZERO;
            //V币件数
            int totalXNum = 0;
            //V币奖励
            BigDecimal totalXPrice = BigDecimal.ZERO;

            List<RollGiftPlus> rollGifts = Optional.ofNullable(rollGiftGroupMap.get(roll.getId())).orElse(new ArrayList<>());
            totalGiftNum = rollGifts.size();
            totalPrice = totalPrice.add(rollGifts.stream().map(RollGiftPlus::getPrice).reduce(BigDecimal::add).orElse(BigDecimal.ZERO));
            List<RollCoins> rollCoins = Optional.ofNullable(rollCoinsGroupMap.get(roll.getId())).orElse(new ArrayList<>());
            totalXNum = rollCoins.size();
            totalXPrice = totalXPrice.add(rollCoins.stream().map(RollCoins::getAmount).reduce(BigDecimal::add).orElse(BigDecimal.ZERO));
            response.setTotalGiftNum(totalGiftNum);
            response.setTotalPrice(totalPrice);
            response.setTotalXNum(totalXNum);
            response.setTotalXPrice(totalXPrice);
            response.setUserCount(rollUserMap.get(roll.getId()));
            return response;
        }, page);
    }

    @DeleteMapping
    public BaseResponse<Void> delete(@Valid @RequestBody DeleteRollRequest request) {
        for (Integer id : request.getIds()) {
            rollService.delete(id);
        }
        return BaseResponse.<Void>builder().get();
    }

    @PutMapping("/{id}/switch")
    public BaseResponse<Void> update(@PathVariable("id") int id) {
        rollService.updateSwitch(id);
        return BaseResponse.<Void>builder().get();
    }

    @GetMapping("/membership/level")
    public BaseResponse<List<Integer>> getLevel() {
        return BaseResponse.<List<Integer>>builder().data(membershipLevelConfigService.findAll().stream().map(MembershipLevelConfig::getLevel).collect(Collectors.toList())).get();
    }
}
