package com.daqula.carmore.repository.impl;

import com.daqula.carmore.model.customer.HistoryCareSuiteItem;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by mdc on 2016/2/4.
 */
public class HistoryCareSuiteItemRepositoryImpl implements HistoryCareSuiteItemRepositoryInterface{
    @Autowired
    private EntityManager em;

    @Override
    public List<HistoryCareSuiteItem> getItemsInfo(String cardNo) {
        StringBuilder sql = new StringBuilder("SELECT * from history_care_suite_item  ");
        sql.append(" WHERE card_no = ?1");
        Query q = em.createNativeQuery(sql.toString(),HistoryCareSuiteItem.class);
        q.setParameter(1, cardNo);
        List<HistoryCareSuiteItem> ret = q.getResultList();
        return ret;
    }
}
