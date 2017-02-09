package com.daqula.carmore.repository.impl;

import com.daqula.carmore.model.order.PurchaseOrder;
import com.daqula.carmore.model.order.PurchasePayment;

import java.util.List;

/**
 * 付款单Dao
 * Created by mdc on 2016/1/21.
 */
public interface PurchasePaymentRepositoryInterface {

    List<PurchaseOrder> findPurchasePayments(PurchaseOrder purchaseOrder,int page, int pageSize);

    int findPurchasePaymentsCount(PurchaseOrder purchaseOrder);

    double findUnspentBalanceByPurchaseOrder (PurchaseOrder purchaseOrder);
}
