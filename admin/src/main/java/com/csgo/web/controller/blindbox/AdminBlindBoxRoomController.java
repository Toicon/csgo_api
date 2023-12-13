package com.csgo.web.controller.blindbox;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.csgo.domain.BlindBoxRoom;
import com.csgo.domain.plus.blind.BlindBoxTurnPlus;
import com.csgo.domain.plus.user.UserPlus;
import com.csgo.domain.user.User;
import com.csgo.domain.user.UserBalance;
import com.csgo.mapper.BlindBoxTurnMapper;
import com.csgo.service.UserBalanceService;
import com.csgo.service.UserService;
import com.csgo.service.blind.BlindBoxRoomService;
import com.csgo.support.PageInfo;
import com.csgo.support.Result;
import com.csgo.util.StringUtil;
import com.csgo.web.controller.BackOfficeController;
import com.csgo.web.support.Log;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/blindBox/blindBoxRoom")
@Api(tags = "后台盲盒房间")
public class AdminBlindBoxRoomController extends BackOfficeController {

    @Autowired
    private BlindBoxRoomService blindBoxRoomService;
    @Autowired
    private BlindBoxTurnMapper blindBoxTurnMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private UserBalanceService userBalanceService;


    @ApiOperation("查询盲盒房间列表")
    @RequestMapping(value = "pageList", method = RequestMethod.GET)
    @Log(desc = "查询盲盒房间列表")
    public Result pageList(@RequestParam(defaultValue = "1") Integer pageNum,
                           @RequestParam(defaultValue = "10") Integer pageSize, String keywords, Integer status
            , Long start_time, Long end_time) {

        if (start_time != null) {
            start_time = start_time / 1000;
        }
        if (end_time != null) {
            end_time = end_time / 1000;
        }
        PageInfo<BlindBoxRoom> blindBoxList = blindBoxRoomService.pageList(pageNum, pageSize, keywords, status, start_time, end_time);
        return new Result().result(blindBoxList);
    }


    @ApiOperation("房间详情")
    @RequestMapping(value = "boxRoomInfo", method = RequestMethod.GET)
    @Log(desc = "查询盲盒房间详情")
    public Result boxRoomInfo(@RequestParam("roomNum") String roomNum) {

        BlindBoxRoom blindBoxRoom = blindBoxRoomService.getBlindBoxRoomByRoomNum(roomNum);
        if (blindBoxRoom == null) {
            return new Result().fairResult("未找到该游戏场次");
        }

        List<UserPlus> userList = userService.findByRoomNum(roomNum);
        List<List<BlindBoxTurnPlus>> lists = new ArrayList<>();
        for (int i = 1; i <= blindBoxRoom.getBlindBoxNum(); i++) {
            List<BlindBoxTurnPlus> blindBoxList = blindBoxRoomService.find(roomNum, i);
            lists.add(blindBoxList);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("blindBoxList", lists);
        map.put("userList", userList);
        return new Result().result(map);
    }


    @ApiOperation("删除接口")
    @DeleteMapping("deleteBath")
    @Log(desc = "删除盲盒房间")
    public Result deleteBath(@RequestBody List<Integer> ids) {
        blindBoxRoomService.deleteBath(ids);
        return new Result().result(true);
    }

    @ApiOperation("关闭房间")
    @PostMapping("closeRoom")
    @Log(desc = "关闭盲盒房间")
    public Result closeRoom(@RequestBody BlindBoxRoom room) {
        if (!room.getStatus().equals(0)) {
            return new Result().fairResult("房间状态不允许此操作");
        }
        // 查询当前房间人数
        int count = blindBoxRoomService.compulsoryUpdateWaitRoomStatus(room.getId());
        if (count > 0) {
            List<User> user = blindBoxTurnMapper.getUserBalanceByRoomNum(room.getRoomNum());
            user.forEach(u -> {
                addUserBalance(u.getId(), room.getPrice());
            });
        }
        return new Result().result(true);
    }


    public void addUserBalance(int userId, BigDecimal amount) {
        UserPlus user = userService.getUserPlus(userId);
        user.setBalance(user.getBalance().add(amount));
        user.setUpdatedAt(new Date());
        userService.update(user);

        UserBalance userBalance = new UserBalance();
        userBalance.setAddTime(new Date());
        userBalance.setAmount(amount);
        userBalance.setType(1); //收入
        userBalance.setRemark("对战房间关闭退还");
        userBalance.setCurrentAmount(user.getBalance());
        userBalance.setUserId(user.getId());
        userBalance.setBalanceNumber(StringUtil.randomNumber(15));
        userBalanceService.add(userBalance);
    }

}
