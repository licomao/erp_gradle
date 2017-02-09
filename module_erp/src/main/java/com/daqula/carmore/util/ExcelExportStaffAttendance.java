package com.daqula.carmore.util;

import org.joda.time.DateTime;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * Created by swj on 2015/10/26.
 */
public class ExcelExportStaffAttendance {

    @ExcelVOAttribute(name = "员工姓名", column = "A", isExport = true)
    public String staffName;

    @ExcelVOAttribute(name = "电话", column = "B", isExport = true)
    public String mobile;

    @ExcelVOAttribute(name = "所属门店", column = "C", isExport = true)
    public String shopName;

    @ExcelVOAttribute(name = "职位(工种)", column = "D", isExport = true)
    public String jobName;

    @ExcelVOAttribute(name = "上班日期", column = "E", isExport = true)
    public String workDate;

    @ExcelVOAttribute(name = "上班时间", column = "F", isExport = true)
    public String startTime;

    @ExcelVOAttribute(name = "下班时间", column = "G", isExport = true)
    public String endTime;


}
