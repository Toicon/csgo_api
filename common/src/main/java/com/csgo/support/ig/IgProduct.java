package com.csgo.support.ig;

import java.io.Serializable;
import java.math.BigDecimal;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author admin
 */
public class IgProduct implements Serializable {
    @JsonProperty("product_id")
    private int productId;
    @JsonProperty("icon_url")
    private String iconUrl;
    @JsonProperty("name")
    private String name;
    @JsonProperty("ctg_name")
    private String ctgName;
    @JsonProperty("market_hash_name")
    private String market_hash_name;
    @JsonProperty("quality_name")
    private String qualityName;
    @JsonProperty("rarity_name")
    private String rarityName;
    @JsonProperty("exterior_name")
    private String exteriorName;
    @JsonProperty("min_price")
    private BigDecimal minPrice;
    @JsonProperty("sale_count")
    private int saleCount;

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCtgName() {
        return ctgName;
    }

    public void setCtgName(String ctgName) {
        this.ctgName = ctgName;
    }

    public String getMarket_hash_name() {
        return market_hash_name;
    }

    public void setMarket_hash_name(String market_hash_name) {
        this.market_hash_name = market_hash_name;
    }

    public String getQualityName() {
        return qualityName;
    }

    public void setQualityName(String qualityName) {
        this.qualityName = qualityName;
    }

    public String getRarityName() {
        return rarityName;
    }

    public void setRarityName(String rarityName) {
        this.rarityName = rarityName;
    }

    public String getExteriorName() {
        return exteriorName;
    }

    public void setExteriorName(String exteriorName) {
        this.exteriorName = exteriorName;
    }

    public BigDecimal getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(BigDecimal minPrice) {
        this.minPrice = minPrice;
    }

    public int getSaleCount() {
        return saleCount;
    }

    public void setSaleCount(int saleCount) {
        this.saleCount = saleCount;
    }
}
