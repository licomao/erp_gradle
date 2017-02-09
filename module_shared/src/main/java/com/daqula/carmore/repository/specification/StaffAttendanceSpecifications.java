package com.daqula.carmore.repository.specification;

import com.daqula.carmore.model.shop.Organization;
import com.daqula.carmore.model.shop.Shop;
import com.daqula.carmore.model.shop.Staff;
import com.daqula.carmore.model.shop.StaffAttendance;
import org.joda.time.DateTime;
import org.springframework.data.jpa.domain.Specification;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2015/10/12 0012.
 */
public class StaffAttendanceSpecifications {

    public static Specification<StaffAttendance> filterByStaff (Staff staff) {
        return (root, query, cb) -> staff != null ? cb.equal(root.get("staff"), staff) : null;
    }

    public static Specification<StaffAttendance> filterByStaffName (String name) {
        return (root, query, cb) -> name != null ? cb.like(root.get("staff").get("name"), "%" + name + "%") : null;
    }

    public static Specification<StaffAttendance> filterByStaffShop (Shop shop) {
        return (root, query, cb) -> shop != null ? cb.equal(root.get("staff").get("shop"), shop) : null;
    }

    public static Specification<StaffAttendance> filterByOrganization (Organization organization) {
        return (root, query, cb) -> organization != null ? cb.equal(root.get("staff").get("shop").get("organization"), organization) : null;
    }

    public static Specification<StaffAttendance> filterByWorkdate (Date workDate) {
        return (root, query, cb) -> workDate != null ? cb.equal(root.get("workDate"), workDate) :
                cb.equal(root.get("workDate"), cb.currentDate());
    }

    public static Specification<StaffAttendance> filterByGtWorkDate(Date date) {
        return (root, query, cb) -> date != null ? cb.greaterThanOrEqualTo(root.get("workDate"), date) : null;
    }
    public static Specification<StaffAttendance> filterByLtWorkDate(Date date) {
        return (root, query, cb) -> date != null ? cb.lessThanOrEqualTo(root.get("workDate"), date) : null;
    }

    public static Specification<StaffAttendance> filterByStaffDeleted(boolean deleted) {
        return (root, query, cb) ->  cb.lessThanOrEqualTo(root.get("staff").get("deleted"), deleted);
    }

    public static Specification<StaffAttendance> filterByWorkDateStart (Date startDate) {
        return (root, query, cb) -> startDate != null ? cb.equal(root.get("workDate"), startDate) : null;
    }

    public static Specification<StaffAttendance> filterByWorkDateEnd (Date endDate) {
        return (root, query, cb) -> endDate != null ? cb.equal(root.get("workDate"), endDate) : null;
    }

}
