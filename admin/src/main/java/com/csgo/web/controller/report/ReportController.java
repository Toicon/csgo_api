package com.csgo.web.controller.report;

import com.csgo.domain.report.GoodDeliverySummaryDto;
import com.csgo.domain.report.GoodShipmentSummaryDto;
import com.csgo.exception.ServiceErrorException;
import com.csgo.service.report.ReportService;
import com.csgo.support.PageInfo;
import com.csgo.util.ExcelUtils;
import com.csgo.web.controller.BackOfficeController;
import com.csgo.web.request.report.UserStoredSummaryRequest;
import com.csgo.web.response.report.UserStoredSummaryResponse;
import com.csgo.web.support.Log;
import com.echo.framework.platform.web.Api;
import com.echo.framework.platform.web.response.BaseResponse;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 报表统计
 *
 * @author admin
 */
@Api
@RequestMapping("/report")
public class ReportController extends BackOfficeController {

    @Autowired
    private ReportService reportService;

    private static final String USER_STORED_SUMMARY = "用户储值收支汇总.xls";

    private static final String SUM_GOOD_SHIPMENT = "商品道具出货统计表.xls";

    private static final String SUM_ZBT_GOOD_DELIVERY = "ZBT平台道具发货统计表.xls";

    private static final String SUM_IG_GOOD_DELIVERY = "IG平台道具发货统计表.xls";

    private static final Integer EXPORT_MAX_SITE = 10000;

    @PostMapping("/userStoredSummary")
    @Log(desc = "用户储值收支汇总")
    public BaseResponse<List<UserStoredSummaryResponse>> userStoredSummary(@Valid @RequestBody UserStoredSummaryRequest request) {
        return BaseResponse.<List<UserStoredSummaryResponse>>builder().data(reportService.userStoredSummary(request)).get();
    }

    @GetMapping(value = "/userStoredSummaryExport")
    @Log(desc = "导出用户储值收支汇总")
    public BaseResponse<Void> userStoredSummaryExport(@RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate, HttpServletRequest request, HttpServletResponse response) throws Exception {
        UserStoredSummaryRequest userStoredSummaryRequest = new UserStoredSummaryRequest();
        userStoredSummaryRequest.setStartDate(startDate);
        userStoredSummaryRequest.setEndDate(endDate);
        List<UserStoredSummaryResponse> responses = reportService.userStoredSummary(userStoredSummaryRequest);
        HSSFWorkbook workbook = ExcelUtils.getHSSFWorkWithHeaders(responses, UserStoredSummaryResponse.class, false);
        ExcelUtils.downFileWithFileName(response, workbook, request, USER_STORED_SUMMARY);
        return BaseResponse.<Void>builder().get();
    }


    @PostMapping("/sumGoodShipment")
    @Log(desc = "商品道具出货统计表")
    public BaseResponse<PageInfo<GoodShipmentSummaryDto>> pageGoodShipmentSummary(@Valid @RequestBody UserStoredSummaryRequest request) {
        return BaseResponse.<PageInfo<GoodShipmentSummaryDto>>builder().data(reportService.pageGoodShipmentSummary(request)).get();
    }

    @GetMapping(value = "/sumGoodShipmentExport")
    @Log(desc = "导出商品道具出货统计表")
    public BaseResponse<Void> sumGoodShipmentExport(@RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate, HttpServletRequest request, HttpServletResponse response) throws Exception {
        UserStoredSummaryRequest totalRequest = new UserStoredSummaryRequest();
        totalRequest.setStartDate(startDate);
        totalRequest.setEndDate(endDate);
        totalRequest.setPageIndex(1);
        totalRequest.setPageSize(this.EXPORT_MAX_SITE);
        PageInfo<GoodShipmentSummaryDto> page = reportService.pageGoodShipmentSummary(totalRequest);
        int total = page.getTotal();
        if (total == 0) {
            throw new ServiceErrorException("导出商品道具出货统计数量0条");
        }
        if (total > this.EXPORT_MAX_SITE) {
            throw new ServiceErrorException("导出商品道具出货统计数量不能超过" + this.EXPORT_MAX_SITE + "条");
        }
        List<GoodShipmentSummaryDto> responses = new ArrayList<>();
        responses.addAll(page.getList());
        BigDecimal sumSaleMoney = responses.stream().map(GoodShipmentSummaryDto::getSaleMoney).reduce(BigDecimal.ZERO, BigDecimal::add);
        Long sumSaleCount = responses.stream().mapToLong(GoodShipmentSummaryDto::getSaleCount).sum();
        //合计
        GoodShipmentSummaryDto summaryDto = new GoodShipmentSummaryDto();
        summaryDto.setProductName("合计");
        summaryDto.setSaleMoney(sumSaleMoney == null ? BigDecimal.ZERO : sumSaleMoney);
        summaryDto.setSaleCount(sumSaleCount == null ? 0L : sumSaleCount);
        responses.add(summaryDto);
        HSSFWorkbook workbook = ExcelUtils.getHSSFWorkWithHeaders(responses, GoodShipmentSummaryDto.class, false);
        ExcelUtils.downFileWithFileName(response, workbook, request, SUM_GOOD_SHIPMENT);
        return BaseResponse.<Void>builder().get();
    }


    @PostMapping("/sumZbtGoodDelivery")
    @Log(desc = "ZBT平台道具发货统计表")
    public BaseResponse<PageInfo<GoodDeliverySummaryDto>> sumZbtGoodDelivery(@Valid @RequestBody UserStoredSummaryRequest request) {
        return BaseResponse.<PageInfo<GoodDeliverySummaryDto>>builder().data(reportService.sumZbtGoodDelivery(request)).get();
    }

    @GetMapping(value = "/sumZbtGoodDeliveryExport")
    @Log(desc = "导出ZBT平台道具发货统计表")
    public BaseResponse<Void> sumZbtGoodDeliveryExport(@RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate,
                                                       HttpServletRequest request, HttpServletResponse response) throws Exception {
        UserStoredSummaryRequest totalRequest = new UserStoredSummaryRequest();
        totalRequest.setStartDate(startDate);
        totalRequest.setEndDate(endDate);
        totalRequest.setPageIndex(1);
        totalRequest.setPageSize(this.EXPORT_MAX_SITE);
        PageInfo<GoodDeliverySummaryDto> page = reportService.sumZbtGoodDelivery(totalRequest);
        int total = page.getTotal();
        if (total == 0) {
            throw new ServiceErrorException("导出ZBT平台道具发货数量0条");
        }
        if (total > this.EXPORT_MAX_SITE) {
            throw new ServiceErrorException("导出ZBT平台道具发货数量不能超过" + this.EXPORT_MAX_SITE + "条");
        }
        List<GoodDeliverySummaryDto> responses = new ArrayList<>();
        responses.addAll(page.getList());
        Long sumSaleCount = responses.stream().mapToLong(GoodDeliverySummaryDto::getSaleCount).sum();
        //合计
        GoodDeliverySummaryDto summaryDto = new GoodDeliverySummaryDto();
        summaryDto.setProductName("合计");
        summaryDto.setSaleCount(sumSaleCount == null ? 0L : sumSaleCount);
        responses.add(summaryDto);
        HSSFWorkbook workbook = ExcelUtils.getHSSFWorkWithHeaders(responses, GoodDeliverySummaryDto.class, false);
        ExcelUtils.downFileWithFileName(response, workbook, request, SUM_ZBT_GOOD_DELIVERY);
        return BaseResponse.<Void>builder().get();
    }

    @PostMapping("/sumIgGoodDelivery")
    @Log(desc = "IG平台道具发货统计表")
    public BaseResponse<PageInfo<GoodDeliverySummaryDto>> sumIgGoodDelivery(@Valid @RequestBody UserStoredSummaryRequest request) {
        return BaseResponse.<PageInfo<GoodDeliverySummaryDto>>builder().data(reportService.sumIgGoodDelivery(request)).get();
    }

    @GetMapping(value = "/sumIgGoodDeliveryExport")
    @Log(desc = "导出IG平台道具发货统计表")
    public BaseResponse<Void> sumIgGoodDeliveryExport(@RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate,
                                                      HttpServletRequest request, HttpServletResponse response) throws Exception {
        UserStoredSummaryRequest totalRequest = new UserStoredSummaryRequest();
        totalRequest.setStartDate(startDate);
        totalRequest.setEndDate(endDate);
        totalRequest.setPageIndex(1);
        totalRequest.setPageSize(this.EXPORT_MAX_SITE);
        PageInfo<GoodDeliverySummaryDto> page = reportService.sumIgGoodDelivery(totalRequest);
        int total = page.getTotal();
        if (total == 0) {
            throw new ServiceErrorException("导出IG平台道具发货数量0条");
        }
        if (total > this.EXPORT_MAX_SITE) {
            throw new ServiceErrorException("导出IG平台道具发货数量不能超过" + this.EXPORT_MAX_SITE + "条");
        }
        List<GoodDeliverySummaryDto> responses = new ArrayList<>();
        responses.addAll(page.getList());
        Long sumSaleCount = responses.stream().mapToLong(GoodDeliverySummaryDto::getSaleCount).sum();
        //合计
        GoodDeliverySummaryDto summaryDto = new GoodDeliverySummaryDto();
        summaryDto.setProductName("合计");
        summaryDto.setSaleCount(sumSaleCount == null ? 0L : sumSaleCount);
        responses.add(summaryDto);
        HSSFWorkbook workbook = ExcelUtils.getHSSFWorkWithHeaders(responses, GoodDeliverySummaryDto.class, false);
        ExcelUtils.downFileWithFileName(response, workbook, request, SUM_IG_GOOD_DELIVERY);
        return BaseResponse.<Void>builder().get();
    }
}
