package com.csgo.web.response.statistical;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author admin
 */
@Getter
@Setter
public class DailyStatisticalResponse {

    private List<String> xAxis;
    private List<String> series;
}
