package com.csgo.service.code;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.code.SearchActivationCodeCondition;
import com.csgo.domain.BaseEntity;
import com.csgo.domain.plus.code.ActivationCode;
import com.csgo.domain.plus.code.ProductType;
import com.csgo.domain.user.User;
import com.csgo.mapper.UserMapper;
import com.csgo.mapper.plus.code.ActivationCodeMapper;
import com.csgo.support.StandardExceptionCode;
import com.csgo.util.CodeUtil;
import com.csgo.util.StringUtil;
import com.csgo.web.request.code.AddActivationCodeView;
import com.csgo.web.response.code.ActivationDownloadView;
import com.echo.framework.platform.exception.ApiException;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author admin
 */
@Service
public class ActivationCodeService {
    @Autowired
    private ActivationCodeMapper activationCodeMapper;
    @Autowired
    private UserMapper userMapper;

    public Page<ActivationCode> find(SearchActivationCodeCondition condition) {
        return activationCodeMapper.find(condition.getCdKey(), condition.getUserName(), condition.getPage());
    }

    public List<ActivationDownloadView> downloadViews(List<Integer> ids) {
        List<ActivationCode> codes = activationCodeMapper.findByList(ids);
        return codes.stream().map(code -> {
            ActivationDownloadView view = new ActivationDownloadView();
            BeanUtils.copyProperties(code, view);
            return view;
        }).collect(Collectors.toList());
    }


    @Transactional
    public List<Integer> add(List<AddActivationCodeView> views, String source) {
        List<Integer> codeIds = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(views)) {
            AddActivationCodeView item = views.get(0);
            String targetUserName = null;
            Integer targetUserId = null;
            if (!StringUtil.isEmpty(item.getTargetUserName())) {
                //判断目标账号是否存在
                User user = userMapper.getUserByUserName(item.getTargetUserName());
                if (user == null) {
                    throw new ApiException(StandardExceptionCode.EDIT_USER_FAILURE, "目标账号不存在，请重新填写！");
                }
                targetUserName = user.getUserName();
                targetUserId = user.getId();
            }
            for (AddActivationCodeView view : views) {
                for (int i = 0; i < view.getNum(); i++) {
                    ActivationCode activationCode = new ActivationCode();
                    activationCode.setCdKey("CSGO" + CodeUtil.generateShortUuid());
                    if (ProductType.VB.equals(view.getProductType())) {
                        activationCode.setProductName(ProductType.VB.getDescription());
                    } else {
                        activationCode.setProductName(view.getProductName());
                        activationCode.setGiftProductId(view.getGiftProductId());
                    }
                    activationCode.setProductType(view.getProductType());
                    activationCode.setPrice(view.getPrice());
                    activationCode.setTargetUserId(targetUserId);
                    activationCode.setTargetUserName(targetUserName);
                    BaseEntity.created(activationCode, source);
                    activationCodeMapper.insert(activationCode);
                    codeIds.add(activationCode.getId());
                }
            }
        }
        return codeIds;
    }


    @Transactional
    public void delete(int id) {
        activationCodeMapper.deleteById(id);
    }
}
