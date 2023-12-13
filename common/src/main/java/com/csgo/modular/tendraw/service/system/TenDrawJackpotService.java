package com.csgo.modular.tendraw.service.system;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.domain.plus.user.UserPlus;
import com.csgo.framework.util.BeanCopyUtil;
import com.csgo.framework.util.BigDecimalUtil;
import com.csgo.modular.system.enums.SystemJackpotTypeEnums;
import com.csgo.modular.system.model.dto.SystemJackpotBillRecordDTO;
import com.csgo.modular.system.service.SystemJackpotService;
import com.csgo.modular.tendraw.domain.TenDrawJackpotBillRecordDO;
import com.csgo.modular.tendraw.mapper.TenDrawJackpotBillRecordMapper;
import com.csgo.mq.MqMessage;
import com.csgo.support.GlobalConstants;
import com.csgo.support.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author admin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TenDrawJackpotService {

    private static final SystemJackpotTypeEnums JACKPOT_TYPE = SystemJackpotTypeEnums.TEN_DRAW;
    private static final SystemJackpotTypeEnums JACKPOT_ANCHOR_TYPE = SystemJackpotTypeEnums.TEN_DRAW_ANCHOR;

    private static final MqMessage.Type MQ_TYPE = MqMessage.Type.TEN_DRAW_JACKPOT;

    private final SystemJackpotService systemJackpotService;

    private final TenDrawJackpotBillRecordMapper tenDrawJackpotBillRecordMapper;

    private SystemJackpotTypeEnums resolveJackpotType(UserPlus player) {
        SystemJackpotTypeEnums type = JACKPOT_TYPE;
        if (GlobalConstants.INTERNAL_USER_FLAG == player.getFlag()) {
            type = JACKPOT_ANCHOR_TYPE;
        }
        return type;
    }

    public BigDecimal getJackpot(UserPlus player) {
        SystemJackpotTypeEnums type = resolveJackpotType(player);
        return systemJackpotService.getJackpot(type);
    }

    public boolean hit(UserPlus player, BigDecimal productPrice) {
        BigDecimal jackpot = getJackpot(player);
        return jackpot.compareTo(productPrice) >= 0;
    }

    public void addJackpot(UserPlus player, BigDecimal changeBalance, BigDecimal inRate) {
        if (BigDecimalUtil.lessEqualZero(changeBalance)) {
            log.error("[addJackpot] price wrong:{}", changeBalance);
            return;
        }
        log.info("[十连] userId:{} addJackpot changeBalance:{} inRate:{}", player.getId(), changeBalance, inRate);
        BigDecimal jacketBalance = changeBalance;
        if (inRate != null) {
            jacketBalance = changeBalance.multiply(inRate);
        }
        jacketBalance = jacketBalance.setScale(2, RoundingMode.DOWN);

        BigDecimal balance = getJackpot(player);
        BigDecimal newBalance = balance.add(jacketBalance);

        BigDecimal spareBalance = BigDecimal.ZERO;
        if (inRate != null) {
            spareBalance = changeBalance.subtract(jacketBalance);
        }

        SystemJackpotTypeEnums type = resolveJackpotType(player);
        systemJackpotService.updateJackpot(type, MQ_TYPE, player, changeBalance, jacketBalance, spareBalance, balance, newBalance);
    }

    public void deductJackpot(UserPlus player, BigDecimal changeBalance) {
        if (BigDecimalUtil.lessEqualZero(changeBalance)) {
            log.error("[deductJackpot] price wrong:{}", changeBalance);
            return;
        }
        log.info("[十连] userId:{} deductJackpot changeBalance:{}", player.getId(), changeBalance);
        SystemJackpotTypeEnums type = resolveJackpotType(player);

        changeBalance = changeBalance.negate();

        BigDecimal balance = getJackpot(player);
        BigDecimal newBalance = balance.add(changeBalance);
        systemJackpotService.updateJackpot(type, MQ_TYPE, player, changeBalance, changeBalance, BigDecimal.ZERO, balance, newBalance);
    }

    @Transactional(rollbackFor = Exception.class)
    public void processRecord(List<SystemJackpotBillRecordDTO> records) {
        if (CollectionUtils.isEmpty(records)) {
            return;
        }

        Map<Integer, List<SystemJackpotBillRecordDTO>> typeGroupRecord = records.stream().collect(Collectors.groupingBy(SystemJackpotBillRecordDTO::getType));
        for (Map.Entry<Integer, List<SystemJackpotBillRecordDTO>> entry : typeGroupRecord.entrySet()) {
            Integer type = entry.getKey();
            List<SystemJackpotBillRecordDTO> itemList = entry.getValue();

            BigDecimal spareBalance = BigDecimal.ZERO;

            SystemJackpotBillRecordDTO last = null;
            for (SystemJackpotBillRecordDTO item : itemList) {
                last = item;

                insertRecord(type, item);

                spareBalance = spareBalance.add(item.getSpareBalance());
            }

            systemJackpotService.persisJackpot(type, last);
            systemJackpotService.addSpareBalance(type, spareBalance, last);
        }
    }

    private void insertRecord(Integer type, SystemJackpotBillRecordDTO item) {
        TenDrawJackpotBillRecordDO entity = BeanCopyUtil.notNullMap(item, TenDrawJackpotBillRecordDO.class);
        entity.setType(type);
        entity.setCreateDate(item.getOperateDate());
        entity.setCreateBy(item.getOperator());
        entity.setUpdateBy(item.getOperator());
        entity.setUpdateDate(item.getOperateDate());
        tenDrawJackpotBillRecordMapper.insert(entity);
    }

    public PageInfo<TenDrawJackpotBillRecordDO> pageList(SystemJackpotTypeEnums type, Integer pageNum, Integer pageSize) {
        Page<TenDrawJackpotBillRecordDO> page = new Page<>(pageNum, pageSize);
        Page<TenDrawJackpotBillRecordDO> billRecordPage = tenDrawJackpotBillRecordMapper.page(type, page);
        return new PageInfo<>(billRecordPage);
    }

}
