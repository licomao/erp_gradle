package com.daqula.carmore.repository.impl;

import com.daqula.carmore.model.shop.Organization;
import org.springframework.beans.factory.annotation.Autowired;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by Administrator on 2015/8/27.
 */
public class OrganizationRepositoryImpl implements OrganizationRepositoryInterface {
    @Autowired
    private EntityManager em;

    @Override
    public List<Organization> findOrgByNameLike(String name, int page, int row, String sord) {

        int max = page*row;
        Query q = em.createNativeQuery("SELECT * FROM organization WHERE `name`  LIKE '%"+name+"%'  order by id " + sord,Organization.class);
        q.setFirstResult(max - row);
        q.setMaxResults(max -1);
        List orgs = q.getResultList();

        return  orgs;
    }
}
