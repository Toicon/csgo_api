package com.csgo.sms;

import com.csgo.redis.RedisTemplateFacde;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author admin
 * 嗖嗖短信接口
 */
@Slf4j
@Service
public class SmsService {

    @Autowired
    private RedisTemplateFacde redisTemplateFacde;

    /**
     * 验证码校验合法性
     *
     * @param phone
     * @param smsCode
     * @return
     */
    public boolean isValidSmsCode(String phone, String smsCode) {
        String key = "sms:" + phone;
        String code = redisTemplateFacde.get(key);
        if (null == code) {
            log.warn("the smscode  error send sms date, date:{}.", smsCode);
            return false;
        }
        if (!smsCode.equals(code)) {
            return false;
        }
        return true;
    }

    public void delSmsCode(String phone) {
        String key = "sms:" + phone;
        redisTemplateFacde.delete(key);
    }
}
