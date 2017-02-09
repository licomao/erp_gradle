package com.daqula.carmore.util;

import org.joda.time.DateTime;

/**
 * Created by wenjie on 2016/7/28.
 */
public class ExcelExportCustomerPurchasedSuiteDetail {

    //姓名
    @ExcelVOAttribute(name = "客户姓名", column = "A", isExport = true)
    public String name;
    //手机
    @ExcelVOAttribute(name = "手机", column = "C", isExport = true)
    public String phone;
    //套餐名称
    @ExcelVOAttribute(name = "套餐名称", column = "B", isExport = true)
    public String suiteName;

    //开卡门店
    @ExcelVOAttribute(name = "开卡门店", column = "E", isExport = true)
    public String shopName;

    @ExcelVOAttribute(name = "有效状态", column = "D", isExport = true)
    public String status;
    @ExcelVOAttribute(name = "商品名称", column = "F", isExport = true)
    public String commodityName;
    @ExcelVOAttribute(name = "品牌名称", column = "G", isExport = true)
    public String brand;
    @ExcelVOAttribute(name = "商品描述", column = "H", isExport = true)
    public String description;
    @ExcelVOAttribute(name = "成本", column = "I", isExport = true)
    public Double ofee;
    @ExcelVOAttribute(name = "可使用次数", column = "L", isExport = true)
    public int usefulTime;
    @ExcelVOAttribute(name = "无限次", column = "K", isExport = true)
    public String isInfinite;
    @ExcelVOAttribute(name = "总次数", column = "J", isExport = true)
    public int total;


}
