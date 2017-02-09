package com.daqula.carmore.util;

/**
 * Created by swj on 2015/10/26.
 */
public class ExcelExportStockModel {

    @ExcelVOAttribute(name = "商品名", column = "A", isExport = true)
    public String name;

    @ExcelVOAttribute(name = "品牌", column = "B", isExport = true)
    public String brandName;

    @ExcelVOAttribute(name = "条形码", column = "C", isExport = true)
    public String barCode;

    @ExcelVOAttribute(name = "顶级分类", column = "D", isExport = true)
    public String rootCategory;

    @ExcelVOAttribute(name = "成本(元)", column = "E", isExport = true)
    public Double stockCost;

    @ExcelVOAttribute(name = "库存数量", column = "F", isExport = true)
    public Integer oldNumber;

    @ExcelVOAttribute(name = "盘后数量", column = "G", isExport = true)
    public Integer calculateNumber;

    @ExcelVOAttribute(name = "盘后总价", column = "H", isExport = true)
    public Double afterNumber;
}
