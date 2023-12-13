package com.csgo.service.roll;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.roll.RollUserSelectCondition;
import com.csgo.condition.roll.SearchRollUserCondition;
import com.csgo.domain.plus.roll.RollGiftPlus;
import com.csgo.domain.plus.roll.RollGiftType;
import com.csgo.domain.plus.roll.RollUserDTO;
import com.csgo.domain.plus.roll.RollUserPlus;
import com.csgo.mapper.RollUserMapper;
import com.csgo.mapper.plus.roll.RollGiftPlusMapper;
import com.csgo.mapper.plus.roll.RollUserPlusMapper;
import com.csgo.support.GlobalConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by Admin on 2021/5/21
 */
@Service
public class RollUserService {
    @Autowired
    private RollUserPlusMapper mapper;
    @Autowired
    private RollUserMapper rollUserMapper;
    @Autowired
    private RollGiftPlusMapper rollGiftPlusMapper;

    public RollUserPlus getAppoint(int rollId, int rollGiftId) {
        return mapper.getAppoint(rollId, rollGiftId);
    }

    public List<RollUserPlus> find(Integer rollId, String flag, String userName) {
        return mapper.find(null, rollId, flag, userName);
    }

    public List<RollUserDTO> findDTO(Integer rollId) {
        return rollUserMapper.find(rollId);
    }

    @Transactional
    public void hit(int rollGiftId, int userId, int rollId) {
        RollGiftPlus rollGiftPlus = rollGiftPlusMapper.selectById(rollGiftId);
        if (Arrays.asList(GlobalConstants.ROLL_USER_CANCEL_SIGN, GlobalConstants.ROLL_USER_INNER_SIGN).contains(userId)) {
            if (GlobalConstants.ROLL_USER_CANCEL_SIGN == userId) {
                rollGiftPlus.setType(RollGiftType.CANCEL);
            }
            if (GlobalConstants.ROLL_USER_INNER_SIGN == userId) {
                rollGiftPlus.setType(RollGiftType.INNER);
            }
            rollGiftPlus.setUt(new Date());
            rollGiftPlusMapper.updateById(rollGiftPlus);
            return;
        }
        rollGiftPlus.setType(RollGiftType.NORMAL);
        rollGiftPlus.setUt(new Date());
        rollGiftPlusMapper.updateById(rollGiftPlus);
        RollUserPlus hitUser = mapper.getAppoint(rollId, rollGiftId);
        if (hitUser != null) {
            hitUser.setIsappoint(GlobalConstants.ROLL_USER_UN_HIT);
            hitUser.setRollgiftId(null);
            hitUser.setRollgiftImg(null);
            hitUser.setRollgiftName(null);
            hitUser.setRollgiftPrice(null);
            hitUser.setRollgiftGrade(null);
            update(hitUser);
        }
        RollUserPlus rollUser = mapper.getUnHit(userId, rollId);
        if (rollUser == null) {
            return;
        }
        rollUser.setIsappoint(GlobalConstants.ROLL_USER_HIT);
        rollUser.setRollgiftId(rollGiftPlus.getId());
        rollUser.setRollgiftImg(rollGiftPlus.getImg());
        rollUser.setRollgiftName(rollGiftPlus.getProductname());
        rollUser.setRollgiftPrice(rollGiftPlus.getPrice());
        rollUser.setRollgiftGrade(rollGiftPlus.getGrade());
        update(rollUser);
    }

    @Transactional
    public void update(RollUserPlus rollUser) {
        rollUser.setUt(new Date());
        mapper.updateById(rollUser);
    }

    public Page<RollUserDTO> findSelectRoomUserPage(RollUserSelectCondition condition) {
        return mapper.findSelectRoomUserPage(condition.getPage(), condition);
    }

    public Page<RollUserDTO> findRoomUserPage(SearchRollUserCondition condition) {
        return mapper.findRoomUserPage(condition.getPage(), condition);
    }

}
