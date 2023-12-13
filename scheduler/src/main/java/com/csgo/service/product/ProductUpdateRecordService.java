package com.csgo.service.product;

import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.csgo.mapper.plus.gift.GiftProductUpdateRecordMapper;

/**
 * @author admin
 */
@Service
public class ProductUpdateRecordService {

    @Autowired
    private GiftProductUpdateRecordMapper giftProductUpdateRecordMapper;

    @Transactional
    public void delete(Date date) {
        giftProductUpdateRecordMapper.delete(date);
    }
}
