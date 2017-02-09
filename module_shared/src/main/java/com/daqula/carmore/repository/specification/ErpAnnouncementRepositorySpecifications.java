package com.daqula.carmore.repository.specification;

import com.daqula.carmore.model.announcement.ErpAnnouncement;
import com.daqula.carmore.model.shop.Organization;
import com.daqula.carmore.model.shop.Shop;
import org.joda.time.DateTime;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;

/**
 * Created by mdc on 2015/10/15.
 */
public class ErpAnnouncementRepositorySpecifications {

    public static Specification<ErpAnnouncement> filterByOrganization(Organization organization) {
        return (root, query, cb) -> organization != null ? cb.equal(root.get("organization"), organization) : null;
    }

    public static Specification<ErpAnnouncement> filterByTitle(String title) {
        return (root, query, cb) -> title != null ? cb.equal(root.get("title"), title) : null;
    }

    public static Specification<ErpAnnouncement> filterByPublisher(String publisher) {
        return (root, query, cb) -> publisher != null ? cb.equal(root.get("publisher"), publisher) : null;
    }

}
