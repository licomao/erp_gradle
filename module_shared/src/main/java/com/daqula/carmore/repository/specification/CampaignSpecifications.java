package com.daqula.carmore.repository.specification;

import com.daqula.carmore.model.admin.Campaign;
import org.joda.time.DateTime;
import org.springframework.data.jpa.domain.Specification;

import java.util.Date;

/**
 * Created by chenxin on 2015/10/26 0026.
 */
public class CampaignSpecifications {

    public static Specification<Campaign> filterByCompaignType (Integer compaignType) {
        return (root, query, cb) -> compaignType != null ? cb.equal(root.get("compaignType"), compaignType) : null;
    }

    public static Specification<Campaign> filterByPublishDateStart(DateTime publishDate) {
        return (root, query, cb) -> (publishDate != null) ?
                cb.greaterThanOrEqualTo(root.get("publishDate"), publishDate) : null;
    }
    public static Specification<Campaign> filterByPublishDateEnd(DateTime publishDate) {
        return (root, query, cb) -> (publishDate != null) ?
                cb.lessThan(root.get("publishDate"), publishDate.plusDays(1)) : null;
    }

}
