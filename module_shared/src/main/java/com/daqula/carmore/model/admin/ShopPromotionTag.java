package com.daqula.carmore.model.admin;

import com.daqula.carmore.model.BaseEntity;

import javax.persistence.Entity;

/**
 * 门店特色标签
 */
@Entity
public class ShopPromotionTag extends BaseEntity {

    /** 标签名字 */
    public String tagName;

}
