package com.csgo.web.controller.report;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.report.SearchAnchorStatisticsCondition;
import com.csgo.domain.report.AdminUserDTO;
import com.csgo.domain.report.AnchorStatisticsDTO;
import com.csgo.domain.report.AnchorUserDTO;
import com.csgo.domain.report.AnchorUserDeptDTO;
import com.csgo.service.AdminUserService;
import com.csgo.service.report.AnchorStatisticsService;
import com.csgo.support.DataConverter;
import com.csgo.support.EasyExcelUtils;
import com.csgo.web.controller.BackOfficeController;
import com.csgo.web.request.report.SearchAnchorStatisticsRequest;
import com.csgo.web.response.report.AnchorStatisticsResponse;
import com.csgo.web.support.Log;
import com.echo.framework.platform.web.response.BaseResponse;
import com.echo.framework.platform.web.response.PageResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/anchor/statistics")
@Slf4j
public class AdminAnchorStatisticsController extends BackOfficeController {

    @Autowired
    private AnchorStatisticsService anchorStatisticsService;
    @Autowired
    private AdminUserService adminUserService;

    /**
     * 查询主播统计信息
     *
     * @return
     */
    @PostMapping("/pagination")
    @Log(desc = "查询主播数据列表")
    public PageResponse<AnchorStatisticsResponse> pagination(@Valid @RequestBody SearchAnchorStatisticsRequest request) {
        return DataConverter.to(this::to, anchorStatisticsService.pagination(DataConverter.to(SearchAnchorStatisticsCondition.class, request)));
    }

    @PostMapping("/financePagination")
    @Log(desc = "查询主播数据列表-财务")
    public PageResponse<AnchorStatisticsResponse> financePagination(@Valid @RequestBody SearchAnchorStatisticsRequest request) {
        return DataConverter.to(this::to, anchorStatisticsService.financePagination(DataConverter.to(SearchAnchorStatisticsCondition.class, request)));
    }

    @GetMapping("/selectAllAnchorList")
    @Log(desc = "获取所有主播用户列表")
    public BaseResponse<List<AnchorUserDTO>> selectAllAnchorList() {
        return BaseResponse.<List<AnchorUserDTO>>builder().data(anchorStatisticsService.selectAllAnchorList()).get();
    }

    /**
     * 获取后台管理用户列表
     */
    @GetMapping("/selectAdminListByDataScope")
    public BaseResponse<List<AdminUserDTO>> selectAdminListByDataScope() {
        return BaseResponse.<List<AdminUserDTO>>builder().data(adminUserService.selectListByDataScope()).get();
    }

    /**
     * 保存主播部门关系
     *
     * @return
     */
    @PostMapping("/saveAdminUserRelationAnchor")
    @Log(desc = "保存主播用户关系")
    public BaseResponse<Void> saveAdminUserRelationAnchor(@Valid @RequestBody AnchorUserDeptDTO request) {
        anchorStatisticsService.saveAdminUserRelationAnchor(request);
        return BaseResponse.<Void>builder().get();
    }

    private AnchorStatisticsResponse to(AnchorStatisticsDTO statisticsDTO) {
        AnchorStatisticsResponse response = new AnchorStatisticsResponse();
        BeanUtils.copyProperties(statisticsDTO, response);
        if (null != statisticsDTO.getExtensionCharge() && statisticsDTO.getChargeCount() > 0) {
            //ARUP值=总充值金额/总充值人数  --（时间段内）
            BigDecimal profit = statisticsDTO.getExtensionCharge().divide(new BigDecimal(statisticsDTO.getChargeCount()), 2, RoundingMode.HALF_UP);
            response.setProfit(profit);
        } else {
            response.setProfit(BigDecimal.ZERO);
        }
        return response;
    }

    @PostMapping(value = "/export-excel")
    @Log(desc = "导出主播数据")
    public BaseResponse<Void> export(@Valid @RequestBody SearchAnchorStatisticsRequest vm,
                                     HttpServletRequest request, HttpServletResponse response) throws Exception {
        SearchAnchorStatisticsCondition condition = DataConverter.to(SearchAnchorStatisticsCondition.class, vm);
        condition.setPage(new Page<>(1, Integer.MAX_VALUE));

        Page<AnchorStatisticsDTO> page = anchorStatisticsService.financePagination(condition);

        AnchorStatisticsResponse totalResponse = new AnchorStatisticsResponse();
        totalResponse.setName("汇总：");
        totalResponse.setProfit(BigDecimal.ZERO);
        totalResponse.setExtensionCharge(BigDecimal.ZERO);
        totalResponse.setAnchorCharge(BigDecimal.ZERO);

        List<AnchorStatisticsResponse> excelList = page.getRecords().stream().map(record -> {
            AnchorStatisticsResponse statisticsResponse = to(record);
            if (null != statisticsResponse.getExtensionCharge()) {
                BigDecimal decimal = totalResponse.getExtensionCharge().add(statisticsResponse.getExtensionCharge());
                totalResponse.setExtensionCharge(decimal);
            }
            if (null != statisticsResponse.getAnchorCharge()) {
                BigDecimal decimal = totalResponse.getAnchorCharge().add(statisticsResponse.getAnchorCharge());
                totalResponse.setAnchorCharge(decimal);
            }
            return statisticsResponse;
        }).collect(Collectors.toList());

        excelList.add(totalResponse);

        EasyExcelUtils.write(response, "推广数据.xlsx", "推广数据", AnchorStatisticsResponse.class, excelList);
        return BaseResponse.<Void>builder().get();
    }

}
