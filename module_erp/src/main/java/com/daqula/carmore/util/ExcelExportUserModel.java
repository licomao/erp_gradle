package com.daqula.carmore.util;

/**
 * Created by swj on 2015/10/26.
 */
public class ExcelExportUserModel {


    @ExcelVOAttribute(name = "姓名", column = "A", isExport = true)
    public String realname;

    @ExcelVOAttribute(name = "电话", column = "B", isExport = true)
    public String mobile;

    @ExcelVOAttribute(name = "车牌号", column = "C", isExport = true)
    public String carNum;

    @ExcelVOAttribute(name = "性别", column = "D", isExport = true)
    public String gender;

    @ExcelVOAttribute(name = "车型", column = "E", isExport = true)
    public String model;


}
