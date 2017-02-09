package com.daqula.carmore.repository.specification;

import com.daqula.carmore.model.shop.Organization;
import com.daqula.carmore.model.shop.Shop;
import org.springframework.data.jpa.domain.Specification;

/**
 * Created by mdc on 2015/10/13.
 */
public class ShopRepositorySpecifications {

    public static Specification<Shop> filteredByOrganization(Organization organization) {
        return (root, query, cb) -> organization != null ? cb.equal(root.get("organization"), organization) : null;
    }

    public static Specification<Shop> filteredByName(String name) {
        return (root, query, cb) -> name != null ? cb.equal(root.get("name"), name) : null;
    }

}
