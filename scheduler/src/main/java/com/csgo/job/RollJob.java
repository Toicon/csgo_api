package com.csgo.job;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import com.csgo.domain.plus.roll.RollPlus;
import com.csgo.service.roll.RollDrawService;
import com.csgo.service.roll.RollService;
import com.echo.framework.scheduler.Job;

import lombok.extern.slf4j.Slf4j;

/**
 * @author admin
 */
@Service
@Slf4j
public class RollJob extends Job {

    @Autowired
    private RollService rollService;
    @Autowired
    private RollDrawService rollDrawService;

    @Override
    protected void execute() throws Throwable {
        List<RollPlus> rollList = rollService.findActive();
        if (CollectionUtils.isEmpty(rollList)) {
            return;
        }
        List<RollPlus> list = rollList.stream().filter(roll -> roll.getDrawDate().getTime() <= new Date().getTime()).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        log.info("开始执行roll福利开奖：{}", list.stream().map(roll -> String.valueOf(roll.getId())).collect(Collectors.joining(",")));

        //获取到距离当前时间俩分钟的进行开奖
        list.forEach(roll -> {
            roll.setStatus("1");
            roll.setUt(new Date());
            // 先提前结束roll福利房间，防止重复执行
            rollService.update(roll);
            rollDrawService.draw(roll);
        });
    }
}
