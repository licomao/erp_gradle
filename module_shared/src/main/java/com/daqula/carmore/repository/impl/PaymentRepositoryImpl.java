package com.daqula.carmore.repository.impl;

import com.daqula.carmore.model.order.Payment;
import com.daqula.carmore.model.shop.Shop;
import com.daqula.carmore.repository.PaymentRepository;
import com.mongodb.util.Hash;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.ManyToOne;
import javax.persistence.Query;
import java.util.*;

/**
 * Created by mdc on 2015/10/22.
 */
public class PaymentRepositoryImpl implements PaymentRepositoryInterface {

    @Autowired
    private EntityManager em;
    /**
     * 统计数据 需用NativeQuery
     * @param startTime
     * @param endTime
     * @param shop
     * @return
     */
    @Override
    public Payment calSaleInfo(DateTime startTime, DateTime endTime, Shop shop) {
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("SELECT IFNULL(sum(p.cash_Amount),0) as cash_Amount, IFNULL(sum(p.app_Amount),0) as app_Amount,IFNULL( sum(p.pos_Amount),0) as posAmount ")
                .append("from payment p left join settle_order so on so.payment_id = p.id ")
                .append(" where so.deleted = false and so.is_Finish = true ")
                .append(" and so.shop_id = ?1 ")
                .append(" and so.finish_date >= ?2 and so.finish_date < ?3 ");
        Query q = em.createNativeQuery(sqlBuffer.toString());
        q.setParameter(1,shop.id);
        q.setParameter(2,startTime.toString("yyyy-MM-dd"));
        q.setParameter(3,endTime.toString("yyyy-MM-dd"));
        List obj = q.getResultList();
        Object[] objects = (Object[])obj.get(0);
        Payment payment = new Payment();
        payment.cashAmount = (Double) objects[0];
        payment.appAmount = (Double) objects[1];
        payment.posAmount = (Double) objects[2];
        payment.amount = payment.cashAmount + payment.appAmount + payment.posAmount;
        return payment;
    }

    /**
     *
     * @param startTime
     * @param endTime
     * @param shop
     * @return
     */
    @Override
    public double getMaterialCal(DateTime startTime, DateTime endTime, Shop shop) {
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("select ifnull(sum(md.cost * md.number),0) as cost from material_order_detail md left join material_order mo on mo.id = md.material_order_id ")
                .append(" where mo.deleted = false ")
                .append(" and mo.shop_id = ?1 ")
                .append(" and (mo.updated_Date >= ?2 and mo.updated_Date < ?3) ");
        Query q = em.createNativeQuery(sqlBuffer.toString());
        q.setParameter(1,shop.id);
        q.setParameter(2,startTime.toString("yyyy-MM-dd"));
        q.setParameter(3,endTime.toString("yyyy-MM-dd"));
        double ret = (double)q.getSingleResult();
        return ret;
    }


}
