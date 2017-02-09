package com.daqula.carmore.controller;


import com.daqula.carmore.model.acl.ERPUser;
import com.daqula.carmore.model.order.OrderDetail;
import com.daqula.carmore.model.order.PresaleOrder;
import com.daqula.carmore.model.order.SettleAccounts;
import com.daqula.carmore.model.shop.Shop;
import com.daqula.carmore.repository.ERPUserRepository;
import com.daqula.carmore.repository.PresaleOrderRepository;
import com.daqula.carmore.repository.SettleAccountsRepository;
import com.daqula.carmore.repository.ShopRepository;
import com.daqula.carmore.util.JqGridDataGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.daqula.carmore.AuthorityConst.*;
import static com.daqula.carmore.util.CollectionUtil.*;
import static com.daqula.carmore.util.FreeMarkerUtil.getAuthorityModel;


/**
 * Created by thy on 2015/8/14.
 */
@Controller
public class PaymentController {

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private PresaleOrderRepository  presaleOrderRepository;

    @Autowired
    private SettleAccountsRepository settleAccountsRepository;

    /**
     * 预约列表
     * @return
     */
    @RequestMapping(value = "/payment/presale")
    public ModelAndView presale(){
        return new ModelAndView("/payment/presale", map(entry("shops", shopRepository.findAll())));
    }

    /**
     * 预约信息分页
     * @return
     */
    @RequestMapping("/payment/list/data")
    public @ResponseBody
    Map<String, Object> listData(@RequestParam int page, @RequestParam int rows,
                                 @RequestParam String sord, @RequestParam String sidx) {

        PageRequest pageRequest = new PageRequest(page-1, rows,
                sord.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
                !StringUtils.isEmpty(sidx) ? sidx : "id");
        Page pageData = presaleOrderRepository.findAll(pageRequest);

        return JqGridDataGenerator.getDataJson(pageData);
    }

    /**
     * 预约信息查询
     * @param shopname 门店名
     * @return
     */
    @RequestMapping("/payment/list/query")
    public @ResponseBody
    Map<String, Object> queryListData(@RequestParam int page, @RequestParam int rows,
                                 @RequestParam String sord, @RequestParam String sidx, @RequestParam String shopname) {

        PageRequest pageRequest = new PageRequest(page-1, rows,
                sord.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
                !StringUtils.isEmpty(sidx) ? sidx : "id");
        Page pageData = presaleOrderRepository.findAll(pageRequest);

        return JqGridDataGenerator.getDataJson(pageData);
    }

    /**
     * 付款信息
     * @return
     */
    @RequestMapping(value = "/payment/query", method = RequestMethod.POST)
    public ModelAndView queryList(@RequestParam String mobile, String id ,String shops) {
        return new ModelAndView("/payment/presale", map(entry("shops", shopRepository.findAll())));
    }

    /**
     * 预约明细
     * @return
     */
    @RequestMapping(value = "/payment/presaledtal")
    public ModelAndView presaledetal(@RequestParam String id) {

        PresaleOrder presaleOrder = presaleOrderRepository.findOne(Long.parseLong(id));

        return new ModelAndView("/payment/presaledtal", map(entry("orders", presaleOrder.orderDetails)) );
    }

    /**
     * 预约信息删除
     * @return
     */
    @RequestMapping(value = "/payment/presaledelete")
    public String presaledelete(@RequestParam String ids) {

        String[] idStr = ids.split(",");
        for(int x = 0; x < idStr.length;x ++){
            //presaleOrderRepository.delete(Long.parseLong(idStr[x]));   //暂时先不做
        }
        return "success";
    }

    /**
     * 预约单开单
     * @return
     */
    @RequestMapping(value = "/payment/settleorder", method = RequestMethod.GET)
    public ModelAndView settleorder() {

        int isClosed  = 0;  //是否已经结算过
        int isCloseSuccess  = 0;  //关店结算操作是否成功
        List<SettleAccounts> r = settleAccountsRepository.queryTodaySettleRecord(Long.parseLong("1"));
        if(r != null && r.size() > 0){
            isClosed = 1;
        }

        return new ModelAndView("/payment/settleorder", map(entry("isClosed", isClosed),entry("isCloseSuccess", isCloseSuccess)) );
    }

    /**
     * 关店结算
     * @return
     */
    @RequestMapping(value = "/payment/settleaccounts", method = RequestMethod.GET)
    public ModelAndView settleAccounts() {

        int isClosed  = 0;  //是否已经结算过
        int isCloseSuccess  = 0;  //计算操作是否成功
        List<SettleAccounts> r = settleAccountsRepository.queryTodaySettleRecord(Long.parseLong("1"));
        if(r == null || r.size() == 0) {
            SettleAccounts settleAccounts = new SettleAccounts();
            settleAccounts.shop = shopRepository.findOne(Long.parseLong("1"));
            settleAccounts.amount =  settleAccountsRepository.qureryTotalAmountsByShopId(Long.parseLong("1"));
            settleAccountsRepository.save(settleAccounts);
            isClosed = 1;
            isCloseSuccess = 1;
        }

        return new ModelAndView("/payment/settleorder", map(entry("isCloseSuccess", isCloseSuccess),entry("isClosed", isClosed)) );
    }
}
