package com.csgo.modular.common.sms.logic;

import com.csgo.modular.common.sms.constant.SmsConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.guerlab.sms.core.domain.NoticeData;
import net.guerlab.sms.server.service.NoticeService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author admin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SmsLogic {

    private final NoticeService noticeService;

    private boolean sendSms(String phone, NoticeData noticeData) {
        try {
            return noticeService.send(noticeData, phone);
        } catch (Exception e) {
            log.error("[短信] 发送失败:" + e.getMessage(), e);
            return false;
        }
    }

    public boolean sendSmsCode(String phone, String code) {
        Map<String, String> params = new HashMap<>();
        params.put("code", code);

        NoticeData noticeData = new NoticeData();
        noticeData.setType(SmsConstant.VerificationCode);
        noticeData.setParams(params);

        return sendSms(phone, noticeData);
    }

    public boolean sendGlobalUpdate(String phone) {
        log.info("[同步] 发送短信 phone:{}", phone);
        Map<String, String> params = new HashMap<>();
        params.put("name", "XXX");

        NoticeData noticeData = new NoticeData();
        noticeData.setType(SmsConstant.GlobalProductUpdate);
        noticeData.setParams(params);

        return sendSms(phone, noticeData);
    }

    public boolean sendItemOfflineSms(String phone, String content) {
        Map<String, String> params = new HashMap<>();
        params.put("content", content);

        NoticeData noticeData = new NoticeData();
        noticeData.setType(SmsConstant.ItemOffline);
        noticeData.setParams(params);

        return sendSms(phone, noticeData);
    }

    public boolean sendItemSendSms(String phone, String content) {
        Map<String, String> params = new HashMap<>();
        params.put("content", content);

        NoticeData noticeData = new NoticeData();
        noticeData.setType(SmsConstant.ItemSend);
        noticeData.setParams(params);

        return sendSms(phone, noticeData);
    }

}
