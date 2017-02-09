package com.daqula.carmore.repository.impl;

import com.daqula.carmore.model.admin.Supplier;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by Administrator on 2015/9/11.
 */
public class SupplierRepositoryImpl implements SupplierRepositoryInterface {

    @Autowired
    private EntityManager em;

    @Override
    public List<Supplier> findSupplierByNameAndOrgid(String name, Long orgid ,int page, int row, String sord) {

        int max = page*row;
        StringBuilder sql = new StringBuilder("SELECT * FROM supplier s WHERE s.`name` LIKE '%"+name+"%'");
        if(orgid != 0)
        {
            sql.append(" and s.organization_id = " + orgid);
        }
        sql.append(" ORDER BY s.id " + sord);
        Query q = em.createNativeQuery( sql.toString(), Supplier.class);
        q.setFirstResult(max - row);
        q.setMaxResults(max - 1);
        List ret = q.getResultList();

        return ret;
    }

}
