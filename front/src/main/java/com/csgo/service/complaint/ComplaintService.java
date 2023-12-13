package com.csgo.service.complaint;

import com.csgo.domain.plus.complaint.Complaint;
import com.csgo.domain.plus.complaint.ComplaintImg;
import com.csgo.domain.plus.complaint.ComplaintStatus;
import com.csgo.mapper.plus.complaint.ComplaintImgMapper;
import com.csgo.mapper.plus.complaint.ComplaintMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Date;
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

    @Transactional
    public void insert(Complaint complaint, List<String> imgs) {
        complaint.setCt(new Date());
        complaint.setStatus(ComplaintStatus.UNCHECKED);
        complaintMapper.insert(complaint);
        if (!CollectionUtils.isEmpty(imgs)) {
            imgs.forEach(img -> {
                ComplaintImg complaintImg = new ComplaintImg();
                complaintImg.setComplaintId(complaint.getId());
                complaintImg.setCt(new Date());
                complaintImg.setImg(img);
                complaintImgMapper.insert(complaintImg);
            });
        }
    }
}
