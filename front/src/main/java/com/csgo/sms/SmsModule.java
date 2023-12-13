package com.csgo.sms;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.QuerySendDetailsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.QuerySendDetailsResponse;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.csgo.autoconfigure.properties.MqProperties;
import com.csgo.util.DateUtils;
import com.echo.framework.util.TimeLength;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * 短信发送
 *
 * @author huanghunbao
 * 2021/5/17
 */
@Service
public class SmsModule {

    private static Logger logger = LoggerFactory.getLogger(SmsModule.class);

    private static final String RESPONSE_OK_CODE = "OK";

    @Autowired
    private MqProperties mqProperties;

    public SendSmsResponse send(String templateCode, String telephone, String content) {
        SendSmsResponse sendSmsResponse = new SendSmsResponse();
        try {
            //初始化ascClient需要的几个参数
            final String product = "Dysmsapi"; //短信API产品名称（短信产品名固定，无需修改）
            final String domain = "dysmsapi.aliyuncs.com"; //短信API产品域名（接口地址固定，无需修改）
            //替换成你的AK
            final String accessKeyId = mqProperties.getAccessKey(); //你的accessKeyId,参考本文档步骤2
            final String accessKeySecret = mqProperties.getSecretKey(); //你的accessKeySecret，参考本文档步骤2
            //初始化ascClient,暂时不支持多region（请勿修改）
            IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
            DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
            IAcsClient acsClient = new DefaultAcsClient(profile);
            //组装请求对象
            SendSmsRequest request = new SendSmsRequest();
            //使用post提交
            request.setMethod(MethodType.POST);
            //必填:待发送手机号。支持以逗号分隔的形式进行批量调用，批量上限为1000个手机号码,批量调用相对于单条调用及时性稍有延迟,验证码类型的短信推荐使用单条调用的方式；发送国际/港澳台消息时，接收号码格式为00+国际区号+号码，如“0085200000000”
            request.setPhoneNumbers(telephone);
            //必填:短信签名-可在短信控制台中找到
            request.setSignName(mqProperties.getSignName());
            //必填:短信模板-可在短信控制台中找到
            request.setTemplateCode(templateCode);
            //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
            //友情提示:如果JSON中需要带换行符,请参照标准的JSON协议对换行符的要求,比如短信内容中包含\r\n的情况在JSON中需要表示成\\r\\n,否则会导致JSON在服务端解析失败
            if (StringUtils.hasText(content)) {
                request.setTemplateParam(content);
            }
            //可选-上行短信扩展码(扩展码字段控制在7位或以下，无特殊需求用户请忽略此字段)
            //request.setSmsUpExtendCode("90997");
            //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
            // request.setOutId("yourOutId");
            //请求失败这里会抛ClientException异常
            sendSmsResponse = acsClient.getAcsResponse(request);
            if (null == sendSmsResponse) {
                return null;
            }
            if (RESPONSE_OK_CODE.equals(sendSmsResponse.getCode())) {
                return sendSmsResponse;
            }
            logger.info("send sms failed, code:{}.", sendSmsResponse.getCode());
        } catch (ClientException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public boolean match(String templateCode, String telephone, TimeLength validity, String code) {
        QuerySendDetailsResponse.SmsSendDetailDTO lastSmsCode = null;
        try {
            //云通信产品-短信API服务产品名称（短信产品名固定，无需修改）
            final String product = "Dysmsapi";
            //云通信产品-短信API服务产品域名（接口地址固定，无需修改）
            final String domain = "dysmsapi.aliyuncs.com";
            //此处需要替换成开发者自己的AK信息
            final String accessKeyId =  mqProperties.getAccessKey();
            final String accessKeySecret = mqProperties.getSecretKey();
            //初始化ascClient
            IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
            DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
            IAcsClient acsClient = new DefaultAcsClient(profile);
            //组装请求对象
            QuerySendDetailsRequest request = new QuerySendDetailsRequest();
            //必填-号码
            request.setPhoneNumber(telephone);
            //可选-调用发送短信接口时返回的BizId
//            request.setBizId("1234567^8901234");
            //必填-短信发送的日期 支持30天内记录查询（可查其中一天的发送数据），格式yyyyMMdd
            request.setSendDate(DateUtils.timeToDate(new Date()));
            //必填-页大小
            request.setPageSize(10L);
            //必填-当前页码从1开始计数
            request.setCurrentPage(1L);
            //hint 此处可能会抛出异常，注意catch
            QuerySendDetailsResponse response = acsClient.getAcsResponse(request);
            //获取返回结果
            if (response.getCode() != null && response.getCode().equals("OK")) {
                lastSmsCode = getLastSmsCode(response.getSmsSendDetailDTOs(), templateCode);
            }
            logger.info("query sms result, code:{}.", response.getCode());
        } catch (ClientException e) {
            logger.error(e.getMessage(), e);
        }
        return null != lastSmsCode && isValidSmsCode(lastSmsCode, validity, code);
    }

    private boolean isValidSmsCode(QuerySendDetailsResponse.SmsSendDetailDTO lastSmsCode, TimeLength length, String smsCode) {
        String content = lastSmsCode.getContent();
        boolean smsCodeEq = content.contains(smsCode);
        if (!smsCodeEq) {
            return false;
        }
        // 判断验证码超时时间
        Date sendDate = DateUtils.stringToDate(lastSmsCode.getSendDate());
        if (null == sendDate) {
            logger.warn("aliyun sms service response error send sms date, date:{}.", lastSmsCode.getSendDate());
            return false;
        }
        long now = new Date().getTime();
        return now - sendDate.getTime() < length.toMilliseconds();
    }

    private QuerySendDetailsResponse.SmsSendDetailDTO getLastSmsCode(List<QuerySendDetailsResponse.SmsSendDetailDTO> smsSendDetailDTOS, final String templateCode) {
        List<QuerySendDetailsResponse.SmsSendDetailDTO> matchTemplateResult = Lists.newArrayList(FluentIterable.from(smsSendDetailDTOS)
                .filter(Predicates.<QuerySendDetailsResponse.SmsSendDetailDTO>notNull())
                .filter(new Predicate<QuerySendDetailsResponse.SmsSendDetailDTO>() {
                    @Override
                    public boolean apply(QuerySendDetailsResponse.SmsSendDetailDTO smsSendDetailDTO) {
                        return templateCode.equals(smsSendDetailDTO.getTemplateCode());
                    }
                }).toList());
        if (CollectionUtils.isEmpty(matchTemplateResult)) {
            return null;
        }
        Collections.sort(matchTemplateResult, new Comparator<QuerySendDetailsResponse.SmsSendDetailDTO>() {
            @Override
            public int compare(QuerySendDetailsResponse.SmsSendDetailDTO o1, QuerySendDetailsResponse.SmsSendDetailDTO o2) {
                return o2.getSendDate().compareTo(o1.getSendDate());
            }
        });
        return matchTemplateResult.get(0);
    }


}
