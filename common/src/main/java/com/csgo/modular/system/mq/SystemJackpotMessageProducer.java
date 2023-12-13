package com.csgo.modular.system.mq;

import com.csgo.mq.Group;
import org.springframework.stereotype.Component;

import static com.csgo.mq.MqConstants.GID_SYSTEM_JACKPOT;


/**
 * 系统奖池
 *
 * @author admin
 */
@Component
@Group(GID_SYSTEM_JACKPOT)
public class SystemJackpotMessageProducer extends AbstractLotteryMessageProducer {

}
