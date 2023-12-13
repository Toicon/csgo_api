package com.csgo.util;

import com.csgo.support.GlobalConstants;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author admin
 */
@Slf4j
public final class SignUtil {

    public static String sign(HttpServletRequest request) {
        return sign(getDigest(request));
    }

    public static String sign(Map<String, Object> map) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            builder.append(entry.getValue().toString());
        }
        try {
            return Base64.getBase64(SecuritySHA1Utils.shaEncode(MD5.md5(builder.toString(), "UTF-8")).toUpperCase());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return "";
    }

    /**
     * 获取request的参数信息
     *
     * @param request
     * @return
     */
    public static Map<String, Object> getDigest(HttpServletRequest request) {
        Map<String, Object> map = new TreeMap<>();
        Enumeration enu = request.getParameterNames();
        while (enu.hasMoreElements()) {
            String paraName = (String) enu.nextElement();
            String value = request.getParameter(paraName);
            if (!paraName.equals("token")) {
                if (StringUtils.isEmpty(value)) {
                    value += GlobalConstants.SIGN_KEY;
                }
                map.put(paraName, value);
            }
        }
        if (map.isEmpty()) {
            return Maps.newHashMap();
        }
        map.put("key", GlobalConstants.SIGN_KEY);
        return map;
    }

}
