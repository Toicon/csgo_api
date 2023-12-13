package com.csgo.job;

import org.springframework.beans.factory.annotation.Autowired;
import com.csgo.mapper.BlindBoxRoomMapper;
import com.echo.framework.scheduler.Job;

import lombok.extern.slf4j.Slf4j;

/**
 * @author admin
 */
@Slf4j
public class UpdateBlindRoomJob extends Job {

    @Autowired
    private BlindBoxRoomMapper blindBoxRoomMapper;
    

    @Override
    protected void execute() throws Throwable {
        log.info("--------定时查看对应的房间状态是否可以结束------------------");
        blindBoxRoomMapper.updateRoomStatus();
    }
}
