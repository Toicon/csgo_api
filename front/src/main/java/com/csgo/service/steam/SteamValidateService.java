package com.csgo.service.steam;

import com.csgo.config.properties.SteamProperties;
import com.csgo.support.StandardExceptionCode;
import com.echo.framework.platform.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.openid4java.consumer.ConsumerManager;
import org.openid4java.consumer.VerificationResult;
import org.openid4java.discovery.DiscoveryException;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.discovery.Identifier;
import org.openid4java.message.ParameterList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author admin
 */
@Slf4j
@Service
public class SteamValidateService {
    private static final Pattern STEAM_REGEX = Pattern.compile("(\\d+)");
    @Autowired
    private SteamProperties properties;
    @Autowired
    private ConsumerManager manager;

    private volatile DiscoveryInformation discovered;

    public String loginUrl() {
        try {
            return manager.authenticate(getDiscovered(), properties.getCallback()).getDestinationUrl(true);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ApiException(StandardExceptionCode.STEAM_LOGIN_FAILURE, "steam登录失败");
        }
    }

    public String verify(String receivingUrl, Map responseMap) {
        ParameterList responseList = new ParameterList(responseMap);
        try {
            VerificationResult verification = manager.verify(receivingUrl, responseList, getDiscovered());
            Identifier verifiedId = verification.getVerifiedId();
            if (verifiedId != null) {
                String id = verifiedId.getIdentifier();
                Matcher matcher = STEAM_REGEX.matcher(id);
                if (matcher.find()) {
                    return matcher.group(1);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ApiException(StandardExceptionCode.STEAM_LOGIN_FAILURE, "steam登录失败");
        }
        return null;
    }

    private DiscoveryInformation getDiscovered() {
        if (this.discovered == null) {
            try {
                this.discovered = manager.associate(manager.discover(properties.getOpenIdUrl()));
                return this.discovered;
            } catch (DiscoveryException e) {
                log.error(e.getMessage(), e);
                throw new ApiException(StandardExceptionCode.STEAM_LOGIN_FAILURE, "steam登录失败");
            }
        }
        return this.discovered;
    }
}
