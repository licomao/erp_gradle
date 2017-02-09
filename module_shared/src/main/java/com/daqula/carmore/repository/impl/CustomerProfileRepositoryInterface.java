package com.daqula.carmore.repository.impl;

import com.daqula.carmore.model.customer.CustomerERPProfile;
import com.daqula.carmore.model.customer.CustomerProfile;
import com.daqula.carmore.model.shop.Organization;
import com.daqula.carmore.model.template.VehicleModel;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CustomerProfileRepositoryInterface {

    CustomerProfile findByMobile(String mobile, Organization org);

    CustomerERPProfile findUsableERPProfileByMobileAndOrgId(String mobile, Long orgId);

    CustomerERPProfile findUsableERPProfileByMobile(String mobile);
}
