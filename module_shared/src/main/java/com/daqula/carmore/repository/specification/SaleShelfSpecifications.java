package com.daqula.carmore.repository.specification;

import com.daqula.carmore.model.shop.Organization;
import com.daqula.carmore.model.shop.SaleShelf;
import org.springframework.data.jpa.domain.Specification;

/**
 * Created by chenxin on 2015/11/2 0002.
 */
public class SaleShelfSpecifications {

    public static Specification<SaleShelf> filterBySaleCategory (Integer saleCategory) {
        return (root, query, cb) -> saleCategory != null ? cb.equal(root.get("saleCategory"), saleCategory) : null;
    }

    public static Specification<SaleShelf> filterByOrganization (Organization organization) {
        return (root, query, cb) -> organization != null ? cb.equal(root.get("organization"), organization) : null;
    }

}


