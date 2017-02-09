package com.daqula.carmore.repository.specification;

import com.daqula.carmore.model.admin.SecondaryCategory;
import com.daqula.carmore.model.shop.Organization;
import org.springframework.data.jpa.domain.Specification;

/**
 * Created by chenxin on 2015/10/16 0016.
 */
public class SecondaryCategorySpecifications {

    public static Specification<SecondaryCategory> filterByRootCategory (Integer rootCategory) {
        return (root, query, cb) -> rootCategory != null ? cb.equal(root.get("rootCategory"), rootCategory) : null;
    }

    public static Specification<SecondaryCategory> filterByOrganization (Organization organization) {
        return (root, query, cb) -> organization != null ? cb.equal(root.get("organization"), organization) : null;
    }

    public static Specification<SecondaryCategory> filterDeleted (Boolean deleted) {
        return (root, query, cb) -> cb.equal(root.get("deleted"), deleted);
    }

}
