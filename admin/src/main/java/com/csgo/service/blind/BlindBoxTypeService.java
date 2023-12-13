package com.csgo.service.blind;

import com.csgo.domain.BlindBoxType;
import com.csgo.domain.plus.blind.BlindBoxTypePlus;
import com.csgo.mapper.BlindBoxTypeMapper;
import com.csgo.mapper.plus.blind.BlindBoxTypePlusMapper;
import com.csgo.repository.BlindBoxTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author admin
 */
@Service
public class BlindBoxTypeService {

    @Autowired
    private BlindBoxTypeMapper blindBoxTypeMapper;
    @Autowired
    private BlindBoxTypeRepository blindBoxTypeRepository;
    @Autowired
    private BlindBoxTypePlusMapper blindBoxTypePlusMapper;

    public BlindBoxTypePlus get(Integer id) {
        return blindBoxTypePlusMapper.selectById(id);
    }

    @Transactional
    public void update(BlindBoxTypePlus type) {
        type.setUpdateTime(new Date());
        blindBoxTypePlusMapper.updateById(type);
    }

    @Transactional
    public int add(BlindBoxType blindBoxType) {
        BlindBoxType blindBoxTypeDo = blindBoxTypeMapper.selectByName(blindBoxType.getName());
        if (blindBoxTypeDo == null) {
            blindBoxType.setAddTime(new Date());
            blindBoxType.setUpdateTime(new Date());
            BlindBoxType save = blindBoxTypeRepository.save(blindBoxType);
            return save.getId();
        }
        return 0;
    }

    public List<BlindBoxType> getList() {
        List<BlindBoxType> blindBoxTypeList = blindBoxTypeMapper.selectAllType();
        return blindBoxTypeList;
    }

    public int update(BlindBoxType blindBoxType) {

        BlindBoxType blindBoxTypeDo = blindBoxTypeMapper.selectByName(blindBoxType.getName());
        if (blindBoxTypeDo != null) {
            if (!blindBoxTypeDo.getId().equals(blindBoxType.getId())) {
                return 0;
            }
        }
        Optional<BlindBoxType> optional = blindBoxTypeRepository.findById(blindBoxType.getId());
        if (optional != null && optional.get() != null) {
            blindBoxType.setAddTime(optional.get().getAddTime());
            blindBoxType.setUpdateTime(new Date());
            BlindBoxType blindBoxType1 = blindBoxTypeRepository.saveAndFlush(blindBoxType);
            if (blindBoxType1 != null) {
                return blindBoxType1.getId();
            }
        }
        return 0;
    }

    public void deleteBath(List<Integer> ids) {
        for (Integer id : ids) {
            blindBoxTypeRepository.deleteById(id);
        }
    }
}
