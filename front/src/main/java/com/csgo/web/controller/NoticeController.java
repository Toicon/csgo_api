package com.csgo.web.controller;

import com.csgo.mapper.NoticeMapper;
import com.csgo.domain.Notice;
import com.csgo.support.Result;
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

/**
 * @ClassName: NoticeController
 * @description: 公告
 * @author: Andy
 * @time: 2020/10/27 18:33
 */
@Api(tags = "公告")
@RestController
@RequestMapping("/notice")
@Slf4j
public class NoticeController {

    @Autowired
    private NoticeMapper mapper;

    /**
     * 根据ID查询到公告信息
     *
     * @return
     */
    @ApiOperation("根据ID查询到公告信息（后台管理）")
    @RequestMapping(value = "queryById", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "ID")
    })
    public Result queryById(int id) {
        Notice co = mapper.selectByPrimaryKey(id);
        return new Result().result(co);
    }

    /**
     * 查询到所有的公告信息
     *
     * @return
     */
    @ApiOperation("查询到所有的公告信息（后台管理）")
    @RequestMapping(value = "queryByAll", method = RequestMethod.GET)
    public Result queryByAll() {
        List<Notice> co = mapper.selectListLt(new Notice());
        return new Result().result(co);
    }

    /**
     * 查询到启用的公告信息
     *
     * @return
     */
    @ApiOperation("查询到启用的公告信息（后台管理）")
    @RequestMapping(value = "use", method = RequestMethod.GET)
    public Result queryByUse(int flag) {
        Notice notice = new Notice();
        notice.setFlag(flag);
        List<Notice> co = mapper.selectListLt(notice);
        return new Result().result(co);
    }

}
