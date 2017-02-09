package com.daqula.carmore.repository.specification;

import com.daqula.carmore.model.order.PurchaseOrder;
import com.daqula.carmore.model.order.SettleOrderHistory;
import com.daqula.carmore.model.shop.Organization;
import com.daqula.carmore.model.shop.Shop;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by swj on 2015/10/13.
 */
public class SettleOrderHistoryRepositorySpecifications {
    public static Specification<SettleOrderHistory> filterOrganization(Organization organization) {
        return (root, query, cb) -> organization != null ? cb.equal(root.get("belongShop").get("organization"), organization) : null;
    }

    public static Specification<SettleOrderHistory> filterHasShop(Shop shop) {
        return (root, query, cb) -> shop != null ? cb.equal(root.get("belongShop"), shop) : null;
    }

    public static Specification<SettleOrderHistory> filterHasKeyWord(String keyWord) {
       /* return (root, query, cb) -> (keyWord != null && keyWord!= "")
                ? cb.or(cb.like(root.get("settleOrder").get("saleNoView"), "%" + keyWord + "%")
                , cb.or(cb.like(root.get("settleOrder").get("customer").get("mobile"), "%" + keyWord + "%"))
//                , cb.or(cb.like(root.get("settleOrder").get("vehicleInfo").get("plateNumber"), "%" + keyWord + "%" ))
//                    , cb.or(cb.like(root.get("settleOrder").get("customer").get("mobile"), "%" + keyWord + "%" ))
        ) : null;*/

        return (root, query, cb) -> {
//            (keyWord != null && keyWord!= "") ?
//                    cb.or(root.get("inShop")., root.get("outShop").in(keyWord)) : null;
            if (keyWord != null && keyWord != "") {

                Predicate p = cb.or(cb.like(root.get("settleOrder").get("saleNoView"), "%" + keyWord + "%")
                        , cb.or(cb.like(root.get("settleOrder").get("customer").get("mobile"), "%" + keyWord + "%"))
//                    , cb.or(cb.like(root.get("settleOrder").get("vehicleInfo").get("plateNumber"), "%" + keyWord + "%" ))
//                    , cb.or(cb.like(root.get("settleOrder").get("customer").get("mobile"), "%" + keyWord + "%" ))
                );
//                p = cb.or(p, cb.or(cb.like(root.get("settleOrder").get("vehicleInfo").get("plateNumber"), "%" + keyWord + "%" )));
                return p;
            }

            return null;
        };
    }

   /* public static Specification<PurchaseOrder> filterHasPurchaseDateEnd(Date purchaseDateEnd) {
        return (root, query, cb) -> purchaseDateEnd != null ? cb.lessThan(root.get("createdDate"), purchaseDateEnd) : null;
    }*/

    public static Specification<SettleOrderHistory> filterDeleteStatus() {
        return (root, query, cb) -> cb.notEqual(root.get("deleted"), true);
    }

}
