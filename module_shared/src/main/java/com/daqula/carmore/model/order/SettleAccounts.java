package com.daqula.carmore.model.order;

import com.daqula.carmore.model.BaseEntity;
import com.daqula.carmore.model.shop.Shop;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;


@Entity
public class SettleAccounts extends BaseEntity {

    /** 判断是哪个店结算的 */
    @OneToOne
    public Shop shop;

    /** 结算金额 */
    @Column(nullable = false)
    public double amount;

    /** 关店日期 */
    @DateTimeFormat(pattern="yyyy/MM/dd")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    public DateTime calDate;
}
