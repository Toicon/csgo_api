package com.csgo.service.face;

import com.csgo.constants.CommonBizCode;
import com.csgo.domain.enums.YesOrNoEnum;
import com.csgo.domain.plus.user.UserPlus;
import com.csgo.framework.exception.BizClientException;
import com.csgo.mapper.plus.user.UserPlusMapper;
import com.csgo.service.user.UserService;
import com.csgo.support.GlobalConstants;
import com.csgo.support.StandardExceptionCode;
import com.echo.framework.platform.exception.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author admin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RealNameService {

    private final UserService userService;

    private final UserPlusMapper userPlusMapper;

    public void checkRealNameVerifyPass(Integer userId) {
        UserPlus user = userService.get(userId);
        //散户需要进行判断实名认证
        if (GlobalConstants.RETAIL_USER_FLAG == user.getFlag()) {
            if (user.getRealNameState() == null || YesOrNoEnum.NO.getCode().equals(user.getRealNameState())) {
                throw BizClientException.of(CommonBizCode.USER_REAL_NAME_NOT_AUTH);
            }
        }
    }

    public void updateRealNameInfo(UserPlus user, String idNo, String name) {
        user.setIdNo(idNo);
        user.setRealName(name);
        user.setRealNameState(YesOrNoEnum.YES.getCode());
        userPlusMapper.updateById(user);
    }

}
