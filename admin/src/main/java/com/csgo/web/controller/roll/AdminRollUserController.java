package com.csgo.web.controller.roll;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.beust.jcommander.internal.Lists;
import com.csgo.condition.roll.RollUserSelectCondition;
import com.csgo.condition.roll.SearchRollUserCondition;
import com.csgo.domain.plus.order.OrderRecord;
import com.csgo.domain.plus.roll.RollUserDTO;
import com.csgo.domain.plus.roll.RollUserPlus;
import com.csgo.domain.plus.user.UserPlus;
import com.csgo.domain.plus.withdraw.WithdrawPropPriceDTO;
import com.csgo.service.UserService;
import com.csgo.service.order.OrderRecordService;
import com.csgo.service.roll.RollUserService;
import com.csgo.service.withdraw.WithdrawPropService;
import com.csgo.support.DataConverter;
import com.csgo.web.controller.BackOfficeController;
import com.csgo.web.request.roll.RollUserSelectRequest;
import com.csgo.web.request.roll.SearchRollUserRequest;
import com.csgo.web.response.roll.RollUserResponse;
import com.echo.framework.platform.web.response.BaseResponse;
import com.echo.framework.platform.web.response.PageResponse;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Api(tags = "roll房间对应的用户")
@RestController
@RequestMapping("/roll/user")
@Slf4j
@Configuration
public class AdminRollUserController extends BackOfficeController {

    @Autowired
    private RollUserService rollUserService;
    @Autowired
    private UserService userService;
    @Autowired
    private OrderRecordService orderRecordService;
    @Autowired
    private WithdrawPropService withdrawPropService;

    @GetMapping("/list/{rollId}")
    @Deprecated
    public BaseResponse<List<RollUserDTO>> find(@PathVariable("rollId") Integer rollId) {
        return BaseResponse.<List<RollUserDTO>>builder().data(rollUserService.findDTO(rollId)).get();
    }

    @GetMapping("/findSelectRoomUserPage")
    public PageResponse<RollUserDTO> findSelectRoomUserPage(@Valid RollUserSelectRequest request) {
        Page<RollUserDTO> page = rollUserService.findSelectRoomUserPage(DataConverter.to(RollUserSelectCondition.class, request));
        PageResponse<RollUserDTO> result = DataConverter.to(luckyProductDTO -> {
            RollUserDTO response = new RollUserDTO();
            BeanUtils.copyProperties(luckyProductDTO, response);
            return response;
        }, page);

        RollUserDTO none = new RollUserDTO();
        none.setUserId(-1);
        none.setUserName("不开");

        RollUserDTO test = new RollUserDTO();
        test.setUserId(0);
        test.setUserName("内部");

        List<RollUserDTO> appends = Lists.newArrayList(none, test);
        if (!CollectionUtils.isEmpty(appends)) {
            result.getData().getRows().addAll(0, appends);
        }
        return result;
    }

    @PutMapping("/{rollGiftId}/{userId}/{rollId}")
    public BaseResponse<Void> hit(@PathVariable("rollGiftId") int rollGiftId, @PathVariable("userId") int userId, @PathVariable("rollId") int rollId) {
        rollUserService.hit(rollGiftId, userId, rollId);
        return BaseResponse.<Void>builder().get();
    }

    @PostMapping("/{id}/detail")
    @Deprecated
    public BaseResponse<List<RollUserResponse>> findUsersByRollId(@PathVariable("id") int id, @Valid @RequestBody SearchRollUserRequest request) {
        List<RollUserPlus> rollUsers = rollUserService.find(id, null, request.getUserName());

        List<Integer> userIds = rollUsers.stream().map(RollUserPlus::getUserId).collect(Collectors.toList());
        final Integer flag = request.getFlag();
        List<UserPlus> userList = userService.findByUserIds(userIds).stream().filter(userPlus -> flag == null || flag.equals(userPlus.getFlag())).collect(Collectors.toList());
        Map<Integer, BigDecimal> rechargeMap = orderRecordService.findByUserIds(userIds).stream().collect(Collectors.groupingBy(OrderRecord::getUserId, Collectors.reducing(BigDecimal.ZERO, OrderRecord::getOrderAmount, BigDecimal::add)));
        Map<Integer, BigDecimal> withdrawMap = withdrawPropService.findByUserIds(userIds).stream().collect(Collectors.groupingBy(WithdrawPropPriceDTO::getUserId, Collectors.reducing(BigDecimal.ZERO, WithdrawPropPriceDTO::getPrice, BigDecimal::add)));
        return BaseResponse.<List<RollUserResponse>>builder().data(
                userList.stream().map(user -> {
                    RollUserResponse response = new RollUserResponse();
                    BeanUtils.copyProperties(user, response);
                    response.setFlag(String.valueOf(user.getFlag()));
                    response.setRechargeAmount(Optional.ofNullable(rechargeMap.get(user.getId())).orElse(BigDecimal.ZERO));
                    response.setWithdrawAmount(Optional.ofNullable(withdrawMap.get(user.getId())).orElse(BigDecimal.ZERO));
                    return response;
                }).sorted((o1, o2) -> o2.getRechargeAmount().compareTo(o1.getRechargeAmount())).collect(Collectors.toList())
        ).get();
    }

    @GetMapping("/findRoomUserPage")
    public PageResponse<RollUserResponse> findRoomUserPage(@Valid SearchRollUserRequest request) {
        Page<RollUserDTO> page = rollUserService.findRoomUserPage(DataConverter.to(SearchRollUserCondition.class, request));
        List<Integer> userIds = page.getRecords().stream().map(RollUserDTO::getUserId).collect(Collectors.toList());

        Map<Integer, BigDecimal> withdrawMap = withdrawPropService.findByUserIds(userIds).stream()
                .collect(Collectors.groupingBy(WithdrawPropPriceDTO::getUserId, Collectors.reducing(BigDecimal.ZERO, WithdrawPropPriceDTO::getPrice, BigDecimal::add)));

        return DataConverter.to(user -> {
            RollUserResponse response = new RollUserResponse();
            BeanUtils.copyProperties(user, response);
            response.setFlag(String.valueOf(user.getFlag()));
            response.setWithdrawAmount(Optional.ofNullable(withdrawMap.get(user.getUserId())).orElse(BigDecimal.ZERO));
            return response;
        }, page);
    }

}
