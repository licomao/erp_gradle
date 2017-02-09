package com.daqula.carmore.controller;

import com.daqula.carmore.AuthorityConst;
import com.daqula.carmore.model.acl.ERPUser;
import com.daqula.carmore.model.admin.BaseSet;
import com.daqula.carmore.model.admin.Supplier;
import com.daqula.carmore.model.order.MaterialOrderDetail;
import com.daqula.carmore.model.order.PurchaseOrder;
import com.daqula.carmore.model.order.PurchaseOrderDetail;
import com.daqula.carmore.model.shop.CustomStockItem;
import com.daqula.carmore.model.shop.Organization;
import com.daqula.carmore.model.shop.Shop;
import com.daqula.carmore.repository.*;
import com.daqula.carmore.repository.specification.PurchaseOrderRepostorySpecifications;
import com.daqula.carmore.util.JqGridDataGenerator;
import com.daqula.carmore.util.OrderUtil;
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
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.websocket.server.PathParam;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.daqula.carmore.ErrorCode.OK;

/**
 * 采购管理
 * Created by swj on 2015/9/23.
 */
@Controller
public class PurchaseOrderController {

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

    @Autowired
    private ERPUserRepository erpUserRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private ShopRepository shopRepository;

    //供应商
    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private PurchaseOrderDetailRepository purchaseOrderDetailRepository;

    //采购单
    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    //库存商品
    @Autowired
    private CustomStockItemRepository customStockItemRepository;

    @InitBinder("purchaseOrder")
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(new PurchaseOrderValidator());
    }

    @RequestMapping(value = "/purchaseorder/{indexPage}/list", method = RequestMethod.GET)
    public ModelAndView list(@PathVariable String indexPage, @AuthenticationPrincipal ERPUser user,HttpSession session) {
        ModelAndView mav = new ModelAndView("/purchaseorder/list");
        List<Shop> shopList = SessionUtil.getShopList(session,organizationRepository,shopRepository);
        mav.addObject("shops", shopList);
        if (!user.checkAuthority(AuthorityConst.MANAGE_ORG_PURCHASEORDER) && indexPage.equals("search")) {
            mav.setViewName("/noauthority");
            return mav;
        } else if (!user.checkAuthority(AuthorityConst.MANAGE_ORG_PURCHASEAPPROVE) && indexPage.equals("approve")) {
            mav.setViewName("/noauthority");
            return mav;
        } else if (!user.checkAuthority(AuthorityConst.MANAGE_ORG_ADDSTORAGE) && indexPage.equals("addstorage")) {
            mav.setViewName("/noauthority");
            return mav;
        }
        mav.addObject("isOrgManage", "false");//TODO 后台获取登录人是否为组织管理者 返回true/false 到前台
        mav.addObject("indexPage", indexPage); //页面功能 search 管理查询页面 approve 审批列表页面 addstorage
        return mav;
    }

    /**
     * 获取参数列表
     *
     * @param page
     * @param rows
     * @param sord
     * @param sidx //     * @param faParam
     * @return
     */
    @RequestMapping(value = "/purchaseorder/list/data", method = RequestMethod.GET)
    @ResponseBody
    @Transactional(readOnly = true)
    public Map<String, Object> listData(@RequestParam int page, @RequestParam int rows,HttpSession session,
                                        @RequestParam String sord, @RequestParam String sidx, PurchaseOrder purchaseOrder) {
        PageRequest pageRequest = new PageRequest(page - 1, rows,
                sord.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
                !StringUtils.isEmpty(sidx) ? sidx : "orderNumber");
//                !StringUtils.isEmpty(sidx) ? sidx : "id");
        Shop shop = shopRepository.findOne(purchaseOrder.purchaseShop.id);
        Specifications spec = null;
        if(purchaseOrder.orderStatus == 0) {
            spec = Specifications.where(PurchaseOrderRepostorySpecifications.filterHasOrderNumber(purchaseOrder.orderNumberView))
                    .and(PurchaseOrderRepostorySpecifications.filterBySaleShop(shop))
                    .and(PurchaseOrderRepostorySpecifications.filterHasOrderStatus(purchaseOrder.orderStatus))
                    .and(PurchaseOrderRepostorySpecifications.filterHasPurchaseDateStart(purchaseOrder.purchaseDateStart))
                    .and(PurchaseOrderRepostorySpecifications.filterHasPurchaseDateEnd(purchaseOrder.purchaseDateEnd))
                    .and(PurchaseOrderRepostorySpecifications.filterPurchaseType(purchaseOrder.purchaseType))
                    .and(PurchaseOrderRepostorySpecifications.filterDeleteStatus(purchaseOrder.deleted));
        } else {
            spec = Specifications.where(PurchaseOrderRepostorySpecifications.filterHasOrderNumber(purchaseOrder.orderNumberView))
                    .and(PurchaseOrderRepostorySpecifications.filterByShop(shop))
                    .and(PurchaseOrderRepostorySpecifications.filterHasOrderStatus(purchaseOrder.orderStatus))
                    .and(PurchaseOrderRepostorySpecifications.filterHasPurchaseDateStart(purchaseOrder.purchaseDateStart))
                    .and(PurchaseOrderRepostorySpecifications.filterHasPurchaseDateEnd(purchaseOrder.purchaseDateEnd))
                    .and(PurchaseOrderRepostorySpecifications.filterPurchaseType(purchaseOrder.purchaseType))
                    .and(PurchaseOrderRepostorySpecifications.filterDeleteStatus(purchaseOrder.deleted));
        }
//        specifications spec = Specifications.where();
        Page pageData = purchaseOrderRepository.findAll(spec, pageRequest);
        return JqGridDataGenerator.getDataJson(pageData);

    }

    /**
     * 进入采购单申请页面
     *
     * @return
     */
    @RequestMapping(value = "/purchaseorder/tosave", method = RequestMethod.GET)
    @Transactional(readOnly = true)
    @ResponseBody
    public ModelAndView toSave(String id, @AuthenticationPrincipal ERPUser user, HttpSession session) {
        ModelAndView mav = new ModelAndView("/purchaseorder/form");
        String responseOption = "create";
        PurchaseOrder purchaseOrder = new PurchaseOrder();
        //当前用户所属店铺
        Shop loginShop = (Shop) session.getAttribute("SHOP");
        purchaseOrder.purchaseShop = loginShop;
        //销售店铺list
        Organization organization = organizationRepository.findOne(user.organization.id);
        List<Shop> saleShopList =
                shopRepository.findByOrganization(organization);
        //经销商
        List<Supplier> supplierList = supplierRepository.findByOrganization(organization);


        if (StringUtils.hasLength(id)) {
            responseOption = "update";
            purchaseOrder = purchaseOrderRepository.findOne(Long.valueOf(id));
            if (purchaseOrder == null) {
                mav.setViewName("/message");
                mav.addObject("message", "进入页面失败");
                mav.addObject("responsePage", "/purchaseorder/list");
                return mav;
            }
        } else {
            purchaseOrder.purchaseOrderDetailList = null;

        }
        mav.addObject("supplierList", supplierList);
        mav.addObject("saleShopList", saleShopList);
        mav.addObject("pageContent", responseOption);
        mav.addObject("purchaseOrder", purchaseOrder);
        return mav;
    }

    /**
     * 保存基础数据设置
     *
     * @param purchaseOrder
     * @param user
     * @return
     */
    @RequestMapping(value = "/purchaseorder/save", method = RequestMethod.POST)
    @Transactional
    public ModelAndView save(String rowDatas, @Valid @ModelAttribute PurchaseOrder purchaseOrder,
                             BindingResult bindingResult, @AuthenticationPrincipal ERPUser user, HttpSession session) {
        Organization organization = (Organization) session.getAttribute("ORGANIZATIONS");
        //当前用户所属店铺
        ERPUser user1 = (ERPUser) session.getAttribute("user");
        user1 = erpUserRepository.findOne(user1.id);
        String realName = user1.realName;
        Shop logoinShop = (Shop) session.getAttribute("SHOP");
        if (bindingResult.hasErrors()) {
            if (purchaseOrder.purchaseType != 0) {    //采购类型  0常规 1临时
                String responseOption = "create";
                PurchaseOrder order = new PurchaseOrder();

                order.purchaseShop = logoinShop;
                //销售店铺list

                organization = organizationRepository.findOne(organization.id);
                List<Shop> saleShopList =
                        shopRepository.findByOrganization(organization);
                //经销商
                List<Supplier> supplierList = supplierRepository.findByOrganization(organization);
                ModelAndView mv = new ModelAndView("/purchaseorder/form");
                mv.addObject("supplierList", supplierList);
                mv.addObject("saleShopList", saleShopList);
                mv.addObject("pageContent", responseOption);
                mv.addObject("purchaseOrder", order);
                return mv;
            }
        }

        purchaseOrder.orderStatus = purchaseOrder.purchaseType == 0 ? 0 : 3;//新增/修改过程中 采购类型置为1时  临时采购  不走审批流程 流程状态置为3 已入库
        System.out.println(purchaseOrder.purchaseType);
        if (StringUtils.hasLength(rowDatas)) {
            purchaseOrder.purchaseOrderDetailList = this.getDetailInfo(rowDatas);
        }

        purchaseOrder.saleShop = shopRepository.findOne(Long.valueOf(purchaseOrder.saleShop.id));
        purchaseOrder.supplier = supplierRepository.findOne(Long.valueOf(purchaseOrder.supplier.id));
        String message = "申请成功";
        try {
            if (purchaseOrder.id != 0) {
                PurchaseOrder purchaseOrderUpdate = purchaseOrderRepository.findOne(purchaseOrder.id);

                purchaseOrder.createdBy = purchaseOrderUpdate.createdBy;
                purchaseOrder.createdDate = purchaseOrderUpdate.createdDate;
                purchaseOrder.applyPerson = realName;
                purchaseOrder.uid = purchaseOrderUpdate.uid;
                purchaseOrder.orderNumber = purchaseOrderUpdate.orderNumber;
                message = "修改成功";
                purchaseOrderRepository.save(purchaseOrder);
            } else {
                purchaseOrder.purchaseShop = shopRepository.findOne(Long.valueOf(purchaseOrder.purchaseShop.id));
                //生成单据编号
                Long count = purchaseOrderRepository.findMaxOrderNum(shopRepository.findOne(logoinShop.id));
                count = count - 1;
                do {
                    count++;
                }
                while (purchaseOrderRepository.findByOrderNumberAndPurchaseShop(count, purchaseOrder.purchaseShop) != null);

                String viewOrderNumber = OrderUtil.getViewOrderNumber(organization, logoinShop, OrderUtil.ORDER_TYPE_PURCHASE, count);
                purchaseOrder.orderNumberView = viewOrderNumber;
//                ERPUser one = erpUserRepository.findOne(user.id);
//                System.out.println(one.realName);
                purchaseOrder.orderNumber = count;
                purchaseOrder.applyPerson = realName;
                purchaseOrderRepository.save(purchaseOrder);

            }

        } catch (Exception ex) {
            ex.printStackTrace();
            message = "保存失败";
        }
        ModelAndView mav = new ModelAndView("/message");
        mav.addObject("message", message);
        mav.addObject("responsePage", "/purchaseorder/search/list");

        return mav;
    }

    public ArrayList<PurchaseOrderDetail> getDetailInfo(String rowDatas) {
        ArrayList<PurchaseOrderDetail> purchaseOrderDetails = new ArrayList<PurchaseOrderDetail>();
        String[] rowData = rowDatas.split(";");
        PurchaseOrderDetail purchaseOrderDetail;
        CustomStockItem customStockItem;
        for (String data : rowData) {
            String[] datas = data.split(",");
            if (datas.length >= 1) {
//                purchaseOrderDetail = purchaseOrderDetailRepository.find
                long id = Long.valueOf(datas[3]);
                purchaseOrderDetail = id != 0 ? purchaseOrderDetailRepository.findOne(id) : new PurchaseOrderDetail();
                customStockItem = customStockItemRepository.findOne(Long.valueOf(datas[0]));
//                customStockItem.id = Long.parseLong(datas[0]);
                purchaseOrderDetail.price = Double.valueOf(datas[1]);
                purchaseOrderDetail.number = Integer.parseInt(datas[2]);
                purchaseOrderDetail.customStockItem = customStockItem;
                purchaseOrderDetail.lastPrice = Double.valueOf(datas[4]);
                purchaseOrderDetail.bankNumber = Integer.valueOf(datas[5]);

//                purchaseOrderDetailRepository.save(purchaseOrderDetail);
                purchaseOrderDetails.add(purchaseOrderDetail);
            }
        }
        return purchaseOrderDetails;
    }

    @RequestMapping(value = "/purchaseorder/delete/{id}", method = RequestMethod.GET)
    @ResponseBody
    @Transactional
    public Map<String, Object> delete(@PathVariable String id) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findOne(Long.valueOf(id));

        if (purchaseOrder != null) purchaseOrder.deleted = true;
        purchaseOrderRepository.save(purchaseOrder);
        return buildSuccessResult();
//        return null;
    }

    /**
     * 转到 审批/入库/详细 页面
     * @param indexPage
     * @param id
     * @return
     */
    @RequestMapping(value = "/purchaseorder/page/{viewStatus}/{indexPage}/{id}", method = RequestMethod.GET)
    @Transactional(readOnly = true)
    @ResponseBody
    public ModelAndView toApprove(@PathVariable String viewStatus ,@PathVariable String indexPage, @PathVariable String id) {
        ModelAndView mav = new ModelAndView("/purchaseorder/approve");

        PurchaseOrder purchaseOrder = purchaseOrderRepository.findOne(Long.valueOf(id));
        if (purchaseOrder == null) {
            mav.setViewName("/message");
            mav.addObject("message", "进入页面失败");
            mav.addObject("responsePage", "/purchaseorder/"+indexPage+"/list");
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
        mav.addObject("indexPage", indexPage);
        mav.addObject("viewStatus", viewStatus);
        mav.addObject("purchaseOrder", purchaseOrder);

        return mav;
    }

    /**
     * 审批处理方法
     * @param responsePage
     * @param resultStatus
     * @param id
     * @param remark
     * @return
     */
    @RequestMapping(value = "/purchaseorder/{responsePage}/result/{resultStatus}/{id}", method = RequestMethod.GET)
    @ResponseBody
    @Transactional
    public ModelAndView approve(@PathVariable String responsePage,@PathVariable String resultStatus, @PathVariable String id, String remark) {
        String message = "";
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findOne(Long.valueOf(id));
        if (StringUtils.isEmpty(remark)){
            remark = "";
        }
        if (purchaseOrder != null) {
            switch (resultStatus) {
                case "1":
                    message = "审批成功";
                    break;
                case "2":
                    message = "退回成功";
                    purchaseOrder.remark = remark;
                    break;
                case "3":
                    message = "入库成功";
                    purchaseOrder.remark = remark;
                    break;
            }
            purchaseOrder.orderStatus = Integer.valueOf(resultStatus);
        }
        ModelAndView mav = new ModelAndView("/message");
        mav.addObject("message", message);
        mav.addObject("responsePage", "/purchaseorder/"+responsePage+"/list");
        return mav;
    }

   /* @RequestMapping(value = "/purchaseorder/approve/return/{id}",method = RequestMethod.GET)
    @ResponseBody
    @Transactional
    public ModelAndView returnApprove(@PathVariable String id){
        String message = "退回成功";

        try {
            PurchaseOrder purchaseOrder = purchaseOrderRepository.findOne(Long.valueOf(id));
            if(purchaseOrder!=null){
                purchaseOrder.orderStatus = 2;
            }
        } catch (Exception e) {
            message = "退回失败";
            e.printStackTrace();
        }
        ModelAndView mav = new ModelAndView("/message");
        mav.addObject("message",message);
        mav.addObject("responsePage","/purchaseorder/list");
        return mav;
    }*/

   /* @RequestMapping(value = "/purchaseorder/toaddstorage/{id}",method = RequestMethod.GET)
    @ResponseBody
    @Transactional
    public ModelAndView toAddStorage(@PathVariable String id){

        ModelAndView mav = new ModelAndView("/purchaseorder/approve");
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findOne(Long.valueOf(id));
        String actionStatus = "addStorage";
        if(purchaseOrder == null){
            mav.setViewName("/message");
            mav.addObject("message","进入页面失败");
            mav.addObject("responsePage","/purchaseorder/list");
            return mav;
        }
//        purchaseOrder.
//        String purchaseTypeName = "";
        switch (purchaseOrder.purchaseType) {
            case 0:purchaseOrder.purchaseTypeName = "常规采购";
            case 1:purchaseOrder.purchaseTypeName = "临时采购";
        }
//        mav.addObject("purchaseTypeName",purchaseTypeName);
        mav.addObject("purchaseOrder",purchaseOrder);
        mav.addObject("actionStatus",actionStatus);

        return mav;
    }*/

    static class PurchaseOrderValidator implements Validator {

        @Override
        public boolean supports(Class<?> clazz) {
            return PurchaseOrder.class.equals(clazz);
        }

        @Override
        public void validate(Object obj, Errors errors) {

            try {
                PurchaseOrder PurchaseOrder = (PurchaseOrder) obj;
                String saleNo = PurchaseOrder.saleNo;
                if (!StringUtils.hasLength(saleNo)) {
                    errors.rejectValue("saleNo", "required", "不能为空");
                }

                /*String posRate = baseSet.posRate;
                if (!StringUtils.hasLength(posRate)) {
                    errors.rejectValue("posRate", "required", "不能为空");
                }
                String posTopRate = baseSet.posTopRate;
                if (!StringUtils.hasLength(posTopRate)) {
                    errors.rejectValue("posTopRate", "required", "不能为空");
                }
                if (baseSet.operationPrice <= 0) {
                    errors.rejectValue("operationPrice", "required", "必须大于0");
                }*/
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
