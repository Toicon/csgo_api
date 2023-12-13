package com.csgo.framework.mo;

import com.google.common.collect.Maps;
import lombok.Data;
import org.joda.time.DateTime;

import java.util.Map;

/**
 * @author admin
 */
@Data
public class RecentDateMO {

    private DateTime now;

    private Period today;

    private Period thisWeek;

    private Period thisMonth;

    private Map<Integer, Period> typeMap;

    public RecentDateMO() {
        DateTime now = DateTime.now();

        DateTime todayStart = now.withTimeAtStartOfDay();
        DateTime weekStart = todayStart.withDayOfWeek(1);
        DateTime monthStart = todayStart.withDayOfMonth(1);

        Period today = new Period(todayStart, now);
        Period week = new Period(weekStart, now);
        Period month = new Period(monthStart, now);
        Map<Integer, Period> typeMap = Maps.newHashMap();
        typeMap.put(0, today);
        typeMap.put(1, week);
        typeMap.put(2, month);

        this.now = now;
        this.today = today;
        this.thisWeek = week;
        this.thisMonth = month;

        this.typeMap = typeMap;
    }

    public Period getByType(Integer type) {
        return typeMap.get(type);
    }

    @Data
    public static class Period {

        private DateTime start;

        private DateTime end;

        public Period(DateTime start, DateTime end) {
            this.start = start;
            this.end = end;
        }
    }

    public static RecentDateMO.Period getPeriod(Integer dateRangeType) {
        RecentDateMO recentDateMO = new RecentDateMO();
        return recentDateMO.getByType(dateRangeType);
    }

}
