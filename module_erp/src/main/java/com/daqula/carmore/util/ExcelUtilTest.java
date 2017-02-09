package com.daqula.carmore.util;

import com.daqula.carmore.model.customer.Customer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * excel工具类 测试类
 *
 * Created by swj on 2015/10/15.
 */
public class ExcelUtilTest {


    //导入测试
    public static void main(String[] args) {
       /* FileInputStream fis = null;
        try {

            fis = new FileInputStream("d:\\success3.xlsx");
            ExcelUtil<ExcelImportUserModel> util = new ExcelUtil<ExcelImportUserModel>(
                    ExcelImportUserModel.class);// 创建excel工具类
            List<ExcelImportUserModel> list = util.importExcel("ERP用户信息", fis, "xlsx");// 导入
            for (ExcelImportUserModel excelImportUserModel : list) {
                System.out.println(excelImportUserModel);
            }

//            System.out.println(list);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }*/
        // 初始化数据
        List<ExcelImportUserModel> list = new ArrayList<ExcelImportUserModel>();

        ExcelImportUserModel vo = new ExcelImportUserModel();
        vo.id = 1;
        vo.realname = "测试1";
        vo.mobile = "1321151321";

        list.add(vo);

        ExcelImportUserModel vo2 = new ExcelImportUserModel();
        vo2.id = 2;
        vo2.realname = "测试2";
        vo2.mobile = "11232131";
        list.add(vo2);

        ExcelImportUserModel vo3 = new ExcelImportUserModel();
        vo3.id = 1;
        vo3.realname = "测试3";
        vo3.mobile = "12345123412";
        list.add(vo3);

        FileOutputStream out = null;
        try {
            out = new FileOutputStream("d:\\export2.xls");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ExcelUtil<ExcelImportUserModel> util = new ExcelUtil<ExcelImportUserModel>(ExcelImportUserModel.class);// 创建工具类.
        util.exportExcel(list, "学生信息", 65536, out);// 导出
        System.out.println("----执行完毕----------");
    }


    public void exportTest(){
        // 初始化数据
        List<ExcelImportUserModel> list = new ArrayList<ExcelImportUserModel>();

        ExcelImportUserModel vo = new ExcelImportUserModel();
        vo.id = 1;
        vo.realname = "测试1";
        vo.mobile = "1321151321";

        list.add(vo);

        ExcelImportUserModel vo2 = new ExcelImportUserModel();
        vo2.id = 2;
        vo2.realname = "测试2";
        vo2.mobile = "11232131";
        list.add(vo2);

        ExcelImportUserModel vo3 = new ExcelImportUserModel();
        vo3.id = 1;
        vo3.realname = "测试3";
        vo3.mobile = "12345123412";
        list.add(vo3);

        FileOutputStream out = null;
        try {
            out = new FileOutputStream("d:\\export2.xlsx");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ExcelUtil<ExcelImportUserModel> util = new ExcelUtil<ExcelImportUserModel>(ExcelImportUserModel.class);// 创建工具类.
        util.exportExcel(list, "学生信息", 65536, out);// 导出
        System.out.println("----执行完毕----------");
    }

}
