package com.daqula.carmore.repository.specification;

import com.daqula.carmore.model.shop.Organization;
import com.daqula.carmore.model.shop.Shop;
import com.daqula.carmore.model.template.FingerPrintScanner;
import org.springframework.data.jpa.domain.Specification;

/**
 * Created by swj on 2015/10/13.
 */
public class FingerPrintScannerRepositorySpecifications {
    public static Specification<FingerPrintScanner> filterByOrganization(Organization organization) {
        return (root, query, cb) -> organization != null ? cb.equal(root.get("organization"), organization) : null;
    }

    public static Specification<FingerPrintScanner> filterByVid(String vid) {
        return (root, query, cb) -> vid != null ? cb.like(root.get("vid"), "%" + vid + "%") : null;
    }

    public static Specification<FingerPrintScanner> filterByPid(String pid) {
        return (root, query, cb) -> pid != null ? cb.like(root.get("pid"), "%" + pid + "%") : null;
    }

    public static Specification<FingerPrintScanner> filterByUsbSn(String usbSn) {
        return (root, query, cb) -> usbSn != null ? cb.like(root.get("usbSn"), "%" + usbSn + "%") : null;
    }

    public static Specification<FingerPrintScanner> filterDeleteStatus(Boolean deleted) {
        return (root, query, cb) -> cb.equal(root.get("deleted"), deleted);
    }

    public static Specification<FingerPrintScanner> filterByShop(Shop shop) {
        return (root, query, cb) -> shop != null ? cb.equal(root.get("shop"), shop) : null;
    }

    public static Specification<FingerPrintScanner> filterBySensorSN(String sensorSN) {
        return (root, query, cb) -> sensorSN != null ? cb.like(root.get("sensorSN"), "%" + sensorSN + "%") : null;
    }
}
