package com.csgo.web.controller.gift;

import com.csgo.domain.GiftGrade;
import com.csgo.service.gift.GiftGradeService;
import com.csgo.support.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Api(tags = "礼包等级")
@RestController
@RequestMapping("/gift/grade")
@Slf4j
public class GiftGradeController {

    @Autowired
    private GiftGradeService service;

    /**
     * 根据ID查询到对应的礼包等级信息
     *
     * @return
     */
    @ApiOperation("根据ID查询到对应的礼包等级信息（后台管理）")
    @RequestMapping(value = "queryById", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "礼包等级ID")
    })
    public Result queryById(int id) {
        GiftGrade giftGrade = service.queryById(id);
        return new Result().result(giftGrade);
    }

    /**
     * 查询所有对应的礼包等级信息
     *
     * @return
     */
    @ApiOperation("查询所有对应的礼包等级信息（后台管理）")
    @RequestMapping(value = "queryAll", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "第几页"),
            @ApiImplicitParam(name = "pageSize", value = "一页多少条数据")
    })
    public Result queryAll(Integer pageNum, Integer pageSize) {
        if (pageSize == null || pageSize == 0) {
            pageSize = 10;
        }
        if (pageNum == null || pageNum == 0) {
            pageNum = 1;
        }
        int p = pageNum;
        pageNum = (pageNum - 1) * pageSize;

        GiftGrade grade = new GiftGrade();
        grade.setPageNum(pageNum);
        grade.setPageSize(pageSize);
        List<GiftGrade> list = service.queryAll(grade);
        if (list != null && list.size() > 0) {
            list.get(0).setTotal(service.queryAll(new GiftGrade()).size());
        }
        return new Result().result(list);
    }

}
