package com.daqula.carmore.repository.impl;

import com.daqula.carmore.model.order.PurchaseOrder;
import com.daqula.carmore.model.order.PurchasePayment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by mdc on 2016/1/26.
 */
public class PurchasePaymentRepositoryImpl implements PurchasePaymentRepositoryInterface {
    @Autowired
    private EntityManager em;

    @Override
    public List<PurchaseOrder> findPurchasePayments(PurchaseOrder purchaseOrder, int page, int pageSize) {
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append(" select po.id ")
                .append(" , po.created_by ")
                .append(" , po.created_date ")
                .append(" , po.deleted ")
                .append(" , po.uid ")
                .append(" , po.updated_by ")
                .append(" , po.updated_date ")
                .append(" , po.ver ")
                .append(" , po.apply_person ")
                .append(" , po.in_stock_person ")
                .append(" , po.order_number ")
                .append(" , po.order_number_view ")
                .append(" , po.order_status ")
                .append(" , po.purchase_type ")
                .append(" , po.remark ")
                .append(" , po.review_person ")
                .append(" , po.sale_no ")
                .append(" , po.purchase_shop_id ")
                .append(" , po.sale_shop_id ")
                .append(" , po.supplier_id ")
        .append(" from purchase_order po ")
                .append(" left join (select purchase_order_id,sum(price*number) as price  from purchase_order_detail group by purchase_order_id) pod on pod.purchase_order_id = po.id")
                .append(" left join (select purchase_order_id,(sum(payment) + sum(deduction_Payment)) as price from purchase_payment where deleted = false group by purchase_order_id ) pp on po.id= pp.purchase_order_id ")
                .append(" where po.order_status = 3  and po.deleted=false and pod.price > IFNULL(pp.price,0)")
                .append(" and po.purchase_shop_id = ?1");
        if (purchaseOrder.supplier != null) {
            sqlBuffer.append(" and po.supplier_id = ?2");
        }
        if (!StringUtils.isEmpty(purchaseOrder.orderNumberView)){
            sqlBuffer.append(" and po.order_number_view like ?3");
        }
        if (purchaseOrder.purchaseType != 99){
            sqlBuffer.append(" and po.purchase_type = ?4");
        }
        sqlBuffer.append(" order by po.created_date desc");
        Query q = em.createNativeQuery(sqlBuffer.toString(), PurchaseOrder.class).setMaxResults(pageSize)
                .setFirstResult((page - 1) * pageSize);
        q.setParameter(1, purchaseOrder.purchaseShop.id);
        if (purchaseOrder.supplier != null) {
            q.setParameter(2, purchaseOrder.supplier.id);
        }
        if (!StringUtils.isEmpty(purchaseOrder.orderNumberView)){
            q.setParameter(3, "%" + purchaseOrder.orderNumberView + "%");
        }
        if (purchaseOrder.purchaseType != 99){
            q.setParameter(4, purchaseOrder.purchaseType);
        }
        List<PurchaseOrder> resultList = q.getResultList();
        return resultList;
    }

    @Override
    public int findPurchasePaymentsCount(PurchaseOrder purchaseOrder) {
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append(" select  count(*) ")
                .append(" from purchase_order po ")
                .append(" left join (select purchase_order_id,sum(price*number) as price  from purchase_order_detail group by purchase_order_id) pod on pod.purchase_order_id = po.id")
                .append(" left join (select purchase_order_id,(sum(payment) + sum(deduction_Payment)) as price from purchase_payment where deleted = false group by purchase_order_id ) pp on po.id= pp.purchase_order_id ")
                .append(" where po.order_status = 3  and po.deleted=false and pod.price > IFNULL(pp.price,0)")
                .append(" and po.purchase_shop_id = ?1");
        if (purchaseOrder.supplier != null) {
            sqlBuffer.append(" and po.supplier_id = ?2");
        }
        if (!StringUtils.isEmpty(purchaseOrder.orderNumberView)){
            sqlBuffer.append(" and po.order_number_view like ?3");
        }
        if (purchaseOrder.purchaseType != 99){
            sqlBuffer.append(" and po.purchase_type = ?4");
        }
        sqlBuffer.append(" order by po.created_date desc");
        Query q = em.createNativeQuery(sqlBuffer.toString());
        q.setParameter(1, purchaseOrder.purchaseShop.id);
        if (purchaseOrder.supplier != null) {
            q.setParameter(2, purchaseOrder.supplier.id);
        }
        if (!StringUtils.isEmpty(purchaseOrder.orderNumberView)){
            q.setParameter(3, "%" + purchaseOrder.orderNumberView + "%");
        }
        if (purchaseOrder.purchaseType != 99){
            q.setParameter(4, purchaseOrder.purchaseType);
        }
        int totalCounts = Integer.parseInt(q.getSingleResult().toString());
        return totalCounts;
    }

    @Override
    public double findUnspentBalanceByPurchaseOrder(PurchaseOrder purchaseOrder) {
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("select IfNULL(sum(payment) + sum(deduction_Payment),0) as price from purchase_payment where deleted = false ")
                .append(" and purchase_order_id = ?1");
        Query q = em.createNativeQuery(sqlBuffer.toString());
        q.setParameter(1, purchaseOrder.id);
        Double unspentBalance = Double.parseDouble(q.getSingleResult().toString());
        return unspentBalance;
    }
}
