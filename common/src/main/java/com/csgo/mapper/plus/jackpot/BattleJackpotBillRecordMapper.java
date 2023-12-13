package com.csgo.mapper.plus.jackpot;

import org.springframework.stereotype.Repository;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.domain.plus.jackpot.BattleJackpotBillRecord;

/**
 * @author admin
 */
@Repository
public interface BattleJackpotBillRecordMapper extends BaseMapper<BattleJackpotBillRecord> {

    default Page<BattleJackpotBillRecord> page(Page<BattleJackpotBillRecord> page) {
        LambdaQueryWrapper<BattleJackpotBillRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(BattleJackpotBillRecord::getCreateDate);
        return selectPage(page, wrapper);
    }
}
