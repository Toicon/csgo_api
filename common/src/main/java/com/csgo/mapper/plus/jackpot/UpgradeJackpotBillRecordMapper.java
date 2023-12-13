package com.csgo.mapper.plus.jackpot;

import org.springframework.stereotype.Repository;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.domain.plus.jackpot.UpgradeJackpotBillRecord;

/**
 * @author admin
 */
@Repository
public interface UpgradeJackpotBillRecordMapper extends BaseMapper<UpgradeJackpotBillRecord> {

    default Page<UpgradeJackpotBillRecord> page(Page<UpgradeJackpotBillRecord> page) {
        LambdaQueryWrapper<UpgradeJackpotBillRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(UpgradeJackpotBillRecord::getCreateDate);
        return selectPage(page, wrapper);
    }
}
