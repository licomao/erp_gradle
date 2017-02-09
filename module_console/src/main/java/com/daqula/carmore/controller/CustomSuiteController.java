package com.daqula.carmore.controller;

import com.daqula.carmore.AuthorityConst;
import com.daqula.carmore.model.acl.ERPUser;
import com.daqula.carmore.model.admin.SuiteItem;
import com.daqula.carmore.model.shop.CustomStockItem;
import com.daqula.carmore.model.shop.CustomSuite;
import com.daqula.carmore.model.shop.Organization;
import com.daqula.carmore.model.shop.Shop;
import com.daqula.carmore.repository.CustomStockItemRepository;
import com.daqula.carmore.repository.CustomSuiteRepository;
import com.daqula.carmore.repository.OrganizationRepository;
import com.daqula.carmore.repository.SuiteItemRepository;
import com.daqula.carmore.util.JqGridDataGenerator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by swj on 2015/10/16.
 */
@Controller
public class CustomSuiteController {

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private CustomStockItemRepository customStockItemRepository;

    /**
     * 门店套餐模板
     */
    @Autowired
    private CustomSuiteRepository customSuiteRepository;
    /**
     * 套餐项目
     */
    @Autowired
    private SuiteItemRepository suiteItemRepository;

    @InitBinder("customSuite")
    public void initBinder(WebDataBinder binder) {

        binder.addValidators(new CustomSuiteValidator());
    }


    /**
     * 设置卡种 首页
     *
     * @return
     */
    @RequestMapping(value = "/customerpurchasesuite/vipcard/list", method = RequestMethod.GET)
    public ModelAndView vipCardList(@AuthenticationPrincipal ERPUser user) {
        ModelAndView mav = new ModelAndView("/customerpurchasesuite/vipList");
        if (!user.checkAuthority(AuthorityConst.MANAGE_ORG_CUSTOMER)) {
            mav.setViewName("/noauthority");
            return mav;
        }

//        Organization organization = new Organization();
//        organization.id = user.organization.id;
//        List<Shop> shopList = shopRepository.findByOrganization(organization);
//        mav.addObject("shopList", shopList);
        return mav;
    }

    /**
     * 设置卡种 表格数据
     *
     * @param page
     * @param rows
     * @param sord
     * @param sidx //     * @param faParam
     * @return
     */
    @RequestMapping(value = "/customerpurchasesuite/vipcard/list/data", method = RequestMethod.GET)
    @ResponseBody
    @Transactional(readOnly = true)
    public Map<String, Object> vipCardListData(@RequestParam int page, @RequestParam int rows,
                                               @RequestParam String sord, @RequestParam String sidx,
                                               @AuthenticationPrincipal ERPUser user, String name) {
        PageRequest pageRequest = new PageRequest(page - 1, rows,
                sord.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
                !org.springframework.util.StringUtils.isEmpty(sidx) ? sidx : "id");
//        Organization organization = new Organization();
//        organization.id = user.organization.id;
        long id = user.organization.id;
        Organization organization = organizationRepository.findOne(id);

        Page pageData;
        if(StringUtils.isNotBlank(name)){
            pageData = customSuiteRepository.findByOrganizationAndNameLike(organization, "%" + name + "%", pageRequest);
        }else{
            pageData = customSuiteRepository.findByOrganization(organization, pageRequest);
        }
        return JqGridDataGenerator.getDataJson(pageData);



    }


    /**
     * 跳转到 添加/编辑会员卡
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/customerpurchasesuite/vipcard/tosave", method = RequestMethod.GET)
    @Transactional(readOnly = true)
    public ModelAndView vipCardToSave(long id, HttpSession session) {
        ModelAndView mav = new ModelAndView("/customerpurchasesuite/vipForm");
        String responseOption = "create";
        CustomSuite customSuite = null;
        if (id != 0) {
            responseOption = "update";
            customSuite = customSuiteRepository.findOne(id);
//            List<SuiteItem> suiteItems = suiteItemRepository.findBySuiteAndDeleted(customSuite, false);
//            customSuite.suiteItems = suiteItems;

            List<SuiteItem> suiteItems = customSuite.suiteItems;
            for (int i = suiteItems.size()-1; i >= 0; i--) {
                if(suiteItems.get(i).deleted) suiteItems.remove(i);
            }

        } else {
            customSuite = new CustomSuite();
        }
        Shop shop = (Shop) session.getAttribute("SHOP");

        mav.addObject("customSuite", customSuite);
        mav.addObject("shopId", shop.id);
        mav.addObject("pageContent", responseOption);

        return mav;
    }


    /**
     * 新增/更新 会员卡
     *
     * @return
     */
    @RequestMapping(value = "/customerpurchasesuite/vipcard/save", method = RequestMethod.POST)
    @Transactional
    public ModelAndView vipCardSave(String rowDatas,@Valid @ModelAttribute CustomSuite customSuite,
                                    BindingResult bindingResult,
                                    @AuthenticationPrincipal ERPUser user, HttpSession session) {
        if (bindingResult.hasErrors()) {
            return this.vipCardToSave(customSuite.id,session);
        }
        String message = "保存成功";
        try {
            if (customSuite.id != 0) {
                //更新操作
                message = "修改成功";
                CustomSuite newCustomSuite = customSuiteRepository.findOne(customSuite.id);
                newCustomSuite.name = customSuite.name;
                newCustomSuite.expiation = customSuite.expiation;
//                newCustomSuite.suiteType = CustomSuite.SUITE_TYPE_VIP;
                newCustomSuite.description = customSuite.description;
                newCustomSuite.price = customSuite.price;
//                newCustomSuite.suiteItems.clear();
                updateSuiteItems(rowDatas, newCustomSuite);
//                newCustomSuite.suiteItems.addAll(this.getSuiteItems(rowDatas));
//                newCustomSuite.suiteItems = this.getSuiteItems(rowDatas);
//            newCustomSuite.

            } else {
                //新增操作
                Organization organization = new Organization();
                organization.id = user.organization.id;
                customSuite.organization = organization;
                customSuite.suiteType = CustomSuite.SUITE_TYPE_VIP;
                customSuite.suiteItems = this.getSuiteItems(rowDatas);
                customSuiteRepository.save(customSuite);
//            customSuiteRepository.sav
            }
        } catch (Exception e) {
            message = "保存失败";
            e.printStackTrace();
        }
        ModelAndView mav = new ModelAndView("/message");
        mav.addObject("message", message);
        mav.addObject("responsePage", "/customerpurchasesuite/vipcard/list");

        return mav;
    }

    private void updateSuiteItems(String rowDatas, CustomSuite newCustomSuite) {
//        List<SuiteItem> suiteItems = new ArrayList<>();
        List<SuiteItem> suiteItems = newCustomSuite.suiteItems;
        //创建一个List容器记录原来的   明细ID
        List<Long> itemIds = new ArrayList<>();
        for (SuiteItem suiteItem : suiteItems) {
            if(!suiteItem.deleted)
                itemIds.add(suiteItem.id);
        }

        String[] rowData = rowDatas.split(";");
        SuiteItem suiteItem;
        CustomStockItem customStockItem;
        for (String data : rowData) {
            String[] datas = data.split(",");
            if (datas.length > 1) {
                long id = Long.valueOf(datas[0]);
                if (id != 0) {
                    if(itemIds.contains(id)){
                        //update
                        suiteItem = suiteItemRepository.findOne(id);
                        if (datas[2].equals("true")) {
                            suiteItem.times = -1;
                        } else {
                            suiteItem.times = Integer.valueOf(datas[3]);
                        }
                        suiteItem.cost = Double.valueOf(datas[4]);
                        itemIds.remove(id);//更新一个后 从Id集合中移除
                    }
                } else {
                    //新增

                    customStockItem = customStockItemRepository.findOne(Long.valueOf(datas[1]));

                    List<SuiteItem> isHasOne = suiteItemRepository.findBySkuItemAndCostAndDeleted(customStockItem, Double.valueOf(datas[3]), true);
                    if(isHasOne != null && isHasOne.size()>0 ){
                        isHasOne.get(0).deleted = false;
                        if (datas[2].equals("true")) {
                            isHasOne.get(0).times = -1;
                        } else {
                            isHasOne.get(0).times = Integer.valueOf(datas[3]);
                        }
                    }else{
                        suiteItem = new SuiteItem();
                        suiteItem.skuItem = customStockItem;
                        if (datas[2].equals("true")) {
                            suiteItem.times = -1;
                        } else {
                            suiteItem.times = Integer.valueOf(datas[3]);
                        }
                        suiteItem.cost = Double.valueOf(datas[4]);
                        suiteItems.add(suiteItem);
                        suiteItemRepository.save(suiteItem);
                    }
                }
            }
        }
        //rowData操作完成
        for (Long itemId : itemIds) {
            SuiteItem deleteOne = suiteItemRepository.findOne(itemId);
            deleteOne.deleted = true;
        }
//        return suiteItems;
    }

    private List<SuiteItem> getSuiteItems(String rowDatas) {
        List<SuiteItem> suiteItems = new ArrayList<>();
        String[] rowData = rowDatas.split(";");
        SuiteItem suiteItem;
        CustomStockItem customStockItem;
        for (String data : rowData) {
            String[] datas = data.split(",");
            if (datas.length > 1) {
                long id = Long.valueOf(datas[0]);
                if (id != 0) {
                    suiteItem = suiteItemRepository.findOne(id);
                    if (datas[2].equals("true")) {
                        suiteItem.times = -1;
                    } else {
                        suiteItem.times = Integer.valueOf(datas[3]);
                    }
                    suiteItem.cost = Double.valueOf(datas[4]);
                } else {
                    suiteItem = new SuiteItem();
                    customStockItem = customStockItemRepository.findOne(Long.valueOf(datas[1]));
                    suiteItem.skuItem = customStockItem;
                    if (datas[2].equals("true")) {
                        suiteItem.times = -1;
                    } else {
                        suiteItem.times = Integer.valueOf(datas[3]);
                    }
                    suiteItem.cost = Double.valueOf(datas[4]);
                    suiteItemRepository.save(suiteItem);
                }
//                suiteItem.times = Integer.valueOf(datas[2]);
//                suiteItem.cost = Double.valueOf(datas[3]);
                suiteItems.add(suiteItem);
            }
        }
        return suiteItems;
    }


    /**
     * 会员套餐管理  页面  点击会员售卡时触发请求   校验是否存在可选会员套餐
     * 返回true false
     * true 存在   则返回页面后跳转会员售卡页面
     * false alert提醒没有套餐
     * @param user
     * @return
     */
    @RequestMapping(value = "/customerpurchasesuite/hasCustomSuite", method = RequestMethod.GET)
    @ResponseBody
    @Transactional(readOnly = true)
    public boolean hasCustomSuite(@AuthenticationPrincipal ERPUser user){
        Organization organization = new Organization();
        organization.id = user.organization.id;
        long total = customSuiteRepository.findTotalByOrganizationAndEnabled(organization,true);
        if(total == 0)
            return false;
        else
            return true;
    }


    /**
     * 卡种设置 启停用
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/customerpurchasesuite/vipcard/enabled", method = RequestMethod.GET)
    @Transactional
    @ResponseBody
    public boolean changeVipCardEnabled(@RequestParam Long id) {

        try {
            CustomSuite customSuite = customSuiteRepository.findOne(id);
            customSuite.enabled = !customSuite.enabled;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }



    static class CustomSuiteValidator implements Validator {

        @Override
        public boolean supports(Class<?> clazz) {
            return CustomSuite.class.equals(clazz);
        }

        @Override
        public void validate(Object obj, Errors errors) {
            CustomSuite customSuite = (CustomSuite) obj;
            String name = customSuite.name;
//            String name = customerPurchasedSuite.;
            if (StringUtils.isBlank(name)) {
                errors.rejectValue("name", "required", "不能为空");
            }
//            String model = customerPurchasedSuite.model;
//            if (!StringUtils.hasLength(model)) {
//                errors.rejectValue("model", "required", "不能为空");
//            }
//            if (customerPurchasedSuite.price <= 0) {
//                errors.rejectValue("price", "required", "必须大于0");
//            }
        }
    }
}
