package com.csgo.modular.bomb.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.modular.bomb.domain.NovBombJackpotBillRecordDO;
import com.csgo.modular.system.enums.SystemJackpotTypeEnums;
import org.springframework.stereotype.Repository;

/**
 * @author admin
 */
@Repository
public interface NovBombJackpotBillRecordMapper extends BaseMapper<NovBombJackpotBillRecordDO> {

    default Page<NovBombJackpotBillRecordDO> page(SystemJackpotTypeEnums type, Page<NovBombJackpotBillRecordDO> page) {
        LambdaQueryWrapper<NovBombJackpotBillRecordDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NovBombJackpotBillRecordDO::getType, type.getCode());
        wrapper.orderByDesc(NovBombJackpotBillRecordDO::getId);
        return selectPage(page, wrapper);
    }

}
