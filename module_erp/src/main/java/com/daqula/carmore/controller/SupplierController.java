package com.daqula.carmore.controller;

import com.daqula.carmore.AuthorityConst;
import com.daqula.carmore.model.acl.ERPUser;
import com.daqula.carmore.model.admin.Suite;
import com.daqula.carmore.model.admin.Supplier;
import com.daqula.carmore.model.shop.Organization;
import com.daqula.carmore.repository.OrganizationRepository;
import com.daqula.carmore.repository.SupplierRepository;
import com.daqula.carmore.repository.specification.SupplierRepositorySpecifications;
import com.daqula.carmore.util.JqGridDataGenerator;
import com.daqula.carmore.util.StringUtil;
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

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.*;

import static com.daqula.carmore.util.CollectionUtil.entry;
import static com.daqula.carmore.util.CollectionUtil.map;

/**
 * Created by thy on 2015/9/11.
 */

@Controller
public class SupplierController {

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @InitBinder("supplier")
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(new SupplierValidator());
    }

    /**
     * 供应商信息列表
     * @return
     */
    @RequestMapping(value = "/supplier/list",method = RequestMethod.GET)
    @Transactional(readOnly = true)
    public ModelAndView list(@AuthenticationPrincipal ERPUser erpUser) {
        ModelAndView mav = new ModelAndView("/supplier/list");

        if (!erpUser.checkAuthority(AuthorityConst.MANAGE_ORG_SUPPLIER)) {
            mav.setViewName("/noauthority");
            return mav;
        }

        Organization organization = organizationRepository.findOne(erpUser.organization.id);
        List<Organization> orgs = new ArrayList<Organization>();
        orgs.add(organization);
        mav.addObject("orgs",orgs);
        return mav;
    }

    /**
     * 供应商列表
     * @param page
     * @param rows
     * @param sord
     * @param sidx
     * @param supplier
     * @return
     */
    @RequestMapping(value = "/supplier/list/data",method = RequestMethod.GET)
    @ResponseBody
    @Transactional(readOnly = true)
    public Map<String, Object> listData(@RequestParam int page, @RequestParam int rows,
                                 @RequestParam String sord, @RequestParam String sidx, @ModelAttribute Supplier supplier) {
        PageRequest pageRequest = new PageRequest(page-1, rows,
                sord.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,!StringUtils.isEmpty(sidx) ? sidx : "id");
        Organization organization = organizationRepository.findOne(supplier.organization.id);
        String name = null;
        if (!StringUtils.isEmpty(supplier.name)){
            name = "%" + supplier.name + "%";
        }
        Specification<Supplier> supplierSpecification = Specifications.where(SupplierRepositorySpecifications.filterByOrganization(organization))
                .and(SupplierRepositorySpecifications.filterByDeleted(supplier.deleted))
                .and(SupplierRepositorySpecifications.filterByName(name));
        Page pageData = supplierRepository.findAll(supplierSpecification,pageRequest);
        return JqGridDataGenerator.getDataJson(pageData);
    }


    /**
     * 供应商表单
     * @return
     */
    @RequestMapping(value = "/supplier/new",method = RequestMethod.GET)
    @Transactional(readOnly = true)
    public ModelAndView toNew(@RequestParam(required = false) Long id ) {
        if(id != null && id >= 0) {
            Supplier sp = supplierRepository.findOne(id);
            ModelAndView model = new ModelAndView("/supplier/form", map(entry("supplier", sp)) );
            model.addObject("pageContent", "更新");
            return model;
        }
        ModelAndView model = new ModelAndView("/supplier/form", map(entry("supplier", new Supplier())) );
        model.addObject("pageContent", "新增");
        return model;
    }

    /**
     * 作废供应商
     * @param id
     * @return
     */
    @RequestMapping(value = "/supplier/delete/{id}", method = RequestMethod.GET)
    @ResponseBody
    @Transactional
    public Map<String, Object> delete(@PathVariable String id) {
        Supplier supplier = supplierRepository.findOne(Long.valueOf(id));
        if (supplier != null) supplier.deleted = !supplier.deleted;
        supplierRepository.save(supplier);
        return PurchaseOrderController.buildSuccessResult();
    }

    /**
     * 供应商表单
     * @return
     */
    @RequestMapping(value = "/supplier/save")
    @Transactional
    public ModelAndView save(@Valid @ModelAttribute Supplier supplier, BindingResult bindingResult, @AuthenticationPrincipal ERPUser user) {
        ModelAndView mav = new ModelAndView("/message");
        if (bindingResult.hasErrors()) { return new ModelAndView("/supplier/form");}
        supplier.organization = organizationRepository.findOne(user.organization.id);
        if(supplier.id != 0){
            Supplier updateSupplier = supplierRepository.findOne(supplier.id);
            updateSupplier.name = supplier.name;
            updateSupplier.fax = supplier.fax;
            updateSupplier.email = supplier.email;
            updateSupplier.description = supplier.description;
            updateSupplier.contactInfo = supplier.contactInfo;
            supplierRepository.save(updateSupplier);
        }else {
            supplierRepository.save(supplier);
        }

        mav.addObject("message","保存成功");
        mav.addObject("responsePage", "/supplier/list");
        return mav;
    }

    /**
     * 参数合法性验证
     */
    static class  SupplierValidator implements Validator {
        @Override
        public boolean supports(Class<?> clazz)
        {return Supplier.class.equals(clazz);}

        @Override
        public void validate(Object obj , Errors errors) {

            Supplier supplier = (Supplier)obj;
            String name = supplier.name;    //供应商
            if (!StringUtils.hasLength(name)) {
                errors.rejectValue("name", "required", "不能为空");
            }
            String contactInfo = supplier.contactInfo;   //联系方式
            if (!StringUtils.hasLength(contactInfo)) {
                errors.rejectValue("contactInfo", "required", "不能为空");
            }/*else if( !StringUtil.isMobile(contactInfo)){
                errors.rejectValue("contactInfo", "required", "号码格式不正确");
            }*/
//            String email = supplier.email;
//            if (!StringUtils.hasLength(email)) {
//                errors.rejectValue("email", "required", "不能为空");
//            }else if( !StringUtil.isEmail(email)){
//                errors.rejectValue("email", "required", "邮箱格式不正确");
//            }
        }
    }

}
