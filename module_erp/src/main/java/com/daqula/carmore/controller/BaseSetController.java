package com.daqula.carmore.controller;

import com.daqula.carmore.AuthorityConst;
import com.daqula.carmore.model.acl.ERPUser;
import com.daqula.carmore.model.admin.BaseSet;
import com.daqula.carmore.model.order.Payment;
import com.daqula.carmore.model.shop.FixedAsset;
import com.daqula.carmore.model.shop.Organization;
import com.daqula.carmore.model.shop.Shop;
import com.daqula.carmore.model.template.FingerPrintScanner;
import com.daqula.carmore.repository.*;
import com.daqula.carmore.repository.specification.BaseSetRepostorySpecifications;
import com.daqula.carmore.repository.specification.FixedAssetRepostorySpecifications;
import com.daqula.carmore.util.JqGridDataGenerator;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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
import javax.websocket.Session;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by swj on 2015/9/21.
 */
@Controller
public class BaseSetController {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private BaseSetRepository baseSetRepository;



    @InitBinder("baseSet")
    public void initBinder(WebDataBinder binder) {

        binder.addValidators(new BaseSetValidator());
    }

   /* @RequestMapping(value = "/baseset/list",method = RequestMethod.GET)
    public ModelAndView list(BaseSet baseSet){
        ModelAndView mav = new ModelAndView("/baseset/list");
        return mav;
    }

    *//**
     * 获取参数列表
     * @param page
     * @param rows
     * @param sord
     * @param sidx
    //     * @param faParam
     * @return
     *//*
    @RequestMapping(value = "/baseset/list/data", method = RequestMethod.GET)
    @ResponseBody
    @Transactional(readOnly = true)
    public Map<String, Object> listData(@RequestParam int page, @RequestParam int rows,
                                        @RequestParam String sord, @RequestParam String sidx,@ModelAttribute BaseSet baseset) {
        PageRequest pageRequest = new PageRequest(page-1, rows,
                sord.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
                !StringUtils.isEmpty(sidx) ? sidx : "id");
        Specifications spec = Specifications.where(BaseSetRepostorySpecifications.filterHasPosRate(baseset.posRate))
                .and(BaseSetRepostorySpecifications.filterHasPosTopRate(baseset.posTopRate))
                .and(BaseSetRepostorySpecifications.filterHasOperationPrice(baseset.operationPrice));
//        specifications spec = Specifications.where();
        Page pageData = baseSetRepository.findAll(spec,pageRequest);
        return JqGridDataGenerator.getDataJson(pageData);
    }*/

    /**
     * TO编辑页面
     *
     * @return
     */
    @RequestMapping(value = "/baseset/tosave", method = RequestMethod.GET)
    @Transactional(readOnly = true)
    public ModelAndView toSave(HttpSession session, @AuthenticationPrincipal ERPUser user) {
        ModelAndView mav = new ModelAndView("/baseset/form");
        if (!user.checkAuthority(AuthorityConst.MANAGE_ORG_BASESET)) {
            mav.setViewName("/noauthority");
            return mav;
        }
        String responseOption = "create";
        BaseSet baseSet = new BaseSet();
        baseSet.posTopRate = "0";
        baseSet.posRate = "0";
        Organization organization = new Organization();
        Shop shop = (Shop) session.getAttribute("SHOP");
        BaseSet base = baseSetRepository.findByShop(shop);
        if (base != null) {
            responseOption = "update";
            baseSet = base;
        }
        if (baseSet.checkDay == 0)baseSet.checkDay = 1;
        mav.addObject("pageContent", responseOption);
        mav.addObject("baseSet", baseSet);
        return mav;
    }

    /**
     * 保存基础数据设置
     *
     * @param baseSet
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/baseset/save", method = RequestMethod.POST)
    @Transactional
//    public ModelAndView save(@Valid @ModelAttribute BaseSet baseSet,
    public ModelAndView save(@Valid @ModelAttribute BaseSet baseSet, HttpSession session,
                             BindingResult bindingResult, @AuthenticationPrincipal ERPUser user) {
        if (bindingResult.hasErrors()) {
            return new ModelAndView("/baseset/form");
        }
        String message = "保存成功";
        try {
            if (baseSet.id != 0) {
                BaseSet newBaseSet = baseSetRepository.findOne(baseSet.id);
                newBaseSet.posRate = baseSet.posRate;
                newBaseSet.posTopRate = baseSet.posTopRate;
                newBaseSet.operationPrice = baseSet.operationPrice;
                newBaseSet.isCheckPd = baseSet.isCheckPd;
                newBaseSet.checkDay = baseSet.checkDay;
            } else {
                Shop shop = (Shop) session.getAttribute("SHOP");
                Organization organization = organizationRepository.findOne(user.organization.id);
                baseSet.organization = organization;
                baseSet.shop = shop;
                baseSetRepository.save(baseSet);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            message = "保存失败";
        }
        ModelAndView mav = new ModelAndView("/message");
        mav.addObject("message", message);
        mav.addObject("responsePage", "/baseset/tosave");

        return mav;
    }




    /**
     * valid
     */
    static class BaseSetValidator implements Validator {

        @Override
        public boolean supports(Class<?> clazz) {
            return BaseSet.class.equals(clazz);
        }

        @Override
        public void validate(Object obj, Errors errors) {

            try {
                BaseSet baseSet = (BaseSet) obj;
                String posRate = baseSet.posRate;
                if (!StringUtils.hasLength(posRate)) {
                    errors.rejectValue("posRate", "required", "不能为空");
                }
                String posTopRate = baseSet.posTopRate;
                if (!StringUtils.hasLength(posTopRate)) {
                    errors.rejectValue("posTopRate", "required", "不能为空");
                }
                if (baseSet.operationPrice <= 0) {
                    errors.rejectValue("operationPrice", "required", "必须大于0");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
