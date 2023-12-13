package com.csgo.mq.producer;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.OnExceptionContext;
import com.aliyun.openservices.ons.api.SendCallback;
import com.aliyun.openservices.ons.api.SendResult;
import com.aliyun.openservices.ons.api.bean.ProducerBean;
import com.csgo.mq.MqMessage;
import com.csgo.redis.RedisTemplateFacde;
import com.echo.framework.support.jackson.json.JSON;
import com.echo.framework.util.Messages;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import static com.csgo.mq.MqConstants.T_LOTTERY;

/**
 * @author admin
 */
@Slf4j
public class MessageProducer extends ProducerBean {

    @Autowired
    private RedisTemplateFacde redisTemplate;

    public void record(List<MqMessage> messages) {
        Message msg = new Message();
        msg.setTopic(T_LOTTERY);
        String body = JSON.toJSON(messages);
        msg.setBody(body.getBytes(StandardCharsets.UTF_8));
        String random = UUID.randomUUID().toString();
        msg.setKey(random);
        try {
            super.sendAsync(msg, new SendCallback() {
                @Override
                public void onSuccess(final SendResult sendResult) {
                    log.info("success result: {}", sendResult.getMessageId());
                }

                @Override
                public void onException(final OnExceptionContext context) {
                    log.error("sendAsync failure: {}, {}", context.getMessageId(), context.getTopic());
                    log.error(context.getException().getMessage(), context.getException());
                    redisTemplate.set(key(random), body);
                }
            });
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            redisTemplate.set(key(random), body);
        }
    }

    private String key(String time) {
        return Messages.format("{}:{}", T_LOTTERY, time);
    }

}
