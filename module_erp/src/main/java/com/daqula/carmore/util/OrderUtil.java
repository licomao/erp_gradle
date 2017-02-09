package com.daqula.carmore.util;

import com.daqula.carmore.model.shop.Organization;
import com.daqula.carmore.model.shop.Shop;

/**
 * Created by mdc on 2015/9/16.
 */
public class OrderUtil {

    public final static String ORDER_TYPE_MATERIAL = "HC";   //耗材领用单
    public final static String ORDER_TYPE_SALE = "SG";   //销售单号
    public final static String ORDER_TYPE_PURCHASE = "CG";   //采购单
    public final static String ORDER_TYPE_REFUND = "TH";   //退货单号
    public final static String ORDER_TYPE_STOCK = "PD";   //库存盘点单
    public final static String ORDER_TYPE_TRANSFER = "DB";//库存调拨

    public static String getViewOrderNumber(Organization org,Shop shop ,String doType,Long number){
        String num = org.serialNum + shop.shopCode + doType + String.format("%05d", number);
        return num.toUpperCase();
    }


}
