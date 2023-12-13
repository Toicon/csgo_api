package com.csgo.mapper.plus.anchor;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.domain.plus.anchor.BoxAnchorJackpotBillRecord;
import org.springframework.stereotype.Repository;

/**
 * @author admin
 */
@Repository
public interface BoxAnchorJackpotBillRecordMapper extends BaseMapper<BoxAnchorJackpotBillRecord> {

    default Page<BoxAnchorJackpotBillRecord> page(Page<BoxAnchorJackpotBillRecord> page) {
        LambdaQueryWrapper<BoxAnchorJackpotBillRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(BoxAnchorJackpotBillRecord::getCreateDate);
        return selectPage(page, wrapper);
    }
}
