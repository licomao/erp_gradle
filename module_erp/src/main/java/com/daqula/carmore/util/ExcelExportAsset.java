package com.daqula.carmore.util;

/**
 * Created by mdc on 2016/4/26.
 */
public class ExcelExportAsset {

    @ExcelVOAttribute(name = "固定资产名称", column = "A", isExport = true)
    public String name;

    @ExcelVOAttribute(name = "型号", column = "B", isExport = true)
    public String style;

    @ExcelVOAttribute(name = "单价", column = "C", isExport = true)
    public double price;

    @ExcelVOAttribute(name = "数量", column = "D", isExport = true)
    public int number;

    @ExcelVOAttribute(name = "合计", column = "E", isExport = true)
    public double sum;

    @ExcelVOAttribute(name = "使用状态", column = "F", isExport = true)
    public String status;

    @ExcelVOAttribute(name = "使用门店", column = "G", isExport = true)
    public String shopName;

}
