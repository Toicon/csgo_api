package com.csgo.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.config.ZBTProperties;
import com.csgo.domain.BaseEntity;
import com.csgo.domain.ExchangeRate;
import com.csgo.domain.ProductFilterCategoryDO;
import com.csgo.domain.plus.gift.GiftProductPlus;
import com.csgo.domain.plus.gift.GiftProductUpdateRecord;
import com.csgo.exception.ServiceErrorException;
import com.csgo.framework.util.BigDecimalUtil;
import com.csgo.mapper.ProductFilterCategoryMapper;
import com.csgo.mapper.plus.gift.GiftProductPlusMapper;
import com.csgo.mapper.plus.gift.GiftProductUpdateRecordMapper;
import com.csgo.modular.product.logic.GiftProductSyncLogic;
import com.csgo.support.PageInfo;
import com.csgo.support.ProductFilters;
import com.csgo.support.ProductFiltersDetails;
import com.csgo.support.ZBT.PriceInfo;
import com.csgo.support.ZBT.ZBTBean;
import com.csgo.support.ZbtProductFiltersReponse;
import com.csgo.util.HttpUtil2;
import com.csgo.util.PageResult;
import com.echo.framework.support.jackson.json.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ZbtProductFiltersService {

    @Autowired
    private ZBTProperties properties;
    @Autowired
    private ProductFilterCategoryMapper productFilterCategoryMapper;
    @Autowired
    private GiftProductPlusMapper giftProductPlusMapper;
    @Autowired
    private GiftProductUpdateRecordMapper giftProductUpdateRecordMapper;
    @Autowired
    private GiftProductSyncLogic giftProductSyncLogic;

    //HttpUtil2
    @Transactional(rollbackFor = Exception.class)
    public void insertProduct(GiftProductPlus gp) {
        giftProductPlusMapper.insert(gp);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateProductPrice(PriceInfo priceInfo, GiftProductPlus gp, ZBTBean zbt, ExchangeRate rate, Set<Integer> useIds) {
        // log.info("更新价格priceInfo>>{}", priceInfo);
        // log.info("更新价格gp>>{}", gp);
        if (null != priceInfo.getPrice() && BigDecimalUtil.greaterThanZero(priceInfo.getPrice())) {
            BigDecimal nowPrice = gp.getPrice();
            BigDecimal nowOriginPrice = gp.getOriginAmount();
            if (BigDecimalUtil.greaterThanZero(nowOriginPrice)) {
                BigDecimal scale = (priceInfo.getPrice().subtract(nowOriginPrice)).divide(nowOriginPrice, 2, BigDecimal.ROUND_DOWN);
                BigDecimal price = priceInfo.getPrice().add(priceInfo.getPrice().multiply(new BigDecimal(rate.getUpsAndDowns()).divide(BigDecimal.valueOf(100)))).setScale(2, BigDecimal.ROUND_HALF_UP);
                if (canUpdatePrice(scale, price)) {
                    gp.setOriginAmount(priceInfo.getPrice());
                    gp.setPrice(price);
                    giftProductSyncLogic.syncRelation(gp);
                } else {
                    GiftProductUpdateRecord record = new GiftProductUpdateRecord();
                    record.setEnglishName(zbt.getMarketHashName());
                    record.setName(zbt.getItemName());
                    record.setImg(zbt.getImageUrl());
                    record.setNowOriginPrice(nowOriginPrice);
                    record.setNowPrice(nowPrice);
                    record.setProductId(gp.getId());
                    record.setUpdateOriginPrice(priceInfo.getPrice());
                    record.setUpdatePrice(price);
                    BaseEntity.created(record, "全局拉取");
                    giftProductUpdateRecordMapper.insert(record);
                }
            } else {
                log.error("[更新价格] 价格错误 原价：{}", nowOriginPrice);
            }
        }
        gp.setZbtStock(priceInfo.getQuantity());
        gp.setName(zbt.getItemName());
        gp.setImg(zbt.getImageUrl());
        gp.setZbtItemId(zbt.getItemId());
        gp.setEnglishName(zbt.getMarketHashName());
        gp.setUpdatedAt(new Date());
        gp.setGrade(zbt.getRarityColor());
        gp.setExteriorName(zbt.getExteriorName());
        giftProductPlusMapper.updateById(gp);
    }

    private boolean canUpdatePrice(BigDecimal price, BigDecimal scale) {
        return price.compareTo(new BigDecimal(5)) < 0 || scale.compareTo(new BigDecimal("0.3")) < 0;
    }

    public String getColour(BigDecimal price) {
        if (price.compareTo(new BigDecimal(19.99)) <= 0) {
            return "#4b69ff";
        }
        if (price.compareTo(new BigDecimal(20)) >= 0 && price.compareTo(new BigDecimal(99.99)) <= 0) {
            return "#eb4b4b";
        }
        return "#e4ae39";
    }

    @Transactional(rollbackFor = Exception.class)
    public ZbtProductFiltersReponse getProductFilters() {
        Map<String, Object> params = new HashMap<>();
        params.put("appId", properties.getAppId());
        params.put("app-key", properties.getAppKey());
        params.put("language", "zh_CN");
        String result = HttpUtil2.doGet(properties.getProductFilter(), params);
        if (StringUtils.isEmpty(result)) {
            throw new ServiceErrorException("获取失败");
        }
        return JSON.fromJSON(result, ZbtProductFiltersReponse.class);
    }

    public List<String> getRarityList() {
        ZbtProductFiltersReponse zbtProductFiltersReponse = getProductFilters();
        if (zbtProductFiltersReponse == null || !zbtProductFiltersReponse.getSuccess()) {
            throw new ServiceErrorException("定时更新价格，获取品质类目信息失败");
        }
        ProductFilters productFilters = zbtProductFiltersReponse.getData();
        if (null == productFilters) {
            throw new ServiceErrorException("定时更新价格，获取品质类目信息失败");
        }
        if (CollectionUtils.isEmpty(productFilters.getList())) {
            throw new ServiceErrorException("定时更新价格，获取品质类目信息失败");
        }
        for (ProductFiltersDetails productFiltersDetails : productFilters.getList()) {
            if (productFiltersDetails.getKey().equals("Rarity")) {
                return productFiltersDetails.getList().stream().map(ProductFiltersDetails::getSearchKey).collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }

    public List<ProductFiltersDetails> updateRefresh() {
        ZbtProductFiltersReponse zbtProductFiltersReponse = getProductFilters();
        if (zbtProductFiltersReponse == null || !zbtProductFiltersReponse.getSuccess()) {
            throw new ServiceErrorException("获取分类信息失败");
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

    public PageResult<ProductFilterCategoryDO> getPageList(Integer pageNum, Integer pageSize, String keywords) {
        PageResult<ProductFilterCategoryDO> pageResult = new PageResult<>();
        Page<ProductFilterCategoryDO> page = new Page<>(pageNum, pageSize);
        Page<ProductFilterCategoryDO> productFilterCategoryDOS = productFilterCategoryMapper.selectPageList(page, keywords);
        PageInfo<ProductFilterCategoryDO> pageInfo = new PageInfo<>(productFilterCategoryDOS);
        pageResult.setCount(pageInfo.getTotal());
        pageResult.setData(pageInfo.getList());
        pageResult.setPage(pageNum);
        pageResult.setSize(pageSize);
        return pageResult;
    }

    public void addCategory(ProductFilterCategoryDO productFilterCategoryDO) {
        productFilterCategoryDO.setAddTime(new Date(System.currentTimeMillis()));
        productFilterCategoryDO.setUpdateTime(new Date(System.currentTimeMillis()));
        Integer count = productFilterCategoryMapper.selectCountByKey(productFilterCategoryDO.getKey(), null);
        if (count > 0) {
            throw new ServiceErrorException("该分类名称已经存在，请重新定义");
        }
        productFilterCategoryMapper.addCategory(productFilterCategoryDO);
    }

    public ProductFilterCategoryDO updateCategory(ProductFilterCategoryDO productFilterCategoryDO) {
        productFilterCategoryDO.setUpdateTime(new Date(System.currentTimeMillis()));
        Integer count = productFilterCategoryMapper.selectCountByKey(productFilterCategoryDO.getKey(), productFilterCategoryDO.getId());
        if (count > 0) {
            throw new ServiceErrorException("该分类名称已经存在，请重新定义");
        }
        productFilterCategoryMapper.updateCategory(productFilterCategoryDO);
        return productFilterCategoryDO;
    }

    public void deleteBach(List<Integer> ids) {
        productFilterCategoryMapper.deleteBach(ids);
    }

    public String getNameByCsgoType(String csgoType) {
        return productFilterCategoryMapper.selectNameByCsgoType(csgoType);
    }

    public List<ProductFilterCategoryDO> getAllZbtProductFilters() {
        return productFilterCategoryMapper.selectAllZbtProductFilters();
    }

    public List<ProductFilterCategoryDO> getCategoryListByParentKey(String parentKey) {
        return productFilterCategoryMapper.getCategoryListByParentKey(parentKey);
    }
}
