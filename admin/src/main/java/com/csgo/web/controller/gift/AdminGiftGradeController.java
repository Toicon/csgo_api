package com.csgo.web.controller.gift;

import com.csgo.domain.GiftGrade;
import com.csgo.service.GiftGradeService;
import com.csgo.support.Result;
import com.csgo.web.controller.BackOfficeController;
import com.csgo.web.support.Log;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@Api(tags = "礼包等级")
@RestController
@RequestMapping("/gift/grade")
@Slf4j
public class AdminGiftGradeController extends BackOfficeController {

    @Autowired
    private GiftGradeService service;


    /**
     * 新增礼包等级
     *
     * @param grade 新增礼包等级
     * @return
     */
    @ApiOperation("新增礼包等级（后台管理）")
    @RequestMapping(value = "add", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "img", value = "礼包等级图片"),
            @ApiImplicitParam(name = "grade", value = "礼包等级")
    })
    @Log(desc = "新增礼包等级")
    public Result add(String img, String grade) {
        GiftGrade grade2 = new GiftGrade();
        grade2.setGrade(grade);
        grade2.setCt(new Date());
        grade2.setImg(img);
        int num = service.add(grade2);
        return new Result().result(num);
    }

    /**
     * 编辑礼包等级信息
     *
     * @return
     */
    @ApiOperation("编辑礼包等级信息（后台管理）")
    @RequestMapping(value = "update", method = RequestMethod.PUT)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "img", value = "礼包等级图片"),
            @ApiImplicitParam(name = "grade", value = "礼包等级"),
            @ApiImplicitParam(name = "id", value = "礼包等级id")
    })
    @Log(desc = "修改礼包等级")
    public Result update(String img, String grade, int id) {
        GiftGrade giftGrade = service.queryById(id);
        if (!StringUtils.isEmpty(grade)) {
            giftGrade.setGrade(grade);
        }
        if (!StringUtils.isEmpty(img)) {
            giftGrade.setImg(img);
        }
        giftGrade.setUt(new Date());
        int num = service.update(giftGrade, id);
        return new Result().result(num);
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
    @Log(desc = "查询礼包等级列表")
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


    /**
     * 根据ID删除对应的礼包等级信息
     *
     * @return
     */
    @ApiOperation("根据ID删除对应的礼包等级信息（后台管理）")
    @RequestMapping(value = "delete", method = RequestMethod.DELETE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "礼包等级id")
    })
    @Log(desc = "删除礼包等级")
    public Result delete(int id) {
        int num = service.delete(id);
        return new Result().result(num);
    }


}
