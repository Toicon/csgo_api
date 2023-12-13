package com.csgo.web.controller.customer;

import com.csgo.domain.CustomerService;
import com.csgo.mapper.CustomerServiceMapper;
import com.csgo.service.CustomerServiceService;
import com.csgo.support.Result;
import com.csgo.web.support.Log;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@Api(tags = "客服信息")
@RestController
@RequestMapping("/customer")
@Slf4j
public class AdminCustomerServiceController {

    @Autowired
    private CustomerServiceService serviceService;
    @Autowired
    private CustomerServiceMapper mapper;


    /**
     * 新增客服信息
     *
     * @param
     * @return
     */
    @ApiOperation("新增客服信息（后台管理）")
    @RequestMapping(value = "add", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "客服名称"),
            @ApiImplicitParam(name = "qq", value = "QQ号"),
            @ApiImplicitParam(name = "phone", value = "手机号"),
            @ApiImplicitParam(name = "img", value = "图片地址信息"),
    })
    @Log(desc = "新增客服")
    public Result add(String name, String qq, String phone, String img) {
        CustomerService service = new CustomerService();
        service.setQq(qq);
        if (mapper.selectOne(service) != null) {
            return new Result().fairResult("qq已经存在");
        }
        CustomerService s = new CustomerService();
        s.setName(name);
        s.setPhone(phone);
        s.setQq(qq);
        s.setImg(img);
        s.setStatus("1");
        s.setCt(new Date());
        int num = serviceService.add(s);
        return new Result().result(num);
    }

    /**
     * 编辑客服信息
     *
     * @return
     */
    @ApiOperation("编辑客服信息（后台管理）")
    @RequestMapping(value = "update", method = RequestMethod.PUT)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id"),
            @ApiImplicitParam(name = "name", value = "客服名称"),
            @ApiImplicitParam(name = "qq", value = "QQ号"),
            @ApiImplicitParam(name = "phone", value = "手机号"),
            @ApiImplicitParam(name = "status", value = "是否启用 0-启用 1-禁用")
    })
    @Log(desc = "修改客服")
    public Result update(String name, String qq, String phone, int id, String status) {
        CustomerService s = serviceService.queryById(id);
        if (!StringUtils.isEmpty(name)) {
            s.setName(name);
        }
        if (!StringUtils.isEmpty(phone)) {
            s.setPhone(phone);
        }
        if (!StringUtils.isEmpty(qq)) {
            s.setQq(qq);
        }
        if (!StringUtils.isEmpty(status)) {
            s.setStatus(status);
        }
        s.setUp(new Date());
        int num = serviceService.update(s, id);
        return new Result().result(num);
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
    @Log(desc = "查询客服列表")
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

    /**
     * 根据ID删除对应的客服信息
     *
     * @return
     */
    @ApiOperation("根据ID删除对应的客服信息（后台管理）")
    @RequestMapping(value = "delete", method = RequestMethod.DELETE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "客服id")
    })
    @Log(desc = "删除客服")
    public Result delete(int id) {
        CustomerService customerService = mapper.selectByPrimaryKey(id);
        if (customerService.getStatus().equals(0)) {
            return new Result().fairResult("该用户在启用中不可删除");
        }
        int num = serviceService.delete(id);
        return new Result().result(num);
    }


}
