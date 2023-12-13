package com.csgo.service;

import com.csgo.mapper.UserMessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserMessageGiftService {

    @Autowired
    private UserMessageMapper messageMapper;


}
