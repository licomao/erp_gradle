package com.daqula.carmore.repository.specification;

import com.daqula.carmore.model.admin.Agency;
import com.daqula.carmore.model.shop.Organization;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;

/**
 * Created by mdc on 2015/10/19.
 */
public class OrganizationRepositorySpecifications {

    public static Specification<Organization> filterByAgency(Agency agency) {
        return (root, query, cb) ->
            agency != null ? cb.equal(root.get("agency"), agency) : null;
    }
    public static Specification<Organization> filterByName(String name) {
        return (root, query, cb) -> name != null ? cb.like(root.get("name"), name) : null;
    }
    public static Specification<Organization> filterByDeleted(boolean deleted) {
        return (root, query, cb) ->  cb.equal(root.get("deleted"), deleted);
    }
}
