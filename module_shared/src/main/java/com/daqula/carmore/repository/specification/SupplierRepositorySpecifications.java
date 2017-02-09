package com.daqula.carmore.repository.specification;

import com.daqula.carmore.model.admin.Supplier;
import com.daqula.carmore.model.shop.Organization;
import org.springframework.data.jpa.domain.Specification;

/**
 * Created by mdc on 2015/10/13.
 */
public class SupplierRepositorySpecifications {

    public static Specification<Supplier> filterByOrganization(Organization organization) {
        return (root, query, cb) -> organization != null ? cb.equal(root.get("organization"), organization) : null;
    }

    public static Specification<Supplier> filterByName(String name) {
        return (root, query, cb) -> name != null ? cb.like(root.get("name"), name) : null;
    }
    public static Specification<Supplier> filterByDeleted(boolean deleted) {
        return (root, query, cb) -> cb.equal(root.get("deleted"), deleted);
    }
}
