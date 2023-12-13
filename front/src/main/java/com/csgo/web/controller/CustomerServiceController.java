package com.csgo.web.controller;

import com.csgo.mapper.CustomerServiceMapper;
import com.csgo.domain.CustomerService;
import com.csgo.service.CustomerServiceService;
import com.csgo.util.QRCodeUtil;
import com.csgo.support.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

@Api(tags = "客服信息")
@RestController
@RequestMapping("/customer")
@Slf4j
public class CustomerServiceController {

    @Autowired
    private CustomerServiceService serviceService;
    @Autowired
    private CustomerServiceMapper mapper;

    /**
     * 生成二维码
     *
     * @param
     * @return
     */
    @ApiOperation("生成二维码（后台管理）")
    @RequestMapping(value = "code", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "qq", value = "QQ号")
    })
    public ResponseEntity<byte[]> QRCodeUtil(String qq) {
        byte[] qrcode = null;
        try {
            qq = new String(qq.getBytes("UTF-8"), "ISO-8859-1");
            try {
                qrcode = QRCodeUtil.getQRCodeImage(qq, 360, 360);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.IMAGE_PNG);
        return new ResponseEntity<>(qrcode, httpHeaders, HttpStatus.CREATED);
    }

    /**
     * 根据ID查询到对应的客服信息
     *
     * @return
     */
    @ApiOperation("根据ID查询到对应的客服信息（后台管理）")
    @RequestMapping(value = "queryById", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "客服id")
    })
    public Result queryById(int id) {
        CustomerService service = serviceService.queryById(id);
        return new Result().result(service);
    }

    /**
     * 查询今日客服
     *
     * @return
     */
    @ApiOperation("查询今日客服")
    @RequestMapping(value = "now", method = RequestMethod.GET)
    public Result queryByIdNow() {
        CustomerService service = serviceService.queryByIdNow();
        if (service == null) {
            service = new CustomerService();
        }
        return new Result().result(service);
    }

    /**
     * 查询所有对应的客服信息
     *
     * @return
     */
    @ApiOperation("查询所有对应的客服信息（后台管理）")
    @RequestMapping(value = "queryAll", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "第几页"),
            @ApiImplicitParam(name = "pageSize", value = "一页多少条数据")
    })
    public Result queryAll(Integer pageNum, Integer pageSize) {
        if (pageSize == null || pageSize == 0) {
            pageSize = 10;
        }
        if (pageNum == null || pageNum == 0) {
            pageNum = 1;
        }
        int p = pageNum;
        pageNum = (pageNum - 1) * pageSize;

        CustomerService s = new CustomerService();
        s.setPageNum(pageNum);
        s.setPageSize(pageSize);
        List<CustomerService> service = serviceService.queryAllLimit(s);
        if (service != null && service.size() > 0) {
            service.get(0).setTotal(serviceService.queryAll().size());
        }
        return new Result().result(service);
    }

}
