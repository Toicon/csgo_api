package com.csgo.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.domain.report.GoodDeliverySummaryDto;
import com.csgo.domain.report.GoodShipmentSummaryDto;
import com.csgo.domain.report.UserStoredSummaryDto;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * 统计报表
 *
 * @author admin
 */
@Repository
public interface ReportMapper {

    /**
     * 储值实付
     *
     * @param startDate
     * @param endDate
     * @return
     */
    List<UserStoredSummaryDto> sumDayPaid(@Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 赠送卡值
     *
     * @param startDate
     * @param endDate
     * @return
     */
    List<UserStoredSummaryDto> sumDayExtra(@Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 道具出售
     *
     * @param startDate
     * @param endDate
     * @return
     */
    BigDecimal sumDayPropSale(@Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 红包收入
     *
     * @param startDate
     * @param endDate
     * @return
     */
    List<UserStoredSummaryDto> sumDayRed(@Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * CDK收入
     *
     * @param startDate
     * @param endDate
     * @return
     */
    List<UserStoredSummaryDto> sumDayCdk(@Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 商城兑换
     *
     * @param startDate
     * @param endDate
     * @return
     */
    BigDecimal sumDayMallExchange(@Param("startDate") String startDate, @Param("endDate") String endDate);


    /**
     * 幸运宝箱
     *
     * @param startDate
     * @param endDate
     * @return
     */
    BigDecimal sumDayLuckyBox(@Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 商品道具出货统计表
     *
     * @param startDate
     * @param endDate
     * @return
     */
    Page<GoodShipmentSummaryDto> sumGoodShipment(Page<GoodShipmentSummaryDto> page, @Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * ZBT平台道具发货统计表
     *
     * @param startDate
     * @param endDate
     * @return
     */
    Page<GoodDeliverySummaryDto> sumZbtGoodDelivery(Page<GoodDeliverySummaryDto> page, @Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * IG平台道具发货统计表
     *
     * @param startDate
     * @param endDate
     * @return
     */
    Page<GoodDeliverySummaryDto> sumIgGoodDelivery(Page<GoodDeliverySummaryDto> page, @Param("startDate") String startDate, @Param("endDate") String endDate);
}
