package com.daqula.carmore.model.admin;

import com.daqula.carmore.model.BaseEntity;
import com.daqula.carmore.model.shop.Organization;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * 商品分类
 */
@Entity
public class SecondaryCategory extends BaseEntity {

    /** 分类名 */
    public String name;

    /** 顶级分类，例如：{@link SkuItem#CATEGORY_WASH_CAR} */
    public int rootCategory;

    /** 加成率 */
//    @NumberFormat(style= NumberFormat.Style.PERCENT)
    public float additionRate;

    /** 所属组织 */
    @ManyToOne
    public Organization organization;

}
