package com.daqula.carmore.model.order;

import com.daqula.carmore.model.BaseEntity;
import com.daqula.carmore.model.acl.ERPUser;
import com.daqula.carmore.model.shop.Shop;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.List;

/**
 * 库存盘点单
 * Created by mdc on 2015/9/11.
 */
@Entity
public class StockingOrder extends BaseEntity {

    /** 盘点单号 */
    public long orderNumber;

    /** 盘点单号组合显示 */
    public String orderNumberView;

    /** 盘点门店 */
    @ManyToOne
    public Shop shop;

    /** 盘点人 */
    @ManyToOne
    public ERPUser erpUser;

    /** 盘点日期 */
    @DateTimeFormat(pattern="yyyy/MM/dd")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    public DateTime stockingDate;

    /** 盘点状态 未完成=0，已完成=1 */
    public int stockingStatus;

    /** 盘点明细单 */
    @OneToMany(cascade = CascadeType.ALL,
            orphanRemoval = true)
    @JoinColumn(name = "stocking_order_id")
    public List<StockingOrderDetail> stockingOrderDetails;
}
