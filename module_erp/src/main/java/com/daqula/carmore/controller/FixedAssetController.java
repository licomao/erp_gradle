package com.daqula.carmore.controller;

import com.daqula.carmore.AuthorityConst;
import com.daqula.carmore.model.acl.ERPUser;
import com.daqula.carmore.model.shop.FixedAsset;
import com.daqula.carmore.model.shop.Shop;
import com.daqula.carmore.repository.FixedAssetRepository;
import com.daqula.carmore.repository.OrganizationRepository;
import com.daqula.carmore.repository.ShopRepository;
import com.daqula.carmore.repository.specification.FixedAssetRepostorySpecifications;
import com.daqula.carmore.util.ExcelExportAsset;
import com.daqula.carmore.util.ExcelUtil;
import com.daqula.carmore.util.JqGridDataGenerator;
import com.daqula.carmore.util.SessionUtil;
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

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * 固定资产管理控制器
 * Created by mdc on 2015/9/14.
 */
@Controller
public class FixedAssetController {

    @Autowired
    public FixedAssetRepository fixedAssetRepository;

    @Autowired
    public ShopRepository shopRepository;

    @Autowired
    public OrganizationRepository organizationRepository;

    @InitBinder("fixedAsset")
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(new FixedAssetValidator());
    }
    /**
     * 固定资产查询
     * @return
     */
    @RequestMapping(value = "/fixedasset/list",method = RequestMethod.GET)
    public ModelAndView list(HttpSession session, @AuthenticationPrincipal ERPUser user) {

        ModelAndView mav = new ModelAndView("/fixedasset/list");
        if (!user.checkAuthority(AuthorityConst.MANAGE_ORG_FIXEDASSET)) {
            mav.setViewName("/noauthority");
            return mav;
        }

        List<Shop> shopList = SessionUtil.getShopList(session, organizationRepository, shopRepository);

        if (shopList == null) {
            mav.setViewName("/logout");
            return mav;
        }
        mav.addObject("shops",shopList);
        return mav;
    }


    /**
     * 获取参数列表
     * @param page
     * @param rows
     * @param sord
     * @param sidx
     * @param faParam
     * @return
     */
    @RequestMapping(value = "/fixedasset/list/data", method = RequestMethod.GET)
    @ResponseBody
    @Transactional(readOnly = true)
    public Map<String, Object> listData(@RequestParam int page, @RequestParam int rows,
                                        @RequestParam String sord, @RequestParam String sidx,@ModelAttribute FixedAsset faParam) {
        PageRequest pageRequest = new PageRequest(page-1, rows,
                sord.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
                !StringUtils.isEmpty(sidx) ? sidx : "id");
        Specifications spec = Specifications.where(FixedAssetRepostorySpecifications.filterByShop(faParam.shop))
                .and(FixedAssetRepostorySpecifications.filterHasAssetStatus(faParam.assetStatus))
                .and(FixedAssetRepostorySpecifications.filterHasName(faParam.name));
        Page pageData = fixedAssetRepository.findAll(spec,pageRequest);
        return JqGridDataGenerator.getDataJson(pageData);
    }


    /**
     * TO编辑页面
     * @param id
     * @return
     */
    @RequestMapping(value = "/fixedasset/tosave", method = RequestMethod.GET)
    @Transactional(readOnly = true)
    public ModelAndView toSave(String id,HttpSession session) {
        ModelAndView mav = new ModelAndView("/fixedasset/form");
        String responseOption = "create";
        FixedAsset fixedAsset = new FixedAsset();
        Shop shop = (Shop)session.getAttribute("SHOP");
        fixedAsset.shop = shop;
        if (StringUtils.hasLength(id)) {
            responseOption = "update";
            fixedAsset = fixedAssetRepository.findOne(Long.parseLong(id));
            if (fixedAsset == null) {
                mav.setViewName("/message");
                mav.addObject("message","进入页面失败");
                mav.addObject("responsePage","/fixedasset/list");
                return mav;
            }
        }
        mav.addObject("pageContent", responseOption);
        mav.addObject("fixedAsset", fixedAsset);
        return mav;
    }

    /**
     * 保存固定资产信息
     * @param fixedAsset
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/fixedasset/save" ,method = RequestMethod.POST)
    @Transactional
    public ModelAndView save(@Valid @ModelAttribute FixedAsset fixedAsset, BindingResult bindingResult,HttpSession session) {
        if (bindingResult.hasErrors()) {
            return new ModelAndView("/fixedasset/form");
        }
        String message = "保存成功";
        Shop shop = (Shop)session.getAttribute("SHOP");
        fixedAsset.shop = shop;
        try {
            fixedAssetRepository.save(fixedAsset);
        } catch (Exception ex) {
            message = "保存失败";
        }
        ModelAndView mav = new ModelAndView("/message");
        mav.addObject("message",message);
        mav.addObject("responsePage","/fixedasset/list");
        return mav;
    }

    /**
     * 启用OR作废
     * @param id
     * @param doType
     * @return
     */
    @RequestMapping(value = "/fixedasset/remove" ,method = RequestMethod.GET)
    public ModelAndView remove(String id,int doType) {
        ModelAndView mav = new ModelAndView("/message");
        if(StringUtils.hasLength(id)) {
            FixedAsset fixedAsset = fixedAssetRepository.findOne(Long.parseLong(id));
            boolean deleted = doType == 1 ? true : false;
            fixedAsset.deleted = deleted;
            fixedAssetRepository.save(fixedAsset);
        }
        return mav;
    }

    @RequestMapping(value = "/fixedasset/excel/export", method = RequestMethod.GET)
    public void downloadAsset(HttpServletResponse response,@ModelAttribute FixedAsset faParam) throws IOException{
        OutputStream outputStream = null;
        try {
            outputStream = response.getOutputStream();
            Calendar cal = Calendar.getInstance();
            Shop shop = shopRepository.findOne(faParam.shop.id);
            response.reset();
            response.setHeader("Content-Disposition", "attachment; filename=" + java.net.URLEncoder.encode("固定资产_"+shop.name, "UTF8")
                    + cal.get(Calendar.YEAR) + "-" + cal.get(Calendar.MONTH) + ".xls");
            response.setContentType("application/octet-stream; charset=utf-8");
            Specifications spec = Specifications.where(FixedAssetRepostorySpecifications.filterByShop(faParam.shop))
                    .and(FixedAssetRepostorySpecifications.filterHasAssetStatus(faParam.assetStatus))
                    .and(FixedAssetRepostorySpecifications.filterHasName(faParam.name));
            List<FixedAsset> fixedAssets = fixedAssetRepository.findAll(spec);
            List<ExcelExportAsset> list = new ArrayList<ExcelExportAsset>();
            for(FixedAsset fixedAsset : fixedAssets){
                ExcelExportAsset excelExportAsset = new ExcelExportAsset();
                excelExportAsset.name = fixedAsset.name;
                excelExportAsset.number = fixedAsset.number;
                excelExportAsset.price = fixedAsset.price;
                excelExportAsset.sum = (fixedAsset.number * fixedAsset.price);
                excelExportAsset.style = fixedAsset.model;
                excelExportAsset.status = fixedAsset.assetStatus == 0 ? "在用" : "报废";
                excelExportAsset.shopName = fixedAsset.shop.name;
                list.add(excelExportAsset);
            }

            ExcelUtil<ExcelExportAsset> util = new ExcelUtil<ExcelExportAsset>(ExcelExportAsset.class);// 创建工具类.
            util.exportExcel(list, "固定资产", 65536, outputStream);// 导出

        } catch (Exception ex){
            ex.printStackTrace();
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }


    /**
     * valid
     */
    static class FixedAssetValidator implements Validator {

        @Override
        public boolean supports(Class<?> clazz) {
            return FixedAsset.class.equals(clazz);
        }

        @Override
        public void validate(Object obj, Errors errors) {
            FixedAsset fixedAsset = (FixedAsset) obj;
            String name = fixedAsset.name;
            if (!StringUtils.hasLength(name)) {
                errors.rejectValue("name", "required", "不能为空");
            }
            String model = fixedAsset.model;
            if (!StringUtils.hasLength(model)) {
                errors.rejectValue("model", "required", "不能为空");
            }
            if (fixedAsset.price <= 0) {
                errors.rejectValue("price", "required", "必须大于0");
            }
        }
    }

}
