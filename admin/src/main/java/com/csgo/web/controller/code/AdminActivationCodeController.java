package com.csgo.web.controller.code;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.code.SearchActivationCodeCondition;
import com.csgo.domain.plus.code.ActivationCode;
import com.csgo.service.code.ActivationCodeService;
import com.csgo.support.DataConverter;
import com.csgo.util.ExcelUtils;
import com.csgo.web.controller.BackOfficeController;
import com.csgo.web.request.code.AddActivationCodeRequest;
import com.csgo.web.request.code.SearchActivationCodeRequest;
import com.csgo.web.response.code.ActivationCodeResponse;
import com.csgo.web.response.code.ActivationDownloadView;
import com.echo.framework.platform.web.Api;
import com.echo.framework.platform.web.response.BaseResponse;
import com.echo.framework.platform.web.response.PageResponse;

@Api
public class AdminActivationCodeController extends BackOfficeController {

    private static final String CDK = "CDK.xls";

    @Autowired
    private ActivationCodeService activationCodeService;


    @PostMapping("/activation/code/pagination")
    public PageResponse<ActivationCodeResponse> pagination(@Valid @RequestBody SearchActivationCodeRequest request) {
        SearchActivationCodeCondition condition = DataConverter.to(SearchActivationCodeCondition.class, request);
        Page<ActivationCode> page = activationCodeService.find(condition);
        return DataConverter.to(code -> {
            ActivationCodeResponse response = new ActivationCodeResponse();
            BeanUtils.copyProperties(code, response);
            return response;
        }, page);
    }

    @PostMapping("/activation/code/add")
    public BaseResponse<List<Integer>> add(@Valid @RequestBody AddActivationCodeRequest request) throws Exception {
        List<Integer> views = activationCodeService.add(request.getViews(), siteContext.getCurrentUser().getName());
        return BaseResponse.<List<Integer>>builder().data(views).get();
    }

    @GetMapping(value = "/activation/code/export")
    public BaseResponse<Void> export(@RequestParam("ids") List<Integer> ids, HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<ActivationDownloadView> views = activationCodeService.downloadViews(ids);
        HSSFWorkbook workbook = ExcelUtils.getHSSFWorkWithHeaders(views, ActivationDownloadView.class, false);
        ExcelUtils.downFileWithFileName(response, workbook, request, CDK);

        return BaseResponse.<Void>builder().get();
    }


    @DeleteMapping("/activation/code/{id}")
    public BaseResponse<Void> delete(@PathVariable("id") int id) {
        activationCodeService.delete(id);
        return BaseResponse.<Void>builder().get();
    }

}
