package com.csgo.mapper.plus.complaint;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.csgo.domain.plus.complaint.ComplaintImg;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author admin
 */
@Repository
public interface ComplaintImgMapper extends BaseMapper<ComplaintImg> {

    default List<ComplaintImg> findByComplaintId(Integer complaintId) {
        LambdaQueryWrapper<ComplaintImg> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(ComplaintImg::getComplaintId, complaintId);
        return selectList(wrapper);
    }
}
