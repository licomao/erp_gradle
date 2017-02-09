package com.daqula.carmore.repository.impl;

import com.daqula.carmore.model.admin.CareSuite;
import com.daqula.carmore.model.admin.CareSuiteGroupItem;
import com.daqula.carmore.model.shop.CareSuiteGroupItemPrice;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CareSuiteRepositoryImpl implements CareSuiteRepositoryInterface {

    @Autowired
    private EntityManager em;

    @Override
    public CareSuite recommendCareSuite(int mileage, int month, long orgId) {
        Query q = em.createQuery(
                "SELECT c FROM CareSuite AS c " +
                        "WHERE c.mileage >= ?1 " +
                        "OR c.month >= ?2 " +
                        "ORDER BY c.mileage ASC", CareSuite.class)
            .setParameter(1, mileage)
            .setParameter(2, month)
            .setMaxResults(1);

        CareSuite result = (CareSuite) q.getSingleResult();

        // Set overridden price
        List<CareSuiteGroupItem> items = new ArrayList<>();
        result.careSuiteGroups.forEach(careSuiteGroup ->
            careSuiteGroup.careSuiteGroupItems.forEach(items::add)
        );

        String itemIds = items.stream().map(item -> Long.toString(item.id)).collect(Collectors.joining(","));
        Query priceQuery = em.createQuery(
                "SELECT p FROM CareSuiteGroupItemPrice p " +
                        "WHERE p.organization.id = ?1 " +
                        "AND p.careSuiteGroupItem.id IN ("+itemIds+")")
                .setParameter(1, orgId);

        List<CareSuiteGroupItemPrice> prices = priceQuery.getResultList();
        prices.forEach(price -> {
            items.forEach(item -> {
                if (item == price.careSuiteGroupItem) item.suitePrice = price.overriddenPrice;
            });
        });

        return result;
    }



}