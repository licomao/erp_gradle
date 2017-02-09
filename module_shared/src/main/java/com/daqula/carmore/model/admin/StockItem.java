package com.daqula.carmore.model.admin;

import com.daqula.carmore.annotation.ApiJsonIgnore;
import org.springframework.format.annotation.NumberFormat;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

/**
 * 库存实物商品，与库存管理相关联
 */
@Entity
@DiscriminatorValue(value=SkuItem.SKU_TYPE_STOCK)
public class StockItem extends SkuItem {

    /** App销售分类 - 配件 - 其它 */
    public static final int CATEGORY_ACCESSORY_OTHER     = 0;
    /** App销售分类 - 配件 - 轮胎 */
    public static final int CATEGORY_ACCESSORY_TIRE      = 1;
    /** App销售分类 - 配件 - 用品 */
    public static final int CATEGORY_ACCESSORY_INVENTORY = 2;
    /** App销售分类 - 配件 - 车载 */
    public static final int CATEGORY_ACCESSORY_MOUNTED   = 3;
    /** App销售分类 - 配件 - 电瓶 */
    public static final int CATEGORY_ACCESSORY_BATTERY   = 4;

    /** 成本 */
    @NumberFormat(style= NumberFormat.Style.CURRENCY)
    @ApiJsonIgnore
    public double cost;

    /** App上架销售的配件分类 */
    public int accessoryCategory = CATEGORY_ACCESSORY_OTHER;

    /** 条形码 */
    public String barCode;

    /** 结算状态 0 = 非铺货，1=铺货 2=月结 3= 现结 */
    @ApiJsonIgnore
    public int isDistribution;

    /** 商品排位序号 */
    @ApiJsonIgnore
    public int appSort;

    /**
     * 额外产品属性，如轮胎扁平比。根据具体商品类型配置
     * For Tire：胎面宽，180，范围135～355，间隔5
     */
    @ApiJsonIgnore
    public String param1;

    /**
     * 额外产品属性
     * For Tire：扁平比，范围25~85，间隔5
     */
    @ApiJsonIgnore
    public String param2;

    /**
     * 额外产品属性
     * For Tire：直径，范围12～22，间隔1
     */
    @ApiJsonIgnore
    public String param3;

    /** 额外产品属性 */
    @ApiJsonIgnore
    public String param4;

    /** 额外产品属性 */
    @ApiJsonIgnore
    public String param5;

    /** 是否App推广, !!废弃，使用SaleShelf */
    @ApiJsonIgnore
    @Deprecated
    public boolean isAppSale;


}
