package com.daqula.carmore.repository.impl;

import com.daqula.carmore.model.shop.Organization;
import java.util.List;

/**
 * Created by Administrator on 2015/8/27.
 */
public interface OrganizationRepositoryInterface {

    List<Organization> findOrgByNameLike(String name, int page, int row, String sord); //根据组织名模糊查询

}
