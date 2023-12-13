package com.csgo.mapper.plus.fish;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.domain.plus.fish.FishUserJackpotBillRecord;
import org.springframework.stereotype.Repository;


@Repository
public interface FishUserJackpotBillRecordMapper extends BaseMapper<FishUserJackpotBillRecord> {

    default Page<FishUserJackpotBillRecord> page(Page<FishUserJackpotBillRecord> page) {
        LambdaQueryWrapper<FishUserJackpotBillRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(FishUserJackpotBillRecord::getCreateDate);
        return selectPage(page, wrapper);
    }
}
