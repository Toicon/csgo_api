package com.csgo.service.dept;


import com.csgo.constants.UserConstants;
import com.csgo.domain.SysDept;
import com.csgo.domain.TreeSelect;
import com.csgo.domain.enums.YesOrNoEnum;
import com.csgo.exception.AdminErrorException;
import com.csgo.mapper.SysDeptMapper;
import com.csgo.service.AdminUserService;
import com.csgo.util.Convert;
import com.csgo.web.support.SiteContext;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 部门管理 服务实现
 *
 * @author admin
 */
@Service
public class SysDeptService {
    @Autowired
    private SysDeptMapper deptMapper;

    @Autowired
    protected SiteContext siteContext;

    @Autowired
    private AdminUserService adminUserService;

/*    @Autowired
    private SysRoleMapper roleMapper;*/

    /**
     * 查询部门管理数据
     *
     * @param dept 部门信息
     * @return 部门信息集合
     */
    public List<SysDept> selectDeptList(SysDept dept) {
        return deptMapper.selectDeptList(dept);
    }

    /**
     * 查询部门管理数据(有数据权限)
     *
     * @param dept 部门信息
     * @return 部门信息集合
     */
    public List<SysDept> dataScopeList(SysDept dept) {
        String dataScope = adminUserService.getUserDataScope("d").replaceAll("dept_id", "id");
        dept.setDataScope(dataScope);
        return deptMapper.selectDeptList(dept);
    }

    /**
     * 查询部门管理数据(有数据权限)
     *
     * @param dept 部门信息
     * @return 部门信息集合
     */
    public List<SysDept> dataScopeListAndSelf(SysDept dept) {
        String dataScope = adminUserService.getUserDataScopeAndSelf("d").replaceAll("dept_id", "id");
        dept.setDataScope(dataScope);
        return deptMapper.selectDeptList(dept);
    }

    /**
     * 根据部门id集合查询部门管理数据
     *
     * @param deptIds 部门信息
     * @return 部门信息集合
     */
    public List<SysDept> selectDeptListInDeptIds(Integer[] deptIds) {
        return deptMapper.selectDeptListInDeptIds(deptIds);
    }


    /**
     * 构建前端所需要树结构
     *
     * @param depts 部门列表
     * @return 树结构列表
     */
    public List<SysDept> buildDeptTree(List<SysDept> depts) {
        List<SysDept> returnList = new ArrayList<>();
        List<Integer> tempList = new ArrayList<>();
        for (SysDept dept : depts) {
            tempList.add(dept.getId());
        }
        for (SysDept dept : depts) {
            // 如果是顶级节点, 遍历该父节点的所有子节点
            if (!tempList.contains(dept.getParentId())) {
                recursionFn(depts, dept);
                returnList.add(dept);
            }
        }
        if (returnList.isEmpty()) {
            returnList = depts;
        }
        return returnList;
    }

    /**
     * 构建前端所需要下拉树结构
     *
     * @param depts 部门列表
     * @return 下拉树结构列表
     */
    public List<TreeSelect> buildDeptTreeSelect(List<SysDept> depts) {
        List<SysDept> deptTrees = buildDeptTree(depts);
        return deptTrees.stream().map(TreeSelect::new).collect(Collectors.toList());
    }

    /*   *//**
     * 根据角色ID查询部门树信息
     *
     * @param roleId 角色ID
     * @return 选中部门列表
     *//*
    public List<Long> selectDeptListByRoleId(Long roleId) {
        SysRole role = roleMapper.selectRoleById(roleId);
        return deptMapper.selectDeptListByRoleId(roleId, role.isDeptCheckStrictly());
    }
*/

    /**
     * 根据部门ID查询信息
     *
     * @param deptId 部门ID
     * @return 部门信息
     */
    public SysDept selectDeptById(Integer deptId) {
        return deptMapper.selectDeptById(deptId);
    }

    /**
     * 根据ID查询所有子部门（正常状态）
     *
     * @param deptId 部门ID
     * @return 子部门数
     */
    public int selectNormalChildrenDeptById(Integer deptId) {
        return deptMapper.selectNormalChildrenDeptById(deptId);
    }

    /**
     * 是否存在子节点
     *
     * @param deptId 部门ID
     * @return 结果
     */
    public boolean hasChildByDeptId(Integer deptId) {
        int result = deptMapper.hasChildByDeptId(deptId);
        return result > 0;
    }

    /**
     * 查询部门是否存在用户
     *
     * @param deptId 部门ID
     * @return 结果 true 存在 false 不存在
     */
    public boolean checkDeptExistUser(Integer deptId) {
        int result = deptMapper.checkDeptExistUser(deptId);
        return result > 0;
    }

    /**
     * 校验部门名称是否唯一
     *
     * @param dept 部门信息
     * @return 结果
     */
    public String checkDeptNameUnique(SysDept dept) {
        Long deptId = dept.getId() == null ? -1L : dept.getId();
        SysDept info = deptMapper.checkDeptNameUnique(dept.getDeptName(), dept.getParentId());
        if (info != null && info.getId().intValue() != deptId.intValue()) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验部门是否有数据权限
     *
     * @param deptId 部门id
     */
 /*   public void checkDeptDataScope(Integer deptId) {
        if (!SysUser.isAdmin(SecurityUtils.getUserId())) {
            SysDept dept = new SysDept();
            dept.setDeptId(deptId);
            List<SysDept> depts = SpringUtils.getAopProxy(this).selectDeptList(dept);
            if (StringUtils.isEmpty(depts)) {
                throw new ServiceException("没有权限访问部门数据！");
            }
        }
    }*/

    /**
     * 新增保存部门信息
     *
     * @param dept 部门信息
     * @return 结果
     */
    public int insertDept(SysDept dept) {
        if (YesOrNoEnum.YES.getCode().toString().equals(this.checkDeptNameUnique(dept))) {
            throw new AdminErrorException("新增部门'" + dept.getDeptName() + "'失败，部门名称已存在");
        }
        SysDept info = deptMapper.selectDeptById(dept.getParentId());
        // 如果父节点不为正常状态,则不允许新增子节点
        if (!YesOrNoEnum.NO.getCode().toString().equals(info.getStatus())) {
            throw new AdminErrorException("部门停用，不允许新增");
        }
        dept.setCreateBy(siteContext.getCurrentUser().getName());
        dept.setCreateDate(new Date());
        dept.setAncestors(info.getAncestors() + "," + dept.getParentId());
        return deptMapper.insertDept(dept);
    }

    /**
     * 修改保存部门信息
     *
     * @param dept 部门信息
     * @return 结果
     */
    public int updateDept(SysDept dept) {
        Integer deptId = dept.getId();
        //this.checkDeptDataScope(deptId);
        if (UserConstants.NOT_UNIQUE.equals(this.checkDeptNameUnique(dept))) {
            throw new AdminErrorException("修改部门'" + dept.getDeptName() + "'失败，部门名称已存在");
        } else if (dept.getParentId().equals(deptId)) {
            throw new AdminErrorException("修改部门'" + dept.getDeptName() + "'失败，上级部门不能是自己");
        } else if (StringUtils.equals(UserConstants.DEPT_DISABLE, dept.getStatus()) && this.selectNormalChildrenDeptById(deptId) > 0) {
            throw new AdminErrorException("该部门包含未停用的子部门！");
        }
        SysDept newParentDept = deptMapper.selectDeptById(dept.getParentId());
        SysDept oldDept = deptMapper.selectDeptById(dept.getId());
        if (newParentDept != null && oldDept != null) {
            String newAncestors = newParentDept.getAncestors() + "," + newParentDept.getId();
            String oldAncestors = oldDept.getAncestors();
            dept.setAncestors(newAncestors);
            updateDeptChildren(dept.getId(), newAncestors, oldAncestors);
        }
        dept.setUpdateBy(siteContext.getCurrentUser().getName());
        dept.setUpdateDate(new Date());
        int result = deptMapper.updateDept(dept);
        if (YesOrNoEnum.NO.getCode().toString().equals(dept.getStatus()) && StringUtils.isNotEmpty(dept.getAncestors())
                && !StringUtils.equals(YesOrNoEnum.NO.getCode().toString(), dept.getAncestors())) {
            // 如果该部门是启用状态，则启用该部门的所有上级部门
            updateParentDeptStatusNormal(dept);
        }
        return result;
    }

    /**
     * 修改该部门的父级部门状态
     *
     * @param dept 当前部门
     */
    private void updateParentDeptStatusNormal(SysDept dept) {
        String ancestors = dept.getAncestors();
        Integer[] deptIds = Convert.toIntArray(ancestors);
        deptMapper.updateDeptStatusNormal(deptIds);
    }

    /**
     * 修改子元素关系
     *
     * @param deptId       被修改的部门ID
     * @param newAncestors 新的父ID集合
     * @param oldAncestors 旧的父ID集合
     */
    public void updateDeptChildren(Integer deptId, String newAncestors, String oldAncestors) {
        List<SysDept> children = deptMapper.selectChildrenDeptById(deptId);
        for (SysDept child : children) {
            child.setAncestors(child.getAncestors().replaceFirst(oldAncestors, newAncestors));
        }
        if (children.size() > 0) {
            deptMapper.updateDeptChildren(children);
        }
    }

    /**
     * 删除部门管理信息
     *
     * @param deptId 部门ID
     * @return 结果
     */
    public int deleteDeptById(Integer deptId) {
        if (this.hasChildByDeptId(deptId)) {
            throw new AdminErrorException("存在下级部门,不允许删除");
        }
        if (this.checkDeptExistUser(deptId)) {
            throw new AdminErrorException("部门存在用户,不允许删除");
        }
        if (deptId.intValue() == UserConstants.NO_DEPART_ID) {
            throw new AdminErrorException("未归属主播部门,不允许删除");
        }
        return deptMapper.deleteDeptById(deptId);
    }

    /**
     * 递归列表
     */
    private void recursionFn(List<SysDept> list, SysDept t) {
        // 得到子节点列表
        List<SysDept> childList = getChildList(list, t);
        t.setChildren(childList);
        for (SysDept tChild : childList) {
            if (hasChild(list, tChild)) {
                recursionFn(list, tChild);
            }
        }
    }

    /**
     * 得到子节点列表
     */
    private List<SysDept> getChildList(List<SysDept> list, SysDept t) {
        List<SysDept> tlist = new ArrayList<>();
        Iterator<SysDept> it = list.iterator();
        while (it.hasNext()) {
            SysDept n = it.next();
            if (n.getParentId() != null && n.getParentId().intValue() == t.getId().intValue()) {
                tlist.add(n);
            }
        }
        return tlist;
    }

    /**
     * 判断是否有子节点
     */
    public boolean hasChild(List<SysDept> list, SysDept t) {
        return getChildList(list, t).size() > 0;
    }
}
