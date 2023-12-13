package com.csgo.web.controller.accessory;

import com.csgo.domain.plus.accessory.LuckyProductType;
import com.csgo.service.accessory.LuckyProductTypeService;
import com.csgo.util.BeanUtilsEx;
import com.csgo.web.controller.BackOfficeController;
import com.csgo.web.request.accessory.LuckyProductTypeRequest;
import com.csgo.web.response.accessory.LuckyProductTypeResponse;
import com.csgo.web.support.Log;
import com.echo.framework.platform.exception.ApiException;
import com.echo.framework.platform.web.Api;
import com.echo.framework.platform.web.response.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/lucky/product/type")
@Api
public class AdminLuckyProductTypeController extends BackOfficeController {

    @Autowired
    private LuckyProductTypeService luckyProductTypeService;

    @GetMapping("/pageList")
    @Log(desc = "查看饰品类型列表")
    public BaseResponse<List<LuckyProductTypeResponse>> pageList() {
        return BaseResponse.<List<LuckyProductTypeResponse>>builder().data(luckyProductTypeService.findList().stream().map(type -> {
            LuckyProductTypeResponse response = new LuckyProductTypeResponse();
            BeanUtilsEx.copyProperties(type, response);
            return response;
        }).collect(Collectors.toList())).get();
    }

    @PostMapping("/insert")
    @Log(desc = "新增饰品类型")
    public BaseResponse<Void> add(@RequestBody LuckyProductTypeRequest request) {
        LuckyProductType type = new LuckyProductType();
        BeanUtilsEx.copyProperties(request, type);
        luckyProductTypeService.insert(type);
        return BaseResponse.<Void>builder().get();
    }

    @PutMapping("/{id}")
    @Log(desc = "修改饰品类型")
    public BaseResponse<Void> update(@PathVariable("id") int id, @RequestBody LuckyProductTypeRequest request) {
        LuckyProductType type = luckyProductTypeService.get(id);
        if (type == null) {
            throw new ApiException(400, "找不到对应类型");
        }
        BeanUtilsEx.copyProperties(request, type);
        luckyProductTypeService.update(type);
        return BaseResponse.<Void>builder().get();
    }

    @DeleteMapping("/{id}")
    @Log(desc = "删除饰品类型")
    public BaseResponse<Void> deleteBath(@PathVariable int id) {
        luckyProductTypeService.delete(id);
        return BaseResponse.<Void>builder().get();
    }

}
