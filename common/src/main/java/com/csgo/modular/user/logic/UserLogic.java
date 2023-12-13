package com.csgo.modular.user.logic;

import com.csgo.constants.CommonBizCode;
import com.csgo.domain.plus.user.UserPlus;
import com.csgo.domain.user.User;
import com.csgo.framework.exception.BizClientException;
import com.csgo.framework.exception.BizServerException;
import com.csgo.framework.util.CollectionKit;
import com.csgo.framework.util.EmailUtil;
import com.csgo.mapper.plus.user.UserPlusMapper;
import com.csgo.modular.user.enums.UserStatusEnums;
import com.csgo.support.ConcurrencyLimit;
import com.echo.framework.platform.exception.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author admin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserLogic {

    private final UserPlusMapper userPlusMapper;

    public UserPlus loadUser(Integer userId) {
        UserPlus player = userPlusMapper.selectById(userId);
        if (player == null) {
            throw BizServerException.of(CommonBizCode.USER_NOT_FOUND);
        }
        return player;
    }

    public Map<Integer, UserPlus> getUserIdMap(List<Integer> userIdList) {
        if (CollectionUtils.isEmpty(userIdList)) {
            return Collections.emptyMap();
        }
        List<UserPlus> list = userPlusMapper.findByIds(userIdList);

        return CollectionKit.convertMap(list, UserPlus::getId);
    }

    public void checkEmailExist(UserPlus user) {
        if (StringUtils.isBlank(user.getEmil())) {
            throw BizClientException.of(CommonBizCode.USER_EMAIL_NOT_EXIST);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @ConcurrencyLimit
    public boolean updateEmail(Integer userId, String email) {
        UserPlus user = Optional.of(userPlusMapper.selectById(userId))
                .orElseThrow(() -> BizServerException.of(CommonBizCode.USER_NOT_FOUND));

        if (StringUtils.isBlank(email)) {
            throw BizClientException.of(CommonBizCode.USER_EMAIL_BLANK);
        }

        if (!EmailUtil.isEmail(email)) {
            throw BizClientException.of(CommonBizCode.USER_EMAIL_ILLEGAL);
        }

        user.setEmil(email);
        return userPlusMapper.updateById(user) > 0;
    }

    public boolean isNormalUser(UserPlus user) {
        if (!UserStatusEnums.NORMAL.getCode().equals(user.getStatus())) {
            return false;
        }
        if (user.isFrozen() || user.getIsDelete() == 0) {
            return false;
        }
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public void changeStatus(Integer userId, UserStatusEnums status) {
        changeStatus(userId, status.getCode());
    }

    @Transactional(rollbackFor = Exception.class)
    public void changeStatus(Integer userId, Integer status) {
        UserPlus user = loadUser(userId);
        user.setStatus(status);
        userPlusMapper.updateById(user);
    }

    public void checkStatus(User user) {
        if (UserStatusEnums.CANCEL.getCode().equals(user.getStatus())) {
            throw new ApiException(4030, "账号已经被注销，请联系客服");
        }
    }

}
