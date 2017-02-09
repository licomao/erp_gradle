package com.daqula.carmore.model.admin;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * 可以添加到套餐里，由平台业务员直接指定具体供货的StockItem
 */
@Entity
@DiscriminatorValue(value=SkuItem.SKU_TYPE_SPECIFY)
public class SpecifyItem extends SkuItem {

    /** 指定具体的 */
    public StockItem stockItem;

}
