package com.daqula.carmore.model.admin;

import com.daqula.carmore.model.BaseEntity;

import javax.persistence.*;
import java.util.List;

/**
 * 套餐 - 系统通用
 */
@Entity
@Inheritance(strategy= InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="custom_suite",
        discriminatorType=DiscriminatorType.INTEGER)
@DiscriminatorValue(value="0")
public class Suite extends BaseEntity {

    /** 服务套餐 */
    public static final int SUITE_TYPE_SERVICES = 0;
    /** 会员卡套餐 */
    public static final int SUITE_TYPE_VIP = 1;

    /** 名称 */
    public String name;

    /** 套餐类型 */
    public int suiteType;

    /** 套餐描述 */
    public String description;

    /** 套餐图片 */
    public String coverUrl;

    /** 套餐价格 */
    public double price;

    /** 有效期天数 */
    public int expiation;

    /** 套餐项目 */
    @OneToMany(cascade = CascadeType.ALL,
            orphanRemoval = true)
    @JoinColumn(name = "suite_id")
    public List<SuiteItem> suiteItems;
}
