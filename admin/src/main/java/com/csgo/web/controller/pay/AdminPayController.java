package com.csgo.web.controller.pay;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.support.EasyExcelUtils;
import com.csgo.condition.order.SearchOrderRecordCondition;
import com.csgo.domain.plus.order.OrderRecord;
import com.csgo.domain.plus.order.OrderRecordDTO;
import com.csgo.service.order.OrderRecordService;
import com.csgo.support.DataConverter;
import com.csgo.support.StandardExceptionCode;
import com.csgo.util.BeanUtilsEx;
import com.csgo.util.DateUtils;
import com.csgo.web.controller.BackOfficeController;
import com.csgo.web.request.pay.SearchOrderRecordRequest;
import com.csgo.web.response.order.OrderRecordExcel;
import com.csgo.web.response.order.OrderRecordResponse;
import com.csgo.web.support.Log;
import com.echo.framework.platform.exception.ApiException;
import com.echo.framework.platform.web.Api;
import com.echo.framework.platform.web.response.PageResponse;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Api
@RequestMapping("/pay")
@Slf4j
public class AdminPayController extends BackOfficeController {

    @Autowired
    private OrderRecordService orderRecordService;


    /**
     * 查询出所有的充值记录
     *
     * @return
     */
    @PostMapping(value = "/pagination")
    @Log(desc = "查询充值记录列表")
    public PageResponse<OrderRecordResponse> queryAll(@Validated @RequestBody SearchOrderRecordRequest request) {
        SearchOrderRecordCondition condition = DataConverter.to(SearchOrderRecordCondition.class, request);
        Page<OrderRecord> page = orderRecordService.pagination(condition);
        AtomicReference<BigDecimal> totalAmount = new AtomicReference<>(BigDecimal.ZERO);
        AtomicReference<BigDecimal> normalAmount = new AtomicReference<>(BigDecimal.ZERO);
        AtomicReference<BigDecimal> memberAmount = new AtomicReference<>(BigDecimal.ZERO);
        if (!CollectionUtils.isEmpty(page.getRecords())) {
            List<OrderRecordDTO> orderRecords = orderRecordService.find(condition.getName(), condition.getFlag(), condition.getStartDate(), condition.getEndDate());
            orderRecords.forEach(record -> {
                totalAmount.getAndSet(totalAmount.get().add(record.getOrderAmount()));
                if (record.getUpdateTime() != null) {
                    normalAmount.getAndSet(normalAmount.get().add(record.getOrderAmount()));
                } else {
                    memberAmount.getAndSet(memberAmount.get().add(record.getOrderAmount()));
                }
            });
        }
        PageResponse<OrderRecordResponse> pageResponse = DataConverter.to(record -> {
            OrderRecordResponse response = new OrderRecordResponse();
            BeanUtilsEx.copyProperties(record, response);
            return response;
        }, page);
        if (null != pageResponse.getData() && !CollectionUtils.isEmpty(pageResponse.getData().getRows())) {
            pageResponse.getData().getRows().get(0).setTotalAmount(totalAmount.get());
            pageResponse.getData().getRows().get(0).setNormalAmount(normalAmount.get());
            pageResponse.getData().getRows().get(0).setMemberAmount(memberAmount.get());
        }
        return pageResponse;
    }

    @GetMapping(value = "/export-excel")
    @Log(desc = "导出充值记录列表")
    public void export(SearchOrderRecordRequest vm, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (vm.getStartDate() == null) {
            throw new ApiException(StandardExceptionCode.CLIENT_ERROR, "开始日期不能为空");
        }
        DateTime startDate = new DateTime(vm.getStartDate());
        DateTime endDate;
        if (vm.getEndDate() != null) {
            endDate = new DateTime(vm.getEndDate());
        } else {
            endDate = DateTime.now();
        }
        int day = Math.abs(Days.daysBetween(startDate, endDate).getDays());
        if (day > 7) {
            throw new ApiException(StandardExceptionCode.CLIENT_ERROR, "只能导出一周的数据");
        }

        SearchOrderRecordCondition condition = DataConverter.to(SearchOrderRecordCondition.class, vm);
        condition.setPage(new Page<>(1, 100000));

        Page<OrderRecord> page = orderRecordService.pagination(condition);

        BigDecimal rate = new BigDecimal("6.5");
        List<OrderRecordExcel> excelList = page.getRecords().stream().map(item -> {
            OrderRecordExcel target = new OrderRecordExcel();
            BeanUtilsEx.copyProperties(item, target);
            target.setPaidTime(DateUtils.dateToString(item.getPaidTime()));
            target.setCreateTime(DateUtils.dateToString(item.getCreateTime()));
            if (item.getPaidAmount() == null) {
                target.setRealPaidAmount(BigDecimal.ZERO);
            } else {
                target.setRealPaidAmount(item.getPaidAmount().divide(rate, 2, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP));
            }
            return target;
        }).collect(Collectors.toList());

        EasyExcelUtils.write(response, "充值记录.xlsx", "充值记录", OrderRecordExcel.class, excelList);
    }

}
