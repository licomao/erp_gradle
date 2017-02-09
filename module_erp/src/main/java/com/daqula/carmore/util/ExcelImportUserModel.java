package com.daqula.carmore.util;

/**
 * Created by swj on 2015/10/15.
 */
public class ExcelImportUserModel {

    @ExcelVOAttribute(name = "序号", column = "A")
    public int id;

    @ExcelVOAttribute(name = "姓名", column = "C", isExport = true)
    public String realname;

    @ExcelVOAttribute(name = "电话", column = "B", isExport = true)
    public String mobile;

    @Override
    public String toString() {

        return "ExcelImportUserModel [id=" + id + ", name=" + realname + ", mobile=" + mobile + "]";
    }
}
