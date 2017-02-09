package com.daqula.carmore.controller;

import com.daqula.carmore.model.acl.ERPUser;
import com.daqula.carmore.model.admin.Agency;
import com.daqula.carmore.model.admin.Supplier;
import com.daqula.carmore.model.shop.Organization;
import com.daqula.carmore.model.shop.Shop;
import com.daqula.carmore.repository.*;
import com.daqula.carmore.repository.specification.OrganizationRepositorySpecifications;
import com.daqula.carmore.repository.specification.PurchasePaymentSpecifications;
import com.daqula.carmore.util.JqGridDataGenerator;
import com.daqula.carmore.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

import static com.daqula.carmore.util.CollectionUtil.entry;
import static com.daqula.carmore.util.CollectionUtil.map;

/**
 * 付款流水记录查询
 * Created by mdc on 2016/3/21.
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

    @Autowired
    private AgencyRepository  agencyRepository;

    /**
     * 已付款记录查询
     * @param erpUser
     * @return
     */
    @RequestMapping(value="/purchasepayment/paymentlist", method = RequestMethod.GET)
    public ModelAndView listPurchasePayment(@AuthenticationPrincipal ERPUser erpUser,String type,HttpSession session){
        ModelAndView mav = new ModelAndView("/purchasepayment/paymentlist");
        List<Organization> organizations;
        if (erpUser.getUsername().equals("administrator")){
            Specifications spec = Specifications.where(OrganizationRepositorySpecifications.filterByDeleted(false));
            organizations = organizationRepository.findAll(spec);
        }else {
            Agency agency = agencyRepository.findByErpUser(erpUser);
            organizations = organizationRepository.findByAgency(agency);
        }
        mav.addObject("organizations",organizations);
        return  mav;
    }


    /**
     * 获得供应商信息
     * @param orgId
     * @return
     */
    @RequestMapping(value = "/purchasepayment/paymentlist/supplier", method = RequestMethod.GET)
    @ResponseBody
    @Transactional(readOnly = true)
    public Map<String, Object> getSupplierInfo(String orgId){
        Organization organization = organizationRepository.findOne(Long.parseLong(orgId));
        List<Supplier> suppliers = supplierRepository.findByOrganization(organization);
        return map(entry("suppliers",suppliers));
    }

    /**
     * getDataList
     * @param page
     * @param rows
     * @param sord
     * @param sidx
     * @param orgId
     * @param supplierId
     * @param payType
     * @param orderNumber
     * @param deleted
     * @param purchaseType
     * @return
     */
    @RequestMapping(value = "/purchasepayment/paymentlist/data", method = RequestMethod.GET)
    @ResponseBody
    @Transactional(readOnly = true)
    public Map<String, Object> paymentListData(@RequestParam int page, @RequestParam int rows,
                                               @RequestParam String sord, @RequestParam String sidx,String orgId,String supplierId, int payType,String orderNumber
            ,boolean deleted  ,int purchaseType){
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
        Specifications spec = Specifications.where(PurchasePaymentSpecifications.filterByDeleted(deleted))
                .and(PurchasePaymentSpecifications.filterByOrganization(organization))
                .and(PurchasePaymentSpecifications.filterByPayType(payType))
                .and(PurchasePaymentSpecifications.filterBySupplier(supplier))
                .and(PurchasePaymentSpecifications.filterByPurchaseOrder(orderNumber))
                .and(PurchasePaymentSpecifications.filterByPurchaseOrderPurchaseType(purchaseType));
        Page pageData = purchasePaymentRepository.findAll(spec, pageRequest);
        return JqGridDataGenerator.getDataJson(pageData);
    }

}
