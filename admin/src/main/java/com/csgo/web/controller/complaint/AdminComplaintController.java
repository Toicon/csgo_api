package com.csgo.web.controller.complaint;

import com.csgo.condition.complaint.SearchComplaintCondition;
import com.csgo.domain.plus.complaint.ComplaintImg;
import com.csgo.service.complaint.ComplaintService;
import com.csgo.support.DataConverter;
import com.csgo.web.controller.BackOfficeController;
import com.csgo.web.request.complaint.SearchComplaintRequest;
import com.csgo.web.response.complaint.ComplaintResponse;
import com.csgo.web.support.Log;
import com.echo.framework.platform.web.response.PageResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/complaint")
@Slf4j
public class AdminComplaintController extends BackOfficeController {

    @Autowired
    private ComplaintService complaintService;

    /**
     * @return
     */
    @PostMapping("/pagination")
    @Log(desc = "查询投诉列表")
    public PageResponse<ComplaintResponse> pagination(@Valid @RequestBody SearchComplaintRequest request) {
        return DataConverter.to(complaint -> {
            ComplaintResponse response = new ComplaintResponse();
            BeanUtils.copyProperties(complaint, response);
            response.setType(complaint.getType());
            List<ComplaintImg> imgs = complaintService.findByComplaintId(complaint.getId());
            if (!CollectionUtils.isEmpty(imgs)) {
                response.setImgs(imgs.stream().map(ComplaintImg::getImg).collect(Collectors.toList()));
            }
            return response;
        }, complaintService.pagination(DataConverter.to(SearchComplaintCondition.class, request)));
    }
}
