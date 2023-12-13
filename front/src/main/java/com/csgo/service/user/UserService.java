package com.csgo.service.user;


import com.csgo.config.properties.SteamProperties;
import com.csgo.config.properties.ZBTProperties;
import com.csgo.constants.CommonBizCode;
import com.csgo.constants.LuckyProductConstants;
import com.csgo.domain.ExchangeRate;
import com.csgo.domain.plus.config.SystemConfigFacade;
import com.csgo.domain.plus.user.SteamUpdateType;
import com.csgo.domain.plus.user.UserInnerRechargeLimit;
import com.csgo.domain.plus.user.UserNameAlterRecord;
import com.csgo.domain.plus.user.UserPlus;
import com.csgo.domain.user.User;
import com.csgo.domain.user.UserCommissionLog;
import com.csgo.framework.exception.BizClientException;
import com.csgo.mapper.ExchangeRateMapper;
import com.csgo.mapper.UserMapper;
import com.csgo.mapper.plus.user.UserInnerRechargeLimitMapper;
import com.csgo.mapper.plus.user.UserNameAlterRecordMapper;
import com.csgo.mapper.plus.user.UserPlusMapper;
import com.csgo.modular.ig.service.IgService;
import com.csgo.redis.RedisTemplateFacde;
import com.csgo.repository.UserCommissionLogRepository;
import com.csgo.service.config.SystemConfigService;
import com.csgo.service.membership.MembershipService;
import com.csgo.support.StandardExceptionCode;
import com.csgo.support.ZBT.CheckCreateDTO;
import com.csgo.support.ZBT.ZBTResult;
import com.csgo.util.DateUtils;
import com.csgo.util.DateUtilsEx;
import com.csgo.util.HttpUtil2;
import com.csgo.util.HttpsUtil2;
import com.csgo.web.request.user.EditUserRequest;
import com.echo.framework.platform.exception.ApiException;
import com.echo.framework.support.jackson.json.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
@Slf4j
public class UserService {
    // 配置文件中缓存的名字
    private static final String CACHE_NAME = "user-frozen-";

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserPlusMapper userPlusMapper;
    @Autowired
    private ExchangeRateMapper exchangeRateMapper;
    @Autowired
    private UserCommissionLogRepository userCommissionLogRepository;
    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private SteamProperties steamProperties;
    @Value("${baseExtensionUrl}")
    private String baseExtensionUrl;
    @Value("${faceUrl}")
    private String faceUrl;
    @Autowired
    private RedisTemplateFacde redisTemplateFacde;
    @Autowired
    private UserSteamUpdateRecordService userSteamUpdateRecordService;
    @Autowired
    private MembershipService membershipService;
    @Autowired
    private UserInnerRechargeLimitMapper userInnerRechargeLimitMapper;
    @Autowired
    private UserNameAlterRecordMapper userNameAlterRecordMapper;
    @Autowired
    private UserCommissionLogService userCommissionLogService;
    @Autowired
    private ZBTProperties properties;
    @Autowired
    private IgService igService;

    public List<UserPlus> findByIds(Collection<Integer> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return new ArrayList<>();
        }
        return userPlusMapper.findByIds(ids);
    }

    public UserPlus get(Integer userId) {
        return userPlusMapper.selectById(userId);
    }

    public boolean frozen(Integer userId) {
        String cacheFrozen = redisTemplateFacde.get(CACHE_NAME + userId);
        if (!StringUtils.hasText(cacheFrozen)) {
            boolean frozen = userPlusMapper.selectById(userId).isFrozen();
            if (!frozen) {
                redisTemplateFacde.set(CACHE_NAME + userId, "false");
            }
            return frozen;
        }
        return false;
    }

    @Transactional
    public UserPlus registerBySteamId(String steamId) {
        String responseBody = HttpUtil2.doGet(steamProperties.getApiUrl() + "/ISteamUser/GetPlayerSummaries/v0002/?key=" + steamProperties.getKey() + "&steamids=" + steamId, new HashMap<>());
        SteamResponse response = JSON.fromJSON(responseBody, SteamResponse.class);
        if (CollectionUtils.isEmpty(response.getResponse().getPlayers())) {
            return null;
        }
        String name = response.getResponse().getPlayers().get(0).getPersonaname();
        UserPlus user = new UserPlus();

        user.setUserName(name);
        user.setUserNumber(name);
        user.setName(name);
        user.setType(0);
        user.setFlag(0);
        user.setIsroll(false);
        user.setCreatedAt(new Date());

        boolean isExist = true;
        while (isExist) {
            String selfExtensionCode = RandomStringUtils.randomNumeric(6);
            user.setExtensionUrl(baseExtensionUrl + selfExtensionCode); //生成随机六位的邀请码
            user.setExtensionCode(selfExtensionCode); //生成随机六位的邀请码
            isExist = null != getUserByExtensionCode(selfExtensionCode.trim());
        }

        user.setExtraNum(0);
        user.setSteam(steamId);
        user.setImg(faceUrl);
        userPlusMapper.insert(user);
        membershipService.insert(user.getId());
        return user;
    }

    @Transactional
    public int register(UserPlus user) {
        boolean isExist = true;
        while (isExist) {
            String selfExtensionCode = RandomStringUtils.randomNumeric(6);
            user.setExtensionUrl(baseExtensionUrl + selfExtensionCode); //生成随机六位的邀请码
            user.setExtensionCode(selfExtensionCode); //生成随机六位的邀请码
            isExist = null != getUserByExtensionCode(selfExtensionCode.trim());
        }

        setAccessoryLucky(user);
        user.setCreatedAt(new Date());
        userPlusMapper.insert(user);
        membershipService.insert(user.getId());
        return user.getId();
    }

    private void setAccessoryLucky(UserPlus user) {
        try {
            SystemConfigFacade config = systemConfigService.findByPrefix(LuckyProductConstants.LUCKY_PRODUCT_PREFIX);
            BigDecimal min = config.decimal(LuckyProductConstants.LUCKY_PRODUCT_USER_INIT_LUCKY_VALUE_MIN);
            BigDecimal max = config.decimal(LuckyProductConstants.LUCKY_PRODUCT_USER_INIT_LUCKY_VALUE_MAX);
            if (max.compareTo(min) >= 0) {
                BigDecimal random = min.add(BigDecimal.valueOf(Math.random()).multiply(max.subtract(min))).setScale(2, RoundingMode.HALF_UP);
                user.setAccessoryLucky(random);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public User login(String username, String password) {
        User user = new User();
        user.setUserName(username);
        user.setPassword(password);
        return userMapper.selectOne(user);
    }

    public void update(UserPlus user) {
        userPlusMapper.updateById(user);
    }

    public void update(UserPlus user, String beforeSteam, String ip) {
        if (StringUtils.hasText(beforeSteam)) {
            userSteamUpdateRecordService.add(user.getId(), beforeSteam, user.getSteam(), SteamUpdateType.USER, ip);
        }
        userPlusMapper.updateById(user);
    }

    public int update(User user, int id) {
        user.setId(id);
        return userMapper.updateByPrimaryKeySelective(user);
    }

    public User queryUserId(int id) {
        User user = new User();
        user.setId(id);
        return userMapper.selectOne(user);
    }

    public List<User> selectList(User user) {
        return userMapper.selectList(user);
    }

    public int delete(int id) {
        return userMapper.deleteByPrimaryKey(id);
    }

    public User getUserByExtensionUrl(String extensionUrl) {
        return userMapper.getUserByExtensionUrl(extensionUrl);
    }

    public User getUserByExtensionCode(String extensionCode) {
        return userMapper.getUserByExtensionCode(extensionCode);
    }

    public User getUserByUserName(String userName) {
        return userMapper.getUserByUserName(userName);
    }

    public User getUserById(Integer userId) {
        return userMapper.selectByPrimaryKey(userId);
    }

    public UserPlus getBySteamId(String steamId) {
        return userPlusMapper.getBySteamId(steamId);
    }
    // 二级分销

    public void userDistribution(String orderNum, Integer userId, Integer parentId, BigDecimal amount, Integer type) {
        ExchangeRate exchangeRate = exchangeRateMapper.getById(1);
        UserCommissionLog userCommissionLog = new UserCommissionLog();
        if (exchangeRate != null) {
            User user = userMapper.selectByPrimaryKey(parentId);
            if ((user != null && type.equals(1)) || (null != parentId && parentId == 0)) {
                log.info("进行一级分销-------------------------");
                BigDecimal firstCommission = exchangeRate.getFirstCommission();
                userCommissionLog.setProportion(firstCommission.doubleValue());
                userCommissionLog.setCommissionAmount(amount.multiply(firstCommission));
                userCommissionLog.setGrade(1);
                // 如果存在上级进行二级分销
//                if (user.getParentId() != null) {
//                    userDistribution(orderNum, userId, user.getParentId(), amount, 2);
//                }
            }
//            if (user != null && type.equals(2)) {
//                log.debug("进行二级分销-------------------------");
//                BigDecimal secondCommission = exchangeRate.getSecondCommission();
//                userCommissionLog.setProportion(secondCommission.doubleValue());
//                userCommissionLog.setCommissionAmount(amount.multiply(secondCommission));
//                userCommissionLog.setGrade(2);
//            }

            userCommissionLog.setOrderNum(orderNum);
            userCommissionLog.setAddTime(new Date());
            userCommissionLog.setAmount(amount);
            userCommissionLog.setUserId(userId);
            userCommissionLog.setCommissionUserId(parentId);
            userCommissionLog.setStatus(3);
            userCommissionLog.setSettlementTime(DateUtils.getNextMon(new Date()));
            userCommissionLogRepository.save(userCommissionLog);


        }
        log.info("分销失败-------------------------");
    }

    public boolean validateInnerRecharge(int userId) {
        UserInnerRechargeLimit recharge = userInnerRechargeLimitMapper.getNotRemoveByUserId(userId);
        if (null != recharge) {
            return true;
        }
        UserInnerRechargeLimit whiteRecharge = userInnerRechargeLimitMapper.getNotRemoveWhiteByUserId(userId);
        return null != whiteRecharge;
    }

    /**
     * 根据身份证号码获取绑定用户列表
     *
     * @param idNo
     * @return
     */
    public List<UserPlus> findByIdNo(String idNo) {
        return userPlusMapper.findByIdNo(idNo);
    }

    /**
     * 校验用户昵称是否重复
     *
     * @param userId
     * @param name
     */
    public boolean checkNameExits(Integer userId, String name) {
        int count = userPlusMapper.getNameCount(userId, name);
        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 校验steam是否超过5个
     *
     * @param userId
     * @param steam
     */
    public boolean checkSteamExits(Integer userId, String steam) {
        int count = userPlusMapper.getSteamCount(userId, steam);
        if (count >= 5) {
            return true;
        } else {
            return false;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateInfo(UserPlus user, EditUserRequest request, String ip) {
        //判断修改用户昵称是否存在
        int userId = user.getId();
        boolean isExits = this.checkNameExits(userId, request.getName());
        if (isExits) {
            throw BizClientException.of(CommonBizCode.USER_USERNAME_EXIST);
        }
        if (!StringUtils.isEmpty(request.getInvitedExtensionCode())) {
            if (new Date().after(DateUtilsEx.calDateByHour(user.getCreatedAt(), 24))) {
                throw BizClientException.of(CommonBizCode.USER_PROMOTION_CODE_FILL_TIMEOUT);
            }
            User userCode = this.getUserByExtensionCode(request.getInvitedExtensionCode());
            if (userCode == null || userCode.getId().equals(userId)) {
                throw BizClientException.of(CommonBizCode.USER_PROMOTION_CODE_ERROR);
            }
            user.setParentId(userCode.getId());
            user.setInvitedDate(new Date());
            userCommissionLogService.updateCommissionUserId(user.getId(), userCode.getId());
        }
        if (!StringUtils.isEmpty(request.getName())) {
            //记录昵称变更记录
            if (!request.getName().equals(user.getName())) {
                UserNameAlterRecord userNameAlterRecord = new UserNameAlterRecord();
                userNameAlterRecord.setUserId(user.getId());
                userNameAlterRecord.setSourceName(user.getName());
                userNameAlterRecord.setAfterName(request.getName());
                userNameAlterRecord.setCt(new Date());
                userNameAlterRecordMapper.insert(userNameAlterRecord);
            }
            user.setName(request.getName());
        }
        String beforeSteam = null;
        if (!StringUtils.isEmpty(request.getSteam())) {
            beforeSteam = user.getSteam();

            //散户判断steam只能绑定五个账号，超过不让保存
    /*        if (GlobalConstants.RETAIL_USER_FLAG == user.getFlag()) {
                boolean isSteamExits = this.checkSteamExits(userId, request.getSteam());
                if (isSteamExits) {
                    throw new ApiException(StandardExceptionCode.STEAM_EXISTS, "很抱歉，您当前的steam链接绑定次数已达上限。");
                }
            }*/

            //验证steam的合法性
            CheckCreateDTO checkCreateDTO = new CheckCreateDTO();
            checkCreateDTO.setAppId(properties.getAppId());
            checkCreateDTO.setTradeUrl(request.getSteam());
            checkCreateDTO.setType(1);
            String result = HttpsUtil2.getJsonData(JSON.toJSON(checkCreateDTO), properties.getSteamCheck());
            ZBTResult zbtResult = JSON.fromJSON(result, ZBTResult.class);
            if (null == zbtResult || !zbtResult.getSuccess()) {
                log.error("[Steam链接校验] url:{} data:{}", request.getSteam(), JSON.toJSON(zbtResult));
                throw BizClientException.of(CommonBizCode.STEAM_URL_ILLEGAL);
            }

            //yyService.checkSteamTradeLinks(request.getSteam());
            //igService.checkSteamTradeLinks(request.getSteam());
            user.setSteam(request.getSteam());
        }
        user.setUpdatedAt(new Date());
        this.update(user, beforeSteam, ip);
    }

    /**
     * 根据身份证号码获取实名认证通过列表
     *
     * @param idNo
     * @return
     */
    public List<UserPlus> findRealNameAuthByIdNo(String idNo) {
        return userPlusMapper.findRealNameAuthByIdNo(idNo);
    }

}
