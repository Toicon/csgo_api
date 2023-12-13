package com.csgo.modular.system.service;

import com.csgo.constants.CommonBizCode;
import com.csgo.domain.BaseEntity;
import com.csgo.domain.plus.jackpot.JackpotOperationRecord;
import com.csgo.domain.plus.jackpot.JackpotType;
import com.csgo.domain.plus.user.UserPlus;
import com.csgo.framework.exception.BizServerException;
import com.csgo.framework.util.BigDecimalUtil;
import com.csgo.mapper.plus.jackpot.JackpotOperationRecordMapper;
import com.csgo.modular.system.domain.SystemJackpotDO;
import com.csgo.modular.system.enums.SystemJackpotTypeEnums;
import com.csgo.modular.system.mapper.SystemJackpotMapper;
import com.csgo.modular.system.model.dto.SystemJackpotBillRecordDTO;
import com.csgo.modular.system.mq.SystemJackpotMessageProducer;
import com.csgo.mq.MqMessage;
import com.echo.framework.support.jackson.json.JSON;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;

/**
 * @author admin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SystemJackpotService {

    private final SystemJackpotMapper systemJackpotMapper;
    private final SystemJackpotMessageProducer systemJackpotMessageProducer;
    private final StringRedisTemplate stringRedisTemplate;

    private final JackpotOperationRecordMapper jackpotOperationRecordMapper;

    private static final String KEY_TEMPLATE = "jackpot:%s";

    public void updateJackpot(SystemJackpotTypeEnums type, MqMessage.Type mqType, UserPlus player,
                              BigDecimal changeBalance, BigDecimal jacketBalance, BigDecimal spareBalance, BigDecimal beforeBalance, BigDecimal newBalance) {
        this.setJackpotByRedis(type, newBalance);

        SystemJackpotBillRecordDTO record = new SystemJackpotBillRecordDTO();
        record.setType(type.getCode());
        record.setBeforeBalance(beforeBalance);
        record.setNewBalance(newBalance);

        record.setChangeBalance(changeBalance);
        record.setJacketBalance(jacketBalance);
        record.setSpareBalance(spareBalance);

        record.setUserId(player.getId());
        record.setPhone(player.getUserName());
        record.setUserName(player.getName());
        record.setOperator(player.getUserName());
        record.setOperateDate(new Date());

        systemJackpotMessageProducer.record(Collections.singletonList(new MqMessage(MqMessage.Category.SYSTEM, mqType, JSON.toJSON(record))));
    }

    public void persisJackpot(Integer type, SystemJackpotBillRecordDTO last) {
        if (last == null || last.getNewBalance() == null) {
            return;
        }
        setBalance(type, last);
    }

    public void addSpareBalance(Integer type, BigDecimal spareBalance, SystemJackpotBillRecordDTO last) {
        if (spareBalance == null || BigDecimalUtil.lessEqualZero(spareBalance)) {
            return;
        }
        Integer spareType = getSpareTypeCode(type);
        addBalance(spareType, spareBalance, last);
    }

    private void setBalance(Integer type, SystemJackpotBillRecordDTO last) {
        SystemJackpotDO entity = systemJackpotMapper.selectByType(type);
        if (entity != null) {
            entity.setBalance(last.getNewBalance());
            entity.setCreateDate(last.getOperateDate());
            entity.setCreateBy(last.getOperator());
            entity.setUpdateBy(last.getOperator());
            entity.setUpdateDate(last.getOperateDate());
            systemJackpotMapper.updateById(entity);
        } else {
            insertJackpot(type, last.getNewBalance(), last);
        }
    }

    private void addBalance(Integer type, BigDecimal addBalance, SystemJackpotBillRecordDTO last) {
        SystemJackpotDO entity = systemJackpotMapper.selectByType(type);
        if (entity != null) {
            BigDecimal balance = entity.getBalance().add(addBalance);
            entity.setBalance(balance);

            if (last != null) {
                entity.setCreateDate(last.getOperateDate());
                entity.setCreateBy(last.getOperator());
                entity.setUpdateBy(last.getOperator());
                entity.setUpdateDate(last.getOperateDate());
            }
            systemJackpotMapper.updateById(entity);
        } else {
            insertJackpot(type, addBalance, last);
        }
    }

    private void insertJackpot(Integer type, BigDecimal changeBalance, SystemJackpotBillRecordDTO last) {
        SystemJackpotDO entity = new SystemJackpotDO();
        entity.setType(type);
        entity.setBalance(changeBalance);
        entity.setCreateDate(last.getOperateDate());
        entity.setCreateBy(last.getOperator());
        entity.setUpdateBy(last.getOperator());
        entity.setUpdateDate(last.getOperateDate());
        systemJackpotMapper.insert(entity);
    }

    public BigDecimal loadJackpotByDb(Integer code) {
        SystemJackpotDO jackpot = systemJackpotMapper.selectByType(code);
        if (jackpot == null) {
            return BigDecimal.ZERO;
        }
        return jackpot.getBalance();
    }

    public BigDecimal loadSpareJackpot(SystemJackpotTypeEnums type) {
        Integer spareTypeCode = getSpareTypeCode(type.getCode());
        return loadJackpotByDb(spareTypeCode);
    }

    public BigDecimal getJackpot(SystemJackpotTypeEnums type) {
        String balance = getJackpotByRedis(type);
        if (StringUtils.hasText(balance)) {
            return new BigDecimal(balance);
        }
        BigDecimal existBalance = loadJackpotByDb(type.getCode());
        setJackpotByRedis(type, existBalance);
        return existBalance;
    }

    private String getJackpotByRedis(SystemJackpotTypeEnums type) {
        String redisKey = String.format(KEY_TEMPLATE, type.name());
        return stringRedisTemplate.opsForValue().get(redisKey);
    }

    private void setJackpotByRedis(SystemJackpotTypeEnums type, BigDecimal existBalance) {
        String redisKey = String.format(KEY_TEMPLATE, type.name());
        stringRedisTemplate.opsForValue().set(redisKey, String.valueOf(existBalance));
    }

    private static Integer getSpareTypeCode(Integer type) {
        return type + SystemJackpotTypeEnums.ADD_SPARE_CODE;
    }

    @Transactional(rollbackFor = Exception.class)
    public void adminUpdateJackpot(SystemJackpotTypeEnums type, JackpotType jackpotType, BigDecimal balance, String name) {
        SystemJackpotDO jackpot = systemJackpotMapper.selectByType(type.getCode());
        if (jackpot == null) {
            throw BizServerException.of(CommonBizCode.COMMON_DATA_NOT_FOUND);
        }

        JackpotOperationRecord record = new JackpotOperationRecord();
        record.setAmount(balance);
        record.setBeforeAmount(jackpot.getBalance());
        record.setJackpotType(jackpotType);
        BaseEntity.created(record, name);
        jackpotOperationRecordMapper.insert(record);

        jackpot.setBalance(balance);
        systemJackpotMapper.updateById(jackpot);
        setJackpotByRedis(type, balance);
    }

}
