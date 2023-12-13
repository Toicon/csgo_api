package com.csgo.service.mine;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.domain.MineProductDTO;
import com.csgo.domain.plus.mine.MineProduct;
import com.csgo.mapper.plus.mine.MineProductMapper;
import com.csgo.support.PageInfo;
import com.csgo.web.support.SiteContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class MineProductService {

    @Autowired
    private MineProductMapper mineProductMapper;

    @Autowired
    protected SiteContext siteContext;


    public void addBath(List<Integer> giftProductIdList) {
        for (Integer giftProductId : giftProductIdList) {
            if (giftProductId == null) {
                continue;
            }
            LambdaQueryWrapper<MineProduct> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(MineProduct::getGiftProductId, giftProductId);
            MineProduct mineProductDB = mineProductMapper.selectOne(wrapper);
            if (mineProductDB == null) {
                MineProduct mineProduct = new MineProduct();
                mineProduct.setCreateDate(new Date());
                mineProduct.setCreateBy(siteContext.getCurrentUser().getName());
                mineProduct.setGiftProductId(giftProductId);
                mineProductMapper.insert(mineProduct);
            }
        }
    }


    public void deleteBath(List<Integer> ids) {
        for (Integer id : ids) {
            mineProductMapper.deleteById(id);
        }
    }

    public PageInfo<MineProductDTO> pageList(Integer pageNum, Integer pageSize, String keywords, String csgoType) {
        Page<MineProductDTO> page = new Page<>(pageNum, pageSize);
        Page<MineProductDTO> randomProductDOList = mineProductMapper.pagination(page, keywords, csgoType);
        return new PageInfo<>(randomProductDOList);
    }
}
