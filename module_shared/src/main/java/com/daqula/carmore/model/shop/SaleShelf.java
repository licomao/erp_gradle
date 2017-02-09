package com.daqula.carmore.model.shop;

import com.daqula.carmore.model.BaseEntity;
import com.daqula.carmore.model.admin.SkuItem;
import com.daqula.carmore.model.admin.Suite;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * App上架信息
 */
@Entity
public class SaleShelf extends BaseEntity {

    /** App销售分类 - 洗车 */
    public static final int CATEGORY_WASH_CAR     = 1;
    /** App销售分类 - 美容 */
    public static final int CATEGORY_BEAUTIFY     = 2;
    /** App销售分类 - 保养 */
    public static final int CATEGORY_CARE         = 3;
    /** App销售分类 - 配件 */
    public static final int CATEGORY_ACCESSORY    = 4;
    /** App销售分类 - 维修 */
    public static final int CATEGORY_REPAIR       = 5;
    /** App销售分类 - 会员 */
    public static final int CATEGORY_VIP          = 6;

    /** 上架的分类 */
    public int saleCategory;

    /** 出售价格 */
    public double price;

    /** 所属组织 */
    @ManyToOne
    public Organization organization;


    /** 所卖门店 */
    @ManyToMany
    public List<Shop> shops;


    /** 上架销售的商品，与suite二选一 */
    @ManyToOne
    public SkuItem skuItem;

    /** 上架销售的套餐，与skuItem二选一 */
    @ManyToOne
    public Suite suite;

}
