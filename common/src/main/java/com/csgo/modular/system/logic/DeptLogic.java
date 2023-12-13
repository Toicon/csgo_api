package com.csgo.modular.system.logic;

import com.csgo.domain.SysDept;
import com.csgo.mapper.SysDeptMapper;
import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author admin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeptLogic {

    private final SysDeptMapper deptMapper;

    public Map<Integer, SysDept> getDeptIdMap(Set<Integer> deptIds) {
        if (deptIds.isEmpty()) {
            return Maps.newHashMap();
        }
        return deptMapper.selectDeptListInDeptIds(deptIds.toArray(new Integer[0])).stream().collect(Collectors.toMap(SysDept::getId, Function.identity()));
    }

}
