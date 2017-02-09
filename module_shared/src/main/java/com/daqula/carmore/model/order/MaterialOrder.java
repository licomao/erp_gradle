package com.daqula.carmore.model.order;

import com.daqula.carmore.model.BaseEntity;
import com.daqula.carmore.model.acl.ERPUser;
import com.daqula.carmore.model.shop.Shop;

import javax.persistence.*;
import java.util.List;

/**
 * 耗材领用单
 * Created by mdc on 2015/9/11.
 */
@Entity
public class MaterialOrder extends BaseEntity {

    /** 领用单号 */
    public long orderNum;

    /** 备注 */
    public String remark;

    /** 领用门店 */
    @ManyToOne
    public Shop shop;

    /** 领用人 */
    @ManyToOne
    public ERPUser erpUser;

    /** 领用日期 */
    public String useDate;

    /** 领用单号Topage */
    public String orderNumView;

    /** 领用单明细 */
    @OneToMany(cascade = CascadeType.ALL,
            orphanRemoval = true)
    @JoinColumn(name = "material_order_id")
    public List<MaterialOrderDetail> materialOrderDetails;

}
