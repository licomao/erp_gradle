package com.daqula.carmore.repository.impl;

import com.daqula.carmore.model.shop.Shop;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

/**
 * Created by mdc on 2015/9/26.
 */
public class StockingOrderRepositoryImpl implements StockingOrderRepositoryInterface {

    @Autowired
    private EntityManager em;
    /**
     * JPQL未找到实现类似IFNULL函数所以只能使用至nativeQuery
     * @param shop
     * @return
     */
    @Override
    public Date findLastStockingDate(Shop shop) {
        Query q = em.createNativeQuery("select IFNULL(created_date,current_timestamp()) from stocking_order where shop_id = ?1 AND deleted = false AND stocking_status = 1 order by created_date desc")
                .setMaxResults(1);
        q.setParameter(1,shop.id);
        List<Date> dateTime =  q.getResultList();
        if(dateTime.size() == 0) {
            return null;
        } else {
            return dateTime.get(0);
        }

    }
}
