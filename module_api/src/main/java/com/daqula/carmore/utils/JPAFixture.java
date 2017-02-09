package com.daqula.carmore.utils;

import com.daqula.carmore.controller.OrderController;
import com.daqula.carmore.controller.PaymentController;
import com.daqula.carmore.model.admin.*;
import com.daqula.carmore.model.customer.Customer;
import com.daqula.carmore.model.customer.CustomerAppProfile;
import com.daqula.carmore.model.customer.CustomerERPProfile;
import com.daqula.carmore.model.customer.VehicleInfo;
import com.daqula.carmore.model.order.Comment;
import com.daqula.carmore.model.order.OrderDetail;
import com.daqula.carmore.model.order.Payment;
import com.daqula.carmore.model.order.SettleOrder;
import com.daqula.carmore.model.shop.*;
import com.daqula.carmore.model.template.City;
import com.daqula.carmore.model.template.VehicleModel;
import com.daqula.carmore.repository.*;
import org.joda.time.DateTime;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

public class JPAFixture {

    ApplicationContext applicationContext;
    SkuItemRepository skuItemRepository;
    SuiteRepository suiteRepository;
    SecondaryCategoryRepository secondaryCategoryRepository;
    ShopRepository shopRepository;
    CareSuiteRepository careSuiteRepository;
    CareSuiteGroupItemPriceRepository careSuiteGroupItemPriceRepository;
    VehicleInfoRepository vehicleInfoRepository;
    VehicleModelRepository vehicleModelRepository;
    CustomerRepository customerRepository;
    CustomerProfileRepository customerProfileRepository;
    CampaignRepository campaignRepository;
    CityRepository cityRepository;
    CustomSuiteRepository customSuiteRepository;
    OrganizationRepository organizationRepository;
    CustomerPurchasedSuiteRepository customerPurchasedSuiteRepository;
    SettleOrderRepository settleOrderRepository;
    CommentRepository commentRepository;
    PaymentRepository paymentRepository;
    RecommendedAccessoryRepository recommendedAccessoryRepository;
    StockItemRepository stockItemRepository;
    SaleShelfRepository saleShelfRepository;

    OrderController orderController;
    PaymentController paymentController;

    public JPAFixture(ApplicationContext context) {
        applicationContext = context;
        skuItemRepository = applicationContext.getBean(SkuItemRepository.class);
        suiteRepository = applicationContext.getBean(SuiteRepository.class);
        secondaryCategoryRepository = applicationContext.getBean(SecondaryCategoryRepository.class);
        shopRepository = applicationContext.getBean(ShopRepository.class);
        careSuiteRepository = applicationContext.getBean(CareSuiteRepository.class);
        careSuiteGroupItemPriceRepository = applicationContext.getBean(CareSuiteGroupItemPriceRepository.class);
        vehicleInfoRepository = applicationContext.getBean(VehicleInfoRepository.class);
        vehicleModelRepository = applicationContext.getBean(VehicleModelRepository.class);
        customerRepository = applicationContext.getBean(CustomerRepository.class);
        customerProfileRepository = applicationContext.getBean(CustomerProfileRepository.class);
        campaignRepository = applicationContext.getBean(CampaignRepository.class);
        cityRepository = applicationContext.getBean(CityRepository.class);
        customSuiteRepository = applicationContext.getBean(CustomSuiteRepository.class);
        customerPurchasedSuiteRepository = applicationContext.getBean(CustomerPurchasedSuiteRepository.class);
        organizationRepository = applicationContext.getBean(OrganizationRepository.class);
        settleOrderRepository = applicationContext.getBean(SettleOrderRepository.class);
        commentRepository = applicationContext.getBean(CommentRepository.class);
        recommendedAccessoryRepository = applicationContext.getBean(RecommendedAccessoryRepository.class);
        stockItemRepository = applicationContext.getBean(StockItemRepository.class);
        saleShelfRepository = applicationContext.getBean(SaleShelfRepository.class);

        orderController = applicationContext.getBean(OrderController.class);
        paymentController = applicationContext.getBean(PaymentController.class);
        paymentRepository = applicationContext.getBean(PaymentRepository.class);
    }

    private ServiceItem carWashServiceItem;

    public void createSuites() {
        Organization organization = organizationRepository.findOne(3l);

        // sku
        if (carWashServiceItem == null) {
            carWashServiceItem = new ServiceItem();
            carWashServiceItem.name = "洗车";
            carWashServiceItem.price = 30;
            carWashServiceItem.needAppointment = false;
            skuItemRepository.save(carWashServiceItem);
        }

        ServiceItem interiorCleanItem = new ServiceItem();
        interiorCleanItem.name = "内饰清洗";
        interiorCleanItem.price = 20;
        interiorCleanItem.needAppointment = false;
        skuItemRepository.save(interiorCleanItem);

        // 创建测试轮胎数据
        String[][] tireDatas = {
                {"米其林", "米其林轮胎 韧悦 ENERGY XM2 %s/%sR%s 91H"},
                {"邓禄普","邓禄普轮胎 SP TOURING T1 %s/%sR%s 91H"},
                {"德国马牌","德国马牌轮胎 ContiMaxContactTM MC5 %s/%sR%s 98WZR FR XL"}
        };

        ArrayList<StockItem> savingStockItems = new ArrayList<>();
        for (String[] tireData: tireDatas) {
            for (int tireParam1=135; tireParam1<=355; tireParam1+=5) {
                for (int tireParam2=25; tireParam2<=85; tireParam2+=5) {
                    for (int tireParam3=12; tireParam3<=22; tireParam3++) {
                        StockItem tire = new StockItem();
                        tire.brandName = tireData[0];
                        tire.name = String.format(tireData[1], tireParam1, tireParam2, tireParam3);
                        tire.cost = new Random().nextInt(499)+1;
                        tire.price = tire.cost+10;
                        tire.accessoryCategory = StockItem.CATEGORY_ACCESSORY_TIRE;
                        tire.param1 = Integer.toString(tireParam1);
                        tire.param2 = Integer.toString(tireParam2);
                        tire.param3 = Integer.toString(tireParam3);
                        savingStockItems.add(tire);
                    }
                }
            }
        }
        ArrayList<SaleShelf> savingSaleShelves = (ArrayList<SaleShelf>) savingStockItems.stream().map(item -> {
            SaleShelf saleShelf = new SaleShelf();
            saleShelf.organization = organization;
            saleShelf.price = item.price-10;
            saleShelf.saleCategory = SaleShelf.CATEGORY_ACCESSORY;
            saleShelf.skuItem = item;
            return saleShelf;
        }).collect(Collectors.toList());

        skuItemRepository.save(savingStockItems);
        saleShelfRepository.save(savingSaleShelves);

        StockItem boschWiper = new StockItem();
        boschWiper.name = "博世无骨雨刮片";
        boschWiper.brandName = "BOSCH";
        boschWiper.cost = 88;
        boschWiper.price = 110;
        boschWiper.accessoryCategory = StockItem.CATEGORY_ACCESSORY_INVENTORY;
        skuItemRepository.save(boschWiper);
        createSaleShelf(organization, boschWiper, 110, SaleShelf.CATEGORY_ACCESSORY);

        StockItem ngk = new StockItem();
        ngk.name = "NGK 双铂金火花塞PZFR6R 5758四支装";
        ngk.brandName = "NGK";
        ngk.cost = 350;
        ngk.price = 400;
        ngk.accessoryCategory = StockItem.CATEGORY_ACCESSORY_INVENTORY;
        skuItemRepository.save(ngk);
        createSaleShelf(organization, ngk, 400, SaleShelf.CATEGORY_ACCESSORY);

        StockItem bosch = new StockItem();
        bosch.name = "博世（BOSCH）精装铂金高性能火花塞";
        bosch.brandName = "NGK";
        bosch.cost = 360;
        bosch.price = 420;
        bosch.accessoryCategory = StockItem.CATEGORY_ACCESSORY_INVENTORY;
        skuItemRepository.save(bosch);
        createSaleShelf(organization, bosch, 420, SaleShelf.CATEGORY_ACCESSORY);


        StockItem engineOil1 = new StockItem();
        engineOil1.name = "壳牌/Shell　超凡喜力全合成机油 ULTRA 5w-30 SL 灰壳 (4L装)";
        engineOil1.brandName = "Shell";
        engineOil1.price = 298;
        engineOil1.accessoryCategory = StockItem.CATEGORY_ACCESSORY_BATTERY;
        skuItemRepository.save(engineOil1);
        createSaleShelf(organization, engineOil1, 298, SaleShelf.CATEGORY_CARE);

        StockItem engineOil2 = new StockItem();
        engineOil2.name = "壳牌/Shell喜力半合成机油HX7 5w-40 SN/CF级蓝壳(4L装)";
        engineOil2.brandName = "Shell";
        engineOil2.price = 208;
        engineOil2.accessoryCategory = StockItem.CATEGORY_ACCESSORY_BATTERY;
        skuItemRepository.save(engineOil2);
        createSaleShelf(organization, engineOil2, 208, SaleShelf.CATEGORY_CARE);

        StockItem engineOil3 = new StockItem();
        engineOil3.name = "壳牌/Shell喜力矿物机油HX5 10w-40 SN级黄壳(1L装)";
        engineOil3.brandName = "Shell";
        engineOil3.price = 49;
        engineOil3.accessoryCategory = StockItem.CATEGORY_ACCESSORY_BATTERY;
        skuItemRepository.save(engineOil3);
        createSaleShelf(organization, engineOil3, 49, SaleShelf.CATEGORY_CARE);

        StockItem airConditionerFilter1 = new StockItem();
        airConditionerFilter1.name = "马勒/MAHLE 空调滤清器 LX3975";
        airConditionerFilter1.brandName = "MAHLE";
        airConditionerFilter1.price = 92;
        airConditionerFilter1.accessoryCategory = StockItem.CATEGORY_ACCESSORY_MOUNTED;
        skuItemRepository.save(airConditionerFilter1);
        createSaleShelf(organization, airConditionerFilter1, 92, SaleShelf.CATEGORY_CARE);

        StockItem airConditionerFilter2 = new StockItem();
        airConditionerFilter2.name = "马勒/MAHLE 空调滤清器 LAK895";
        airConditionerFilter2.brandName = "MAHLE";
        airConditionerFilter2.price = 52;
        airConditionerFilter2.accessoryCategory = StockItem.CATEGORY_ACCESSORY_MOUNTED;
        skuItemRepository.save(airConditionerFilter2);
        createSaleShelf(organization, airConditionerFilter2, 52, SaleShelf.CATEGORY_CARE);

        // 洗车套餐模板,没有二级分类
        Suite carWashSuite1 = new Suite();
        carWashSuite1.suiteType = Suite.SUITE_TYPE_SERVICES;
        //carWashSuite1.saleCategory = SaleShelf.CATEGORY_WASH_CAR;
        carWashSuite1.price = 500;
        carWashSuite1.name = "超值洗车套餐500十次哦";
        SuiteItem carWashSuiteItem1 = new SuiteItem();
        carWashSuiteItem1.times = 10;
        carWashSuiteItem1.skuItem = carWashServiceItem;
        carWashSuite1.suiteItems = new ArrayList<>();
        carWashSuite1.suiteItems.add(carWashSuiteItem1);
        suiteRepository.save(carWashSuite1);
        carWashSuite1.expiation = 365;
        carWashSuite1.description = "洗车套餐描述1";

        Suite carWashSuite2 = new Suite();
        carWashSuite2.suiteType = Suite.SUITE_TYPE_SERVICES;
        //carWashSuite2.saleCategory = SaleShelf.CATEGORY_WASH_CAR;
        carWashSuite2.price = 1000;
        carWashSuite2.name = "超值洗车套餐1000每年不限次数";
        carWashSuite2.suiteItems = new ArrayList<>();
        SuiteItem carWashSuiteItem2 = new SuiteItem();
        carWashSuiteItem2.times = -1;
        carWashSuite2.expiation = 365;
        carWashSuite2.description = "洗车套餐描述2";
        carWashSuiteItem2.skuItem = carWashServiceItem;
        carWashSuite2.suiteItems.add(carWashSuiteItem2);
        suiteRepository.save(carWashSuite2);

        // 美容套餐模板
        List<ServiceItem> serviceItems = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            ServiceItem serviceItem = new ServiceItem();
            serviceItem.name = "美容项目" + i;
            serviceItem.price = 20 + i;
            serviceItems.add(serviceItem);
        }
        skuItemRepository.save(serviceItems);

        // vip套餐模板
        List<Suite> vipSuites = new ArrayList<>();

        ServiceItem vipItem1 = new ServiceItem();
        vipItem1.name = "无限次洗车项目";
        vipItem1.price = 20;
        vipItem1.needAppointment = false;
        skuItemRepository.save(vipItem1);
        ServiceItem vipItem2 = new ServiceItem();
        vipItem2.name = "无限次美容项目";
        vipItem2.price = 30;
        skuItemRepository.save(vipItem2);
        ServiceItem vipItem3 = new ServiceItem();
        vipItem3.name = "精品美容";
        vipItem3.price = 40;
        skuItemRepository.save(vipItem3);
        ServiceItem vipItem4 = new ServiceItem();
        vipItem4.name = "精品保养";
        vipItem4.price = 50;
        skuItemRepository.save(vipItem4);

        for (int i = 0; i < 5; i++) {
            Suite vipSuite = new Suite();
            vipSuite.suiteType = Suite.SUITE_TYPE_VIP;
            //vipSuite.saleCategory = SaleShelf.CATEGORY_VIP;
            vipSuite.price = (i+1)*1000;
            vipSuite.name = "vip套餐" + i;
            vipSuite.expiation = 365;
            vipSuite.description = "套餐描述套餐描述套餐描述套餐描述套餐描述套餐描述";
            vipSuite.suiteItems = new ArrayList<>();
            {
                SuiteItem suiteItem = new SuiteItem();
                suiteItem.saleCategory = SaleShelf.CATEGORY_WASH_CAR;
                suiteItem.skuItem = vipItem1;
                vipSuite.suiteItems.add(suiteItem);
            }
            {
                SuiteItem suiteItem = new SuiteItem();
                suiteItem.saleCategory = SaleShelf.CATEGORY_BEAUTIFY;
                suiteItem.skuItem = vipItem2;
                vipSuite.suiteItems.add(suiteItem);
            }
            {
                SuiteItem suiteItem = new SuiteItem();
                suiteItem.times = 10;
                suiteItem.saleCategory = SaleShelf.CATEGORY_BEAUTIFY;
                suiteItem.skuItem = vipItem4;
                vipSuite.suiteItems.add(suiteItem);
            }
            {
                SuiteItem suiteItem = new SuiteItem();
                suiteItem.times = 4;
                suiteItem.saleCategory = SaleShelf.CATEGORY_CARE;
                suiteItem.skuItem = vipItem4;
                vipSuite.suiteItems.add(suiteItem);
            }
            vipSuites.add(vipSuite);
        }
        suiteRepository.save(vipSuites);

        // 上架模板套餐
        createSaleShelf(organization, carWashSuite1, 400, SaleShelf.CATEGORY_WASH_CAR);

        // 上架自定义套餐
        CustomSuite customCarWashSuite2 = new CustomSuite();
        customCarWashSuite2.organization = organization;
        customCarWashSuite2.suiteType = Suite.SUITE_TYPE_SERVICES;
        customCarWashSuite2.price = 1200;
        customCarWashSuite2.name = "自定义超值洗车套餐1000每年不限次数";
        customCarWashSuite2.description = "自定义洗车大套餐描述";
        customCarWashSuite2.suiteItems = new ArrayList<>();
        SuiteItem customCarWashSuiteItem2 = new SuiteItem();
        customCarWashSuiteItem2.times = -1;
        customCarWashSuite2.expiation = 365;
        customCarWashSuiteItem2.skuItem = carWashServiceItem;
        customCarWashSuite2.suiteItems.add(customCarWashSuiteItem2);
        suiteRepository.save(customCarWashSuite2);
        createSaleShelf(organization, customCarWashSuite2, 1200, SaleShelf.CATEGORY_WASH_CAR);

        // 上架美容项目
        serviceItems.forEach(serviceItem -> {
            createSaleShelf(organization, serviceItem, serviceItem.price, SaleShelf.CATEGORY_BEAUTIFY);
        });

        // 上架vip套餐
        for (int i = 0; i < 5; i++) {
            createSaleShelf(organization, vipSuites.get(i), (i+1)*1000, SaleShelf.CATEGORY_VIP);
        }

        // 店铺保养套餐
        int[] miles = {10000,15000,20000,25000};
        int[] month = {3,6,12,18};
        for (int i = 0; i < miles.length; i++) {
            CareSuiteGroupItem careSuiteGroupItem1 = new CareSuiteGroupItem();
            careSuiteGroupItem1.skuItem = engineOil1;
            careSuiteGroupItem1.suitePrice = 278;
            careSuiteGroupItem1.forClazz = VehicleModel.VehicleClass.C;

            CareSuiteGroupItem careSuiteGroupItem2 = new CareSuiteGroupItem();
            careSuiteGroupItem2.skuItem = engineOil2;
            careSuiteGroupItem2.suitePrice = 188;
            careSuiteGroupItem2.forClazz = VehicleModel.VehicleClass.B;

            CareSuiteGroupItem careSuiteGroupItem3 = new CareSuiteGroupItem();
            careSuiteGroupItem3.skuItem = engineOil3;
            careSuiteGroupItem3.suitePrice = 44;
            careSuiteGroupItem3.forClazz = VehicleModel.VehicleClass.A;

            CareSuiteGroup careSuiteGroup1 = new CareSuiteGroup();
            careSuiteGroup1.careSuiteGroupItems = new ArrayList<>();
            careSuiteGroup1.careSuiteGroupItems.add(careSuiteGroupItem1);
            careSuiteGroup1.careSuiteGroupItems.add(careSuiteGroupItem2);
            careSuiteGroup1.careSuiteGroupItems.add(careSuiteGroupItem3);

            CareSuiteGroupItem careSuiteGroupItem4 = new CareSuiteGroupItem();
            careSuiteGroupItem4.skuItem = airConditionerFilter1;
            careSuiteGroupItem4.suitePrice = 88;
            careSuiteGroupItem4.forClazz = VehicleModel.VehicleClass.C;

            CareSuiteGroupItem careSuiteGroupItem5 = new CareSuiteGroupItem();
            careSuiteGroupItem5.skuItem = airConditionerFilter2;
            careSuiteGroupItem5.suitePrice = 45;
            careSuiteGroupItem5.forClazz = VehicleModel.VehicleClass.B;

            CareSuiteGroup careSuiteGroup2 = new CareSuiteGroup();
            careSuiteGroup2.careSuiteGroupItems = new ArrayList<>();
            careSuiteGroup2.careSuiteGroupItems.add(careSuiteGroupItem4);
            careSuiteGroup2.careSuiteGroupItems.add(careSuiteGroupItem5);

            CareSuite careSuite = new CareSuite();
            careSuite.mileage = miles[i];
            careSuite.month = month[i];
            careSuite.careSuiteGroups = new ArrayList<>();
            careSuite.careSuiteGroups.add(careSuiteGroup1);
            careSuite.careSuiteGroups.add(careSuiteGroup2);
            careSuiteRepository.save(careSuite);

            // 设置自定义价格
            customizeCareSuiteGroupItemPrice(organization, careSuiteGroupItem1);
            customizeCareSuiteGroupItemPrice(organization, careSuiteGroupItem2);
            customizeCareSuiteGroupItemPrice(organization, careSuiteGroupItem3);
            customizeCareSuiteGroupItemPrice(organization, careSuiteGroupItem4);
            customizeCareSuiteGroupItemPrice(organization, careSuiteGroupItem5);
        }

        // APP顾客车辆信息
        VehicleInfo vehicleInfo = new VehicleInfo();
        VehicleModel vehicleModel = vehicleModelRepository.findOne(10001l);
        Customer customer = customerRepository.findOne(10001l);
        CustomerAppProfile profile = customerProfileRepository.findAppProfileByCustomer(customer);
        //customer.vehicles = new ArrayList<>();
        profile.vehicles.add(vehicleInfo);
        vehicleInfo.mileage = 16000;
        vehicleInfo.onRoadDate = DateTime.parse("2014-05-10");
        vehicleInfo.model = vehicleModel;
        customerProfileRepository.save(profile);

        // ERP顾客车辆信息，创建App顾客时，这些信息应该被拷贝过去
        vehicleInfo = new VehicleInfo();
        customer = customerRepository.findOne(10003l);
        CustomerERPProfile erpProfile = new CustomerERPProfile();
        erpProfile.customer = customer;
        erpProfile.vehicles = new ArrayList<>();
        erpProfile.vehicles.add(vehicleInfo);
        vehicleInfo.mileage = 20000;
        vehicleInfo.onRoadDate = DateTime.parse("2013-05-10");
        vehicleInfo.model = vehicleModel;
        customerProfileRepository.save(erpProfile);
    }

    public void customizeCareSuiteGroupItemPrice(Organization organization, CareSuiteGroupItem careSuiteGroupItem1) {
        CareSuiteGroupItemPrice customPrice = new CareSuiteGroupItemPrice();
        customPrice.organization = organization;
        customPrice.careSuiteGroupItem = careSuiteGroupItem1;
        customPrice.overriddenPrice = 200.0; // 278
        careSuiteGroupItemPriceRepository.save(customPrice);
    }

    public void createSaleShelf(Organization organization, Suite suite,
                                double price, int saleCategory) {
        SaleShelf saleShelf = new SaleShelf();
        saleShelf.organization = organization;
        saleShelf.price = price;
        saleShelf.saleCategory = saleCategory;
        saleShelf.suite = suite;
        saleShelfRepository.save(saleShelf);
    }

    public void createSaleShelf(Organization organization, SkuItem sukItem,
                                double price, int saleCategory) {
        SaleShelf saleShelf = new SaleShelf();
        saleShelf.organization = organization;
        saleShelf.price = price;
        saleShelf.saleCategory = saleCategory;
        saleShelf.skuItem = sukItem;
        saleShelfRepository.save(saleShelf);
    }

    public void createCampaigns(){
        Shop shop = shopRepository.findOne(10001l);
        City cityGZ = new City();
        cityGZ.name = "guangzhou";
        cityGZ.area = City.AREA_SOUTH;
        cityRepository.save(cityGZ);
        double[][] coordinates = {{23.143682,113.328572}, {23.137568,113.31348},
                {23.134377,113.372697},{23.392413,113.3096},};
        for (int i = 0; i < 4; i++) {
            Campaign campaign = new Campaign();
            campaign.city =cityGZ;
            campaign.bannerImageUrl = "no url";
            campaign.compaignType = Campaign.CAMPAIGN_TYPE_GENERAL;
            campaign.onBanner = true;
            campaign.shop = shop;
            campaign.summary = "GENERAL促销活动" + i;
            campaign.url = "http://www.163.com";
            campaign.bannerImageUrl = "http://www.carmore.cc:8084/stylesheets/images/erp/index_logo.png";
            campaign.latitude = coordinates[i][0];
            campaign.longitude = coordinates[i][1];
            campaignRepository.save(campaign);
        }
        for (int i = 0; i < 4; i++) {
            Campaign campaign = new Campaign();
            campaign.city =cityGZ;
            campaign.compaignType = Campaign.CAMPAIGN_TYPE_CITY;
            campaign.onBanner = true;
            campaign.summary = "CITY促销活动" + i;
            campaign.url = "http://www.163.com";
            campaign.bannerImageUrl = "http://www.carmore.cc:8084/stylesheets/images/erp/index_logo.png";
            campaign.latitude = coordinates[i][0];
            campaign.longitude = coordinates[i][1];
            campaignRepository.save(campaign);
        }
        for (int i = 0; i < 4; i++) {
            Campaign campaign = new Campaign();
            campaign.bannerImageUrl = "no url";
            campaign.city =cityGZ;
            campaign.compaignType = Campaign.CAMPAIGN_TYPE_NEARBY;
            campaign.onBanner = false;
            campaign.summary = "NEARBY促销活动" + i;
            campaign.url = "http://www.163.com";
            campaign.bannerImageUrl = "http://www.carmore.cc:8084/stylesheets/images/erp/index_logo.png";
            campaign.latitude = coordinates[i][0];
            campaign.longitude = coordinates[i][1];
            campaignRepository.save(campaign);
        }
    }

    public void initSettleOrderAndComments() {
        Shop shop = shopRepository.findOne(10001l);

        Customer customer = customerRepository.findOne(10001l);
        CustomerAppProfile profile = customerProfileRepository.findAppProfileByCustomer(customer);
        for (int i = 0; i < 20; i++) {
            SettleOrder settleOrder = new SettleOrder();
            settleOrder.orderDetails = new ArrayList<>();
            OrderDetail orderDetail = new OrderDetail();
            SkuItem skuItem;
            if (i < 5) {
                skuItem = skuItemRepository.findOne(4l);
                settleOrder.saleCategory = SaleShelf.CATEGORY_ACCESSORY;
            } else if (i < 10) {
                skuItem = skuItemRepository.findOne(4l);
                settleOrder.saleCategory = SaleShelf.CATEGORY_CARE;
            } else {
                skuItem = skuItemRepository.findOne(carWashServiceItem.id);
                settleOrder.saleCategory = SaleShelf.CATEGORY_WASH_CAR;
            }
            orderDetail.orderedItem = skuItem;
            settleOrder.orderDetails.add(orderDetail);
            settleOrder.customer = customer;
            settleOrder.shop = shop;
            if (i < 10) {
                settleOrder.commented = true;
                //settleOrder.createdDate = DateTime.now().plusDays(i);
            }
            settleOrderRepository.save(settleOrder);
            if (i < 10) {
                Comment comment = new Comment();
                comment.settleOrder = settleOrder;
                comment.shop = shop;
                comment.customer = profile;
                comment.comment = "全套服务周到,对的起这个价钱,下次介绍朋友来,不是托,全套服务周到,对的起这个价钱,下次介绍朋友来,不是托";
                commentRepository.save(comment);
            }
        }
        final int[] i = {0};
        settleOrderRepository.findAll().forEach(settleOrder -> {
            i[0]++;
            settleOrder.createdDate = DateTime.now().plusDays(i[0]);
            settleOrderRepository.save(settleOrder);

        });
    }

    public void initOrders() {
        Customer customer = customerRepository.findOne(10001l);
        Organization organization = organizationRepository.findOne(3l);
        Suite carWashSuite = (Suite) saleShelfRepository.findSuiteByOrganizationAndSaleCategory(
                organization, SaleShelf.CATEGORY_WASH_CAR).get(0)[0];
        Suite vipSuite = (Suite) saleShelfRepository.findSuiteByOrganizationAndSaleCategory(
                organization, SaleShelf.CATEGORY_VIP).get(0)[0];

        VehicleInfo vehicleInfo = vehicleInfoRepository.findOne(10001l);

        // Create CareSuite order
        List<Long> items = new ArrayList<>();
        items.add(8l);
        items.add(9l);
        paymentController.createCareOrder(10001l, items, 2l, vehicleInfo.uid, DateTime.now(), customer, UUID.randomUUID(),
                initPayment(100.0, customer, "test123"));

        // Create Suite order
        paymentController.createSuiteOrder(10001l, carWashSuite.id, customer, UUID.randomUUID(),
                initPayment(100.0, customer, "test123"));
        paymentController.createSuiteOrder(10001l, vipSuite.id, customer, UUID.randomUUID(),
                initPayment(100.0, customer, "test123"));

        // Create Accessory order
        for (int i = 0; i < 20; i ++) {
            paymentController.createAccessoryOrder(10001l, 4l, 2, vehicleInfo.uid, DateTime.now(), customer, UUID.randomUUID(),
                    SaleShelf.CATEGORY_ACCESSORY, initPayment(100.0, customer, "test123"));
        }
    }

    public Payment initPayment(double amount, Customer customer, String chargeId) {
        Payment payment = new Payment();
        payment.amount = 100.0;
        payment.customer = customer;
        payment.chargeId ="test123";
        return paymentRepository.save(payment);
    }

    public void initRecommendAccessories() {
        Specification<StockItem> spec = (root, query, cb) -> cb.notEqual(root.get("accessoryCategory"), StockItem.CATEGORY_ACCESSORY_TIRE);
        List<StockItem> stockItems = stockItemRepository.findAll(Specifications.where(spec));
        stockItems.forEach(stockItem -> {
            RecommendedAccessory r = new RecommendedAccessory();
            r.price = 200;
            r.skuItem = stockItem;
            recommendedAccessoryRepository.save(r);
        });
    }

}