package com.daqula.carmore.controller;

import com.daqula.carmore.ErrorCode;
import com.daqula.carmore.exception.BizException;
import com.daqula.carmore.model.customer.Customer;
import com.daqula.carmore.model.customer.CustomerAppProfile;
import com.daqula.carmore.model.customer.CustomerPurchasedSuite;
import com.daqula.carmore.model.customer.VehicleInfo;
import com.daqula.carmore.model.shop.Shop;
import com.daqula.carmore.pojo.AppSuite;
import com.daqula.carmore.pojo.CustomerData;
import com.daqula.carmore.repository.*;
import com.daqula.carmore.repository.specification.CustomerPurchasedSuiteSpecifications;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.daqula.carmore.util.CollectionUtil.*;
import static com.daqula.carmore.utils.JsonResultBuilder.buildSuccessResult;

@RestController
public class CustomerController {

    @Autowired
	private CustomerRepository customerRepository;

    @Autowired
    private CustomerPurchasedSuiteRepository customerPurchasedSuiteRepository;

    @Autowired
    private VehicleInfoRepository vehicleInfoRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private CustomerProfileRepository customerProfileRepository;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private PresaleOrderRepository presaleOrderRepository;

    @Autowired
    private SettleOrderRepository settleOrderRepository;

    /**
     * Return customer profile /api/customer/profile
     */
    @RequestMapping(value="/api/customer/profile",  method = RequestMethod.GET)
	public Map<String, Object> profile(@AuthenticationPrincipal Customer customer) {
        CustomerAppProfile profile = customerProfileRepository.findAppProfileByCustomer(customer);
        CustomerData data = CustomerData.build(customer, profile,
                customerPurchasedSuiteRepository,
                presaleOrderRepository,
                settleOrderRepository);
        return buildSuccessResult(data);
	}

	/**
	 * 编辑用户信息 /api/customer/edit
	 * @return
	 */
	@RequestMapping(value = "/api/customer/edit", method = RequestMethod.POST)
	public Map<String, Object> edit(@AuthenticationPrincipal Customer customer,
                                    @RequestParam Optional<String> nickName,
                                    @RequestParam Optional<String> avatarUrl) {
        CustomerAppProfile profile = customerProfileRepository.findAppProfileByCustomer(customer);

        if (nickName.isPresent()) profile.nickName = nickName.get();
        if (avatarUrl.isPresent()) profile.avatarUrl = avatarUrl.get();
        profile = customerProfileRepository.save(profile);

        CustomerData data = CustomerData.build(customer, profile,
                customerPurchasedSuiteRepository,
                presaleOrderRepository,
                settleOrderRepository);
        return buildSuccessResult(data);
    }

    /**
     * 用户套餐列表 /api/customer/suites
     * @return
     */
	@RequestMapping(value = "/api/customer/suites", method = RequestMethod.GET)
	public Map<String, Object> suiteList(@AuthenticationPrincipal Customer customer) {
        // 返回同一组织下所有门店的可用套餐
        CustomerAppProfile profile = customerProfileRepository.findAppProfileByCustomer(customer);

        Specifications spec = Specifications
                .where(CustomerPurchasedSuiteSpecifications.filterByCustomer(customer))
                .and(CustomerPurchasedSuiteSpecifications.filterHasTimesLeft());
        if (profile.bindingShop != null) {
            spec = spec.and(CustomerPurchasedSuiteSpecifications.filterByOrganization(profile.bindingShop.organization));
        }
        List<CustomerPurchasedSuite> purchasedSuites = customerPurchasedSuiteRepository.findAll(spec);

        List<AppSuite> appSuites = new ArrayList<>();
        purchasedSuites.forEach(purchasedSuite -> {
            AppSuite suites = AppSuite.build(purchasedSuite);
            suites.suiteItems.forEach(item -> {
                item.appointed = presaleOrderRepository
                        .findByAppointedPresaleOrder(item.purchasedSuiteItemId) != null;
            });
            appSuites.add(suites);
        });
        return buildSuccessResult(appSuites);
    }

    /**
     * 添加用户车型信息 /api/customer/vehicle/add
     * @param vehicleInfo
     * @return
     */
    @RequestMapping(value = "/api/customer/vehicle/add", method = RequestMethod.POST)
    public Map<String, Object> addVehicleInfo(@AuthenticationPrincipal Customer customer,
                                              @RequestBody VehicleInfo vehicleInfo) {
        if (vehicleInfo.model == null) {
            throw new BizException(ErrorCode.VEHICLE_MODEL_NOT_SET, "Vehicle model is not set.");
        }
        vehicleInfo.id = 0;

        CustomerAppProfile profile = customerProfileRepository.findAppProfileByCustomer(customer);
        profile.vehicles.add(vehicleInfo);
        customerProfileRepository.save(profile);
        return buildSuccessResult();
    }

    /**
     * 批量添加用户车型信息 /api/customer/vehicle/add_batch
     * @param vehicleInfos
     * @return
     */
    @RequestMapping(value = "/api/customer/vehicle/add_batch", method = RequestMethod.POST)
    public Map<String, Object> addVehicleInfos(@AuthenticationPrincipal Customer customer,
                                               @RequestBody List<VehicleInfo> vehicleInfos) {
        CustomerAppProfile profile = customerProfileRepository.findAppProfileByCustomer(customer);

        vehicleInfos.stream().forEach(vehicleInfo -> {
            if (vehicleInfo.model == null) {
                throw new BizException(ErrorCode.VEHICLE_MODEL_NOT_SET, "Vehicle model is not set.");
            }
            vehicleInfo.id = 0;
            profile.vehicles.add(vehicleInfo);
        });

        customerProfileRepository.save(profile);

        // return
        CustomerData data = CustomerData.build(customer, profile,
                customerPurchasedSuiteRepository,
                presaleOrderRepository,
                settleOrderRepository);
        return buildSuccessResult(data);
    }

    /**
     * 修改用户车型信息 /api/customer/vehicle/edit
     *
     * @param modifiedVehicleInfo
     * @return
     */
    @RequestMapping(value = "/api/customer/vehicle/edit", method = RequestMethod.POST)
    public Map<String, Object> editVehicleInfo(@AuthenticationPrincipal Customer customer,
                                               @RequestBody VehicleInfo modifiedVehicleInfo) {
        CustomerAppProfile profile = customerProfileRepository.findAppProfileByCustomer(customer);
        Optional<VehicleInfo> vehicle = profile.vehicles.stream().filter(
                (v) -> v.uid.equals(modifiedVehicleInfo.uid)).findAny();
        if (!vehicle.isPresent()) {
            throw new BizException(ErrorCode.EDITED_VEHICLE_INFO_EXISTED, "Edited VehicleInfo is not existed");
        }

        if (!StringUtils.isEmpty(modifiedVehicleInfo.vinCode)) vehicle.get().vinCode = modifiedVehicleInfo.vinCode;
        if (!StringUtils.isEmpty(modifiedVehicleInfo.vinImageUrl)) vehicle.get().vinImageUrl = modifiedVehicleInfo.vinImageUrl;
        if (!StringUtils.isEmpty(modifiedVehicleInfo.tire)) vehicle.get().tire = modifiedVehicleInfo.tire;
        if (!StringUtils.isEmpty(modifiedVehicleInfo.plateNumber)) vehicle.get().plateNumber = modifiedVehicleInfo.plateNumber;
        if (modifiedVehicleInfo.onRoadDate != null) vehicle.get().onRoadDate = modifiedVehicleInfo.onRoadDate;
        if (modifiedVehicleInfo.mileage > 0) {
            vehicle.get().mileage = modifiedVehicleInfo.mileage;
            vehicle.get().mileageUpdatedDate = DateTime.now();
        }
        if (!StringUtils.isEmpty(modifiedVehicleInfo.obdSN)) vehicle.get().obdSN = modifiedVehicleInfo.obdSN;

        vehicleInfoRepository.save(vehicle.get());
        return buildSuccessResult();
    }

    /**
     * 删除用户车型信息 /api/customer/vehicle/delete/{vehicleInfoUid}
     *
     * @param vehicleInfoUid
     * @return
     */
    @RequestMapping(value = "/api/customer/vehicle/delete/{vehicleInfoUid}", method = RequestMethod.POST)
    @Transactional
    public Map<String, Object> deleteVehicleInfo(@AuthenticationPrincipal Customer customer,
                                                 @PathVariable UUID vehicleInfoUid) {
        CustomerAppProfile profile = customerProfileRepository.findAppProfileByCustomer(customer);
        if (!profile.vehicles.stream().anyMatch(v -> v.uid.equals(vehicleInfoUid))) {
            throw new BizException(ErrorCode.EDITED_VEHICLE_INFO_EXISTED, "Deleted VehicleInfo is not existed");
        }

        VehicleInfo vehicleInfo = vehicleInfoRepository.findByUid(vehicleInfoUid);
        if (vehicleInfo != null) vehicleInfo.deleted = true;
        vehicleInfoRepository.save(vehicleInfo);
        return buildSuccessResult();
    }

    /**
     * 绑定门店 /api/customer/bindorganization
     * @param shopId
     * @return
     */
    @RequestMapping(value = "/api/customer/bind_shop", method = RequestMethod.POST)
    @Transactional
    public Map<String, Object> bindShop(@AuthenticationPrincipal Customer customer,
                                        @RequestParam long shopId) {
        Shop shop = shopRepository.findOne(shopId);
        if (shop == null) {
            throw new BizException(ErrorCode.ENTITY_NOT_FOUND, "Shop is not found");
        }

        CustomerAppProfile profile = customerProfileRepository.findAppProfileByCustomer(customer);

        // 已经绑定这个组织
        if (profile.bindingShop != null &&
                shopId == profile.bindingShop.id) {
            return buildSuccessResult(null,
                    String.format("Shop %s(%s) is already bind to current customer",
                            profile.bindingShop.name, profile.bindingShop.id));
        }
        Specifications spec = Specifications.where(CustomerPurchasedSuiteSpecifications.filterHasTimesLeft())
                .and(CustomerPurchasedSuiteSpecifications.filterByCustomer(customer))
                .and(CustomerPurchasedSuiteSpecifications.filterByNotOrganization(shop.organization));
        List<CustomerPurchasedSuite> purchasedSuites = customerPurchasedSuiteRepository.findAll(spec);

        // todo 解绑生成一个顾客回访事件

        // 检查在其它门店是否还有未使用完的套餐
        List<Shop> shops = new ArrayList<>();
        if (purchasedSuites.size() > 0) {
            purchasedSuites.stream()
                    .filter(CustomerPurchasedSuite::isExpired)
                    .filter(purchasedSuite -> !shops.contains(purchasedSuite.shop))
                    .forEach(purchasedSuite -> shops.add(purchasedSuite.shop));
        }
        profile.bindingShop = shop;
        customerProfileRepository.save(profile);

        return buildSuccessResult(map(
                entry("previousBindingShop", shops)
        ));
    }

}
