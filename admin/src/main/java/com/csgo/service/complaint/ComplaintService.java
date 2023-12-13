package com.csgo.service.complaint;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.complaint.SearchComplaintCondition;
import com.csgo.domain.plus.complaint.Complaint;
import com.csgo.domain.plus.complaint.ComplaintImg;
import com.csgo.mapper.plus.complaint.ComplaintImgMapper;
import com.csgo.mapper.plus.complaint.ComplaintMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author admin
 */
@Service
public class ComplaintService {

    @Autowired
    private ComplaintMapper complaintMapper;
    @Autowired
    private ComplaintImgMapper complaintImgMapper;

    public Page<Complaint> pagination(SearchComplaintCondition condition) {
        return complaintMapper.pagination(condition);
    }

    public Complaint get(int id) {
        return complaintMapper.selectById(id);
    }

    public List<ComplaintImg> findByComplaintId(Integer complaintId) {
        return complaintImgMapper.findByComplaintId(complaintId);
    }
}
