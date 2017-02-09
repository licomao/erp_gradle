package com.daqula.carmore.pojo;

import com.daqula.carmore.model.customer.Customer;
import com.daqula.carmore.model.customer.CustomerAppProfile;
import com.daqula.carmore.model.customer.VehicleInfo;
import com.daqula.carmore.model.shop.Shop;
import com.daqula.carmore.repository.CustomerPurchasedSuiteRepository;
import com.daqula.carmore.repository.PresaleOrderRepository;
import com.daqula.carmore.repository.SettleOrderRepository;
import com.daqula.carmore.repository.impl.PresaleOrderSpecifications;
import com.daqula.carmore.repository.specification.CustomerPurchasedSuiteSpecifications;
import com.daqula.carmore.repository.specification.SettleOrderSpecifications;
import org.springframework.data.jpa.domain.Specifications;

import java.util.List;
import java.util.stream.Collectors;

public class CustomerData {

    public long id;

    public String uid;

    public String token;

    public String mobile;

    public List<VehicleInfo> vehicles;

    public String nickName;

    public String avatarUrl;

    public Shop bindingShop;

    public int subscribedOrderCount;

    public int needCommentOrderCount;

    public int purchasedSuiteCount;

    public static CustomerData build(Customer customer, CustomerAppProfile profile,
                                     CustomerPurchasedSuiteRepository customerPurchasedSuiteRepository,
                                     PresaleOrderRepository presaleOrderRepository,
                                     SettleOrderRepository settleOrderRepository) {
        // fetch lazy collection
        profile.vehicles.size();

        CustomerData customerData = new CustomerData();
        customerData.id = customer.id;
        customerData.uid = customer.uid.toString();
        customerData.token = customer.token;
        customerData.mobile = customer.mobile;
        customerData.nickName = profile.nickName;
        customerData.avatarUrl = profile.avatarUrl;
        customerData.bindingShop = profile.bindingShop;

        if (profile.vehicles != null) {
            customerData.vehicles = profile.vehicles.stream().filter(
                    vehicleInfo -> !vehicleInfo.deleted).collect(Collectors.toList());
        }

        // 已买套餐数量
        Specifications spec = Specifications
                .where(CustomerPurchasedSuiteSpecifications.filterByCustomer(customer))
                .and(CustomerPurchasedSuiteSpecifications.filterHasTimesLeft());
        if (profile.bindingShop != null) {
            spec = spec.and(CustomerPurchasedSuiteSpecifications.filterByOrganization(profile.bindingShop.organization));
        }
        customerData.purchasedSuiteCount = (int) customerPurchasedSuiteRepository.count(spec);

        // 已预约订单
        customerData.subscribedOrderCount = (int) presaleOrderRepository.count(Specifications
                .where(PresaleOrderSpecifications.filteredByCustomer(customer))
                .and(PresaleOrderSpecifications.settleOrderIsNull()));

        // 待评论订单
        customerData.needCommentOrderCount = (int) settleOrderRepository.count(Specifications
                .where(SettleOrderSpecifications.filteredByCustomer(customer))
                .and(SettleOrderSpecifications.filteredByCommented(false))
                .and(SettleOrderSpecifications.isNotPurchaseSuiteOrder())
                .and(SettleOrderSpecifications.createDaysLessThan(30)));

        return customerData;
    }
}
