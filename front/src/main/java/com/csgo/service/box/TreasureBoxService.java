package com.csgo.service.box;

import com.csgo.domain.plus.box.TreasureBox;
import com.csgo.domain.plus.box.TreasureBoxRelate;
import com.csgo.mapper.plus.box.TreasureBoxMapper;
import com.csgo.mapper.plus.box.TreasureBoxRelateMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * @author admin
 */
@Service
public class TreasureBoxService {

    @Autowired
    private TreasureBoxMapper treasureBoxMapper;
    @Autowired
    private TreasureBoxRelateMapper treasureBoxRelateMapper;

    public List<TreasureBox> findAll() {
        return treasureBoxMapper.selectList(null);
    }

    public List<TreasureBoxRelate> findRelateByTreasureBoxIds(Collection<Integer> treasureBoxIds) {
        if (CollectionUtils.isEmpty(treasureBoxIds)) {
            return new ArrayList<>();
        }
        return treasureBoxRelateMapper.findRelateByTreasureBoxIds(treasureBoxIds);
    }

    public TreasureBox getByGiftId(int id) {
        TreasureBoxRelate relate = treasureBoxRelateMapper.getByGiftId(id);
        return treasureBoxMapper.selectById(Optional.ofNullable(relate).map(TreasureBoxRelate::getTreasureBoxId).orElse(0));
    }
}
