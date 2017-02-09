package com.daqula.carmore.repository.specification;

import com.daqula.carmore.model.shop.Job;
import com.daqula.carmore.model.shop.Organization;
import org.springframework.data.jpa.domain.Specification;

/**
 * Created by chenxin on 2015/10/10 0010.
 */
public class JobSpecifications {

    public static Specification<Job> filterByName (String name) {
        return (root, query, cb) -> name != null ? cb.equal(root.get("name"), name) : null;
    }

    public static Specification<Job> filterByOrganization (Organization organization) {
        return (root, query, cb) -> organization != null ? cb.equal(root.get("organization"), organization) : null;
    }

    public static Specification<Job> filterByDeleted (Boolean deleted) {
        return (root, query, cb) -> cb.equal(root.get("deleted"), deleted);
    }

}
