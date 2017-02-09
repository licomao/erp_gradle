package com.daqula.carmore.util;

import org.joda.time.DateTime;

/**
 * Created by wenjie on 2016/7/28.
 */
public class ExcelExportCustomerPurchasedSuite {

    //姓名
    @ExcelVOAttribute(name = "客户姓名", column = "A", isExport = true)
    public String name;
    //手机
    @ExcelVOAttribute(name = "手机", column = "D", isExport = true)
    public String phone;
    //套餐名称
    @ExcelVOAttribute(name = "套餐名称", column = "B", isExport = true)
    public String suiteName;

    //套餐售价
    @ExcelVOAttribute(name = "套餐定价", column = "C", isExport = true)
    public Double suiteFee;
    //开卡门店
    @ExcelVOAttribute(name = "开卡门店", column = "H", isExport = true)
    public String shopName;
    //开卡日期
    @ExcelVOAttribute(name = "办卡日期", column = "E", isExport = true)
    public DateTime startDate;
    //剩余天数
    @ExcelVOAttribute(name = "剩余天数", column = "F", isExport = true)
    public String listDay;
    //有效状态
    @ExcelVOAttribute(name = "有效状态", column = "G", isExport = true)
    public String status;
    //售卡人员
    @ExcelVOAttribute(name = "售卡人员", column = "I", isExport = true)
    public String saleName;
    //实际售价
    @ExcelVOAttribute(name = "实际售价", column = "J", isExport = true)
    public Double realFee;
    //折扣授权人
    @ExcelVOAttribute(name = "折扣授权人", column = "K", isExport = true)
    public String offName;




}
