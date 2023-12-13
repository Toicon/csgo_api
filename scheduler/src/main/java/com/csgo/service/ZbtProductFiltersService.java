package com.csgo.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import com.csgo.config.ZBTProperties;
import com.csgo.domain.ProductFilterCategoryDO;
import com.csgo.mapper.ProductFilterCategoryMapper;
import com.csgo.support.ProductFilters;
import com.csgo.support.ProductFiltersDetails;
import com.csgo.support.ZbtProductFiltersReponse;
import com.csgo.util.HttpUtil2;
import com.echo.framework.support.jackson.json.JSON;

@Service
public class ZbtProductFiltersService {
    @Autowired
    private ZBTProperties properties;
    @Autowired
    private ProductFilterCategoryMapper productFilterCategoryMapper;

    @Transactional(rollbackFor = Exception.class)
    public ZbtProductFiltersReponse getProductFilters() {
        Map<String, Object> params = new HashMap<>();
        params.put("appId", properties.getAppId());
        params.put("app-key", properties.getAppKey());
        params.put("language", "zh_CN");
        String result = HttpUtil2.doGet(properties.getProductFilter(), params);
        if (StringUtils.isEmpty(result)) {
            throw new RuntimeException("获取失败");
        }
        return JSON.fromJSON(result, ZbtProductFiltersReponse.class);
    }

    public List<ProductFiltersDetails> updateRefresh() {
        ZbtProductFiltersReponse zbtProductFiltersReponse = getProductFilters();
        if (zbtProductFiltersReponse == null || !zbtProductFiltersReponse.getSuccess()) {
            throw new RuntimeException("获取分类信息失败");
        }
        ProductFilters productFilters = zbtProductFiltersReponse.getData();
        List<ProductFiltersDetails> list = productFilters.getList().stream().filter(a -> a.getKey().equals("Type")).collect(Collectors.toList());
        List<ProductFilterCategoryDO> productFilterCategoryDOListUpdate = new LinkedList<>();
        List<ProductFilterCategoryDO> productFilterCategoryDOListAdd = new LinkedList<>();
        List<ProductFiltersDetails> categorys = list.get(0).getList();
        if (categorys == null) {
            categorys = new ArrayList<>();
        }
        categorys.forEach(items -> {
            ProductFilterCategoryDO productFilterCategoryDO = productFilterCategoryMapper.selectProductFilterCategoryByKeyAndName(items.getKey(), items.getName());
            if (productFilterCategoryDO != null) {
//                productFilterCategoryDO.setAddTime(new Date(System.currentTimeMillis()));
//                productFilterCategoryDO.setUpdateTime(new Date(System.currentTimeMillis()));
//                productFilterCategoryDO.setName(items.getName()==null?"":items.getName());
//                productFilterCategoryDO.setSearchKey(items.getSearchKey()==null?"":items.getSearchKey());
//                productFilterCategoryDO.setParentKey("0");
//                productFilterCategoryDO.setImgUrl(items.getImage()==null?"":items.getImage());
//                productFilterCategoryDO.setKey(items.getKey()==null?"":items.getKey());
//                productFilterCategoryDOListUpdate.add(productFilterCategoryDO);
                productFilterCategoryDO.setUpdateTime(new Date(System.currentTimeMillis()));
                productFilterCategoryDO.setName(items.getName() == null ? "" : items.getName());
                productFilterCategoryDO.setSearchKey(items.getSearchKey() == null ? "" : items.getSearchKey());
                productFilterCategoryDO.setKey(items.getKey() == null ? "" : items.getKey());
                productFilterCategoryDO.setParentKey("0");
                productFilterCategoryDO.setImgUrl(items.getImage() == null ? "" : items.getImage());
                productFilterCategoryMapper.updateCategory(productFilterCategoryDO);
            } else {
                productFilterCategoryDO = new ProductFilterCategoryDO();
                productFilterCategoryDO.setAddTime(new Date(System.currentTimeMillis()));
                productFilterCategoryDO.setUpdateTime(new Date(System.currentTimeMillis()));
                productFilterCategoryDO.setName(items.getName() == null ? "" : items.getName());
                productFilterCategoryDO.setSearchKey(items.getSearchKey() == null ? "" : items.getSearchKey());
                productFilterCategoryDO.setKey(items.getKey() == null ? "" : items.getKey());
                productFilterCategoryDO.setParentKey("0");
                productFilterCategoryDO.setImgUrl(items.getImage() == null ? "" : items.getImage());
                productFilterCategoryDOListAdd.add(productFilterCategoryDO);
            }
            treeChildCategory(productFilterCategoryDOListAdd, productFilterCategoryDOListUpdate, items.getList(), items.getKey());
        });
//        List<ProductFilterCategoryDO>  productFilterCategoryDOListUpdate2=productFilterCategoryDOListUpdate.stream().filter(a->a.getName().equals("折叠刀")).collect(Collectors.toList());
//        if (productFilterCategoryDOListUpdate.size() > 0) {
//          productFilterCategoryMapper.updateBath(productFilterCategoryDOListUpdate);
//        }
        if (productFilterCategoryDOListAdd.size() > 0) {
            productFilterCategoryMapper.addBath(productFilterCategoryDOListAdd);
        }
        return list;
    }

    public void treeChildCategory(List<ProductFilterCategoryDO> productFilterCategoryDOListAdd,
                                  List<ProductFilterCategoryDO> productFilterCategoryDOListUpdate,
                                  List<ProductFiltersDetails> categorys, String parentKey) {
        if (categorys == null) {
            categorys = new ArrayList<>();
        }
        categorys.forEach(items -> {
            ProductFilterCategoryDO productFilterCategoryDO = productFilterCategoryMapper.selectProductFilterCategoryByKeyAndName(items.getKey(), items.getName());
            if (productFilterCategoryDO != null) {
//                productFilterCategoryDO.setAddTime(new Date(System.currentTimeMillis()));
//                productFilterCategoryDO.setUpdateTime(new Date(System.currentTimeMillis()));
//                productFilterCategoryDO.setName(items.getName()==null?"":items.getName());
//                productFilterCategoryDO.setSearchKey(items.getSearchKey()==null?"":items.getSearchKey());
//                productFilterCategoryDO.setParentKey(parentKey==null?"":parentKey);
//                productFilterCategoryDO.setImgUrl(items.getImage()==null?"":items.getImage());
//                productFilterCategoryDO.setKey(items.getKey()==null?"":items.getKey());
//                productFilterCategoryDOListUpdate.add(productFilterCategoryDO);
                productFilterCategoryDO.setUpdateTime(new Date(System.currentTimeMillis()));
                productFilterCategoryDO.setName(items.getName() == null ? "" : items.getName());
                productFilterCategoryDO.setSearchKey(items.getSearchKey() == null ? "" : items.getSearchKey());
                productFilterCategoryDO.setKey(items.getKey() == null ? "" : items.getKey());
                productFilterCategoryDO.setParentKey(parentKey == null ? "" : parentKey);
                productFilterCategoryDO.setImgUrl(items.getImage() == null ? "" : items.getImage());
                productFilterCategoryMapper.updateCategory(productFilterCategoryDO);
            } else {
                productFilterCategoryDO = new ProductFilterCategoryDO();
                productFilterCategoryDO.setAddTime(new Date(System.currentTimeMillis()));
                productFilterCategoryDO.setUpdateTime(new Date(System.currentTimeMillis()));
                productFilterCategoryDO.setName(items.getName() == null ? "" : items.getName());
                productFilterCategoryDO.setSearchKey(items.getSearchKey() == null ? "" : items.getSearchKey());
                productFilterCategoryDO.setKey(items.getKey() == null ? "" : items.getKey());
                productFilterCategoryDO.setParentKey(parentKey == null ? "" : parentKey);
                productFilterCategoryDO.setImgUrl(items.getImage() == null ? "" : items.getImage());
                productFilterCategoryDOListAdd.add(productFilterCategoryDO);
            }
            treeChildCategory(productFilterCategoryDOListAdd, productFilterCategoryDOListUpdate,
                    items.getList(), items.getKey());
        });
    }
}
