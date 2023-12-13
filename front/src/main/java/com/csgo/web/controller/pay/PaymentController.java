package com.csgo.web.controller.pay;

import com.csgo.domain.plus.recharge.RechargeChannelType;
import com.csgo.service.recharge.RechargeService;
import com.echo.framework.platform.interceptor.session.RequireSession;
import com.echo.framework.platform.web.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author admin
 */
@Api
@RequireSession
@Slf4j
public class PaymentController {

    @Autowired
    private RechargeService rechargeService;


    /**
     * 支付宝支付回调
     *
     * @return
     */
    @PostMapping("/pay/ali/notify")
    public void aliCallback(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        rechargeService.getChannelByType(RechargeChannelType.ALI_PAY).callback(req, resp, RechargeChannelType.ALI_PAY);
        printResponse(resp, "success");
    }

    protected void printResponse(HttpServletResponse response, String content) throws IOException {
        PrintWriter writer = null;
        try {
            writer = response.getWriter();
            writer.write(content);
            writer.flush();
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }
    

}
