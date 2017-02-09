package com.daqula.carmore.repository.specification;

import com.daqula.carmore.model.shop.*;
import org.springframework.data.jpa.domain.Specification;

/**
 * Created by chenxin on 2015/10/9.
 */
public class StaffSpecifications {

    public static Specification<Staff> filterByName (String name) {
        return (root, query, cb) -> name != null ? cb.like(root.get("name"), "%" + name + "%") : null;
    }

    public static Specification<Staff> filterByShop (Shop shop) {
        return (root, query, cb) -> shop != null ? cb.equal(root.get("shop"), shop) : null;
    }

    public static Specification<Staff> filterByStatus (String status) {
        return (root, query, cb) -> status != null ? cb.equal(root.get("status"), status) : null;
    }

    public static Specification<Staff> filterByOrganization (Organization organization) {
        return (root, query, cb) -> organization != null ? cb.equal(root.get("shop").get("organization"), organization) : null;
    }

    public static Specification<Staff> filterByStatusNot (String status) {
        return (root, query, cb) -> status != null ? cb.notEqual(root.get("status"), status) : null;
    }

    public static Specification<Staff> filterByPhone (String phone) {
        return (root, query, cb) -> phone != null ? cb.equal(root.get("phone"), phone) : null;
    }

    public static Specification<Staff> filterByDeleted (boolean deleted) {
        return (root, query, cb) -> cb.equal(root.get("deleted"), deleted) ;
    }

}
