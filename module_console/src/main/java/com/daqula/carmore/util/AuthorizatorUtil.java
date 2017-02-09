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
            checkBoxName += ",func1";
        }
        if((authorityMask & AuthorityConst.MANAGE_ORG_ACCOUNT) == AuthorityConst.MANAGE_ORG_ACCOUNT) {
            checkBoxName += ",func2";
        }
        if((authorityMask & AuthorityConst.MANAGE_ORG_FIXEDASSET) == AuthorityConst.MANAGE_ORG_FIXEDASSET) {
            checkBoxName += ",func11";
        }
        if((authorityMask & AuthorityConst.MANAGE_ORG_MATERIALORDER) == AuthorityConst.MANAGE_ORG_MATERIALORDER) {
            checkBoxName += ",func12";
        }
        if((authorityMask & AuthorityConst.MANAGE_ORG_CUSTOMSTOCKITEM) == AuthorityConst.MANAGE_ORG_CUSTOMSTOCKITEM) {
            checkBoxName += ",func13";
        }
        if((authorityMask & AuthorityConst.MANAGE_ORG_SUPPLIER) == AuthorityConst.MANAGE_ORG_SUPPLIER) {
            checkBoxName += ",func14";
        }
        if((authorityMask & AuthorityConst.MANAGE_ORG_CUSTOMER) == AuthorityConst.MANAGE_ORG_CUSTOMER) {
            checkBoxName += ",func15";
        }
        if((authorityMask & AuthorityConst.MANAGE_ORG_EXPENSE) == AuthorityConst.MANAGE_ORG_EXPENSE) {
            checkBoxName += ",func16";
        }
        if((authorityMask & AuthorityConst.MANAGE_ORG_STOCK) == AuthorityConst.MANAGE_ORG_STOCK) {
            checkBoxName += ",func17";
        }
        if((authorityMask & AuthorityConst.MANAGE_ORG_STOCKINGORDER) == AuthorityConst.MANAGE_ORG_STOCKINGORDER) {
            checkBoxName += ",func18";
        }
        if((authorityMask & AuthorityConst.MANAGE_ORG_STOCKTRANSFERORDER) == AuthorityConst.MANAGE_ORG_STOCKTRANSFERORDER) {
            checkBoxName += ",func19";
        }
        if((authorityMask & AuthorityConst.MANAGE_ORG_COLLAPSEORGS) != 0) {
            checkBoxName += ",bigFunc1";
        }
        if((authorityMask & AuthorityConst.MANAGE_ORG_COLLAPSEPT) != 0) {
            checkBoxName += ",bigFunc2";
        }

        checkBoxName += ",";

        return checkBoxName;
    }


}
