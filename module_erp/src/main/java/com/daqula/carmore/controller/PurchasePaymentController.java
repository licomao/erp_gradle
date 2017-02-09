package com.daqula.carmore.controller;

import com.daqula.carmore.model.acl.ERPUser;
import com.daqula.carmore.model.admin.Supplier;
import com.daqula.carmore.model.order.PurchaseOrder;
import com.daqula.carmore.model.order.PurchasePayment;
import com.daqula.carmore.model.shop.Organization;
import com.daqula.carmore.model.shop.Shop;
import com.daqula.carmore.repository.*;
import com.daqula.carmore.repository.specification.PurchasePaymentSpecifications;
import com.daqula.carmore.util.JqGridDataGenerator;
import com.daqula.carmore.util.SessionUtil;
import com.daqula.carmore.util.StringUtil;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.*;

import static com.daqula.carmore.ErrorCode.OK;

/**
 * 付款单控制器
 * Created by mdc on 2016/1/21.
 */
@Controller
public class PurchasePaymentController {

    @Autowired
    private PurchasePaymentRepository purchasePaymentRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    /**
     * 未付款采购单查询
     * @param erpUser
     * @return
     */
    @RequestMapping(value="/purchasepayment/willpaylist",method = RequestMethod.GET)
    public ModelAndView findPurchaseWillPayList(@AuthenticationPrincipal ERPUser erpUser, HttpSession session){
        ModelAndView mav = new ModelAndView("/purchasepayment/willpaylist");
        List<Shop> shops = SessionUtil.getShopList(session, organizationRepository, shopRepository);
        List<Supplier> suppliers = supplierRepository.findByOrganizationAndDeleted(erpUser.organization, false);
        mav.addObject("suppliers",suppliers);
        mav.addObject("shops",shops);
        return mav;
    }

    /**
     * 未付款或未结清采购单信息查询
     * @param page
     * @param rows
     * @param sord
     * @param sidx
     * @param purchaseOrder
     * @param user
     * @return
     */
    @RequestMapping(value = "/purchasepayment/willpaylist/data", method = RequestMethod.GET)
    @ResponseBody
    @Transactional(readOnly = true)
    public Map<String, Object> listData(@RequestParam int page, @RequestParam int rows,
                                        @RequestParam String sord, @RequestParam String sidx,PurchaseOrder purchaseOrder, @AuthenticationPrincipal ERPUser user){
        int totalCount = purchasePaymentRepository.findPurchasePaymentsCount(purchaseOrder);
        List<PurchaseOrder> list = purchasePaymentRepository.findPurchasePayments(purchaseOrder, page, rows);
        for (PurchaseOrder po : list){
            po.unspentCost = po.getCostSum() - purchasePaymentRepository.findUnspentBalanceByPurchaseOrder(po);
        }
        return JqGridDataGenerator.getNativeDataJson(list, totalCount, rows, page);
    }

    /**
     * 进入付款记录填写
     * @param erpUser
     * @param purchaseId
     * @return
     */
    @RequestMapping(value="/purchasepayment/payment",method = RequestMethod.GET)
    public ModelAndView toPayment(@AuthenticationPrincipal ERPUser erpUser, String purchaseId){
        ModelAndView mav = new ModelAndView("/purchasepayment/payment");
        if(StringUtil.IsNullOrEmpty(purchaseId)){
            mav.setViewName("/message");
            mav.addObject("message", "进入页面失败");
            mav.addObject("responsePage", "/purchasepayment/willpaylist");
            return mav;
        }
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findOne(Long.parseLong(purchaseId));
        Double unspentBalance = purchasePaymentRepository.findUnspentBalanceByPurchaseOrder(purchaseOrder);
        PurchasePayment purchasePayment = new PurchasePayment();
        purchasePayment.purchaseOrder = purchaseOrder;
        purchasePayment.supplier = purchaseOrder.supplier;
        mav.addObject("unspentBalance",unspentBalance);
        mav.addObject("purchasePayment",purchasePayment);
        return mav;
    }


    /**
     * 保存付款单
     * @param erpUser
     * @param purchasePayment
     * @return
     */
    @RequestMapping(value="/purchasepayment/save",method = RequestMethod.POST)
    public ModelAndView savePayment(@AuthenticationPrincipal ERPUser erpUser, PurchasePayment purchasePayment){
        ModelAndView mav = new ModelAndView("/message");
        Organization organization = organizationRepository.findOne(erpUser.organization.id);
        purchasePayment.organization = organization;
        PurchasePayment purchasePaymentSave = purchasePaymentRepository.save(purchasePayment);
        if (purchasePaymentSave != null) {
            mav.addObject("message", "保存成功");
            mav.addObject("responsePage", "/purchasepayment/willpaylist");
        } else {
            mav.addObject("message", "保存失败!");
            mav.addObject("responsePage", "/purchasepayment/payment?purchaseId=" + purchasePayment.purchaseOrder.id);
        }
        return mav;
    }

    /**
     * 已付款记录查询
     * @param erpUser
     * @return
     */
    @RequestMapping(value="/purchasepayment/paymentlist",method = RequestMethod.GET)
    public ModelAndView listPurchasePayment(@AuthenticationPrincipal ERPUser erpUser,String type,HttpSession session){
        ModelAndView mav = new ModelAndView("/purchasepayment/paymentlist");
        Organization organization = organizationRepository.findOne(erpUser.organization.id);
        mav.addObject("org",organization);
        mav.addObject("type",type);//可否作废
        List<Supplier> suppliers = supplierRepository.findByOrganizationAndDeleted(erpUser.organization, false);
        mav.addObject("suppliers",suppliers);
        List<Shop> shops = SessionUtil.getShopList(session, organizationRepository, shopRepository);
        mav.addObject("shops",shops);
        return  mav;
    }

    @RequestMapping(value = "/purchasepayment/paymentlist/data", method = RequestMethod.GET)
    @ResponseBody
    @Transactional(readOnly = true)
    public Map<String, Object> paymentListData(@RequestParam int page, @RequestParam int rows,
                                        @RequestParam String sord, @RequestParam String sidx,String orgId,String supplierId, int payType,String orderNumber
                                        ,boolean deleted ,Long shopId ,int purchaseType){
        Organization organization = organizationRepository.findOne(Long.parseLong(orgId));
        Supplier supplier = null;
        if(!StringUtil.IsNullOrEmpty(supplierId)){
            supplier = supplierRepository.findOne(Long.parseLong(supplierId));
        }
        if (StringUtil.IsNullOrEmpty(orderNumber))orderNumber="";
        orderNumber = "%"+orderNumber +"%";
        PageRequest pageRequest = new PageRequest(page-1, rows,
                sord.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
                !StringUtils.isEmpty(sidx) ? sidx : "id");
        Shop shop = shopRepository.findOne(shopId);
        Specifications spec = Specifications.where(PurchasePaymentSpecifications.filterByDeleted(deleted))
                .and(PurchasePaymentSpecifications.filterByOrganization(organization))
                .and(PurchasePaymentSpecifications.filterByPayType(payType))
                .and(PurchasePaymentSpecifications.filterBySupplier(supplier))
                .and(PurchasePaymentSpecifications.filterByPurchaseOrder(orderNumber))
                .and(PurchasePaymentSpecifications.filterByPurchaseOrderPurchaseType(purchaseType))
                .and(PurchasePaymentSpecifications.filterByPurchaseOrderShop(shop));
        Page pageData = purchasePaymentRepository.findAll(spec, pageRequest);
        return JqGridDataGenerator.getDataJson(pageData);
    }



    @RequestMapping(value = "/purchasepayment/orderview", method = RequestMethod.GET)
    @Transactional(readOnly = true)
    public ModelAndView toApprove(String orderNumberView) {
        ModelAndView mav = new ModelAndView("/purchasepayment/purchaseview");

        PurchaseOrder purchaseOrder = purchaseOrderRepository.findByOrderNumberView(orderNumberView);
        if (purchaseOrder == null) {
            mav.setViewName("/message");
            mav.addObject("message", "进入页面失败");
            mav.addObject("responsePage", "/purchasepayment/willpaylist");
            return mav;
        }
        switch (purchaseOrder.purchaseType) {
            case 0:
                purchaseOrder.purchaseTypeName = "常规采购";
                break;
            case 1:
                purchaseOrder.purchaseTypeName = "临时采购";
                break;
        }

        mav.addObject("purchaseOrder", purchaseOrder);
        return mav;
    }


    /**
     * 删除记录单
     * @param id
     * @return
     */
    @RequestMapping(value = "/purchasepayment/delete/{doType}/{id}", method = RequestMethod.GET)
    @ResponseBody
    @Transactional
    public Map<String, Object> delete(@PathVariable String id, @PathVariable boolean doType) {
        PurchasePayment purchasePayment = purchasePaymentRepository.findOne(Long.valueOf(id));
        if (purchasePayment != null) purchasePayment.deleted = doType;
        purchasePaymentRepository.save(purchasePayment);
        return buildSuccessResult();
    }

    public static Map<String, Object> buildSuccessResult() {
        return buildResult(null, OK, "");
    }
    public static Map<String, Object> buildResult(Map<String, Object> data, int retCode, String msg) {
        Map<String, Object> result = new HashMap<>();
        if (data != null) {
            data.forEach((key, value) -> {
                result.put(key, value);
            });
        }
        if (!StringUtils.isEmpty(msg)) result.put("msg", msg);
        result.put("retCode", retCode);
        result.put("svrTime", DateTime.now().getMillis());
        return result;
    }
}
