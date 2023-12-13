package com.csgo.service.envelop;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.csgo.domain.enums.RedEnvelopStatus;
import com.csgo.domain.plus.envelop.RedEnvelop;
import com.csgo.domain.plus.envelop.RedEnvelopItem;
import com.csgo.mapper.plus.envelop.RedEnvelopItemMapper;
import com.csgo.mapper.plus.envelop.RedEnvelopMapper;
import com.csgo.redis.RedisTemplateFacde;
import com.csgo.util.BeanUtilsEx;

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
}
