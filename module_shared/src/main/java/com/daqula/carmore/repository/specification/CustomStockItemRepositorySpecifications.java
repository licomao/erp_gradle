package com.daqula.carmore.repository.specification;

import com.daqula.carmore.model.admin.SecondaryCategory;
import com.daqula.carmore.model.shop.CustomStockItem;
import com.daqula.carmore.model.shop.Organization;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;

/**
 * Created by mdc on 2015/9/17.
 */
public class CustomStockItemRepositorySpecifications {

    public static Specification<CustomStockItem> filterHasName(String name) {
        return (root, query, cb) -> name != null ? cb.equal(root.get("name"), name) : null;
    }

    public static Specification<CustomStockItem> filterByOrganization(Organization organization) {
        return (root, query, cb) -> organization != null ? cb.equal(root.get("organization"), organization) : null;
    }

    public static Specification<CustomStockItem> filterByRootCategory(int rootCategory) {
        return (root, query, cb) ->{
            Join<CustomStockItem, SecondaryCategory> join
                    = root.join("secondaryCategory");
           return cb.equal(join.get("rootCategory"), rootCategory);

        };
    }

    public static Specification<CustomStockItem> filterByName (String name) {
        return (root, query, cb) -> name != null ? cb.like(root.get("name"), "%" + name + "%") : null;
    }

    public static Specification<CustomStockItem> filterByRootCategory (Integer rootCategory) {
        return (root, query, cb) -> rootCategory != 99 ? cb.equal(root.get("rootCategory"), rootCategory) : null;
    }

    public static Specification<CustomStockItem> filterDeleted (Boolean deleted) {
        return (root, query, cb) -> cb.equal(root.get("deleted"), deleted);
    }


}
