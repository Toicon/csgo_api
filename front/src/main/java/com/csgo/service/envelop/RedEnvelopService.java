package com.csgo.service.envelop;

import com.csgo.domain.plus.envelop.RedEnvelop;
import com.csgo.mapper.plus.envelop.RedEnvelopMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author admin
 */
@Service
public class RedEnvelopService {
    @Autowired
    private RedEnvelopMapper redEnvelopMapper;

    public List<RedEnvelop> findAll() {
        return redEnvelopMapper.findAll();
    }

    public RedEnvelop get(int id) {
        return redEnvelopMapper.selectById(id);
    }

    public RedEnvelop findByGrade(Integer grade) {
        return redEnvelopMapper.findByGrade(grade);
    }

}
