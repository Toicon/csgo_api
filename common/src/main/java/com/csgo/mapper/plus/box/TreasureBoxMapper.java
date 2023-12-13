package com.csgo.mapper.plus.box;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.box.SearchTreasureBoxCondition;
import com.csgo.domain.plus.box.TreasureBox;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @author admin
 */
@Repository
public interface TreasureBoxMapper extends BaseMapper<TreasureBox> {

    Page<TreasureBox> pagination(IPage<TreasureBox> page, @Param("condition") SearchTreasureBoxCondition condition);
}
