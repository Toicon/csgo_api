package com.csgo.service.envelop;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.csgo.domain.plus.envelop.RedEnvelopItem;
import com.csgo.domain.plus.envelop.RedEnvelopItemDTO;
import com.csgo.mapper.plus.envelop.RedEnvelopItemMapper;

/**
 * Created by Admin on 2021/4/30
 */
@Service
public class RedEnvelopItemService {
    @Autowired
    private RedEnvelopItemMapper mapper;

    public List<RedEnvelopItemDTO> findAll() {
        return mapper.findAll();
    }

    public RedEnvelopItem get(String token) {
        return mapper.get(token);
    }

    public RedEnvelopItem get(int id) {
        return mapper.selectById(id);
    }

    public RedEnvelopItem getByEnvelopId(int redEnvelopId) {
        return mapper.getByEnvelopId(redEnvelopId);
    }
}
