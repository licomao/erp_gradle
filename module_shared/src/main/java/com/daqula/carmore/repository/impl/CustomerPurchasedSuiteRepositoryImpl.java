package com.daqula.carmore.repository.impl;

import com.daqula.carmore.model.shop.Shop;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * Created by swj on 2015/10/9.
 */
public class CustomerPurchasedSuiteRepositoryImpl implements CustomerPurchasedSuiteRepositoryInterface {

    @Autowired
    private EntityManager em;


    @Override
    public List<Object[]> findListByKeyWordAndShop(String keyWord, Shop shop, Long orgId,DateTime calDateStart,DateTime calDateEnd) {
        List<Object[]> list = em.createQuery(
                "SELECT cps.customer.mobile, " +
                        "cps.suite.suiteType, " +
                        "cps.startDate, " +
                        "cps.enabled, " +
                        "cps.shop.name, " +
                        "erp.realName, " +
                        "cps , " +
                        "cps.suite.name, " +
                        " su.expiation-(year(CURRENT_DATE())*365+month(CURRENT_DATE())*30+day(CURRENT_DATE())) +" +
                        "            (year(cps.startDate)*365+month(cps.startDate)*30+day(cps.startDate)), " +
                        "cps.staff.name, " +
                        "cps.suite.price, " +
                        "so.payment.cashAmount + so.payment.posAmount, " +
                        "cps.authButtonStr " +
                        "FROM CustomerPurchasedSuite cps, CustomerERPProfile erp, Suite su ,SettleOrder so " +
                        "WHERE cps.customer = erp.customer " +
                        "AND cps.settleOrderId = so.id " +
                        "AND cps.suite.id = su.id " +
                        "AND cps.shop = ?0 " +
                        "AND erp.organization.id = ?3 " +
                        "AND cps.createdDate >= ?4 " +
                        "AND cps.createdDate <= ?5 " +
                        "AND (erp.realName like ?1 OR cps.customer.mobile like ?2) ")
                .setParameter(0, shop)
                .setParameter(1, "%" + keyWord + "%")
                .setParameter(2, "%" + keyWord + "%")
                .setParameter(3, orgId)
                .setParameter(4, calDateStart)
                .setParameter(5, calDateEnd)
                .getResultList();

        return list;
    }

    @Override
    public List<Object[]> findListByKeyWordAndShop(String keyWord, Shop shop, Long orgId, int page, int rows, String sord, String sidx,DateTime calDateStart,DateTime calDateEnd) {
        List<Object[]> list = em.createQuery(
                "SELECT cps.customer.mobile, " +
                        "cps.suite.suiteType, " +
                        "cps.startDate, " +
                        "cps.enabled, " +
                        "cps.shop.name, " +
                        "erp.realName, " +
                        "cps.id, " +
                        "cps.suite.name, " +
                        " su.expiation-(year(CURRENT_DATE())*365+month(CURRENT_DATE())*30+day(CURRENT_DATE())) +" +
                        "            (year(cps.startDate)*365+month(cps.startDate)*30+day(cps.startDate)), " +
                        "cps.staff.name, " +
                        "cps.suite.price, " +
                        "so.payment.cashAmount + so.payment.posAmount, " +
                        "cps.authButtonStr, " +
                        "cps.remark " +
                        "FROM CustomerPurchasedSuite cps, CustomerERPProfile erp, Suite su ,SettleOrder so " +
                        "WHERE cps.customer = erp.customer " +
                        "AND cps.settleOrderId = so.id " +
                        "AND cps.suite.id = su.id " +
                        "AND cps.shop = ?0 " +
                        "AND erp.organization.id = ?3 " +
                        "AND cps.createdDate >= ?4 " +
                        "AND cps.createdDate <= ?5 " +
                        "AND (erp.realName like ?1 OR cps.customer.mobile like ?2) " +
                        "ORDER BY " + sidx + " " + sord)
                .setParameter(0, shop)
                .setParameter(1, "%" + keyWord + "%")
                .setParameter(2, "%" + keyWord + "%")
                .setParameter(3, orgId)
                .setParameter(4, calDateStart)
                .setParameter(5, calDateEnd)
                .setFirstResult((page - 1) * rows)
                .setMaxResults(rows)
                .getResultList();

        return list;
    }

    @Override
    public long findCountByKeyWordAndShop(String keyWord, Shop shop, Long orgId,DateTime calDateStart,DateTime calDateEnd) {
        long count = (long) em.createQuery("SELECT count(cps.id) " +
                /*"FROM CustomerPurchasedSuite cps, CustomerERPProfile erp " +
                "WHERE cps.customer = erp.customer " +
                "AND cps.shop = ?0 " +
                "AND erp.organization.id = ?3 " +
                "AND cps.createdDate >= ?4 " +
                "AND cps.createdDate <= ?5 " +
                "AND (erp.realName like ?1 OR cps.customer.mobile like ?2) ")*/
                "FROM CustomerPurchasedSuite cps, CustomerERPProfile erp, Suite su ,SettleOrder so " +
                "WHERE cps.customer = erp.customer " +
                "AND cps.settleOrderId = so.id " +
                "AND cps.suite.id = su.id " +
                "AND cps.shop = ?0 " +
                "AND erp.organization.id = ?3 " +
                "AND cps.createdDate >= ?4 " +
                "AND cps.createdDate <= ?5 " +
                "AND (erp.realName like ?1 OR cps.customer.mobile like ?2) ")
                        .setParameter(0, shop)
                        .setParameter(1, "%" + keyWord + "%")
                        .setParameter(2, "%" + keyWord + "%")
                        .setParameter(3, orgId)
                        .setParameter(4, calDateStart)
                        .setParameter(5, calDateEnd)
                        .getSingleResult();
                /*.setParameter(0, shop)
                .setParameter(1, "%" + keyWord + "%")
                .setParameter(2, "%" + keyWord + "%")
                .setParameter(3, orgId)
                .setParameter(4, calDateStart)
                .setParameter(5, calDateEnd)
                .getSingleResult();*/
        return count;
    }


}
