package com.daqula.carmore.repository.impl;

import com.daqula.carmore.model.acl.ERPUser;
import com.daqula.carmore.model.shop.Organization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Random;

/**
 * Created by swj on 2015/11/30.
 */
public class ERPUserRepositoryImpl implements ERPUserRepositoryInterface {

    @Autowired
    private EntityManager em;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public ERPUser findByUsernameAndPasswordUseBCrypt(String username, String password) {

//        bCryptPasswordEncoder = new BCryptPasswordEncoder();
        ERPUser erpUser = (ERPUser) em
                .createQuery("SELECT p FROM ERPUser p WHERE p.username = ?1 and p.deleted = false")
                .setParameter(1, username)
                .getSingleResult();
        if (erpUser != null) {
            if (!bCryptPasswordEncoder.matches(password, erpUser.password)) erpUser = null;
        }


        return erpUser;
    }

    @Override
    public List<ERPUser> findByOrganizationAndFingerPrintNotNullWithNoCache(Organization organization) {
        Random random = new Random();
        int ran = random.nextInt(10000);
        List<ERPUser> list = (List<ERPUser>)em.createQuery(
                String.format("SELECT p FROM ERPUser p where p.organization = ?1 and fingerPrint != ?2 and %s = %s", ran, ran))
            .setParameter(1, organization)
            .setParameter(2, "")
            .getResultList();
//        return null;
        return list;
    }
}
