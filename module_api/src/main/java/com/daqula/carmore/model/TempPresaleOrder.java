package com.daqula.carmore.model;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import java.util.List;
import java.util.UUID;

/**
 * App临时预约订单
 */
@Entity
public class TempPresaleOrder extends BaseEntity {

    public static String ORDER_TYPE_CARE = "care";

    public static String ORDER_TYPE_SKU_ITEM = "accessory";

    public long shopId;

    public long customerId;

    public UUID vehicleInfoUid;

    public long skuId;

    public int skuCount;

    @ElementCollection
    public List<Long> careSuiteGroupItemIds;

    public long careSuiteId;

    public double price;

    public String orderType;

    public int saleCategory;

    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    public DateTime appointmentDate;
}
