package com.csgo.modular.tendraw.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.modular.system.enums.SystemJackpotTypeEnums;
import com.csgo.modular.tendraw.domain.TenDrawJackpotBillRecordDO;
import org.springframework.stereotype.Repository;

/**
 * @author admin
 */
@Repository
public interface TenDrawJackpotBillRecordMapper extends BaseMapper<TenDrawJackpotBillRecordDO> {

    default Page<TenDrawJackpotBillRecordDO> page(SystemJackpotTypeEnums type, Page<TenDrawJackpotBillRecordDO> page) {
        LambdaQueryWrapper<TenDrawJackpotBillRecordDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TenDrawJackpotBillRecordDO::getType, type.getCode());
        wrapper.orderByDesc(TenDrawJackpotBillRecordDO::getId);
        return selectPage(page, wrapper);
    }

}
