package com.daqula.carmore.repository.impl;

import com.daqula.carmore.model.customer.CustomerAppProfile;
import com.daqula.carmore.model.customer.CustomerERPProfile;
import com.daqula.carmore.model.customer.CustomerProfile;
import com.daqula.carmore.model.shop.Organization;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import java.util.List;

public class CustomerProfileRepositoryImpl implements CustomerProfileRepositoryInterface {

    @Autowired
    private EntityManager em;

    @Override
    public CustomerProfile findByMobile(String mobile, Organization org) {
        List<CustomerProfile> allProfiles = em.createQuery(
                "SELECT p FROM CustomerProfile p WHERE p.customer.mobile = ?1 ")
                .setParameter(1, mobile)
                .getResultList();

        CustomerProfile erpProfile = null;
        for (CustomerProfile profile : allProfiles) {
            if (profile instanceof CustomerAppProfile) {
                // 如果App顾客已绑定该组织门店，则优先返回
                CustomerAppProfile appProfile = (CustomerAppProfile) profile;
                if (appProfile.bindingShop != null && appProfile.bindingShop.organization.id == org.id) {
                    return profile;
                }

            } else if (profile instanceof CustomerERPProfile) {
                // 否则取该组织下的顾客信息
                CustomerERPProfile tmpProfile = (CustomerERPProfile) profile;
                if (tmpProfile.organization.id == org.id) {
                    erpProfile = profile;
                    continue;
                }
            }
        }
        return erpProfile;
    }

    @Override
    public CustomerERPProfile findUsableERPProfileByMobileAndOrgId(String mobile, Long orgId) {
        List<CustomerERPProfile> profiles = em.createQuery(
                "SELECT p FROM CustomerERPProfile p " +
                "WHERE p.customer.mobile like ?1 " +
                "AND p.organization.id = ?2 " +
                "ORDER BY p.createdDate DESC")
                .setParameter(1, "%" + mobile +"%").setParameter(2, orgId).getResultList();

        for (CustomerERPProfile profile : profiles) {
//            if (profile.vehicles == null || profile.vehicles.size() <= 0) continue;
            return profile;
        }

        return null;
    }

    @Override
    public CustomerERPProfile findUsableERPProfileByMobile(String mobile) {
        List<CustomerERPProfile> profiles = em.createQuery(
                "SELECT p FROM CustomerERPProfile p " +
                        "WHERE p.customer.mobile = ?1 " +
                        "ORDER BY p.createdDate DESC")
                .setParameter(1, mobile).getResultList();

        for (CustomerERPProfile profile : profiles) {
            if (profile.vehicles == null || profile.vehicles.size() <= 0) continue;
            return profile;
        }

        return null;
    }
}
