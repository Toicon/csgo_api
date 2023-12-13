package com.csgo.mapper.plus.mine;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.domain.MineProductDTO;
import com.csgo.domain.plus.mine.MineProduct;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 扫雷随机饰品信息
 *
 * @author admin
 */
@Repository
public interface MineProductMapper extends BaseMapper<MineProduct> {

    Page<MineProductDTO> pagination(Page<MineProductDTO> page, @Param("keywords") String keywords, @Param("csgoType") String csgoType);

    /**
     * 获取随机保底饰品信息
     *
     * @return
     */
    MineProduct getRandProduct();

}
