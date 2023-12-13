package com.csgo.support.ZBT;

public class ZBTBean {
    //游戏id
    private Integer appId;
    //外观
    private String exterior;
    //外观名称
    private String exteriorName;
    //图片地址
    private String imageUrl;
    //饰品id
    private String itemId;
    //饰品名称
    private String itemName;
    //英文名
    private String marketHashName;
    //价格
    private PriceInfo priceInfo;
    //商品类型
    private String type;
    //品质
    private String qualit;
    //稀有度
    private String rarity;
    //稀有度颜色
    private String rarityColor;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRarityColor() {
        return rarityColor;
    }

    public void setRarityColor(String rarityColor) {
        this.rarityColor = rarityColor;
    }

    public Integer getAppId() {
        return appId;
    }

    public void setAppId(Integer appId) {
        this.appId = appId;
    }

    public String getExterior() {
        return exterior;
    }

    public void setExterior(String exterior) {
        this.exterior = exterior;
    }

    public String getExteriorName() {
        return exteriorName;
    }

    public void setExteriorName(String exteriorName) {
        this.exteriorName = exteriorName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getMarketHashName() {
        return marketHashName;
    }

    public void setMarketHashName(String marketHashName) {
        this.marketHashName = marketHashName;
    }

    public PriceInfo getPriceInfo() {
        return priceInfo;
    }

    public void setPriceInfo(PriceInfo priceInfo) {
        this.priceInfo = priceInfo;
    }

    public String getQualit() {
        return qualit;
    }

    public void setQualit(String qualit) {
        this.qualit = qualit;
    }

    public String getRarity() {
        return rarity;
    }

    public void setRarity(String rarity) {
        this.rarity = rarity;
    }

    @Override
    public String toString() {
        return "ZBTBean{" +
                "appId=" + appId +
                ", exterior='" + exterior + '\'' +
                ", exteriorName='" + exteriorName + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", itemId='" + itemId + '\'' +
                ", itemName='" + itemName + '\'' +
                ", marketHashName='" + marketHashName + '\'' +
                ", priceInfo=" + priceInfo +
                ", qualit='" + qualit + '\'' +
                ", rarity='" + rarity + '\'' +
                '}';
    }

    public static int compare(Object o1, Object o2) {
        if (null == o1 || null == o2) {
            return 0;
        }
        ZBTBean zbtBean1 = (ZBTBean) o1;
        ZBTBean zbtBean2 = (ZBTBean) o2;
        PriceInfo priceInfo1 = zbtBean1.getPriceInfo();
        PriceInfo priceInfo2 = zbtBean2.getPriceInfo();
        if (null == priceInfo1 || null == priceInfo2 || null == priceInfo1.getPrice() || null == priceInfo2.getPrice()) {
            return 0;
        }
        return priceInfo1.getPrice().compareTo(priceInfo2.getPrice());
    }
}
