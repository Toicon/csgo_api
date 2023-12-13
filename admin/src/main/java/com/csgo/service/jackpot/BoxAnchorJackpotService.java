package com.csgo.service.jackpot;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.domain.BaseEntity;
import com.csgo.domain.plus.anchor.BoxAnchorJackpot;
import com.csgo.domain.plus.anchor.BoxAnchorJackpotBillRecord;
import com.csgo.domain.plus.anchor.BoxAnchorSpareJackpot;
import com.csgo.domain.plus.anchor.BoxAnchorSpareJackpotBillRecord;
import com.csgo.domain.plus.jackpot.JackpotOperationRecord;
import com.csgo.domain.plus.jackpot.JackpotType;
import com.csgo.mapper.plus.anchor.BoxAnchorJackpotBillRecordMapper;
import com.csgo.mapper.plus.anchor.BoxAnchorJackpotMapper;
import com.csgo.mapper.plus.anchor.BoxAnchorSpareJackpotBillRecordMapper;
import com.csgo.mapper.plus.anchor.BoxAnchorSpareJackpotMapper;
import com.csgo.mapper.plus.jackpot.JackpotOperationRecordMapper;
import com.csgo.redis.RedisTemplateFacde;
import com.csgo.support.PageInfo;
import com.echo.framework.util.Messages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author admin
 */
@Service
public class BoxAnchorJackpotService {
    private static final String BOX_ANCHOR_JACKPOT_BALANCE = "BOX_ANCHOR_JACKPOT_BALANCE";
    @Autowired
    private RedisTemplateFacde redisTemplateFacde;
    @Autowired
    private BoxAnchorJackpotMapper boxAnchorJackpotMapper;
    @Autowired
    private BoxAnchorSpareJackpotMapper boxAnchorSpareJackpotMapper;
    @Autowired
    private BoxAnchorJackpotBillRecordMapper boxAnchorJackpotBillRecordMapper;
    @Autowired
    private BoxAnchorSpareJackpotBillRecordMapper boxAnchorSpareJackpotBillRecordMapper;
    @Autowired
    private JackpotOperationRecordMapper jackpotOperationRecordMapper;

    @Transactional
    public void record(List<BoxAnchorJackpotBillRecord> records, String source) {
        if (CollectionUtils.isEmpty(records)) {
            return;
        }
        BoxAnchorJackpot boxAnchorJackpot = boxAnchorJackpotMapper.selectOne(null);
        if (records.size() > 1) {
            return;
        }
        BoxAnchorJackpotBillRecord boxAnchorJackpotBillRecord = records.get(0);
        boxAnchorJackpot.setBalance(boxAnchorJackpotBillRecord.getBalance());
        BaseEntity.updated(boxAnchorJackpot, source);
        boxAnchorJackpotMapper.updateById(boxAnchorJackpot);
        for (BoxAnchorJackpotBillRecord record : records) {
            boxAnchorJackpotBillRecordMapper.insert(record);
        }
        if (boxAnchorJackpotBillRecord.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        BigDecimal spare = boxAnchorJackpotBillRecord.getAmount().divide(new BigDecimal(9), 2, BigDecimal.ROUND_DOWN);
        BoxAnchorSpareJackpot boxAnchorSpareJackpot = boxAnchorSpareJackpotMapper.selectOne(null);
        boxAnchorSpareJackpot.setBalance(boxAnchorSpareJackpot.getBalance().add(spare));
        BaseEntity.updated(boxAnchorSpareJackpot, source);
        boxAnchorSpareJackpotMapper.updateById(boxAnchorSpareJackpot);
        BoxAnchorSpareJackpotBillRecord record = new BoxAnchorSpareJackpotBillRecord();
        record.setAmount(spare);
        BaseEntity.created(record, source);
        boxAnchorSpareJackpotBillRecordMapper.insert(record);
    }

    public BoxAnchorJackpot getBoxJackpot() {
        return boxAnchorJackpotMapper.selectOne(null);
    }

    public BoxAnchorSpareJackpot getBoxSpareJackpot() {
        return boxAnchorSpareJackpotMapper.selectOne(null);
    }

    @Transactional
    public void updateBoxJackpot(BigDecimal amount, String user) {
        BoxAnchorJackpot boxAnchorJackpot = boxAnchorJackpotMapper.selectOne(null);
        JackpotOperationRecord record = new JackpotOperationRecord();
        record.setAmount(amount);
        record.setBeforeAmount(boxAnchorJackpot.getBalance());
        record.setJackpotType(JackpotType.ANCHORBOX);
        BaseEntity.created(record, user);
        jackpotOperationRecordMapper.insert(record);
        boxAnchorJackpot.setBalance(amount);
        boxAnchorJackpotMapper.updateById(boxAnchorJackpot);
        redisTemplateFacde.set(getBoxJackpotKey(), String.valueOf(amount));
    }

    public PageInfo<BoxAnchorJackpotBillRecord> pageList(Integer pageNum, Integer pageSize) {
        Page<BoxAnchorJackpotBillRecord> page = new Page<>(pageNum, pageSize);
        Page<BoxAnchorJackpotBillRecord> billRecordPage = boxAnchorJackpotBillRecordMapper.page(page);
        return new PageInfo<>(billRecordPage);
    }


    private String getBoxJackpotKey() {
        return Messages.format("balance:{}", BOX_ANCHOR_JACKPOT_BALANCE);
    }


}
