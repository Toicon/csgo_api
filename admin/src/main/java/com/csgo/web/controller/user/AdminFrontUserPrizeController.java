package com.csgo.web.controller.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.prize.SearchUserPrizeDTOCondition;
import com.csgo.domain.plus.gift.GiftPlus;
import com.csgo.domain.plus.user.UserPlus;
import com.csgo.domain.plus.user.UserPrizeDTO;
import com.csgo.domain.user.User;
import com.csgo.service.GiftService;
import com.csgo.service.UserService;
import com.csgo.service.prize.UserPrizePlusService;
import com.csgo.support.DataConverter;
import com.csgo.util.BeanUtilsEx;
import com.csgo.web.controller.BackOfficeController;
import com.csgo.web.request.prize.SearchUserPrizePlusRequest;
import com.csgo.web.response.prize.UserPrizePlusResponse;
import com.csgo.web.support.Log;
import com.echo.framework.platform.web.response.PageResponse;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Api(tags = "抽奖记录")
@RestController
@RequestMapping("/userprize")
@Slf4j
public class AdminFrontUserPrizeController extends BackOfficeController {


    @Autowired
    private UserService userService;
    @Autowired
    private UserPrizePlusService userPrizePlusService;
    @Autowired
    private GiftService giftService;

    @PostMapping("/pagination")
    @Log(desc = "查询用户抽奖记录列表")
    public PageResponse<UserPrizePlusResponse> pagination(@Valid @RequestBody SearchUserPrizePlusRequest request) {
        SearchUserPrizeDTOCondition condition = DataConverter.to(SearchUserPrizeDTOCondition.class, request);
        if (!StringUtils.isEmpty(request.getUserName())) {
            User user = userService.getUserByUserName(request.getUserName());
            if (null != user) {
                condition.setUserId(user.getId());
            } else {
                condition.setUserId(-1);
            }
        }
        Page<UserPrizeDTO> pagination = userPrizePlusService.pagination(condition);
        Set userIds = pagination.getRecords().stream().map(UserPrizeDTO::getUserId).collect(Collectors.toSet());
        Map<Integer, UserPlus> userMap = new HashMap<>();
        if (userIds != null && userIds.size() > 0) {
            userMap.putAll(userService.findByUserIds(new ArrayList<Integer>(userIds)).stream().collect(Collectors.toMap(UserPlus::getId, user -> user)));
        }
        return DataConverter.to(prize -> {
            UserPrizePlusResponse response = new UserPrizePlusResponse();
            BeanUtilsEx.copyProperties(prize, response);
            GiftPlus gift = giftService.get(prize.getGiftId());
            if (null != gift) {
                response.setGiftName(gift.getName());
            }
            if (userMap != null && userMap.size() > 0 && userMap.containsKey(response.getUserId())) {
                UserPlus user = userMap.get(response.getUserId());
                response.setUserName(user.getUserName());
                response.setUserNameQ(user.getName());
            }
            return response;
        }, pagination);
    }

}
