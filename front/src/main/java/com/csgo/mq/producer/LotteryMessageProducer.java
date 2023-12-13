package com.csgo.mq.producer;

import com.csgo.mq.Group;
import org.springframework.stereotype.Component;

import static com.csgo.mq.MqConstants.GID_LOTTERY;

/**
 * @author admin
 */
@Component
@Group(GID_LOTTERY)
public class LotteryMessageProducer extends MessageProducer {
}
