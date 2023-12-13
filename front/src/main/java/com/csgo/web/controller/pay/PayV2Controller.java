package com.csgo.web.controller.pay;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import com.csgo.domain.plus.order.OrderRecord;
import com.csgo.service.OrderRecordService;
import com.csgo.support.StandardExceptionCode;
import com.csgo.util.SignUtil;
import com.csgo.web.intecepter.LoginRequired;
import com.csgo.web.response.pay.OrderRecordResponse;
import com.csgo.web.support.SiteContext;
import com.echo.framework.platform.exception.ApiException;
import com.echo.framework.platform.interceptor.session.RequireSession;
import com.echo.framework.platform.web.Api;
import com.echo.framework.platform.web.response.BaseResponse;

/**
 * @author admin
 */
@Api
@LoginRequired
@RequireSession
@RequestMapping("/pay")
public class PayV2Controller {

    @Autowired
    private OrderRecordService orderRecordService;
    @Autowired
    private SiteContext siteContext;

    @GetMapping("/v2/sign")
    public BaseResponse<String> sign(HttpServletRequest request) {
        Map<String, Object> map = SignUtil.getDigest(request);
        map.put("userId", siteContext.getCurrentUser().getId());
        return BaseResponse.<String>builder().data(SignUtil.sign(map)).get();
    }

    /**
     * 根据订单编号查询到对应的订单信息
     *
     * @return
     */
    @GetMapping("/order/{orderNum}")
    public BaseResponse<OrderRecordResponse> queryOrderNum(@PathVariable("orderNum") String orderNum) {
        if (StringUtils.isEmpty(orderNum)) {
            throw new ApiException(StandardExceptionCode.RECHARGE_FAILURE, "订单有误");
        }
        OrderRecord orderRecord = orderRecordService.queryOrderNum(orderNum);
        if (!orderRecord.getUserId().equals(siteContext.getCurrentUser().getId())) {
            throw new ApiException(StandardExceptionCode.RECHARGE_FAILURE, "订单有误");
        }
        OrderRecordResponse response = new OrderRecordResponse();
        BeanUtils.copyProperties(orderRecord, response);
        response.setStyle(orderRecord.getStyle().getDescription());
        return BaseResponse.<OrderRecordResponse>builder().data(response).get();
    }
}
