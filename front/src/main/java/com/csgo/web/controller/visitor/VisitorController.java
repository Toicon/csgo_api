package com.csgo.web.controller.visitor;

import com.csgo.support.Result;
import com.csgo.util.CookieUtils;
import com.csgo.util.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Api(tags = "游客模块")
@RestController
@RequestMapping("/visitor")
@Slf4j
public class VisitorController {

    /**
     * 判断是否首次访问网站
     *
     * @return
     */
    @ApiOperation("判断是否首次访问网站")
    @RequestMapping(value = "access", method = RequestMethod.GET)
    public Result access(HttpServletRequest request, HttpServletResponse response) {
        boolean isFirst = true;
        String cookieName = "firstAccess";
        String cookie = CookieUtils.getCookieValue(request, cookieName);
        if (!StringUtil.isEmpty(cookie)) {
            isFirst = false;
        } else {
            try {
                Date currentDate = new Date();
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat nextDf = new SimpleDateFormat("yyyy-MM-dd 23:59:58");
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date nextDate = df.parse(nextDf.format(calendar.getTime()));
                Long cookieMaxAge = (nextDate.getTime() - currentDate.getTime()) / 1000;
                CookieUtils.setCookie(null, response, cookieName, "1", cookieMaxAge.intValue());
            } catch (Exception ex) {
                CookieUtils.setCookie(null, response, cookieName, "1", 2 * 60 * 60);
                log.error("首次访问网站转换错误");
            }
        }
        return new Result().result(isFirst);
    }

}
