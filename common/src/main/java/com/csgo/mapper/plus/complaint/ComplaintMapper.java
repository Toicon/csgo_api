package com.csgo.mapper.plus.complaint;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.complaint.SearchComplaintCondition;
import com.csgo.domain.plus.complaint.Complaint;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

/**
 * @author admin
 */
@Repository
public interface ComplaintMapper extends BaseMapper<Complaint> {

    default Page<Complaint> pagination(SearchComplaintCondition condition) {
        LambdaQueryWrapper<Complaint> wrapper = Wrappers.lambdaQuery();
        if (StringUtils.hasText(condition.getTelephone())) {
            wrapper.like(Complaint::getTelephone, condition.getTelephone());
        }
        if (StringUtils.hasText(condition.getType())) {
            wrapper.like(Complaint::getType, condition.getType());
        }
        wrapper.orderByDesc(Complaint::getCt);
        return selectPage(condition.getPage(), wrapper);
    }
}
