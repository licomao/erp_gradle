package com.daqula.carmore.repository.impl;

import com.daqula.carmore.model.order.SettleAccounts;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * Created by Administrator on 2015/8/25.
 */
public class SettleAccountsRepositoryImpl implements SettleAccountsRepositoryInterface{

    @Autowired
    private EntityManager em;

    @Override
    public List<SettleAccounts> queryTodaySettleRecord(Long shopId)
    {
        Query q= em.createNativeQuery("SELECT * FROM settle_accounts WHERE TO_DAYS(created_date) = TO_DAYS(NOW()) and shop_id = (?1)" , SettleAccounts.class);
        q.setParameter(1, shopId);
        List<SettleAccounts> r = q.getResultList();
        return r;
    }

    @Override
    public double qureryTotalAmountsByShopId(Long shopId) {
        double ret = 0.0;
        Query q= em.createNativeQuery("SELECT SUM(c.amount) as amount from (SELECT b.amount FROM settle_order a ,payment b WHERE a.created_date > IFNULL((SELECT created_date FROM settle_accounts WHERE shop_id = (?1) ORDER BY created_date DESC LIMIT 1),'') and a.shop_id = (?2) and a.payment_id = b.id) as c;");
        q.setParameter(1, shopId);
        q.setParameter(2, shopId);
        List result = q.getResultList();
        if(result != null && result.size() > 0)
        {
              ret = Double.parseDouble(result.get(0).toString());
        }
        return ret;
    }

}
