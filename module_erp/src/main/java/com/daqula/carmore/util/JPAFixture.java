package com.daqula.carmore.util;

import com.daqula.carmore.model.acl.ERPRole;
import com.daqula.carmore.model.acl.ERPUser;
import com.daqula.carmore.model.admin.*;
import com.daqula.carmore.model.customer.Customer;
import com.daqula.carmore.model.customer.CustomerERPProfile;
import com.daqula.carmore.model.customer.CustomerPurchasedSuite;
import com.daqula.carmore.model.order.*;
import com.daqula.carmore.model.shop.*;
import com.daqula.carmore.repository.*;
import org.joda.time.DateTime;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JPAFixture {

    ApplicationContext applicationContext;
    SkuItemRepository skuItemRepository;
    SuiteRepository suiteRepository;
    SuiteItemRepository suiteItemRepository;
    SecondaryCategoryRepository secondaryCategoryRepository;
    ShopRepository shopRepository;
    CustomStockItemRepository customStockItemRepository;
    CustomSuiteRepository customSuiteRepository;
    StockItemRepository stockItemRepository;
    OrganizationRepository organizationRepository;
    ERPUserRepository erpUserRepository;
    ERPRoleRepository erpRoleRepository;
    SupplierRepository supplierRepository;
    StockingOrderRepository stockingOrderRepository;
    PresaleOrderRepository presaleOrderRepository;
    CustomerRepository customerRepository;
    CustomerERPProfileRepository customerERPProfileRepository;
    PaymentRepository paymentRepository;
    SettleOrderRepository settleOrderRepository;
    CustomerPurchasedSuiteRepository customerPurchasedSuiteRepository;


    public JPAFixture(ApplicationContext context) {
        applicationContext = context;
        skuItemRepository = applicationContext.getBean(SkuItemRepository.class);
        suiteRepository = applicationContext.getBean(SuiteRepository.class);
        suiteItemRepository = applicationContext.getBean(SuiteItemRepository.class);
        customSuiteRepository = applicationContext.getBean(CustomSuiteRepository.class);
        secondaryCategoryRepository = applicationContext.getBean(SecondaryCategoryRepository.class);
        shopRepository = applicationContext.getBean(ShopRepository.class);
        customStockItemRepository = applicationContext.getBean(CustomStockItemRepository.class);
        stockItemRepository = applicationContext.getBean(StockItemRepository.class);
        organizationRepository = applicationContext.getBean(OrganizationRepository.class);
        erpUserRepository = applicationContext.getBean(ERPUserRepository.class);
        erpRoleRepository = applicationContext.getBean(ERPRoleRepository.class);
        supplierRepository = applicationContext.getBean(SupplierRepository.class);
        stockingOrderRepository = applicationContext.getBean(StockingOrderRepository.class);
        presaleOrderRepository = applicationContext.getBean(PresaleOrderRepository.class);
        customerRepository = applicationContext.getBean(CustomerRepository.class);
        customerERPProfileRepository = applicationContext.getBean(CustomerERPProfileRepository.class);
        paymentRepository = applicationContext.getBean(PaymentRepository.class);
        settleOrderRepository = applicationContext.getBean(SettleOrderRepository.class);
        customerPurchasedSuiteRepository = applicationContext.getBean(CustomerPurchasedSuiteRepository.class);

    }

//    public void  initOrganization() {
//        Organization organization = new Organization();
//        organization.bankAccount = "6225882128964410000";
//        organization.bankName = "招商银行";
//        organization.name = "上海达丘拉网络科技有限公司";
//        organization.businessLicenseImageUrl = "http://www.163.com";
//        organization.contact = "dbcooper";
//        organization.contactAddress = "上海市徐汇区漕河泾";
//        organization.contactPhone = "18664842521";
//        organization.serialNum = "12312312";
//        organization.shopQuota = 12;
//        organization.taxNumber = "121212";
//        organization = organizationRepository.save(organization);
//        ERPRole erpRole = new ERPRole();
//        erpRole.role = "系统管理员";
//        erpRole.authorityMask = 1;
//        erpRole.organization = organization;
//        erpRoleRepository.save(erpRole);
//
//        Shop shop = new Shop();
//        shop.name = "龙里店";
//        shop.address = "上海市闵行区龙里路";
//        shop.shopCode = "LLD";
//        shop.organization = organization;
//        shop.openingHours = "8:00";
//        shop.phone = "231231";
//        shopRepository.save(shop);
//        shop = new Shop();
//        shop.name = "大渡河店";
//        shop.address = "普陀区大渡河路1501号";
//        shop.shopCode = "DDH";
//        shop.openingHours = "8:00";
//        shop.phone = "231231";
//        shop.organization = organization;
//        shopRepository.save(shop);
//    }
//    public void  initPreSaleOrder () {
//        Customer customer = new Customer();
//        customer.mobile = "1231123213";
//        customer.token = UUID.randomUUID().toString();
//        customer = customerRepository.save(customer);
//        PresaleOrder presaleOrder = new PresaleOrder();
//        presaleOrder.source = 0;
//        presaleOrder.appointmentDate = DateTime.now();
//        presaleOrder.customer = customer;
//        Shop shop = new Shop();
//        shop.id = 1l;
//        presaleOrder.shop = shop;
//        presaleOrder.cancelled = false;
//        presaleOrder.saleCategory = SaleShelf.CATEGORY_REPAIR;
//        presaleOrder.description = "车轮胎有钉子";
//        presaleOrderRepository.save(presaleOrder);
//    }
//
//    public void initCustomerStock() {
//        SecondaryCategory secondaryCategory = new SecondaryCategory();
//        secondaryCategory.rootCategory = SkuItem.CATEGORY_INVENTORY;
//        secondaryCategory.name = "二级分类";
//
//        secondaryCategory = secondaryCategoryRepository.save(secondaryCategory);
//        CustomStockItem customStockItem = new CustomStockItem();
//        customStockItem.organization = new Organization();
//        customStockItem.organization.id = 1;
//        customStockItem.secondaryCategory = secondaryCategory;
//        customStockItem.brandName = "马牌";
//        customStockItem.description = "test description";
//        customStockItem.price = 20;
//        customStockItem.rootCategory = SkuItem.CATEGORY_ACCESSORY;
//        customStockItem.name = "测试商品1";
//
//        CustomStockItem customStockItem2 = new CustomStockItem();
//        customStockItem2.organization = new Organization();
//        customStockItem2.organization.id = 1;
//        customStockItem2.secondaryCategory = secondaryCategory;
//        customStockItem2.brandName = "品牌xx";
//        customStockItem2.description = "测试描述内容";
//        customStockItem2.price = 10;
//        customStockItem2.rootCategory = SkuItem.CATEGORY_ACCESSORY;
//        customStockItem2.name = "测试商品2";
//        customStockItemRepository.save(customStockItem);
//        customStockItemRepository.save(customStockItem2);
//    }
//
//    public void initStocking() {
//        StockingOrder stockingOrder = new StockingOrder();
//        Shop shop = new Shop();
//        shop.id = 1;
//        ERPUser erpUser = erpUserRepository.findOne(1L);
//        stockingOrder.shop = shop;
//        CustomStockItem customStockItem = new CustomStockItem();
//        customStockItem.id = 1;
//        StockingOrderDetail stockingOrderDetail = new StockingOrderDetail();
//        stockingOrderDetail.calculateNumber = 8;
//        stockingOrderDetail.customStockItem = customStockItem;
//        stockingOrderDetail.stockCost = 30D;
//        List<StockingOrderDetail> list = new ArrayList<StockingOrderDetail>() ;
//        stockingOrder.stockingOrderDetails = list;
//        stockingOrder.erpUser = erpUser;
//        stockingOrderRepository.save(stockingOrder);
//    }
//
//    /**
//     * 添加测试用户
//     */
//    public void initUsers() {
//        Organization org = organizationRepository.findOne(1L);
//        ERPRole erpRole = new ERPRole();
//        erpRole.role = "管理员test";
//        erpRole.authorityMask = 137438953408L;
//        erpRole.ver = 1;
//        erpRole.organization=org;
//        erpRoleRepository.save(erpRole);
//
//        ERPUser user = new ERPUser();
//        user.username = "testForLogin";
//        user.password = "111111";
//        user.enable = true;
//        user.organization = org;
////        ERPRole role = erpRoleRepository.findOne(1L);
//        user.role = erpRole;
//        user.shops = new ArrayList<>();
//        user.shops.add(shopRepository.findOne(1L));
//        user.shops.add(shopRepository.findOne(2L));
//
//        erpUserRepository.save(user);
//    }
//
//    /**
//     * 初始化供应商
//     */
//    public void initSupplier() {
//
//        Supplier supplier = new Supplier();
//        supplier.email = "test@zhaitech.com";
//        supplier.contactInfo = "021-1122334";
//        supplier.fax = "传真xxx";
//        supplier.name = "测试供应商";
//        supplier.organization = organizationRepository.findOne(1L);
//        supplier.description = "供应商描述xxx";
//        supplierRepository.save(supplier);
//    }
//
//    /**
//     * 初始化ERP用户
//     */
//    public void initCustomerErpProfile(){
//        Organization organization = new Organization();
//        organization.id = 1;
//        Customer customer = new Customer();
//        customer.id = 1;
//
//       /* Customer customer2 = new Customer();
//        customer2.id = 2;
//        customer2.mobile = "15221083837";*/
//
//        CustomerERPProfile erpProfile = new CustomerERPProfile();
//        erpProfile.realName = "测试ERP用户";
//        erpProfile.gender = 1;
//        erpProfile.organization = organization;
//        erpProfile.customer = customer;
//        erpProfile.profileType = CustomerERPProfile.PROFILE_TYPE_ERP;
//
//        customerERPProfileRepository.save(erpProfile);
//
//       /* CustomerERPProfile erpProfile2 = new CustomerERPProfile();
//        erpProfile2.realName = "测试ERP用户02";
//        erpProfile2.gender = 0;
//        erpProfile2.organization = organization;
//        erpProfile2.customer = customer2;
//        erpProfile2.profileType = CustomerERPProfile.PROFILE_TYPE_ERP;*/
//
//
//
//
//    }
//
//    public void initSettleOrderHistory(){
//        SettleOrderHistory settleOrderHistory = new SettleOrderHistory();
//        Payment payment = new Payment();
//        payment.amount = 100;
//
//        Customer customer = new Customer();
//        customer.id = 1;
//        payment.customer = customer;
//        paymentRepository.save(payment);
//
//
//
//        Shop shop = new Shop();
//        Shop belongShop = new Shop();
//        shop.id = 2;
//        belongShop.id = 1;
//        SettleOrder settleOrder = new SettleOrder();
//        settleOrder.id = 1;
//        settleOrder.shop = shop;
//        settleOrder.payment = payment;
//        settleOrder.customer = customer;
//        settleOrder.saleNo = "1";
//        settleOrder.saleNoView = "12312312NULLSG00001";
//        settleOrder.saleCategory = SaleShelf.CATEGORY_VIP;
//
//        CustomerPurchasedSuite customerPurchasedSuite = new CustomerPurchasedSuite();
//        CustomSuite suite = new CustomSuite();
//        suite.id = 1;
//        customerPurchasedSuite.suite = suite;
//        customerPurchasedSuite.shop = belongShop;
//        customerPurchasedSuite.customer = customer;
//        customerPurchasedSuite.enabled = true;
//        customerPurchasedSuite.startDate = new DateTime();
//        customerPurchasedSuiteRepository.save(customerPurchasedSuite);
//
//
//        settleOrder.customerPurchasedSuite = customerPurchasedSuite;
//        settleOrderRepository.save(settleOrder);
//
//    }
//
//    /**
//     * 添加门店套餐及套餐明细
//     */
//    public void initCustomSuiteAndSuiteItem() {
//        CustomSuite suite = new CustomSuite();
//        suite.suiteType = Suite.SUITE_TYPE_VIP;
//        suite.price = 189L;
//        suite.expiation = 30;
//        suite.description = "会员套餐描述内容 xxxxxxx";
//        suite.name = "测试套餐01 会员";
//        suite.enabled = true;
//        Organization organization = organizationRepository.findOne(1L);
//        suite.organization = organization;
//
//        List<SuiteItem> list = new ArrayList<>();
//        for (int i = 1; i <= 2 ; i++) {
//
//
//            SuiteItem suiteItem = new SuiteItem();
//            int num = 10;
//            if(i == 1){
//                num = 5;
//            }
//            suiteItem.times = num;
//            suiteItem.timesLeft = num;
//            suiteItem.usedTimes = 0;
//            CustomStockItem customStockItem = customStockItemRepository.findOne(Long.valueOf(i));
////            CustomStockItem skuItem = new CustomStockItem();
////            skuItem.id = i;
//            suiteItem.skuItem = customStockItem;
//
//
//            suiteItemRepository.save(suiteItem);
//            list.add(suiteItem);
//        }
//        suite.suiteItems = list;
//
//
//        CustomSuite suite2 = new CustomSuite();
//        suite2.suiteType = Suite.SUITE_TYPE_SERVICES;
//        suite2.price = 89L;
//        suite2.expiation = 30;
//        suite2.description = "服务套餐描述内容 xxxxxxx";
//        suite2.name = "测试套餐02 服务";
//        suite2.enabled = true;
//        suite2.organization = organization;
//
//        List<SuiteItem> list2 = new ArrayList<>();
//        for (int i = 1; i <= 2 ; i++) {
//
//
//            SuiteItem suiteItem = new SuiteItem();
//            int num = 5;
//            if(i == 1){
//                num = 10;
//            }
//            suiteItem.times = num;
//            suiteItem.timesLeft = num;
//            suiteItem.usedTimes = 0;
//
//
//
//            CustomStockItem customStockItem = customStockItemRepository.findOne(Long.valueOf(i));
////            CustomStockItem skuItem = new CustomStockItem();
////            customStockItem.id = i;
//            suiteItem.skuItem = customStockItem;
//
//
//            suiteItemRepository.save(suiteItem);
//            list2.add(suiteItem);
//        }
//
//        suite2.suiteItems = list2;
//
//        customSuiteRepository.save(suite);
//        customSuiteRepository.save(suite2);
//    }


}
