package com.daqula.carmore.repository.specification;

import com.daqula.carmore.model.acl.ERPRole;
import com.daqula.carmore.model.shop.Organization;
import org.springframework.data.jpa.domain.Specification;

/**
 * Created by mdc on 2015/10/10.
 */
public class ERPRoleRepositorySpecifications {

    public static Specification<ERPRole> filterByRoleName(String roleName) {
        return (root, query, cb) -> roleName != null ? cb.equal(root.get("role"), roleName) : null;
    }

    public static Specification<ERPRole> filterByDeleted(boolean deleted) {
        return (root, query, cb) ->  cb.equal(root.get("deleted"), deleted);
    }

    public static Specification<ERPRole> filterByOrganization(Organization organization) {
        return (root, query, cb) -> organization != null ? cb.equal(root.get("organization"), organization) : null;
    }
}
