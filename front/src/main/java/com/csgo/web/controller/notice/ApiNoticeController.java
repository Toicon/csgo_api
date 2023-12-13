package com.csgo.web.controller.notice;

import com.csgo.mapper.NoticeMapper;
import com.csgo.domain.Notice;
import com.csgo.support.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "公告")
@RestController
@RequestMapping("/api/notice")
@Slf4j
public class ApiNoticeController {

    @Autowired
    private NoticeMapper mapper;

    /**
     * 查询到所有的公告信息
     *
     * @return
     */
    @ApiOperation("查询到所有的公告信息")
    @RequestMapping(value = "queryByAll", method = RequestMethod.GET)
    public Result queryByAll() {
        List<Notice> co = mapper.selectListLt(new Notice());
        return new Result().result(co);
    }

}
