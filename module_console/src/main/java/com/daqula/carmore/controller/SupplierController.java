package com.daqula.carmore.controller;

import com.daqula.carmore.model.admin.Supplier;
import com.daqula.carmore.repository.OrganizationRepository;
import com.daqula.carmore.repository.SupplierRepository;
import com.daqula.carmore.util.JqGridDataGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import com.daqula.carmore.util.StringUtil;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

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

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(new SupplierValidator());
    }

    /**
     * 供应商信息列表
     * @return
     */
    @RequestMapping(value = "/supplier/list")
    public ModelAndView list() {
        return new ModelAndView("/supplier/list" ,map(entry("orgs", organizationRepository.findAll()),entry("curOrgid", 0)) );
    }

    /**
     * 公告信息查询
     * @param name   供应商名
     * @param orgid  所属组织ID
     * @return
     */
    @RequestMapping("/supplier/list/data")
    public @ResponseBody
    Map<String, Object> listData(@RequestParam int page, @RequestParam int rows,
                                 @RequestParam String sord, @RequestParam String sidx,
                                 @RequestParam(required = false) String name,
                                 @RequestParam(required = false) Long orgid) {

        if(StringUtil.IsNullOrEmpty(name) && (orgid == null || orgid == 0) ){
            PageRequest pageRequest = new PageRequest(page-1, rows,
                    sord.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
                    !StringUtils.isEmpty(sidx) ? sidx : "id");
            Page pageData = supplierRepository.findAll(pageRequest);

            return JqGridDataGenerator.getDataJson(pageData);
        } else {
            if(StringUtil.IsNullOrEmpty(name))  name = "";
            List<Supplier> rst = supplierRepository.findSupplierByNameAndOrgid(name,orgid,page,rows,sord);
            int total = rst.size()/rows ;
            if(rst.size() < rows || total * rows < rst.size()) {
                total = total + 1;
            }
            return map(
                    entry("total", total),
                    entry("page", page),
                    entry("records", rst.size()),
                    entry("rows", rst)
            );
        }
    }


    /**
     * 供应商表单
     * @return
     */
    @RequestMapping(value = "/supplier/new")
    public ModelAndView snew(@RequestParam(required = false) Long id ) {
        if(id != null && id >= 0)
        {
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
     * 供应商表单
     * @return
     */
    @RequestMapping(value = "/supplier/save")
    public ModelAndView save(@Valid @ModelAttribute Supplier supplier ,  BindingResult bindingResult) {

        ModelAndView mav =  new ModelAndView("redirect:/supplier/list");
        if (bindingResult.hasErrors()) {
            return new ModelAndView("/supplier/form");
        }
        if( supplier.organization == null || supplier.organization.id == 0) {
            supplier.organization = organizationRepository.findOne(Long.parseLong("1"));  // 应该从session里边取
        }

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
            }else if( !StringUtil.isMobile(contactInfo)){
                errors.rejectValue("contactInfo", "required", "号码格式不正确");
            }
            String email = supplier.email;
            if (!StringUtils.hasLength(email)) {
                errors.rejectValue("email", "required", "不能为空");
            }else if( !StringUtil.isEmail(email)){
                errors.rejectValue("email", "required", "邮箱格式不正确");
            }
        }
    }

}
