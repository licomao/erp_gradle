package com.daqula.carmore.repository.impl;

import com.daqula.carmore.model.announcement.ErpAnnouncement;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by Administrator on 2015/9/7.
 */
public class ErpAnnouncementRepositoryImpl implements ErpAnnouncementRepositoryInterface {

    @Autowired
    private EntityManager em;

    @Override
    public List<ErpAnnouncement> findAnnounceByTittleAndUnameLike(String tittle, String publisher ,int page, int row, String sord) {
        int max = page*row;

        StringBuilder sql = new StringBuilder("SELECT * from erp_announcement ");
        sql.append("WHERE tittle LIKE '%"+tittle+"%' AND publisher  LIKE '%"+publisher+"%'");
        sql.append(" ORDER BY id ").append(sord);

        Query q = em.createNativeQuery(sql.toString(),ErpAnnouncement.class);
        q.setFirstResult(max - row);
        q.setMaxResults(max - 1);
        List ret = q.getResultList();

        return ret;
    }

}
