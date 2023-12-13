package com.csgo.service.blind;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.blind.SearchBlindBoxProductCondition;
import com.csgo.domain.BlindBoxProduct;
import com.csgo.domain.plus.blind.BlindBoxProductDTO;
import com.csgo.mapper.BlindBoxProductMapper;
import com.csgo.mapper.plus.blind.BlindBoxProductPlusMapper;
import com.csgo.repository.BlindBoxProductRepository;

@Service
public class BlindBoxProductService {

    @Autowired
    private BlindBoxProductMapper BlindBoxProductMapper;
    @Autowired
    private BlindBoxProductPlusMapper blindBoxProductPlusMapper;

    @Autowired
    private BlindBoxProductRepository BlindBoxProductRepository;


    public void addBath(List<BlindBoxProduct> blindBoxProductS) {
        List<BlindBoxProduct> blindBoxProductList = new ArrayList<>();
        for (BlindBoxProduct blindBox : blindBoxProductS) {
            BlindBoxProduct BlindBoxProduct = BlindBoxProductMapper.selectByGiftProductId(blindBox.getGiftProductId(), blindBox.getBlindBoxId());
            if (BlindBoxProduct == null) {
                BlindBoxProduct = new BlindBoxProduct();
                BlindBoxProduct.setAddTime(new Date());
                BlindBoxProduct.setUpdateTime(new Date());
                BlindBoxProduct.setGiftProductId(blindBox.getGiftProductId());
                BlindBoxProduct.setProbability(0.00);
                BlindBoxProduct.setImgUrl(blindBox.getImgUrl());
                BlindBoxProduct.setPrice(blindBox.getPrice());
                BlindBoxProduct.setBlindBoxId(blindBox.getBlindBoxId());
                BlindBoxProduct.setWeight(0);
                blindBoxProductList.add(BlindBoxProduct);
            }
        }
        if (blindBoxProductList.size() > 0) {
            BlindBoxProductRepository.saveAll(blindBoxProductList);
        }
    }

    public void update(BlindBoxProduct blindBoxProduct) {
        Optional<BlindBoxProduct> optional = BlindBoxProductRepository.findById(blindBoxProduct.getId());
        if (optional != null && optional.get() != null) {
            BlindBoxProduct blindBoxProduct1 = optional.get();
            blindBoxProduct1.setImgUrl(blindBoxProduct.getImgUrl());
            blindBoxProduct1.setPrice(blindBoxProduct.getPrice());
            blindBoxProduct1.setProbability(blindBoxProduct.getProbability());
            blindBoxProduct1.setOutProbability(blindBoxProduct.getOutProbability());
            blindBoxProduct1.setWeight(blindBoxProduct.getWeight());
            BlindBoxProductRepository.saveAndFlush(blindBoxProduct1);
        }
    }

    public void deleteBath(List<Integer> ids) {
        for (Integer id : ids) {
            BlindBoxProductRepository.deleteById(id);
        }
    }

    public Page<BlindBoxProductDTO> pagination(SearchBlindBoxProductCondition condition) {
        return blindBoxProductPlusMapper.pagination(condition.getPage(), condition);
    }

    public void updateProbabilityBath(BlindBoxProduct blindBoxProduct) {
        BlindBoxProductMapper.updateProbabilityBath(blindBoxProduct.getIds(), blindBoxProduct.getProbability());
    }

    public String findBoxNames(Integer giftProductId) {
        return blindBoxProductPlusMapper.findBoxNames(giftProductId);
    }
}
