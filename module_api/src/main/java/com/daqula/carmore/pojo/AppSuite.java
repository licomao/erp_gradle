package com.daqula.carmore.pojo;

import com.daqula.carmore.model.admin.SpecifyItem;
import com.daqula.carmore.model.admin.Suite;
import com.daqula.carmore.model.customer.CustomerPurchasedSuite;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class AppSuite {

    /** 服务套餐 */
    public static final int SUITE_TYPE_SERVICES = 0;
    /** 会员卡套餐 */
    public static final int SUITE_TYPE_VIP = 1;

    public long suiteId;

    public long purchasedSuiteId;

    /** 名称 */
    public String name;

    /** 套餐类型 */
    public int suiteType;

    /** 套餐描述 */
    public String description;

    /** 套餐价格 */
    public double price;

    /** 有效期天数 */
    public int expiation;

    /** 套餐项目 */
    public List<AppSuiteItem> suiteItems;

    /** 套餐开始时间。如果不为空则表示是已购买套餐 */
    public DateTime startDate;

    public static AppSuite build(Suite suite) {
        AppSuite appSuite = new AppSuite();
        List<AppSuiteItem> appSuiteItems = new ArrayList<>();
        appSuite.suiteId = suite.id;
        appSuite.name = suite.name;
        appSuite.description = suite.description;
        appSuite.expiation = suite.expiation;
        appSuite.price = suite.price;
        appSuite.suiteType = suite.suiteType;
        appSuite.suiteItems = appSuiteItems;
        suite.suiteItems.forEach(suiteItem -> {
            AppSuiteItem appSuiteItem = new AppSuiteItem();
            appSuiteItem.suiteItemId = suiteItem.id;
            appSuiteItem.times = suiteItem.times;
            appSuiteItem.timesLeft = suiteItem.timesLeft;
            appSuiteItem.usedTimes = suiteItem.usedTimes;
            appSuiteItem.skuItem = suiteItem.skuItem instanceof SpecifyItem
                ? ((SpecifyItem) suiteItem.skuItem).stockItem
                : suiteItem.skuItem;
            appSuiteItems.add(appSuiteItem);
        });
        return appSuite;
    }

    public static List<AppSuite> build(List<Suite> suiteList) {
        List<AppSuite> appSuiteList = new ArrayList<>();
        suiteList.forEach(suite -> appSuiteList.add(build(suite)));
        return appSuiteList;
    }

    public static AppSuite build(CustomerPurchasedSuite purchasedSuite) {
        AppSuite appSuite = new AppSuite();
        List<AppSuiteItem> appSuiteItems = new ArrayList<>();
        appSuite.suiteId = purchasedSuite.suite.id;
        appSuite.purchasedSuiteId = purchasedSuite.id;
        appSuite.name = purchasedSuite.suite.name;
        appSuite.description = purchasedSuite.suite.description;
        appSuite.expiation = purchasedSuite.suite.expiation;
        appSuite.price = purchasedSuite.suite.price;
        //appSuite.saleCategory = purchasedSuite.purchasedSuite.saleCategory;
        appSuite.startDate = purchasedSuite.startDate;
        appSuite.suiteType = purchasedSuite.suite.suiteType;
        appSuite.suiteItems = appSuiteItems;
        purchasedSuite.purchasedSuiteItems.forEach(purchasedSuiteItem -> {
            AppSuiteItem appSuiteItem = new AppSuiteItem();
            appSuiteItem.purchasedSuiteItemId = purchasedSuiteItem;
            appSuiteItem.times = purchasedSuiteItem.times;
            appSuiteItem.timesLeft = purchasedSuiteItem.getTimesLeft();
            appSuiteItem.usedTimes = purchasedSuiteItem.usedTimes;
            appSuiteItem.skuItem = purchasedSuiteItem.customStockItem instanceof SpecifyItem
                    ? ((SpecifyItem) purchasedSuiteItem.customStockItem).stockItem
                    : purchasedSuiteItem.customStockItem;
            appSuiteItems.add(appSuiteItem);
        });
        return appSuite;
    }
}
