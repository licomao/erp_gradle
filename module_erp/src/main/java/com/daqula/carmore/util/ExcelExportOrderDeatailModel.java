package com.daqula.carmore.util;

/**
 * Created by chenxin on 2015/11/30.
 */
public class ExcelExportOrderDeatailModel {


    @ExcelVOAttribute(name = "品名", column = "A", isExport = true)
    public String realname;

    @ExcelVOAttribute(name = "品牌", column = "B", isExport = true)
    public String brandName;

    @ExcelVOAttribute(name = "成本价(元)", column = "C", isExport = true)
    public double cost;

    @ExcelVOAttribute(name = "数量", column = "D", isExport = true)
    public int count;

    @ExcelVOAttribute(name = "实际售价(元)", column = "E", isExport = true)
    public double receivable;

    @ExcelVOAttribute(name = "施工人员", column = "F", isExport = true)
    public String staffName;
}
