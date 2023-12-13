package com.csgo.mq.producer;

import com.csgo.mq.Group;
import org.springframework.stereotype.Component;

import static com.csgo.mq.MqConstants.GID_LOTTERY;
import static com.csgo.mq.MqConstants.GID_LUCKY_PRODUCT_LOTTERY;

/**
 * @author admin
 */
@Component
@Group(GID_LUCKY_PRODUCT_LOTTERY)
public class LuckyProductLotteryMessageProducer extends MessageProducer {
}
