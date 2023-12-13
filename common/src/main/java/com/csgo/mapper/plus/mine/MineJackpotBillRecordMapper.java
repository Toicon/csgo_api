package com.csgo.mapper.plus.mine;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.domain.plus.mine.MineJackpotBillRecord;
import org.springframework.stereotype.Repository;

/**
 * 扫雷奖池变化
 *
 * @author admin
 */
@Repository
public interface MineJackpotBillRecordMapper extends BaseMapper<MineJackpotBillRecord> {

    default Page<MineJackpotBillRecord> page(Page<MineJackpotBillRecord> page) {
        LambdaQueryWrapper<MineJackpotBillRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(MineJackpotBillRecord::getCreateDate);
        return selectPage(page, wrapper);
    }
}
