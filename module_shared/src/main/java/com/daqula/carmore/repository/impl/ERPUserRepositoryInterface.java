package com.daqula.carmore.repository.impl;

import com.daqula.carmore.model.acl.ERPUser;
import com.daqula.carmore.model.shop.Organization;

import java.util.List;

/**
 * Created by swj on 2015/11/30.
 */
public interface ERPUserRepositoryInterface {

    ERPUser findByUsernameAndPasswordUseBCrypt(String username, String password);

    List<ERPUser> findByOrganizationAndFingerPrintNotNullWithNoCache(Organization organization);
}
