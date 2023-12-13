package com.csgo.service.envelop;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.envelop.SearchRedEnvelopCondition;
import com.csgo.condition.envelop.SearchRedEnvelopRecordCondition;
import com.csgo.domain.enums.RedEnvelopStatus;
import com.csgo.domain.plus.envelop.RedEnvelop;
import com.csgo.domain.plus.envelop.RedEnvelopItem;
import com.csgo.domain.plus.envelop.RedEnvelopRecord;
import com.csgo.mapper.plus.envelop.RedEnvelopItemMapper;
import com.csgo.mapper.plus.envelop.RedEnvelopMapper;
import com.csgo.mapper.plus.envelop.RedEnvelopRecordMapper;
import com.csgo.redis.RedisTemplateFacde;
import com.csgo.support.StandardExceptionCode;
import com.csgo.util.BeanUtilsEx;
import com.echo.framework.platform.exception.ApiException;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Admin on 2021/4/27
 */
@Service
public class RedEnvelopService {

    @Autowired
    private RedEnvelopMapper mapper;
    @Autowired
    private RedEnvelopItemMapper itemMapper;
    @Autowired
    private RedisTemplateFacde redisTemplateFacde;
    @Autowired
    private RedEnvelopRecordMapper recordMapper;

    @Transactional
    public void levelRedEnvelop(int grade, BigDecimal amount) {
        if (BigDecimal.ZERO.compareTo(amount) > 0) {
            return;
        }
        RedEnvelop redEnvelop = mapper.findByGrade(grade);
        if (null == redEnvelop) {
            if (BigDecimal.ZERO.compareTo(amount) == 0) {
                return;
            }
            Date date = new Date();
            redEnvelop = new RedEnvelop();
            redEnvelop.setToken(""); //无红包口令
            redEnvelop.setNum(0); //红包数量无限
            redEnvelop.setAuto(false);
            redEnvelop.setShowNum(false);
            redEnvelop.setEffectiveStartTime(date); //有效时间
            redEnvelop.setEffectiveEndTime(DateUtils.addYears(date, 15));
            redEnvelop.setMinAmount(amount);
            redEnvelop.setMaxAmount(amount);
            redEnvelop.setStatus(RedEnvelopStatus.NORMAL);
            redEnvelop.setGrade(grade);
            redEnvelop.setName("会员LV" + grade + "红包");
            mapper.insert(redEnvelop);
            RedEnvelopItem item = new RedEnvelopItem();
            BeanUtilsEx.copyProperties(redEnvelop, item, new HashSet<>(Collections.singletonList("id")));
            item.setEnvelopId(redEnvelop.getId());
            itemMapper.insert(item);
        } else if (redEnvelop.getMinAmount().compareTo(amount) != 0) {
            redEnvelop.setMinAmount(amount);
            redEnvelop.setMaxAmount(amount);
            mapper.updateById(redEnvelop);

            RedEnvelopItem item = itemMapper.getByEnvelopId(redEnvelop.getId());
            item.setMinAmount(amount);
            item.setMaxAmount(amount);
            itemMapper.updateById(item);
        }
    }

    public List<RedEnvelop> find() {
        return mapper.find();
    }

    @Transactional
    public void expireThenDistribute(List<RedEnvelop> redEnvelops) {
        List<Integer> redEnvelopIds = redEnvelops.stream().map(RedEnvelop::getId).collect(Collectors.toList());
        Map<Integer, RedEnvelopItem> redEnvelopItemMap = itemMapper.find(redEnvelopIds).stream().collect(Collectors.toMap(RedEnvelopItem::getEnvelopId, redEnvelopItem -> redEnvelopItem));
        redEnvelops.forEach(redEnvelop -> {
            Date startTime = DateUtils.truncate(new Date(), Calendar.DATE);
            if (redEnvelopItemMap.containsKey(redEnvelop.getId())) {
                RedEnvelopItem redEnvelopItem = redEnvelopItemMap.get(redEnvelop.getId());
                // 是否在间隔时间内
                if (DateUtils.addDays(redEnvelopItem.getEffectiveStartTime(), redEnvelop.getTimeInterval()).compareTo(startTime) > 0) {
                    return;
                }
                redEnvelopItem.setStatus(RedEnvelopStatus.EXPIRED);
                itemMapper.updateById(redEnvelopItem);
                redisTemplateFacde.delete("envelopItemId-" + redEnvelopItem.getId());
            }


            RedEnvelopItem item = new RedEnvelopItem();
            BeanUtilsEx.copyProperties(redEnvelop, item);
            item.setEffectiveStartTime(startTime);
            item.setEffectiveEndTime(DateUtils.addDays(startTime, 1));
            item.setEnvelopId(redEnvelop.getId());
            itemMapper.insert(item);
            redisTemplateFacde.set("envelopItemId-" + item.getId(), item.getNum().toString());
        });
    }

    @Transactional
    public void add(RedEnvelop redEnvelop) {
        RedEnvelop exists = mapper.get(redEnvelop.getToken());
        if (exists != null) {
            throw new ApiException(StandardExceptionCode.RED_ENVELOP_FAILURE, "口令重复");
        }
        Date current = new Date();
        redEnvelop.setCreateDate(current);
        mapper.insert(redEnvelop);

        RedEnvelopItem item = new RedEnvelopItem();
        BeanUtilsEx.copyProperties(redEnvelop, item, new HashSet<>(Collections.singletonList("id")));
        if (redEnvelop.isAuto()) {
            Date startTime = DateUtils.truncate(current, Calendar.DATE);
            item.setEffectiveStartTime(startTime);
            item.setEffectiveEndTime(DateUtils.addDays(startTime, 1));
        }
        item.setEnvelopId(redEnvelop.getId());
        itemMapper.insert(item);

        redisTemplateFacde.set("envelopItemId-" + item.getId(), item.getNum().toString());
    }

    @Transactional
    public void update(RedEnvelop redEnvelop) {
        RedEnvelop exists = mapper.get(redEnvelop.getToken());
        if (null != exists && !exists.getId().equals(redEnvelop.getId())) {
            throw new ApiException(StandardExceptionCode.RED_ENVELOP_FAILURE, "口令重复");
        }

        Date current = new Date();
        redEnvelop.setUpdateDate(current);
        mapper.updateById(redEnvelop);
        RedEnvelopItem item;
        if (redEnvelop.isAuto()) {
            item = itemMapper.get(redEnvelop.getId(), current);
            if (item == null) {
                item = getRedEnvelopItem(redEnvelop, current);
            }
        } else {
            item = getRedEnvelopItem(redEnvelop, null);
        }
        List<RedEnvelopRecord> records = recordMapper.find(null, item.getId(), item.getEffectiveStartTime());
        int size = 0;
        if (!CollectionUtils.isEmpty(records)) {
            size = records.size();
        }
        int num = redEnvelop.getNum() - size;
        redisTemplateFacde.set("envelopItemId-" + item.getId(), Integer.toString(num));
        BeanUtilsEx.copyProperties(redEnvelop, item, new HashSet<>(Collections.singletonList("id")));
        if (null != item.getId() && item.getId() > 0) {
            itemMapper.updateById(item);
            return;
        }
        itemMapper.insert(item);
    }

    private RedEnvelopItem getRedEnvelopItem(RedEnvelop redEnvelop, Date current) {
        RedEnvelopItem item = itemMapper.get(redEnvelop.getId(), null);
        if (item == null) {
            item = new RedEnvelopItem();
        }
        item.setEnvelopId(redEnvelop.getId());
        if (redEnvelop.isAuto()) {
            Date startTime = DateUtils.truncate(current, Calendar.DATE);
            item.setEffectiveStartTime(startTime);
            item.setEffectiveEndTime(DateUtils.addDays(startTime, 1));
        } else {
            item.setEffectiveStartTime(redEnvelop.getEffectiveStartTime());
            item.setEffectiveEndTime(redEnvelop.getEffectiveEndTime());
        }
        return item;
    }

    public RedEnvelop get(int id) {
        return mapper.selectById(id);
    }

    public Page<RedEnvelop> pagination(SearchRedEnvelopCondition condition) {
        return mapper.pagination(condition);
    }

    @Transactional
    public void delete(int id) {
        List<RedEnvelopItem> items = itemMapper.findByEnvelopId(id);
        items.forEach(item -> {
            item.setStatus(RedEnvelopStatus.DELETE);
            itemMapper.updateById(item);
            redisTemplateFacde.delete("envelopItemId-" + item.getId());
        });
        RedEnvelop redEnvelop = mapper.selectById(id);
        redEnvelop.setStatus(RedEnvelopStatus.DELETE);
        mapper.updateById(redEnvelop);
    }

    public BigDecimal getSendAmount(Integer redEnvelopId) {
        return mapper.getSendAmount(redEnvelopId);
    }

    public List<RedEnvelopItem> findByRedEnvelopId(Integer redEnvelopId) {
        return itemMapper.findByEnvelopId(redEnvelopId);
    }

    public Page<RedEnvelopRecord> recordPagination(SearchRedEnvelopRecordCondition condition) {
        return recordMapper.pagination(condition);
    }

    public RedEnvelop findByGrade(Integer level) {
        return mapper.findByGrade(level);
    }
}
