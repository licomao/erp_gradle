package com.daqula.carmore.controller;

import com.daqula.carmore.model.admin.SecondaryCategory;
import com.daqula.carmore.model.admin.Suite;
import com.daqula.carmore.model.admin.Supplier;
import com.daqula.carmore.model.customer.*;
import com.daqula.carmore.model.order.Payment;
import com.daqula.carmore.model.order.SettleOrder;
import com.daqula.carmore.model.shop.*;
import com.daqula.carmore.repository.*;
import com.daqula.carmore.util.OrderUtil;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by mdc on 2016/2/4.
 */
@Controller
public class HistoryCardController {

    @Autowired
    private HistoryCareSuiteItemRepository historyCareSuiteItemRepository;

    @Autowired
    private HistoryCustomerCardSuiteRepository historyCustomerCardSuiteRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerERPProfileRepository customerERPProfileRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private SettleOrderRepository settleOrderRepository;

    @Autowired
    private CustomerPurchasedSuiteRepository customerPurchasedSuiteRepository;

    @Autowired
    private CustomStockItemRepository customStockItemRepository;

    @Autowired
    private CustomerPurchasedSuiteItemRepository customerPurchasedSuiteItemRepository;

    @Autowired
    private SuiteRepository suiteRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private SecondaryCategoryRepository secondaryCategoryRepository;

    @Autowired
    private SupplierRepository supplierRepository;


    @RequestMapping(value = "/histroycard/copydata", method = RequestMethod.GET)
    public ModelAndView toCopyData(){
        ModelAndView   mav = new ModelAndView("/message");
        Organization tyre = organizationRepository.findOne(3L);
        Organization ycy = organizationRepository.findOne(18L);


        int secondaryNum = this.saveSecondary(tyre,ycy);
        int supplierNum = this.saveSupplier(tyre,ycy);
        int stockNum = this.saveCustomStock(tyre,ycy);
        //获取泰尔二级分类,供应商,商品
        mav.addObject("responsePage", "/index");
        mav.addObject("message", "导入成功"+supplierNum + "," +secondaryNum + "," + stockNum);
        return  mav;
    }

    @Transactional(readOnly = true)
    public SecondaryCategory getSecondary(CustomStockItem stock,Organization ycy){
        if (stock.secondaryCategory == null) {
            return null;
        }
        List<SecondaryCategory> sec = secondaryCategoryRepository.findByOrganizationAndNameAndAdditionRate(ycy,stock.secondaryCategory.name,stock.secondaryCategory.additionRate);
        if (sec.size() > 0){
            return sec.get(0);
        }
        return null;
    }
    @Transactional(readOnly = true)
    public Supplier getSupplier(CustomStockItem stock,Organization ycy){
        List<Supplier> supplier = supplierRepository.findByOrganizationAndName(ycy, stock.supplier.name);
        if (supplier.size() > 0){
            return supplier.get(0);
        }
        return null;
    }

    @Transactional
    public int saveCustomStock(Organization tyre,Organization ycy){
        int stockNum = 0;
        List<CustomStockItem> customStockItems = customStockItemRepository.findByOrganization(tyre);
        for(CustomStockItem stock : customStockItems){
            CustomStockItem csi = new CustomStockItem();
            csi.organization = ycy;
            csi.barCode = stock.barCode;
            csi.name = stock.name;
            csi.brandName = stock.brandName;
            csi.rootCategory = stock.rootCategory;
            csi.secondaryCategory = this.getSecondary(stock,ycy);
            csi.supplier = this.getSupplier(stock,ycy);
            csi.isDistribution  = stock.isDistribution;
            csi.cost = stock.cost;
            customStockItemRepository.save(csi);
            stockNum++;
        }
        return stockNum;
    }

    @Transactional
    public int saveSecondary(Organization tyre,Organization ycy){
        int secondaryNum = 0;
        List<SecondaryCategory> secondaryCategories = secondaryCategoryRepository.findByOrganization(tyre);
        for (SecondaryCategory secondaryCategory : secondaryCategories){
            SecondaryCategory  ycySecondary = new SecondaryCategory();
            ycySecondary.name = secondaryCategory.name;
            ycySecondary.rootCategory = secondaryCategory.rootCategory;
            ycySecondary.organization = ycy;
            ycySecondary.additionRate = secondaryCategory.additionRate;
            secondaryCategoryRepository.save(ycySecondary);
            secondaryNum++;
        }
        return secondaryNum;
    }

    @Transactional
    public int saveSupplier(Organization tyre,Organization ycy){
        int supplierNum = 0;
        List<Supplier> suppliers = supplierRepository.findByOrganization(tyre);
        for (Supplier supplier : suppliers){
            Supplier ycySupplier = new Supplier();
            ycySupplier.name = supplier.name;
            ycySupplier.contactInfo = supplier.contactInfo;
            ycySupplier.organization = ycy;
            supplierRepository.save(ycySupplier);
            supplierNum++;
        }
        return supplierNum;
    }

    /*******  会员卡导入source   ******/
    @RequestMapping(value = "/histroycard/save", method = RequestMethod.GET)
    public ModelAndView toNewCard(){
        ModelAndView mav;
        List<HistoryCustomerCardSuite> cardSuites = historyCustomerCardSuiteRepository.findHistoryInfo();
        List<HistoryCareSuiteItem> items;
        int i = 0;
        for (HistoryCustomerCardSuite hcs : cardSuites){
            if (i == 500) {
                break;
            }
            saveInfo(hcs);
            i++;
        }
        String message = "保存成功"+ i +"条";
        mav = new ModelAndView("/message");
        mav.addObject("responsePage", "/customerpurchasesuite/list");
        mav.addObject("message", message);
        return mav;
    }

    @Transactional
    public void saveInfo(HistoryCustomerCardSuite hcs){
        try {
            List<HistoryCareSuiteItem> items = historyCareSuiteItemRepository.getItemsInfo(hcs.cardNo);
            Organization organization = organizationRepository.findOne(Long.parseLong("27"));
            List<Customer> customers = customerRepository.findByMobile(hcs.mobile);
            Customer customer ;
            if (customers.size() == 0) {
                customer = new Customer();
                customer.token = UUID.randomUUID().toString();
                customer.mobile = hcs.mobile;
                customer = customerRepository.save(customer);
            } else {
                customer = customers.get(0);
            }

            CustomerERPProfile erpProfile = customerERPProfileRepository.findByOrganizationAndCustomer(organization,customer);
            if (erpProfile == null) {
                erpProfile = new CustomerERPProfile();
                erpProfile.organization = organization;
                erpProfile.realName = hcs.name;
                erpProfile.customer = customer;
                erpProfile = customerERPProfileRepository.save(erpProfile);
            }
            List<CustomerPurchasedSuite> customerSuites = customerPurchasedSuiteRepository.findByCustomer(customer);
            CustomerPurchasedSuite customerPurchasedSuite = new CustomerPurchasedSuite();
            List<Suite> suites = suiteRepository.findByNameLike("%" + hcs.cardName + "%");
            if (suites.size() > 0) {
                customerPurchasedSuite.suite = suites.get(0);
            } else {
                Suite suite = suiteRepository.findOne(Long.parseLong("45"));
                if (suite == null) {
                    suite = suiteRepository.findOne(Long.parseLong("1"));
                }
                customerPurchasedSuite.suite = suite;
            }
            customerPurchasedSuite.customer = erpProfile.customer;
            SettleOrder settleOrder = new SettleOrder();
            settleOrder.customer = erpProfile.customer;
            settleOrder.close = false;
            settleOrder.commented = false;
            settleOrder.isFinish = true;
            Staff staff = staffRepository.findByIdentityCard("310110197712223847");
            settleOrder.receiver = staff;
            Shop shop = shopRepository.findByName(hcs.shop);
            if (shop != null) {
                Payment payment = new Payment();
                payment.amount = 0;
                payment.customer = customerPurchasedSuite.customer;
                payment = paymentRepository.save(payment);
                settleOrder.shop = shop;
                settleOrder.payment = payment;
                Long count = settleOrderRepository.findMaxOrderNum(shop);
                count = count - 1;
                do {
                    count++;
                } while (settleOrderRepository.findBySaleNoAndShop(count, settleOrder.shop) != null);

                String saleNoView = OrderUtil.getViewOrderNumber(organization, shop, OrderUtil.ORDER_TYPE_SALE, count);
                settleOrder.saleNo = count;
                settleOrder.saleNoView = saleNoView;
                settleOrder.saleCategory = SaleShelf.CATEGORY_VIP;
                customerPurchasedSuite.shop = shop;
                customerPurchasedSuite.startDate = new DateTime();
                customerPurchasedSuite.enabled = true;
                customerPurchasedSuite.staff = staff;
                CustomerPurchasedSuite purchasedSuite = customerPurchasedSuiteRepository.save(customerPurchasedSuite);
                for (HistoryCareSuiteItem hcsi : items) {
                    CustomerPurchasedSuiteItem customerPurchasedSuiteItem = new CustomerPurchasedSuiteItem();
                    CustomStockItem customStockItem;
                    List<CustomStockItem> customStockItems = customStockItemRepository.findByNameAndOrganization(hcsi.cardDetailName, organization);
                    if (customStockItems.size() > 0) {
                        customStockItem = customStockItems.get(0);
                        customerPurchasedSuiteItem.customStockItem = customStockItem;
                        customerPurchasedSuiteItem.cost = 0.00;
                        customerPurchasedSuiteItem.times = hcsi.number;
                        customerPurchasedSuiteItem.usedTimes = 0;
                        customerPurchasedSuiteItem.purchasedSuite = customerPurchasedSuite;
                        customerPurchasedSuiteItemRepository.save(customerPurchasedSuiteItem);
                    } else {
                        continue;
                    }
                }
                settleOrder.customerPurchasedSuite = purchasedSuite;
                SettleOrder order = settleOrderRepository.save(settleOrder);
                purchasedSuite.settleOrderId = order.id;
            }

            hcs.deleted = false;
            historyCustomerCardSuiteRepository.save(hcs);
            Thread t = new Thread();
            t.sleep(100);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
