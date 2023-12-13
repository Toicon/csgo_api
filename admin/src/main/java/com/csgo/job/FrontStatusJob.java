//package com.csgo.job;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//import com.csgo.mapper.BlindBoxRoomMapper;
//import com.csgo.service.UserBalanceService;
//import com.csgo.service.blind.BlindBoxTurnService;
//
//import lombok.extern.slf4j.Slf4j;
//
///**
// * @ClassName: SpringScheduled
// * @description:
// * @author: Andy
// * @time: 2020/11/9 10:10
// */
//@Service
//@Slf4j
//public class FrontStatusJob {
//
//    @Autowired
//    private BlindBoxRoomMapper blindBoxRoomMapper;
//    @Autowired
//    private BlindBoxTurnService blindBoxTurnService;
//    @Autowired
//    private UserBalanceService userBalanceService;
//
//    /**
//     * 定时查看对应的房间状态是否可以结束
//     * 每5秒执行一次
//     */
//    @Scheduled(cron = "0/5 * * * * ? ")
//    public void updateRoomStatus() {
//        log.info("--------定时查看对应的房间状态是否可以结束------------------");
//        blindBoxRoomMapper.updateRoomStatus();
//    }
//
//    /**
//     * 定时结束正在等待中的房间 （超过三分钟还在等待则自动结束，因为没分钟查询一次所以不一定刚好三分钟）
//     * 每一分钟执行一次
//     */
////    @Scheduled(cron = "0 0/1 * * * ? ")
////    public void updateWaitRoomStatus() {
////        log.info("--------定时结束正在等待中的房间------------------");
////
////        List<BlindBoxRoom> waitRoomList = blindBoxRoomMapper.getWaitRoomList();
////        waitRoomList.forEach(item -> {
////            // 查询当前房间人数
////            List<UserPlus> user = blindBoxTurnService.findByRoomNum(item.getRoomNum());
////            int count = blindBoxRoomMapper.updateWaitRoomStatus(item.getId());
////            if (count > 0) {
////                user.forEach(u -> userBalanceService.add(u, item.getPrice(), "对战房间关闭退还"));
////            }
////        });
////    }
//
//}
