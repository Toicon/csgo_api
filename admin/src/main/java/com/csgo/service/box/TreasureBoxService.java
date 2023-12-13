package com.csgo.service.box;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.box.SearchTreasureBoxCondition;
import com.csgo.domain.plus.box.TreasureBox;
import com.csgo.domain.plus.box.TreasureBoxRelate;
import com.csgo.mapper.plus.box.TreasureBoxMapper;
import com.csgo.mapper.plus.box.TreasureBoxRelateMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author admin
 */
@Service
public class TreasureBoxService {
    @Autowired
    private TreasureBoxMapper treasureBoxMapper;
    @Autowired
    private TreasureBoxRelateMapper treasureBoxRelateMapper;

    @Transactional
    public void insert(TreasureBox treasureBox, String operator) {
        treasureBox.setCb(operator);
        treasureBox.setCt(new Date());
        treasureBox.setUb(operator);
        treasureBox.setUt(new Date());
        treasureBoxMapper.insert(treasureBox);
    }

    public Page<TreasureBox> pagination(SearchTreasureBoxCondition condition) {
        return treasureBoxMapper.pagination(condition.getPage(), condition);
    }

    public List<TreasureBoxRelate> findRelateByTreasureBoxIds(List<Integer> treasureBoxIds) {
        if (CollectionUtils.isEmpty(treasureBoxIds)) {
            return new ArrayList<>();
        }
        return treasureBoxRelateMapper.findRelateByTreasureBoxIds(treasureBoxIds);
    }

    @Transactional
    public void relate(int treasureBoxId, List<Integer> giftIds, String operator) {
        treasureBoxRelateMapper.deleteByTreasureBoxId(treasureBoxId);
        for (Integer giftId : giftIds) {
            TreasureBoxRelate relate = new TreasureBoxRelate();
            relate.setTreasureBoxId(treasureBoxId);
            relate.setGiftId(giftId);
            relate.setCb(operator);
            relate.setCt(new Date());
            treasureBoxRelateMapper.insert(relate);
        }
    }

    @Transactional
    public void updateRelate(int boxId, int giftId, String operator) {
        TreasureBoxRelate relate = treasureBoxRelateMapper.getByGiftId(giftId);
        if (null != relate) {
            treasureBoxRelateMapper.deleteById(relate.getId());
        }
        if (null == relate) {
            relate = new TreasureBoxRelate();
        }
        relate.setTreasureBoxId(boxId);
        relate.setGiftId(giftId);
        relate.setCb(operator);
        relate.setCt(new Date());
        treasureBoxRelateMapper.insert(relate);
    }

    public List<TreasureBoxRelate> findAllRelateExclude(int treasureBoxId) {
        return treasureBoxRelateMapper.findAllRelateExclude(treasureBoxId);
    }

    public TreasureBox get(int id) {
        return treasureBoxMapper.selectById(id);
    }

    @Transactional
    public void update(TreasureBox box, String operator) {
        box.setUt(new Date());
        box.setUb(operator);
        treasureBoxMapper.updateById(box);
    }

    @Transactional
    public void delete(int id) {
        treasureBoxRelateMapper.deleteByTreasureBoxId(id);
        treasureBoxMapper.deleteById(id);
    }

    public List<TreasureBox> findList() {
        return treasureBoxMapper.selectList(null);
    }

    public TreasureBoxRelate getRelate(int giftId) {
        return treasureBoxRelateMapper.getByGiftId(giftId);
    }
}
