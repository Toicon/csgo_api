package com.csgo.framework.model;

import com.google.common.collect.Lists;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author admin
 */
@ApiModel(description = "分页响应结果")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class PageVO<T> implements Serializable {

    @ApiModelProperty(name = "pageIndex", notes = "分页页码，从0开始", example = "0")
    @XmlElement(name = "pageIndex")
    private Long pageIndex;

    @ApiModelProperty(name = "pageSize", notes = "单页数量", example = "30")
    @XmlElement(name = "pageSize")
    private Long pageSize;

    @ApiModelProperty(name = "total", notes = "本次查询结果总数", example = "100")
    @XmlElement(name = "total")
    private Long total;

    @ApiModelProperty(name = "rows", notes = "本次分页内容")
    @XmlElementWrapper(name = "rows")
    @XmlElement(name = "row")
    private List<T> rows;

    public PageVO() {
        rows = Lists.newArrayList();
    }

    public PageVO(List<T> rows, Long total) {
        this.rows = rows;
        this.total = total;
    }

    public PageVO(Long total) {
        this.rows = new ArrayList<>();
        this.total = total;
    }

    public static <T> PageVO<T> empty() {
        return new PageVO<>(0L);
    }

    public static <T> PageVO<T> empty(Long total) {
        return new PageVO<>(total);
    }

}
