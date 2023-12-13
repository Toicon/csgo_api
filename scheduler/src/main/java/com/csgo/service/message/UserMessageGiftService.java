package com.csgo.service.message;

import com.csgo.domain.user.UserMessageGift;
import com.csgo.mapper.UserMessageGiftMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author admin
 */
@Service
public class UserMessageGiftService {

    @Autowired
    private UserMessageGiftMapper messageMapper;

    @Transactional
    public void insert(UserMessageGift userMessageGift) {
        messageMapper.insert(userMessageGift);
    }

}
