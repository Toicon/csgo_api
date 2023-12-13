package com.csgo.service.data;

import com.csgo.domain.plus.user.UserMessagePlus;
import com.csgo.mapper.plus.message.UserMessageItemRecordMapper;
import com.csgo.mapper.plus.message.UserMessageRecordMapper;
import com.csgo.mapper.plus.user.UserMessageGiftPlusMapper;
import com.csgo.mapper.plus.user.UserMessagePlusMapper;
import com.csgo.util.DateUtilsEx;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author admin
 */
@Slf4j
@Service
public class CleanDataService {

    @Autowired
    private UserMessagePlusMapper userMessagePlusMapper;
    @Autowired
    private UserMessageGiftPlusMapper userMessageGiftPlusMapper;
    @Autowired
    private UserMessageItemRecordMapper userMessageItemRecordMapper;
    @Autowired
    private UserMessageRecordMapper userMessageRecordMapper;

    public void cleanInnerData() {
        log.info("---------------开始定时清理内部用户数据------------------");
        Date date = new Date();
        UserMessagePlus last = userMessagePlusMapper.selectLast();
        int day = DateUtilsEx.getDifferDay(date, last.getDrawDare()).intValue();
        if (day > 7 || day < -7) {
            log.info("---------------清理内部用户数据系统时间异常停止清理------------------");
            return;
        }
        Date beforeTime = DateUtilsEx.calDateByDay(date, -10);
        int userMessageCount = userMessagePlusMapper.deleteBeforeTime(1, beforeTime);
        log.info("---------------清理user_message表，条数：{}------------------", userMessageCount);
        int userMessageGiftCount = userMessageGiftPlusMapper.deleteBeforeTime(1, beforeTime);
        log.info("---------------清理user_message_gift表，条数：{}------------------", userMessageGiftCount);
        int userMessageItemRecordCount = userMessageItemRecordMapper.deleteBeforeTime(1, beforeTime);
        log.info("---------------清理user_message_item_record表，条数：{}------------------", userMessageItemRecordCount);
        int userMessageRecordCount = userMessageRecordMapper.deleteBeforeTime(1, beforeTime);
        log.info("---------------清理user_message_record表，条数：{}------------------", userMessageRecordCount);
        log.info("---------------结束定时清理内部用户数据------------------");
    }

}
