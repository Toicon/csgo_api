package com.csgo.service.membership;

import com.csgo.domain.plus.membership.Membership;
import com.csgo.mapper.plus.membership.MembershipMapper;
import com.echo.framework.platform.exception.ApiException;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * Created by Admin on 2021/12/14
 */
@Service
public class MembershipService {
    @Autowired
    private MembershipMapper mapper;

    public Membership findByUserId(Integer userId) {
        return mapper.get(userId);
    }

    @Transactional
    public void insert(Integer userId) {
        Membership membership = new Membership();
        membership.setUserId(userId);
        mapper.insert(membership);
    }

    @Transactional
    public void update(Integer userId, String idCard, String realName, Date birthDate) {
        Membership membership = mapper.selectById(userId);
        if (StringUtils.hasText(membership.getIdCard())) {
            throw new ApiException(HttpStatus.SC_BAD_REQUEST, "生日信息已登记，请勿重复设置");
        }
        membership.setIdCard(idCard);
        membership.setBirthday(birthDate);
        membership.setRealName(realName);
        mapper.updateById(membership);
    }

    @Transactional
    public void updateImg(Membership membership) {
        membership.setUt(new Date());
        mapper.updateById(membership);
    }
}
