package com.csgo.web.controller.notice;

import com.csgo.domain.Notice;
import com.csgo.mapper.NoticeMapper;
import com.csgo.support.Result;
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
public class AdminNoticeController {

    @Autowired
    private NoticeMapper mapper;


    /**
     * 新增公告
     *
     * @param
     * @return
     */
    @ApiOperation("新增公告（后台管理）")
    @RequestMapping(value = "add", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "title", value = "标题"),
            @ApiImplicitParam(name = "flag", value = "是否展示,0-不展示,1-展示"),
            @ApiImplicitParam(name = "content", value = "内容")
    })
    @Log(desc = "新增公告")
    public Result add(String title, String flag, String content) {
        Notice co = new Notice();
        if (!StringUtils.isEmpty(title)) {
            co.setTitle(title);
        }
        if (!StringUtils.isEmpty(content)) {
            co.setContent(content);
        }
        if (StringUtils.isEmpty(flag)) {
            co.setFlag(1);
        }

        co.setCt(new Date());
        int num = mapper.insert(co);
        if (num == 0) {
            return new Result().result("新增公告失败");
        }
        return new Result().result(num);
    }

    /**
     * 编辑公告
     *
     * @return
     */
    @ApiOperation("编辑公告（后台管理）")
    @RequestMapping(value = "update", method = RequestMethod.PUT)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id"),
            @ApiImplicitParam(name = "title", value = "标题"),
            @ApiImplicitParam(name = "flag", value = "是否展示,0-不展示,1-展示"),
            @ApiImplicitParam(name = "content", value = "内容")
    })
    @Log(desc = "修改公告")
    public Result update(int id, String title, int flag, String content) {
        Notice co = mapper.selectByPrimaryKey(id);
        if (co == null) {
            return new Result().result("请填写正确的id");
        }
        if (!StringUtils.isEmpty(title)) {
            co.setTitle(title);
        }
        if (!StringUtils.isEmpty(content)) {
            co.setContent(content);
        }
        if (!StringUtils.isEmpty(flag)) {
            co.setFlag(flag);
        }
        co.setUt(new Date());
        int num = mapper.updateByPrimaryKeySelective(co);
        if (num == 0) {
            return new Result().result("失败");
        }
        return new Result().result(num);
    }

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
    @Log(desc = "根据ID查询公告")
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
    @Log(desc = "查询所有公告")
    public Result queryByAll() {
        List<Notice> co = mapper.selectListLt(new Notice());
        return new Result().result(co);
    }


    /**
     * 根据ID删除对应的公告信息
     *
     * @return
     */
    @ApiOperation("根据ID删除对应的公告信息（后台管理）")
    @RequestMapping(value = "delete", method = RequestMethod.DELETE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "礼包等级id")
    })
    @Log(desc = "删除公告")
    public Result delete(int id) {
        int num = mapper.deleteByPrimaryKey(id);
        return new Result().result(num);
    }
}
