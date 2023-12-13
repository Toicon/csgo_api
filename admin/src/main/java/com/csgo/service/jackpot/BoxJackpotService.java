package com.csgo.service.jackpot;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.domain.BaseEntity;
import com.csgo.domain.plus.jackpot.BoxJackpot;
import com.csgo.domain.plus.jackpot.BoxJackpotBillRecord;
import com.csgo.domain.plus.jackpot.BoxSpareJackpot;
import com.csgo.domain.plus.jackpot.BoxSpareJackpotBillRecord;
import com.csgo.domain.plus.jackpot.JackpotOperationRecord;
import com.csgo.domain.plus.jackpot.JackpotType;
import com.csgo.mapper.plus.jackpot.BoxJackpotBillRecordMapper;
import com.csgo.mapper.plus.jackpot.BoxJackpotMapper;
import com.csgo.mapper.plus.jackpot.BoxSpareJackpotBillRecordMapper;
import com.csgo.mapper.plus.jackpot.BoxSpareJackpotMapper;
import com.csgo.mapper.plus.jackpot.JackpotOperationRecordMapper;
import com.csgo.redis.RedisTemplateFacde;
import com.csgo.support.PageInfo;
import com.echo.framework.util.Messages;

/**
 * @author admin
 */
@Service
public class BoxJackpotService {
    private static final String BOX_JACKPOT_BALANCE = "BOX_JACKPOT_BALANCE";
    @Autowired
    private RedisTemplateFacde redisTemplateFacde;
    @Autowired
    private BoxJackpotMapper boxJackpotMapper;
    @Autowired
    private BoxSpareJackpotMapper boxSpareJackpotMapper;
    @Autowired
    private BoxJackpotBillRecordMapper boxJackpotBillRecordMapper;
    @Autowired
    private BoxSpareJackpotBillRecordMapper boxSpareJackpotBillRecordMapper;
    @Autowired
    private JackpotOperationRecordMapper jackpotOperationRecordMapper;

    @Transactional
    public void record(List<BoxJackpotBillRecord> records, String source) {
        if (CollectionUtils.isEmpty(records)) {
            return;
        }
        BoxJackpot boxJackpot = boxJackpotMapper.selectOne(null);
        if (records.size() > 1) {
            return;
        }
        BoxJackpotBillRecord boxJackpotBillRecord = records.get(0);
        boxJackpot.setBalance(boxJackpotBillRecord.getBalance());
        BaseEntity.updated(boxJackpot, source);
        boxJackpotMapper.updateById(boxJackpot);
        for (BoxJackpotBillRecord record : records) {
            boxJackpotBillRecordMapper.insert(record);
        }
        if (boxJackpotBillRecord.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        BigDecimal spare = boxJackpotBillRecord.getAmount().divide(new BigDecimal(9), 2, BigDecimal.ROUND_DOWN);
        BoxSpareJackpot boxSpareJackpot = boxSpareJackpotMapper.selectOne(null);
        boxSpareJackpot.setBalance(boxSpareJackpot.getBalance().add(spare));
        BaseEntity.updated(boxSpareJackpot, source);
        boxSpareJackpotMapper.updateById(boxSpareJackpot);
        BoxSpareJackpotBillRecord record = new BoxSpareJackpotBillRecord();
        record.setAmount(spare);
        BaseEntity.created(record, source);
        boxSpareJackpotBillRecordMapper.insert(record);
    }

    public BoxJackpot getBoxJackpot() {
        return boxJackpotMapper.selectOne(null);
    }

    public BoxSpareJackpot getBoxSpareJackpot() {
        return boxSpareJackpotMapper.selectOne(null);
    }

    @Transactional
    public void updateBoxJackpot(BigDecimal amount, String user) {
        BoxJackpot boxJackpot = boxJackpotMapper.selectOne(null);
        JackpotOperationRecord record = new JackpotOperationRecord();
        record.setAmount(amount);
        record.setBeforeAmount(boxJackpot.getBalance());
        record.setJackpotType(JackpotType.BOX);
        BaseEntity.created(record, user);
        jackpotOperationRecordMapper.insert(record);
        boxJackpot.setBalance(amount);
        boxJackpotMapper.updateById(boxJackpot);
        redisTemplateFacde.set(getBoxJackpotKey(), String.valueOf(amount));
    }

    public PageInfo<BoxJackpotBillRecord> pageList(Integer pageNum, Integer pageSize) {
        Page<BoxJackpotBillRecord> page = new Page<>(pageNum, pageSize);
        Page<BoxJackpotBillRecord> billRecordPage = boxJackpotBillRecordMapper.page(page);
        return new PageInfo<>(billRecordPage);
    }


    private String getBoxJackpotKey() {
        return Messages.format("balance:{}", BOX_JACKPOT_BALANCE);
    }


}
