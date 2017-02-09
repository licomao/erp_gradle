package com.daqula.carmore.util;

import com.daqula.carmore.AuthorityConst;

/**
 * Created by Administrator on 2015/10/15 0015.
 */
public class AuthorizatorUtil {

    /**
     * 根据权限值，获取要勾选的checkbox名
     * @param authorityMask
     * @return
     */
    public static String GetAuthorityCheck(Long authorityMask) {
        String checkBoxName = "";




        if((authorityMask & AuthorityConst.MANAGE_ORG_ROLEAUTHORIZATION) == AuthorityConst.MANAGE_ORG_ROLEAUTHORIZATION) {
            checkBoxName += ",fu91";
        }
        if((authorityMask & AuthorityConst.MANAGE_ORG_FIXEDASSET) == AuthorityConst.MANAGE_ORG_FIXEDASSET) {
            checkBoxName += ",fu1";
        }
        if((authorityMask & AuthorityConst.MANAGE_ORG_MATERIALORDER) == AuthorityConst.MANAGE_ORG_MATERIALORDER) {
            checkBoxName += ",fu2";
        }
        if((authorityMask & AuthorityConst.MANAGE_ORG_ACCOUNT) == AuthorityConst.MANAGE_ORG_ACCOUNT) {
            checkBoxName += ",fu92";
        }
        if((authorityMask & AuthorityConst.MANAGE_ORG_CUSTOMSTOCKITEM) == AuthorityConst.MANAGE_ORG_CUSTOMSTOCKITEM) {
            checkBoxName += ",fu4";
        }
        if((authorityMask & AuthorityConst.MANAGE_ORG_SECONDARYITEM) == AuthorityConst.MANAGE_ORG_SECONDARYITEM) {
            checkBoxName += ",fu94";
        }
        if((authorityMask & AuthorityConst.MANAGE_ORG_SHOPMANAGE) == AuthorityConst.MANAGE_ORG_SHOPMANAGE) {
            checkBoxName += ",fu93";
        }
        if((authorityMask & AuthorityConst.MANAGE_ORG_SUPPLIER) == AuthorityConst.MANAGE_ORG_SUPPLIER) {
            checkBoxName += ",fu3";
        }
        if((authorityMask & AuthorityConst.MANAGE_ORG_NOTICE) == AuthorityConst.MANAGE_ORG_NOTICE) {
            checkBoxName += ",fu95";
        }
        if((authorityMask & AuthorityConst.MANAGE_ORG_CUSTOMER) == AuthorityConst.MANAGE_ORG_CUSTOMER) {
            checkBoxName += ",fu5";
        }
        if((authorityMask & AuthorityConst.MANAGE_ORG_EXPENSE) == AuthorityConst.MANAGE_ORG_EXPENSE) {
            checkBoxName += ",fu6";
        }
        if((authorityMask & AuthorityConst.MANAGE_ORG_STOCK) == AuthorityConst.MANAGE_ORG_STOCK) {
            checkBoxName += ",fu11";
        }
        if((authorityMask & AuthorityConst.MANAGE_ORG_STOCKINGORDER) == AuthorityConst.MANAGE_ORG_STOCKINGORDER) {
            checkBoxName += ",fu12";
        }
        if((authorityMask & AuthorityConst.MANAGE_ORG_STOCKTRANSFERORDER) == AuthorityConst.MANAGE_ORG_STOCKTRANSFERORDER) {
            checkBoxName += ",fu13";
        }
        if((authorityMask & AuthorityConst.MANAGE_ORG_BASESET) == AuthorityConst.MANAGE_ORG_BASESET) {
            checkBoxName += ",fu21";
        }
        if((authorityMask & AuthorityConst.MANAGE_ORG_BASESETMONTHLY) == AuthorityConst.MANAGE_ORG_BASESETMONTHLY) {
            checkBoxName += ",fu22";
        }
        if((authorityMask & AuthorityConst.MANAGE_ORG_PURCHASEORDER) == AuthorityConst.MANAGE_ORG_PURCHASEORDER) {
            checkBoxName += ",fu31";
        }
        if((authorityMask & AuthorityConst.MANAGE_ORG_PURCHASEAPPROVE) == AuthorityConst.MANAGE_ORG_PURCHASEAPPROVE) {
            checkBoxName += ",fu32";
        }
        if((authorityMask & AuthorityConst.MANAGE_ORG_ADDSTORAGE) == AuthorityConst.MANAGE_ORG_ADDSTORAGE) {
            checkBoxName += ",fu33";
        }
        if((authorityMask & AuthorityConst.MANAGE_ORG_PURCHASEPAYMENT) == AuthorityConst.MANAGE_ORG_PURCHASEPAYMENT) {
            checkBoxName += ",fu34";
        }
        if((authorityMask & AuthorityConst.MANAGE_ORG_PAYMENTQUERY) == AuthorityConst.MANAGE_ORG_PAYMENTQUERY) {
            checkBoxName += ",fu35";
        }
        if((authorityMask & AuthorityConst.MANAGE_ORG_PAYMENTQUERY_EDIT) == AuthorityConst.MANAGE_ORG_PAYMENTQUERY_EDIT) {
            checkBoxName += ",fu36";
        }
        if((authorityMask & AuthorityConst.MANAGE_ORG_REFUNDORDER) == AuthorityConst.MANAGE_ORG_REFUNDORDER) {
            checkBoxName += ",fu41";
        }
        if((authorityMask & AuthorityConst.MANAGE_ORG_REFUNDAPPROVE) == AuthorityConst.MANAGE_ORG_REFUNDAPPROVE) {
            checkBoxName += ",fu42";
        }
        if((authorityMask & AuthorityConst.MANAGE_ORG_CUSTOMERPURCHASESUITE) == AuthorityConst.MANAGE_ORG_CUSTOMERPURCHASESUITE) {
            checkBoxName += ",fu51";
        }
        if((authorityMask & AuthorityConst.MANAGE_ORG_CUSTOMERPURCHASESUITEREMOTE) == AuthorityConst.MANAGE_ORG_CUSTOMERPURCHASESUITEREMOTE) {
            checkBoxName += ",fu52";
        }
        if((authorityMask & AuthorityConst.MANAGE_ORG_CUSTOMERPURCHASESUITEVIPCARD) == AuthorityConst.MANAGE_ORG_CUSTOMERPURCHASESUITEVIPCARD) {
            checkBoxName += ",fu53";
        }
        if((authorityMask & AuthorityConst.MANAGE_ORG_STAFF) == AuthorityConst.MANAGE_ORG_STAFF) {
            checkBoxName += ",fu61";
        }
        if((authorityMask & AuthorityConst.MANAGE_ORG_JOB) == AuthorityConst.MANAGE_ORG_JOB) {
            checkBoxName += ",fu62";
        }
        if((authorityMask & AuthorityConst.MANAGE_ORG_STAFFATTENDANCE) == AuthorityConst.MANAGE_ORG_STAFFATTENDANCE) {
            checkBoxName += ",fu63";
        }
        if((authorityMask & AuthorityConst.MANAGE_ORG_DOSTAFFATTENDANCE) == AuthorityConst.MANAGE_ORG_DOSTAFFATTENDANCE) {
            checkBoxName += ",fu64";
        }
        if((authorityMask & AuthorityConst.MANAGE_ORG_FINGERATTENDANCE) == AuthorityConst.MANAGE_ORG_FINGERATTENDANCE) {
            checkBoxName += ",fu65";
        }
        if((authorityMask & AuthorityConst.MANAGE_ORG_DISCOUNTAPPROVE) == AuthorityConst.MANAGE_ORG_DISCOUNTAPPROVE) {
            checkBoxName += ",fu71";
        }
        if((authorityMask & AuthorityConst.MANAGER_ORG_STAFF_DELETE) == AuthorityConst.MANAGER_ORG_STAFF_DELETE){
            checkBoxName += ",fu72";
        }
        if((authorityMask & AuthorityConst.MANAGE_ORG_PAYMENTPRE) == AuthorityConst.MANAGE_ORG_PAYMENTPRE) {
            checkBoxName += ",fu81";
        }
        if((authorityMask & AuthorityConst.MANAGE_ORG_SEARCHCUSTOMINFO) == AuthorityConst.MANAGE_ORG_SEARCHCUSTOMINFO) {
            checkBoxName += ",fu82";
        }
        if((authorityMask & AuthorityConst.MANAGE_ORG_SEARCHSETTLEINFO) == AuthorityConst.MANAGE_ORG_SEARCHSETTLEINFO) {
            checkBoxName += ",fu83";
        }
        if((authorityMask & AuthorityConst.MANAGE_ORG_CALDAYSALEPRICE) == AuthorityConst.MANAGE_ORG_CALDAYSALEPRICE) {
            checkBoxName += ",fu84";
        }
        if((authorityMask & AuthorityConst.MANAGE_ORG_SEARCHORDERDEATIAL) == AuthorityConst.MANAGE_ORG_SEARCHORDERDEATIAL) {
            checkBoxName += ",fu85";
        }
        if((authorityMask & AuthorityConst.MANAGE_ORG_COLLAPSESHOP) != 0) {
            checkBoxName += ",bigfu1";
        }
        if((authorityMask & AuthorityConst.MANAGE_ORG_COLLAPSESTOCK) != 0) {
            checkBoxName += ",bigfu2";
        }
        if((authorityMask & AuthorityConst.MANAGE_ORG_COLLAPSEBASE) != 0) {
            checkBoxName += ",bigfu3";
        }
        if((authorityMask & AuthorityConst.MANAGE_ORG_COLLAPSEPURCHASE) != 0) {
            checkBoxName += ",bigfu4";
        }
        if((authorityMask & AuthorityConst.MANAGE_ORG_COLLAPSEREFUNDORDER) != 0) {
            checkBoxName += ",bigfu5";
        }
        if((authorityMask & AuthorityConst.MANAGE_ORG_COLLAPSECUSTOMERPURCHASEDSUITE) != 0) {
            checkBoxName += ",bigfu6";
        }
        if((authorityMask & AuthorityConst.MANAGE_ORG_COLLAPSESTAFF) != 0) {
            checkBoxName += ",bigfu7";
        }
        if((authorityMask & AuthorityConst.MANAGE_ORG_SPECIAL) != 0) {
            checkBoxName += ",bigfu8";
        }
        if((authorityMask & AuthorityConst.MANAGE_ORG_COLLAPSEPAYMENT) != 0) {
            checkBoxName += ",bigfu9";
        }
        if((authorityMask & AuthorityConst.MANAGE_ORG_COLLAPSEORG) != 0) {
            checkBoxName += ",bigfu10";
        }
        checkBoxName += ",";

        return checkBoxName;
    }


}
