package com.csgo.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.domain.ProductFilterCategoryDO;

@Repository
@Mapper
public interface ProductFilterCategoryMapper {
    ProductFilterCategoryDO selectProductFilterCategoryByKeyAndName(@Param("key") String key, @Param("name") String name);

    void updateBath(@Param("list") List<ProductFilterCategoryDO> productFilterCategoryDOListUpdate);

    void addBath(@Param("list") List<ProductFilterCategoryDO> productFilterCategoryDOListAdd);

    long selectAdminCount(@Param("keywords") String keywords);

    Page<ProductFilterCategoryDO> selectPageList(Page<ProductFilterCategoryDO> page, @Param("keywords") String keywords);

    void deleteById(@Param("id") Integer id);

    void addCategory(ProductFilterCategoryDO productFilterCategoryDO);

    void updateCategory(ProductFilterCategoryDO productFilterCategoryDO);

    void deleteBach(@Param("ids") List<Integer> ids);

    Integer selectCountByKey(@Param("key") String key, @Param("id") Integer id);

    String selectNameByCsgoType(@Param("csgoType") String csgoType);

    List<ProductFilterCategoryDO> selectAllZbtProductFilters();

    List<ProductFilterCategoryDO> getLuckyCategoryList();

    List<ProductFilterCategoryDO> getCategoryListByParentKey(@Param("parentKey") String parentKey);
}
