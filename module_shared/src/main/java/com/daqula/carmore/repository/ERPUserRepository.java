package com.daqula.carmore.repository;

import com.daqula.carmore.model.acl.ERPUser;
import com.daqula.carmore.model.shop.Organization;
import com.daqula.carmore.repository.impl.ERPUserRepositoryInterface;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ERPUserRepository extends CrudRepository<ERPUser, Long>, JpaSpecificationExecutor, ERPUserRepositoryInterface {
    ERPUser findByUsernameAndDeleted(String username, boolean deleted);

    ERPUser findByUsername(String username);

    ERPUser findByUsernameAndPassword(String username, String password);

    List<ERPUser> findByOrganization(Organization organization);

    List<ERPUser> findByOrganizationAndFingerPrintNotNull(Organization organization);



}
