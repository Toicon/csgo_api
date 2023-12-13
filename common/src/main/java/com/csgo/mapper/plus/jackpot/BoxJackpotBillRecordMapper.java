package com.csgo.mapper.plus.jackpot;

import org.springframework.stereotype.Repository;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.domain.plus.jackpot.BoxJackpotBillRecord;

/**
 * @author admin
 */
@Repository
public interface BoxJackpotBillRecordMapper extends BaseMapper<BoxJackpotBillRecord> {

    default Page<BoxJackpotBillRecord> page(Page<BoxJackpotBillRecord> page) {
        LambdaQueryWrapper<BoxJackpotBillRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(BoxJackpotBillRecord::getCreateDate);
        return selectPage(page, wrapper);
    }
}
