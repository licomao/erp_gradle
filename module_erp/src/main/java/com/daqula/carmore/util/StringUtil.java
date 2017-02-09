package com.daqula.carmore.util;

import org.jadira.usertype.spi.utils.lang.StringUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import org.apache.commons.lang3.StringUtils;

/**
 * 字符串相关类
 * 
 * 2007/04/04 添加getBASE64/getFromBASE64两个方法
 * 
 * @since
 */
public class StringUtil {
    public static final String INDEX_JSP = "index.html";

    private static final String YEAR = "年";

    private static final String MONTH = "月";

    private static final String DAY = "日";

    private static final String NBSP = "&nbsp;";

    private static final String NULL = "";

    // private static final String NONE = "(无)";

    private static final String ISO8859_1 = "ISO8859_1";

    private static final String GBK = "GBK";

    public static Boolean IsNullOrEmpty(String value) {
        Boolean retValue = true;
        if (value == null)
            return retValue;

        if ("".equals(value.trim()))
            return retValue;

        retValue = false;
        return retValue;
    }

    public static String IsNullOrEmpty(String orgValue, String defalutValue) {
        if (IsNullOrEmpty(orgValue))
            return defalutValue;

        return orgValue;
    }

    public static BigDecimal add(BigDecimal a, BigDecimal b) {
        BigDecimal c = BigDecimal.valueOf(0);
        try {
            c = a.add(b);
        } catch (Exception e) {
        }
        return c;
    }

    /*
     * 返回字符串的数值加i的字符串,如add('2', 1)='3'
     */
    public static String add(String s, int i) {
        String sResult = String.valueOf(Integer.parseInt(s.trim()) + i);
        return sResult;
    }

    public static String add(String s, String i) {
        String sResult = String.valueOf(Integer.parseInt(s.trim()) + Integer.parseInt(i.trim()));
        return sResult;
    }

    public static String trim(String s) {
        return s.trim();
    }

    public static String toUpper(String s) {
        return s.toUpperCase();
    }

    public static String toLower(String s) {
        return s.toLowerCase();
    }

    public static String toShortPrivate(String account) {
        if (account.length() == 12)
            return account.substring(0, 9);
        else
            return account;
    }

    public static String toLongPrivate(String account) {
        if (account != null && account.length() == 9)
            return account + "205";
        else
            return account;
    }

    public static String privateToExtra(String account) {
        if (account == null || NULL.equals(account))
            return account;
        String sExtraAccount = "209" + account.substring(0, 8);
        int i = Integer.parseInt(account.substring(8, 9)) + 1;
        i %= 10;
        sExtraAccount = sExtraAccount + i;
        return sExtraAccount;
    }

    public static String privateToAllowance1(String account) {
        if (account == null || NULL.equals(account))
            return account;
        String sAllowance = account.substring(0, 11) + "1";
        return sAllowance;
    }

    public static String privateToAllowance2(String account) {
        if (account == null || NULL.equals(account))
            return account;
        String sAllowance = account.substring(0, 11) + "2";
        return sAllowance;
    }

    private static String format_inner(String date, String def, int start) {
        String str = def;
        if (date == null)
            return str;
        date = date.trim();
        try {
            Integer.parseInt(date);
        } catch (Exception e) {
            return str;
        }
        if (date.length() == 4)
            str = date.substring(0, 2) + MONTH + date.substring(2) + DAY;
        else if (date.length() == 6)
            str = date.substring(start, 4) + YEAR + date.substring(4) + MONTH;
        else if (date.length() == 8)
            str = date.substring(start, 4) + YEAR + date.substring(4, 6) + MONTH + date.substring(6) + DAY;
        return str;
    }

    public static String format(String date) {
        return toISO(format_inner(date, NBSP, 0));
    }

    public static String format_sms(String date) {
        return format_inner(date, NULL, 2);
    }

    public static String nullToBlank(String str) {
        return str == null ? NBSP : str;
    }

    public static String dealNull(String str, String defaultStr) {
        return str == null ? defaultStr : str;
    }

    public static String toGBK(String str) {
        return toGBK(str, GBK);
    }

    public static String toGBK(String str, String encoding) {
        String gbkStr = NULL;
        try {
            gbkStr = new String(str.getBytes(ISO8859_1), encoding);
        } catch (Exception e) {
        }
        return gbkStr;
    }

    public static String toISO(String str) {
        return toISO(str, GBK);
    }

    public static String changeString(String str, String originEncoding, String targetEncoding) {
        String returnVal = NULL;
        try {
            returnVal = new String(str.getBytes(), targetEncoding);
        } catch (Exception e) {
            returnVal = "";
            System.out.println(e.getMessage());
        }
        return returnVal;

    }

    public static String toUTF8(String str) {
        String utf8Str = NULL;
        try {
            utf8Str = new String(str.getBytes("gb2312"), "UTF-8");
        } catch (Exception ex) {
            utf8Str = ex.getMessage();
        }
        return utf8Str;
    }

    public static String toISO(String str, String encoding) {
        String isoStr = NULL;
        try {
            isoStr = new String(str.getBytes(encoding), ISO8859_1);
        } catch (Exception e) {
        }
        return isoStr;
    }


    /**
     *  根据关键字分割字符串
     * 
     */
    public static ArrayList<String> splitByKeyWord(String source, String keyWord) {
        ArrayList<String> resultList = new ArrayList<String>();

        String sourceString = source == null ? "" : source.trim();
        String key = keyWord == null ? "" : keyWord.trim();
        int startpos = 0;
        int resultpos = 0;

        resultpos = sourceString.indexOf(key, startpos);

        while (resultpos > -1) {
            resultList.add(sourceString.substring(startpos, resultpos));
            startpos = resultpos + keyWord.length();
            resultpos = sourceString.indexOf(key, startpos);
        }

        if (startpos < sourceString.length())
            resultList.add(sourceString.substring(startpos, sourceString.length()));
        return resultList;
    }

    public static String getRandomNumCode(int codeLength) {
        Random r = new Random(new Date().getTime());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < codeLength; i++) {
            sb.append(r.nextInt(9));
        }
        return sb.toString();
    }

    public static StringBuilder appendNewLine(StringBuilder sb, String value) {
        sb = sb == null ? new StringBuilder() : sb;
        if (!StringUtil.IsNullOrEmpty(value))
            sb.append(value);
        return sb.append("\r\n");
    }

    public static StringBuilder appendNewLine(StringBuilder sb) {
        return StringUtil.appendNewLine(sb, "");
    }

    public static StringBuffer appendNewLine(StringBuffer sb, String value) {
        sb = sb == null ? new StringBuffer() : sb;
        if (!StringUtil.IsNullOrEmpty(value))
            sb.append(value);
        return sb.append("\r\n");
    }

    public static StringBuffer appendNewLine(StringBuffer sb) {
        return StringUtil.appendNewLine(sb, "");
    }

    public static boolean CheckIDCard(String idcard) {
        String errorInfo = "";// 记录错误信息
        String[] ValCodeArr = { "1", "0", "x", "9", "8", "7", "6", "5", "4", "3", "2" };
        String[] Wi = { "7", "9", "10", "5", "8", "4", "2", "1", "6", "3", "7", "9", "10", "5", "8", "4", "2" };
        // String[] Checker = {"1","9","8","7","6","5","4","3","2","1","1"};
        String Ai = "";

        // ================ 号码的长度 15位或18位 ================
        if (idcard.length() != 15 && idcard.length() != 18) {
            errorInfo = "号码长度应该为15位或18位。";
            System.out.println(errorInfo);
            return false;
        }
        // =======================(end)========================

        // ================ 数字 除最后以为都为数字 ================
        if (idcard.length() == 18) {
            Ai = idcard.substring(0, 17);
        } else if (idcard.length() == 15) {
            Ai = idcard.substring(0, 6) + "19" + idcard.substring(6, 15);
        }

        if (IsNumeric(Ai) == false) {
            errorInfo = "15位号码都应为数字 ; 18位号码除最后一位外，都应为数字。";
            System.out.println(errorInfo);
            return false;
        }
        // =======================(end)========================

        // ================ 出生年月是否有效 ================
        String strYear = Ai.substring(6, 10);// 年份
        String strMonth = Ai.substring(10, 12);// 月份
        String strDay = Ai.substring(12, 14);// 月份

        if (IsDate(strYear + "-" + strMonth + "-" + strDay) == false) {
            errorInfo = "生日无效。";
            System.out.println(errorInfo);
            return false;
        }

        GregorianCalendar gc = new GregorianCalendar();
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");

        try {
            if ((gc.get(Calendar.YEAR) - Integer.parseInt(strYear)) > 150
                    || (gc.getTime().getTime() - s.parse(strYear + "-" + strMonth + "-" + strDay).getTime()) < 0) {
                errorInfo = "生日不在有效范围。";
                System.out.println(errorInfo);
                return false;
            }
        } catch (Exception ex) {
            errorInfo = "生日不在有效范围。" + ex.getMessage();
            System.out.println(errorInfo);
            return false;
        }

        if (Integer.parseInt(strMonth) > 12 || Integer.parseInt(strMonth) == 0) {
            errorInfo = "月份无效";
            System.out.println(errorInfo);
            return false;
        }
        if (Integer.parseInt(strDay) > 31 || Integer.parseInt(strDay) == 0) {
            errorInfo = "日期无效";
            System.out.println(errorInfo);
            return false;
        }
        // =====================(end)=====================

        // ================ 地区码时候有效 ================
        Hashtable<String, String> h = StringUtil.GetAreaCode();
        if (h.get(Ai.substring(0, 2)) == null) {
            errorInfo = "地区编码错误。";
            System.out.println(errorInfo);
            return false;
        }
        // ==============================================

        // ================ 判断最后一位的值 ================
        int TotalmulAiWi = 0;
        for (int i = 0; i < 17; i++) {
            TotalmulAiWi = TotalmulAiWi + Integer.parseInt(String.valueOf(Ai.charAt(i))) * Integer.parseInt(Wi[i]);
        }
        int modValue = TotalmulAiWi % 11;
        String strVerifyCode = ValCodeArr[modValue];
        Ai = Ai + strVerifyCode;

        if (idcard.length() == 18) {
            if (Ai.equals(idcard.toLowerCase()) == false) {
                errorInfo = "身份证无效，最后一位字母错误";
                System.out.println(errorInfo);
                return false;
            }
        } else {
            // System.out.println("所在地区:" + h.get(Ai.substring(0,
            // 2).toString()));
            // System.out.println("新身份证号:" + Ai);
            return true;
        }
        // =====================(end)=====================
        // System.out.println("所在地区:" + h.get(Ai.substring(0, 2).toString()));
        return true;

    }

    /**
     * ====================================================================== 功能：设置地区编码
     * 
     * @return Hashtable<String, String> 对象
     */
    private static Hashtable<String, String> GetAreaCode() {
        Hashtable<String, String> hashtable = new Hashtable<String, String>();
        hashtable.put("11", "北京");
        hashtable.put("12", "天津");
        hashtable.put("13", "河北");
        hashtable.put("14", "山西");
        hashtable.put("15", "内蒙古");
        hashtable.put("21", "辽宁");
        hashtable.put("22", "吉林");
        hashtable.put("23", "黑龙江");
        hashtable.put("31", "上海");
        hashtable.put("32", "江苏");
        hashtable.put("33", "浙江");
        hashtable.put("34", "安徽");
        hashtable.put("35", "福建");
        hashtable.put("36", "江西");
        hashtable.put("37", "山东");
        hashtable.put("41", "河南");
        hashtable.put("42", "湖北");
        hashtable.put("43", "湖南");
        hashtable.put("44", "广东");
        hashtable.put("45", "广西");
        hashtable.put("46", "海南");
        hashtable.put("50", "重庆");
        hashtable.put("51", "四川");
        hashtable.put("52", "贵州");
        hashtable.put("53", "云南");
        hashtable.put("54", "西藏");
        hashtable.put("61", "陕西");
        hashtable.put("62", "甘肃");
        hashtable.put("63", "青海");
        hashtable.put("64", "宁夏");
        hashtable.put("65", "新疆");
        hashtable.put("71", "台湾");
        hashtable.put("81", "香港");
        hashtable.put("82", "澳门");
        hashtable.put("91", "国外");
        return hashtable;
    }

    /**
     * ====================================================================== 功能：判断字符串是否为数字
     * 
     * @param str
     * @return
     */
    private static boolean IsNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (isNum.matches()) {
            return true;
        } else {
            return false;
        }
        /*
         * 判断一个字符时候为数字 if(Character.isDigit(str.charAt(0))) { return true; } else { return false; }
         */
    }

    /**
     * 判断是否为手机号码
     * @param str
     * @return
     */
    public static boolean isMobile(String str) {
        if(IsNumeric(str)){
            if (str.length() == 11) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * 判断是否为邮箱格式
     * @param str
     * @return
     */
    public static boolean isEmail(String str) {
        Pattern p = Pattern.compile("^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(\\.([a-zA-Z0-9_-])+)+$");
        Matcher m = p.matcher(str);
        //Mather m = p.matcher("wangxu198709@gmail.com.cn");这种也是可以的！
        return m.matches();
    }

    /**
     * ====================================================================== 功能：判断字符串是否为日期格式
     * 
     * @param strDate
     * @return
     */
    public static boolean IsDate(String strDate) {
        Pattern pattern = Pattern
                .compile("^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$");
        Matcher m = pattern.matcher(strDate);
        if (m.matches()) {
            return true;
        } else {
            return false;
        }
    }


    public static int GetSex(String idcard) {
        String sex = "";
        int retvalue = 1;
        if (idcard.length() == 18)// 处理18位的身份证号码从号码中得到生日和性别代码
        {

            sex = idcard.substring(14, 17);
        }

        if (idcard.length() == 15) {
            sex = idcard.substring(12, 15);
        }

        if (Integer.valueOf(sex) % 2 == 0)// 性别代码为偶数是女性奇数为男性
        {
            retvalue = 2;
        } else {
            retvalue = 1;
        }

        return retvalue;
    }

    /** 大写数字 */
    private static final String[] NUMBERS = { "零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖" };
    /** 整数部分的单位 */
    private static final String[] IUNIT = { "元", "拾", "佰", "仟", "万", "拾", "佰", "仟", "亿", "拾", "佰", "仟", "万", "拾", "佰",
            "仟" };
    /** 小数部分的单位 */
    private static final String[] DUNIT = { "角", "分", "厘" };

    public static String GetChineseMoney(BigDecimal money) {
        String str = money.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
        str = str.replaceAll(",", "");// 去掉","
        String integerStr = "";// 整数部分数字
        String decimalStr = "";// 小数部分数字

        if (money.compareTo(new BigDecimal("0")) == 0)
            return "零";

        if (str.indexOf("-") >= 0) {
            if (str.indexOf(".") > 0) {
                integerStr = str.substring(1, str.indexOf("."));
                decimalStr = str.substring(str.indexOf(".") + 1);
            } else if (str.indexOf(".") == 0) {
                integerStr = "";
                decimalStr = str.substring(1);
            } else {
                integerStr = str;
                decimalStr = "";
            }
        } else {
            if (str.indexOf(".") > 0) {
                integerStr = str.substring(0, str.indexOf("."));
                decimalStr = str.substring(str.indexOf(".") + 1);
            } else if (str.indexOf(".") == 0) {
                integerStr = "";
                decimalStr = str.substring(1);
            } else {
                integerStr = str;
                decimalStr = "";
            }
        }
        // integerStr去掉首0，不必去掉decimalStr的尾0(超出部分舍去)
        if (!integerStr.equals("")) {
            integerStr = Long.toString(Long.parseLong(integerStr));
            if (integerStr.equals("0")) {
                integerStr = "";
            }
        }
        // overflow超出处理能力，直接返回
        if (integerStr.length() > IUNIT.length) {
            System.out.println(str + ":超出处理能力");
            return str;
        }

        int[] integers = toArray(integerStr);// 整数部分数字
        boolean isMust5 = isMust5(integerStr);// 设置万单位
        int[] decimals = toArray(decimalStr);// 小数部分数字

        String retvalue = getChineseInteger(integers, isMust5) + getChineseDecimal(decimals);
        if (retvalue.substring(retvalue.length() - 1).equalsIgnoreCase("元"))
            retvalue = retvalue.substring(0, retvalue.length() - 1);
        if (str.indexOf("-") >= 0) {
            retvalue = "负" + retvalue;
        }
        return retvalue;
    }

    /**
     * 整数部分和小数部分转换为数组，从高位至低位
     */
    private static int[] toArray(String number) {
        int[] array = new int[number.length()];
        for (int i = 0; i < number.length(); i++) {
            array[i] = Integer.parseInt(number.substring(i, i + 1));
        }
        return array;
    }

    /**
     * 得到中文金额的整数部分。
     */
    private static String getChineseInteger(int[] integers, boolean isMust5) {
        StringBuffer chineseInteger = new StringBuffer("");
        int length = integers.length;
        for (int i = 0; i < length; i++) {
            // 0出现在关键位置：1234(万)5678(亿)9012(万)3456(元)
            // 特殊情况：10(拾元、壹拾元、壹拾万元、拾万元)
            String key = "";
            if (integers[i] == 0) {
                if ((length - i) == 13)// 万(亿)(必填)
                    key = IUNIT[4];
                else if ((length - i) == 9)// 亿(必填)
                    key = IUNIT[8];
                else if ((length - i) == 5 && isMust5)// 万(不必填)
                    key = IUNIT[4];
                else if ((length - i) == 1)// 元(必填)
                    key = IUNIT[0];// 不要元
                // 0遇非0时补零，不包含最后一位
                if ((length - i) > 1 && integers[i + 1] != 0)
                    key += NUMBERS[0];
            }
            chineseInteger.append(integers[i] == 0 ? key : (NUMBERS[integers[i]] + IUNIT[length - i - 1]));
        }
        return chineseInteger.toString();
    }

    /**
     * 得到中文金额的小数部分。
     */
    private static String getChineseDecimal(int[] decimals) {
        StringBuffer chineseDecimal = new StringBuffer("");
        for (int i = 0; i < decimals.length; i++) {
            // 舍去2位小数之后的
            if (i < 2) {
                chineseDecimal.append(NUMBERS[decimals[i]] + DUNIT[i]);
            } else
                break;
            // chineseDecimal.append(NUMBERS[decimals[i]] + DUNIT[i]);

        }
        String str = chineseDecimal.toString();
        if (str.compareToIgnoreCase("零角零分") == 0)
            str = "整";

        if (str.startsWith("零角"))
            str = str.replaceAll("零角", "零");

        if (str.endsWith("零分"))
            str = str.replaceAll("零分", "");
        return str;
    }

    /**
     * 判断第5位数字的单位"万"是否应加。
     */
    private static boolean isMust5(String integerStr) {
        int length = integerStr.length();
        if (length > 4) {
            String subInteger = "";
            if (length > 8) {
                // 取得从低位数，第5到第8位的字串
                subInteger = integerStr.substring(length - 8, length - 4);
            } else {
                subInteger = integerStr.substring(0, length - 4);
            }
            return Integer.parseInt(subInteger) > 0;
        } else {
            return false;
        }
    }

    public static String removeLastZeroBehindDot(String number) {
        int dotIndex = number.indexOf(".");
        if (dotIndex == 0)
            number = "0" + number;
        if (dotIndex > 0) {
            while (number.lastIndexOf("0") > number.length() - 2) {
                // System.out.println(number);
                number = number.substring(0, number.lastIndexOf("0"));
            }
        }
        if (dotIndex == number.length() - 1)
            number += "0";
        return number;
    }

    /**
     * 获取5位随机号
     * 
     * @param bigDecimal
     * @return
     */
    public static String getFlowOrder(BigDecimal bigDecimal) {

        String pattern = "000";

        DecimalFormat df = new DecimalFormat(pattern);

        return df.format(bigDecimal.longValue());
    }



    /**
     * 去除重复的string
     * 
     * @param array
     * @return
     */
    public static ArrayList<String> ditinctArray(ArrayList<String> array) {
        ArrayList<String> l = new ArrayList<String>();
        for (String a : array) {
            if (!l.contains(a)) {
                l.add(a);
            }
        }
        // System.out.println(l);
        return l;
    }

    public static String nullToString(String str) {
        if (StringUtils.isNotEmpty(str)) {
            return str;
        } else {
            return "";
        }
    }

}
