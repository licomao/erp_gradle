package com.daqula.carmore.model.admin;

import com.daqula.carmore.annotation.ApiJsonIgnore;
import com.daqula.carmore.model.BaseEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.persistence.*;

/**
 * 商品SKU，包括实物和服务，这里定义基础属性和顶级分类
 */
@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="sku_type",
        discriminatorType=DiscriminatorType.STRING)
public abstract class SkuItem extends BaseEntity {

    /** 顶级分类 - 洗车    - FOR APP */
    public static final int CATEGORY_WASH_CAR     = 1;
    /** 顶级分类 - 美容    - FOR APP */
    public static final int CATEGORY_BEAUTIFY     = 2;
    /** 顶级分类 - 保养    - FOR APP */
    public static final int CATEGORY_CARE         = 3;
    /** 顶级分类 - 配件    - FOR APP */
    public static final int CATEGORY_ACCESSORY    = 4;
    /** 顶级分类 - 精品用品 */
    public static final int CATEGORY_INVENTORY    = 5;
    /** 顶级分类 - 维修    - FOR APP */
    public static final int CATEGORY_REPAIR       = 6;
    /** 顶级分类 - 会员    - FOR APP */
    public static final int CATEGORY_VIP          = 7;

    public static final String SKU_TYPE_STOCK          = "stock";
    public static final String SKU_TYPE_SERVICE        = "service";
    public static final String SKU_TYPE_OPERATION      = "operation";
    public static final String SKU_TYPE_CUSTOM_STOCK   = "custom_stock";
    public static final String SKU_TYPE_CUSTOM_SERVICE = "custom_service";
    public static final String SKU_TYPE_SPECIFY        = "specify";

    /** 商品名称 */
    public String name;

    /** 品牌名称 */
    public String brandName;

    @Column(name = "sku_type", insertable = false, updatable = false)
    public String skuType;

    /** 商品描述 */
    public String description;

    /** 顶级分类 */
    @ApiJsonIgnore
    public int rootCategory;

    /** 二级分类 */
    @ManyToOne
    @ApiJsonIgnore
    public SecondaryCategory secondaryCategory;

    /** 建议零售价格 */
    public double price;

    /** App订单需要预约 */
    public boolean needAppointment = true;

    /** 展示图片，200*200 */
    public String coverImageUrl;

    /** 产品供应商 */
    @ApiJsonIgnore
    @OneToOne
    public Supplier supplier;

}
