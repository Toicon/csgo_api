package com.csgo.web.controller.dept;


import com.csgo.domain.SysDept;
import com.csgo.service.dept.SysDeptService;
import com.csgo.support.Result;
import com.echo.framework.platform.interceptor.session.RequireSession;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 部门信息
 *
 * @author admin
 */
@Slf4j
@RequireSession
@RestController
@RequestMapping("/dept")
public class SysDeptController {
    @Autowired
    private SysDeptService deptService;

    /**
     * 获取部门列表
     */
    @GetMapping("/list")
    public Result list(SysDept dept) {
        List<SysDept> depts = deptService.selectDeptList(dept);
        return new Result().result(depts);
    }

    /**
     * 获取部门列表数据权限
     */
    @GetMapping("/dataScope/list")
    public Result dataScopeList(SysDept dept) {
        List<SysDept> depts = deptService.dataScopeListAndSelf(dept);
        return new Result().result(depts);
    }

    /**
     * 查询部门列表（排除节点）
     */
    @GetMapping("/list/exclude/{deptId}")
    public Result excludeChild(@PathVariable(value = "deptId", required = false) Integer deptId) {
        List<SysDept> depts = deptService.selectDeptList(new SysDept());
        Iterator<SysDept> it = depts.iterator();
        while (it.hasNext()) {
            SysDept d = it.next();
            if (d.getId().intValue() == deptId
                    || ArrayUtils.contains(StringUtils.split(d.getAncestors(), ","), deptId + "")) {
                it.remove();
            }
        }
        return new Result().result(depts);
    }

    /**
     * 根据部门编号获取详细信息
     */
    @GetMapping(value = "/{deptId}")
    public Result getInfo(@PathVariable Integer deptId) {
        //deptService.checkDeptDataScope(deptId);
        return new Result().result(deptService.selectDeptById(deptId));
    }

    /**
     * 获取部门下拉树列表
     */
    @GetMapping("/treeselect")
    public Result treeselect(SysDept dept) {
        List<SysDept> depts = deptService.selectDeptList(dept);
        return new Result().result(deptService.buildDeptTreeSelect(depts));
    }

    /**
     * 加载对应角色部门列表树
     */
    @GetMapping(value = "/roleDeptTreeselect/{roleId}")
    public Result roleDeptTreeselect(@PathVariable("roleId") Long roleId) {
        List<SysDept> depts = deptService.selectDeptList(new SysDept());
        Map<String, Object> result = new HashMap<>();
        //result.put("checkedKeys", deptService.selectDeptListByRoleId(roleId));
        result.put("checkedKeys", null);
        result.put("depts", deptService.buildDeptTreeSelect(depts));
        return new Result().result(result);
    }

    /**
     * 新增部门
     */
    @PostMapping
    public Result add(@Validated @RequestBody SysDept dept) {
        return new Result().result(deptService.insertDept(dept));
    }

    /**
     * 修改部门
     */
    @PutMapping
    public Result edit(@Validated @RequestBody SysDept dept) {
        return new Result().result(deptService.updateDept(dept));
    }

    /**
     * 删除部门
     */
    @DeleteMapping("/{deptId}")
    public Result remove(@PathVariable Integer deptId) {
        return new Result().result(deptService.deleteDeptById(deptId));
    }
}
