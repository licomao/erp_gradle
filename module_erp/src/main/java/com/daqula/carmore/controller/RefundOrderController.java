package com.daqula.carmore.controller;

import com.daqula.carmore.AuthorityConst;
import com.daqula.carmore.model.acl.ERPUser;
import com.daqula.carmore.model.admin.Supplier;
import com.daqula.carmore.model.order.RefundOrder;
import com.daqula.carmore.model.order.RefundOrderDetail;
import com.daqula.carmore.model.shop.CustomStockItem;
import com.daqula.carmore.model.shop.Organization;
import com.daqula.carmore.model.shop.Shop;
import com.daqula.carmore.repository.*;
import com.daqula.carmore.repository.specification.RefundOrderRepostorySpecifications;
import com.daqula.carmore.util.JqGridDataGenerator;
import com.daqula.carmore.util.OrderUtil;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.daqula.carmore.ErrorCode.OK;

/**
 * Created by swj on 2015/9/27.
 */
@Controller
public class RefundOrderController {

    @Autowired
    private ERPUserRepository erpUserRepository;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private CustomStockItemRepository customStockItemRepository;

    @Autowired
    private RefundOrderDetailRepository refundOrderDetailRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private RefundOrderRepository refundOrderRepository;

    @RequestMapping(value = "/refundorder/{menuStatus}/list", method = RequestMethod.GET)
    public ModelAndView list(@PathVariable String menuStatus, @AuthenticationPrincipal ERPUser user) {

        ModelAndView mav = new ModelAndView("/refundorder/list");

        if (!user.checkAuthority(AuthorityConst.MANAGE_ORG_REFUNDORDER) && menuStatus.equals("search")) {
            mav.setViewName("/noauthority");
            return mav;
        } else if (!user.checkAuthority(AuthorityConst.MANAGE_ORG_REFUNDAPPROVE) && menuStatus.equals("approve")) {
            mav.setViewName("/noauthority");
            return mav;
        }

        mav.addObject("menuStatus", menuStatus);
        return mav;
    }

    /**
     * 退货单管理表格数据查询
     *
     * @param page
     * @param rows
     * @param sord
     * @param sidx
     * @param refundOrder
     * @return
     */
    @RequestMapping(value = "/refundorder/list/data", method = RequestMethod.GET)
    @ResponseBody
    @Transactional(readOnly = true)
    public Map<String, Object> listData(@RequestParam int page, @RequestParam int rows,HttpSession session,
                                        @RequestParam String sord, @RequestParam String sidx, RefundOrder refundOrder) {
        PageRequest pageRequest = new PageRequest(page - 1, rows,
                sord.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
                !StringUtils.isEmpty(sidx) ? sidx : "createdDate");
        Shop shop = (Shop) session.getAttribute("SHOP");
//        refundOrder.
        Specifications spec = Specifications.where(RefundOrderRepostorySpecifications.filterHasOrderNumber(refundOrder.orderNumberView))
                .and(RefundOrderRepostorySpecifications.filterByShop(shop))
                .and(RefundOrderRepostorySpecifications.filterHasOrderStatus(refundOrder.orderStatus))
                .and(RefundOrderRepostorySpecifications.filterHasRefundDateStart(refundOrder.refundDateStart))
                .and(RefundOrderRepostorySpecifications.filterHasRefundDateEnd(refundOrder.refundDateEnd))
                .and(RefundOrderRepostorySpecifications.filterDeleteStatus());
        Page pageData = refundOrderRepository.findAll(spec, pageRequest);
        return JqGridDataGenerator.getDataJson(pageData);
    }

    /**
     * 进入退货单申请/修改 页面
     *
     * @return
     */
    @RequestMapping(value = "/refundorder/tosave", method = RequestMethod.GET)
    @Transactional(readOnly = true)
    @ResponseBody
    public ModelAndView toSave(String id, @AuthenticationPrincipal ERPUser user, HttpSession session) {
        ModelAndView mav = new ModelAndView("/refundorder/form");
        String responseOption = "create";
        RefundOrder refundOrder = new RefundOrder();

        //当前用户所属店铺 退货门店
        Shop loginShop = (Shop) session.getAttribute("SHOP");
        refundOrder.refundShop = loginShop;

        //经销商/供应商
        Organization organization = (Organization) session.getAttribute("ORGANIZATIONS");
        organization = organizationRepository.findOne(organization.id);
        List<Supplier> supplierList = supplierRepository.findByOrganization(organization);


        if (StringUtils.hasLength(id)) {
            responseOption = "update";
            refundOrder = refundOrderRepository.findOne(Long.valueOf(id));
            if (refundOrder == null) {
                mav.setViewName("/message");
                mav.addObject("message", "进入页面失败");
                mav.addObject("responsePage", "/refundorder/list");
                return mav;
            }
        } else {

            refundOrder.refundOrderDetails = null;

        }
        mav.addObject("supplierList", supplierList);
//        mav.addObject("saleShopList", saleShopList);
        mav.addObject("pageContent", responseOption);
        mav.addObject("refundOrder", refundOrder);
        return mav;
    }

    @RequestMapping(value = "/refundorder/save", method = RequestMethod.POST)
    @Transactional
    public ModelAndView save(String rowDatas,@ModelAttribute RefundOrder refundOrder,
                             @AuthenticationPrincipal ERPUser user, HttpSession session) {
        Organization organization = (Organization) session.getAttribute("ORGANIZATIONS");
        //当前用户所属店铺
        Shop logoinShop = (Shop) session.getAttribute("SHOP");
        user = erpUserRepository.findOne(user.id);
        System.out.println(refundOrder.id);
        String message = "申请成功";
        try {
            if (refundOrder.id != 0) {
                RefundOrder refundOrderUpdate = refundOrderRepository.findOne(refundOrder.id);
                refundOrderUpdate.remark = refundOrder.remark;
                refundOrderUpdate.supplier = supplierRepository.findOne(Long.valueOf(refundOrder.supplier.id));
                if (StringUtils.hasLength(rowDatas)) {
                    refundOrderUpdate.refundOrderDetails.clear();
                    refundOrderUpdate.refundOrderDetails.addAll(this.getDetailInfo(rowDatas));
                }
                message = "修改成功";
            } else {
                if (StringUtils.hasLength(rowDatas)) {
                    refundOrder.refundOrderDetails = this.getDetailInfo(rowDatas);
                }
                refundOrder.refundShop = shopRepository.findOne(Long.valueOf(refundOrder.refundShop.id));
                //生成单据编号
                Long count = refundOrderRepository.findMaxOrderNum(shopRepository.findOne(logoinShop.id));
                count = count - 1;
                //校验单据编号 唯一性
                do {
                    count++;
                }
                while (refundOrderRepository.findByOrderNumberAndRefundShop(count, refundOrder.refundShop) != null);
                //生成页面显示用的单据编号
                String viewOrderNumber = OrderUtil.getViewOrderNumber(organization, logoinShop, OrderUtil.ORDER_TYPE_REFUND, count);
                refundOrder.orderNumberView = viewOrderNumber;
                refundOrder.orderNumber = count;
                refundOrder.applyPerson = user.realName;

                refundOrderRepository.save(refundOrder);

            }

        } catch (Exception ex) {
            ex.printStackTrace();
            message = "保存失败";
        }
        ModelAndView mav = new ModelAndView("/message");
        mav.addObject("message", message);
        mav.addObject("responsePage", "/refundorder/search/list");

        return mav;
    }

    public ArrayList<RefundOrderDetail> getDetailInfo(String rowDatas) {
        ArrayList<RefundOrderDetail> refundOrderDetails = new ArrayList<RefundOrderDetail>();
        String[] rowData = rowDatas.split(";");
        RefundOrderDetail refundOrderDetail;
        CustomStockItem customStockItem;
        for (String data : rowData) {
            String[] datas = data.split(",");
            if (datas.length > 1) {
                long id = Long.valueOf(datas[3]);
                refundOrderDetailRepository.findOne(id);
                refundOrderDetail = id != 0 ? refundOrderDetailRepository.findOne(id) : new RefundOrderDetail();
                customStockItem = customStockItemRepository.findOne(Long.valueOf(datas[0]));
                refundOrderDetail.cost = Double.valueOf(datas[1]);
                refundOrderDetail.number = Integer.parseInt(datas[2]);
                refundOrderDetail.customStockItem = customStockItem;
                refundOrderDetail.bankNumber = Double.valueOf(datas[4]);
                refundOrderDetails.add(refundOrderDetail);
            }
        }
        return refundOrderDetails;
    }


    /**
     * 删除退货单
     * @param id
     * @return
     */
    @RequestMapping(value = "/refundorder/delete/{id}", method = RequestMethod.GET)
    @ResponseBody
    @Transactional
    public Map<String, Object> delete(@PathVariable String id) {
        RefundOrder refundOrder = refundOrderRepository.findOne(Long.valueOf(id));

        if (refundOrder != null) refundOrder.deleted = true;
        return buildSuccessResult();
    }

    /**
     * 跳转 审批/明细 页面
     * @param viewStatus       页面显示标识符 detail 明细 approve 审批
     * @param indexPage        当前的上一个页面标识符 approve 退货单审批   search退货单管理
     * @param id
     * @return
     */
    @RequestMapping(value = "/refundorder/page/{viewStatus}/{indexPage}/{id}", method = RequestMethod.GET)
    @Transactional(readOnly = true)
    @ResponseBody
    public ModelAndView toApprove(@PathVariable String viewStatus ,@PathVariable String indexPage, @PathVariable String id) {
        ModelAndView mav = new ModelAndView("/refundorder/approve");

        RefundOrder refundOrder = refundOrderRepository.findOne(Long.valueOf(id));
//        refundOrder.refundOrderDetails.get(0).n
        if (refundOrder == null) {
            mav.setViewName("/message");
            mav.addObject("message", "进入页面失败");
            mav.addObject("responsePage", "/refundorder/"+indexPage+"/list");
            return mav;
        }

        mav.addObject("indexPage", indexPage);
        mav.addObject("viewStatus", viewStatus);
        mav.addObject("refundOrder", refundOrder);

        return mav;
    }

    /**
     * 审批处理方法
     * @param indexPage    当前页面上一个页面标识符  search 退货单管理   approve 退货单审批
     * @param resultStatus 结果标识符 1 审批通过 2 退回
     * @param id
     * @return
     */
    @RequestMapping(value = "/refundorder/{indexPage}/result/{resultStatus}/{id}", method = RequestMethod.GET)
    @ResponseBody
    @Transactional
    public ModelAndView approve(@PathVariable String indexPage,@PathVariable String resultStatus, @PathVariable String id) {

        String message = "";

        try {
            RefundOrder refundOrder = refundOrderRepository.findOne(Long.valueOf(id));
            if (refundOrder != null) {
                switch (resultStatus) {
                    case "1":
                        message = "审批成功";
                        break;
                    case "2":
                        message = "退回成功";
                        break;
                   /* case "3":
                        message = "入库成功";
                        break;*/
                }
                refundOrder.orderStatus = Integer.valueOf(resultStatus);
            }
        } catch (Exception e) {
            message = "审批失败";
            e.printStackTrace();
        }
        ModelAndView mav = new ModelAndView("/message");
        mav.addObject("message", message);
        mav.addObject("responsePage", "/refundorder/"+indexPage+"/list");
        return mav;
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
