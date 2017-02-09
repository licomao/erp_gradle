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
 * 库存调拨单
 * Created by mdc on 2015/9/21.
 */
@Entity
public class StockTransferOrder extends BaseEntity {

    /** 调拨单号 */
    public long orderNumber;

    /**调拨单号显示与list*/
    public String orderNumberView;

    /** 调入盘点门店 */
    @ManyToOne
    public Shop inShop;

    /** 调出盘点门店 */
    @ManyToOne
    public Shop outShop;

    /** 调拨人 */
    @ManyToOne
    public ERPUser erpUser;

    /** 调拨日期 */
    @DateTimeFormat(pattern="yyyy/MM/dd")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    public DateTime transferDate;

    /** 入库日期 */
    @DateTimeFormat(pattern="yyyy/MM/dd")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    public DateTime stockDate;

    /** 调拨状态 申请中=0 ，已退回 = 1 在途中=2，已入库=3 */
    public int transferStatus;

    /** 备注 */
    public String remark;

    /** 盘点明细单 */
    @OneToMany(cascade = CascadeType.ALL,
            orphanRemoval = true)
    @JoinColumn(name = "stock_transfer_order_id")
    public List<StockTransferOrderDetail> stockTransferOrderDetails;
}
