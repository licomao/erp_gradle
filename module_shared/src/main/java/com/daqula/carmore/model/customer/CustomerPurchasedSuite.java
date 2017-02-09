package com.daqula.carmore.model.customer;

import com.daqula.carmore.annotation.ApiJsonIgnore;
import com.daqula.carmore.model.BaseEntity;
import com.daqula.carmore.model.admin.Suite;
import com.daqula.carmore.model.admin.SuiteItem;
import com.daqula.carmore.model.shop.Shop;
import com.daqula.carmore.model.shop.Staff;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.PeriodType;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

/**
 * 顾客已购买套餐
 */
@Entity
public class CustomerPurchasedSuite extends BaseEntity {

    /** 在哪个门店购买的套餐。同组织下的不同门店可以通用，但结算流程按"异地消费"走。 */
    @ManyToOne
    @ApiJsonIgnore
    public Shop shop;

    /** 购买了本套餐的顾客 */
    @ManyToOne(optional = false)
    @ApiJsonIgnore
    public Customer customer;

    /** 购买了哪个套餐 */
    @ManyToOne(optional = false)
    public Suite suite;

    /** 套餐开始日期 */
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    public DateTime startDate;

    /** 套餐项目划扣使用详情 */
    @OneToMany(cascade = CascadeType.ALL,
                orphanRemoval = true,
                mappedBy = "purchasedSuite")
    public List<CustomerPurchasedSuiteItem> purchasedSuiteItems;

    /** 该套餐是否已停用 */
    public boolean enabled;

    /** 销售套餐人员  **/
    @ManyToOne
    public Staff staff;

    /**折扣授权人**/
    public String authButtonStr;

    /** 备注 **/
    public String remark;

//    @OneToOne(fetch = FetchType.LAZY, optional = false)
//    @JsonIgnore
//    public SettleOrder settleOrder;

    public Long settleOrderId;


    public int  getLastDay(){
        DateTime dateTime = DateTime.now();
        Period p = new Period(this.createdDate,dateTime, PeriodType.days());//最后一个参数如果不写的话，下面的返回值将会是错误的。
        return p.getDays();
    }

    //**************************************************************************
    // Domain Methods
    //**************************************************************************

    public boolean isExpired() {
        return startDate.plusDays(suite.expiation).isBeforeNow();
    }

    //**************************************************************************
    // Builder
    //**************************************************************************

    public static CustomerPurchasedSuite build(Customer customer, Suite suite, Shop shop) {
        CustomerPurchasedSuite purchasedSuite = new CustomerPurchasedSuite();
        purchasedSuite.customer = customer;
        purchasedSuite.startDate = DateTime.now().withTimeAtStartOfDay();
        purchasedSuite.suite = suite;
        purchasedSuite.shop = shop;
        purchasedSuite.purchasedSuiteItems = new ArrayList<>();
        for (SuiteItem suiteItem : suite.suiteItems) {
            CustomerPurchasedSuiteItem customerPurchasedSuiteItem = new CustomerPurchasedSuiteItem();
            customerPurchasedSuiteItem.purchasedSuite = purchasedSuite;
            customerPurchasedSuiteItem.suiteItem = suiteItem;
            customerPurchasedSuiteItem.customStockItem = suiteItem.skuItem;
            customerPurchasedSuiteItem.cost = suiteItem.cost;
            customerPurchasedSuiteItem.saleCategory = suiteItem.saleCategory;
            customerPurchasedSuiteItem.times = suiteItem.times;
            customerPurchasedSuiteItem.usedTimes = 0;
            purchasedSuite.purchasedSuiteItems.add(customerPurchasedSuiteItem);
        }
        return purchasedSuite;
    }

}
