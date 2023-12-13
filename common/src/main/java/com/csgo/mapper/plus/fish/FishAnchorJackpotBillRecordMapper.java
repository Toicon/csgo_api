package com.csgo.mapper.plus.fish;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.domain.plus.fish.FishAnchorJackpotBillRecord;
import org.springframework.stereotype.Repository;


@Repository
public interface FishAnchorJackpotBillRecordMapper extends BaseMapper<FishAnchorJackpotBillRecord> {

    default Page<FishAnchorJackpotBillRecord> page(Page<FishAnchorJackpotBillRecord> page) {
        LambdaQueryWrapper<FishAnchorJackpotBillRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(FishAnchorJackpotBillRecord::getCreateDate);
        return selectPage(page, wrapper);
    }
}
