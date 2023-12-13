package com.csgo.web.controller.face;


import com.csgo.service.face.FaceManageService;
import com.csgo.web.intecepter.LoginRequired;
import com.csgo.web.request.face.AliFaceCheckResult;
import com.csgo.web.request.face.AliFaceRequest;
import com.csgo.web.response.pay.OrderRecordResponse;
import com.echo.framework.platform.interceptor.session.RequireSession;
import com.echo.framework.platform.web.Api;
import com.echo.framework.platform.web.response.BaseResponse;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 支付宝人脸核身
 */
@Api
@io.swagger.annotations.Api(tags = "支付宝人脸核身")
@RequestMapping("/ali/face")
public class AliFaceController {
    @Autowired
    private FaceManageService faceManageService;

    /**
     * 保存身份证信息
     *
     * @param aliFaceRequest
     * @return
     */
    @ApiOperation("保存身份证信息")
    @LoginRequired
    @RequireSession
    @PostMapping("/saveUserIdentityInfo")
    public BaseResponse<String> saveUserIdentityInfo(@RequestBody AliFaceRequest aliFaceRequest) {
        faceManageService.saveUserIdentityInfo(aliFaceRequest);
        return BaseResponse.<String>builder().data(null).get();
    }

    /**
     * 检查人脸是否通过
     *
     * @param orderNo
     * @return
     */
    @ApiOperation("检查人脸是否通过")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderNo", value = "订单编号")
    })
    @GetMapping("/checkFace")
    public BaseResponse<AliFaceCheckResult> checkFace(@RequestParam("orderNo") String orderNo) {
        return BaseResponse.<AliFaceCheckResult>builder().data(faceManageService.checkFace(orderNo)).get();
    }

    /**
     * 重新人脸识别
     *
     * @param orderNo
     * @return
     */
    @ApiOperation("重新人脸识别")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderNo", value = "订单编号")
    })
    @GetMapping("/againFace")
    public BaseResponse<Map<String, String>> againFace(@RequestParam("orderNo") String orderNo) {
        return BaseResponse.<Map<String, String>>builder().data(faceManageService.againFace(orderNo)).get();
    }


    /**
     * 重新保存身份证信息（非登录）
     *
     * @param aliFaceRequest
     * @return
     */
    @ApiOperation("保存身份证信息")
    @PostMapping("/againSaveUserIdentityInfo")
    public BaseResponse<String> againSaveUserIdentityInfo(@RequestBody AliFaceRequest aliFaceRequest) {
        faceManageService.againSaveUserIdentityInfo(aliFaceRequest);
        return BaseResponse.<String>builder().data(null).get();
    }

    /**
     * 确认支付
     *
     * @param orderNo
     * @param servletRequest
     * @return
     */
    @ApiOperation("确认支付")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderNo", value = "订单编号")
    })
    @GetMapping("/recharge/qrcode")
    public BaseResponse<Map<String, String>> pay(@RequestParam("orderNo") String orderNo, HttpServletRequest servletRequest) {
        return BaseResponse.<Map<String, String>>builder().data(faceManageService.pay(orderNo, servletRequest)).get();
    }

    /**
     * 根据订单编号查询到对应的订单信息
     *
     * @return
     */
    @ApiOperation("根据订单编号查询到对应的订单信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderNum", value = "订单编号")
    })
    @GetMapping("/order/{orderNum}")
    public BaseResponse<OrderRecordResponse> queryOrderNum(@PathVariable("orderNum") String orderNum) {
        return BaseResponse.<OrderRecordResponse>builder().data(faceManageService.queryOrderNum(orderNum)).get();
    }
}
