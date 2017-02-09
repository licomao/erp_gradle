package com.daqula.carmore.controller;

import com.daqula.carmore.model.acl.ERPRole;
import com.daqula.carmore.model.acl.ERPUser;
import com.daqula.carmore.model.admin.Agency;
import com.daqula.carmore.model.shop.Organization;
import com.daqula.carmore.model.shop.Shop;
import com.daqula.carmore.repository.AgencyRepository;
import com.daqula.carmore.repository.ERPRoleRepository;
import com.daqula.carmore.repository.ERPUserRepository;
import com.daqula.carmore.repository.OrganizationRepository;
import com.daqula.carmore.repository.impl.OrganizationRepositoryImpl;
import com.daqula.carmore.util.JqGridDataGenerator;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 代理商
 * Created by mdc on 2015/10/18.
 */
@Controller
public class AgencyController {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private ERPUserRepository erpUserRepository;

    @Autowired
    private AgencyRepository agencyRepository;

    @Autowired
    private ERPRoleRepository erpRoleRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @InitBinder("agency")
    public void initBinder(WebDataBinder binder) {
        AgencyValidator agencyValidator = new AgencyValidator();
        binder.addValidators(agencyValidator);
    }

    /**
     * 代理商管理 列表
     * @return
     */
    @RequestMapping(value = "/agency/list",method = RequestMethod.GET)
    public ModelAndView list(){
        ModelAndView mav = new ModelAndView("/agency/list");

        String name  = request.getSession().getAttribute("USER_NAME").toString();
        ERPUser userInfo = erpUserRepository.findByUsername(name);

//        List<Shop> shopList =  (List<Shop>)request.getSession().getAttribute("SHOPS");
//        mav.addObject("shopList",shopList);
        mav.addObject("user",userInfo);
        return mav;
    }


    /**
     * 代理商管理 列表数据
     * @param page
     * @param rows
     * @param sord
     * @param sidx
     * @param accountName
     * @param organization
     * @return
     */
    @RequestMapping(value = "/agency/list/data", method = RequestMethod.GET)
    @ResponseBody
    @Transactional(readOnly = true)
    public Map<String, Object> getListData(@RequestParam int page, @RequestParam int rows,
                                           @RequestParam String sord, @RequestParam String sidx,
                                           String accountName,String organization){

        PageRequest pageRequest = new PageRequest(page - 1, rows,
                sord.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
                !org.springframework.util.StringUtils.isEmpty(sidx) ? sidx : "id");

        Page<Agency> pageData;

        if(StringUtils.isNotBlank(organization)){
            Organization org = new Organization();
            org.id = Long.valueOf(organization);
            List<Organization> organizations = new ArrayList<>();
            organizations.add(org);

            if(StringUtils.isBlank(accountName))
                pageData = agencyRepository.findByOrganizations(organizations, pageRequest);
            else
                pageData = agencyRepository.findByNameLikeAndOrganization("%" + accountName + "%", organizations, pageRequest);
        }else{
            if(StringUtils.isBlank(accountName))
                pageData = agencyRepository.findAll(pageRequest);
            else
                pageData = agencyRepository.findByNameLike("%" + accountName + "%", pageRequest);
        }


        for(Agency agency : pageData) {
            agency.erpUser.showedDate = agency.createdDate == null ? "" : agency.createdDate.toString();
        }
        return JqGridDataGenerator.getDataJson(pageData);
    }

    @RequestMapping("/agency/updateInfo")
    @ResponseBody
    @Transactional(readOnly = true)
    public ModelAndView updateInfo(@RequestParam(required = false) Long id) {
        ModelAndView mav = new ModelAndView("/agency/infoForm");

        return mav;
    }

    /**
     * 转到 新增/编辑 代理商 页面
     * @param id
     * @return
     */
    @RequestMapping("/agency/tosave")
    @ResponseBody
    @Transactional(readOnly = true)
    public ModelAndView toSave(@RequestParam(required = false) Long id) {
        ModelAndView mav = new ModelAndView("/agency/form");
        Agency agency ;
        if(id != null && id > 0) {
            agency = agencyRepository.findOne(id);
            agency.updatedBy = "update";
        }else {
            agency = new Agency();
            agency.updatedBy = "";
//            agency.updatedBy = "create";
        }
        mav.addObject("agency", agency);
        return mav;
    }

    /**
     * 新增/编辑(修改密码)  代理商
     * @param agency
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/agency/save", method = RequestMethod.POST)
    @Transactional
    public ModelAndView save(@Valid @ModelAttribute Agency agency, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
//            toSave(agency.id);
           return new ModelAndView("/agency/form");
           /* if(agency.id != 0)
                agency = agencyRepository.findOne(agency.id);
            else
                agency = new Agency();
            mav.addObject("agency",agency);*/
        }
        agency.createdBy = "";
        ModelAndView mav = new ModelAndView("/message");
        String message = "保存成功";
        if(agency.id == 0){
            //新增
            ERPRole role = new ERPRole();
            role.id = 2;
            Organization organization = new Organization();
            organization.id = 1;
            Shop shop = new Shop();
            shop.id = 1;

            List<Shop> shops = new ArrayList<>();
            shops.add(shop);

            ERPUser user = new ERPUser();
            user.realName = agency.erpUser.realName;
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            user.password = encoder.encode(agency.erpUser.password);
//            user.password = agency.erpUser.password;
            user.username = agency.erpUser.username;
            user.phone = agency.erpUser.phone;
            user.enable = true;
            user.role = role;
            user.shops = shops;
            user.organization = organization;

            user = erpUserRepository.save(user);
            agency.erpUser = user;
            agencyRepository.save(agency);

        }else{
            //编辑
            Agency newAgency = agencyRepository.findOne(agency.id);
//            if(changeType.equals("password")){

//                if(newAgency.erpUser.password.equals(oldPassword)){
                    //原密码正确 更新密码
//                    newAgency.erpUser.password = agency.erpUser.password;
                    message = "更新成功";
//                }else{
//                    message = "密码错误";
//
//                }
//            }else if(changeType.equals("info")){
                newAgency.erpUser.realName = agency.erpUser.realName;
                newAgency.erpUser.phone = agency.erpUser.phone;
//            }
        }
        mav.addObject("message", message);
        mav.addObject("responsePage", "/agency/list");
        return mav;
    }


    static class AgencyValidator implements Validator {
        /**
         * This Validator validates *just* StockItem instances
         */
        @Override
        public boolean supports(Class<?> clazz) {
            return Agency.class.equals(clazz);
        }

        @Override
        public void validate(Object obj, Errors errors) {
            Agency agency = (Agency)obj;
//            Agency agency = agency.agency;
            if (!org.springframework.util.StringUtils.hasLength(agency.erpUser.username)) {
                errors.rejectValue("erpUser.username", "required", "不能为空");
            }
            if (!org.springframework.util.StringUtils.hasLength(agency.erpUser.realName)) {
                errors.rejectValue("erpUser.realName", "required", "不能为空");
            }
            if (!org.springframework.util.StringUtils.hasLength(agency.erpUser.phone)) {
                errors.rejectValue("erpUser.phone", "required", "不能为空");
            }
            if (!org.springframework.util.StringUtils.hasLength(agency.erpUser.password)) {
                if(agency.erpUser.enable)
                    errors.rejectValue("erpUser.password", "required", "不能为空");
            }
        }
    }


}
