package com.daqula.carmore.repository.impl;

import com.daqula.carmore.model.order.SettleOrderHistory;
import com.daqula.carmore.model.shop.Organization;
import com.daqula.carmore.model.shop.Shop;
import com.daqula.carmore.util.CollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.*;

/**
 * Created by swj on 2015/10/13.
 */
public class SettleOrderHistoryRepositoryImpl implements SettleOrderHistoryRepositoryInterface {

    @Autowired
    private EntityManager em;


    @Override
    public List<SettleOrderHistory> findListByOrganizationAndKeyWordAndShop(Organization organization, String keyWord, Shop shop, int page, int rows) {

        StringBuffer buffer = new StringBuffer("SELECT s FROM SettleOrderHistory s WHERE s.belongShop.organization = :organization ");
        Map<String, Object> map = new HashMap<>();
        map.put("organization", organization);
        if (shop != null) {
            buffer.append("AND s.shop = :shop ");
            map.put("shop", shop);
        }
        if (keyWord != null && keyWord != "") {
            buffer.append("AND (s.settleOrder.saleNoView LIKE :keyWord OR s.settleOrder.customer.mobile LIKE :keyWord )");
            map.put("keyWord", "%" + keyWord + "%");
        }
        Query query = em.createQuery(buffer.toString());
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            query.setParameter(entry.getKey(),entry.getValue());
        }
        List<SettleOrderHistory> resultList = query.setFirstResult((page - 1) * rows)
                .setMaxResults(rows)
                .getResultList();



        return resultList;
    }

    @Override
    public long findCountByOrganizationAndKeyWordAndShop(Organization organization, String keyWord, Shop shop) {
        StringBuffer buffer = new StringBuffer("SELECT count(s.id) FROM SettleOrderHistory s WHERE s.belongShop.organization = :organization ");
        Map<String, Object> map = new HashMap<>();
        map.put("organization", organization);
        if (shop != null) {
            buffer.append("AND s.shop = :shop ");
            map.put("shop", shop);
        }
        if (keyWord != null && keyWord != "") {
            buffer.append("AND (s.settleOrder.saleNoView LIKE :keyWord OR s.settleOrder.customer.mobile LIKE :keyWord )");
            map.put("keyWord", "%" + keyWord + "%");
        }
        Query query = em.createQuery(buffer.toString());

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            query.setParameter(entry.getKey(),entry.getValue());
        }

        long total = (long) query.getSingleResult();
        return total;
        /*long count = (long) em.createQuery("SELECT count(cps.id) " +
                "FROM CustomerPurchasedSuite cps, CustomerERPProfile erp " +
                "WHERE cps.customer = erp.customer " +
                "AND cps.shop = ?0 " +
                "AND (erp.realName like ?1 OR cps.customer.mobile like ?2) ")
                .setParameter(0, shop)
                .setParameter(1, "%" + keyWord + "%")
                .setParameter(2, "%" + keyWord + "%")
                .getSingleResult();
        return count;*/
    }/* @Override
    public List<SettleOrderHistory> findListByOrganizationAndKeyWordAndShop(Organization organization, String keyWord, Shop shop, int page, int rows) {

        StringBuffer buffer = new StringBuffer("SELECT s FROM SettleOrderHistory s WHERE s.belongShop.organization = :organization ");
        Map<String, Object> map = new HashMap<>();
        map.put("organization", organization);
        if (shop != null) {
            buffer.append("AND s.shop = :shop ");
            map.put("shop", shop);
        }
        if (keyWord != null && keyWord != "") {
            buffer.append("AND (s.settleOrder.saleNoView LIKE :keyWord OR s.settleOrder.customer.mobile LIKE :keyWord )");
            map.put("keyWord", "%" + keyWord + "%");
        }
        Query query = em.createQuery(buffer.toString());
//        Set<Map.Entry<String, Object>> entries = map.entrySet();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            query.setParameter(entry.getKey(),entry.getValue());
        }
        List<SettleOrderHistory> resultList = query.setFirstResult((page - 1) * rows)
                .setMaxResults(rows)
                .getResultList();



        return resultList;
    }

    @Override
    public long findCountByOrganizationAndKeyWordAndShop(Organization organization, String keyWord, Shop shop) {
        StringBuffer buffer = new StringBuffer("SELECT count(s.id) FROM SettleOrderHistory s WHERE s.belongShop.organization = :organization ");
        Map<String, Object> map = new HashMap<>();
        map.put("organization", organization);
        if (shop != null) {
            buffer.append("AND s.shop = :shop ");
            map.put("shop", shop);
        }
        if (keyWord != null && keyWord != "") {
            buffer.append("AND (s.settleOrder.saleNoView LIKE :keyWord OR s.settleOrder.customer.mobile LIKE :keyWord )");
            map.put("keyWord", "%" + keyWord + "%");
        }
        Query query = em.createQuery(buffer.toString());

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            query.setParameter(entry.getKey(),entry.getValue());
        }

        long total = (long) query.getSingleResult();
        return total;
        *//*long count = (long) em.createQuery("SELECT count(cps.id) " +
                "FROM CustomerPurchasedSuite cps, CustomerERPProfile erp " +
                "WHERE cps.customer = erp.customer " +
                "AND cps.shop = ?0 " +
                "AND (erp.realName like ?1 OR cps.customer.mobile like ?2) ")
                .setParameter(0, shop)
                .setParameter(1, "%" + keyWord + "%")
                .setParameter(2, "%" + keyWord + "%")
                .getSingleResult();
        return count;*//*
    }*/
}
