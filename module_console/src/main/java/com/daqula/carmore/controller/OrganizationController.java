package com.daqula.carmore.controller;

import com.daqula.carmore.model.acl.ERPRole;
import com.daqula.carmore.model.acl.ERPUser;
import com.daqula.carmore.model.admin.Agency;
import com.daqula.carmore.model.shop.Organization;
import com.daqula.carmore.model.shop.Shop;
import com.daqula.carmore.repository.*;
import com.daqula.carmore.repository.specification.OrganizationRepositorySpecifications;
import com.daqula.carmore.util.JqGridDataGenerator;
import com.daqula.carmore.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.daqula.carmore.util.CollectionUtil.entry;
import static com.daqula.carmore.util.CollectionUtil.map;

@Controller
public class OrganizationController {

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private AgencyRepository agencyRepository;


    @Autowired
    private ERPUserRepository erpUserRepository;

    @Autowired
    private ERPRoleRepository erpRoleRepository;

    @Autowired
    private ShopRepository shopRepository;

    private final String DEFAULT_PWD = "$2a$10$bwIFziCnJfF7leDXxKriE.glZ0EP1e3D9Jnk9T8uuk9ZsozSBFQbW";

    @InitBinder("organization")
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(new OrganizationValidator(organizationRepository));
    }

    /**
     * 获取组织列表
     * @return
     */
    @RequestMapping(value = "/organizations/list", method = RequestMethod.GET)
    public ModelAndView list(@AuthenticationPrincipal ERPUser erpUser) {
        ModelAndView mav = new ModelAndView("/organizations/list");
        mav.addObject("erpUser",erpUser);
        return mav;
    }

    /**
     * 组织信息异步查询加载
     * @param name 组织名
     * @return
     */
    @RequestMapping("/organizations/list/data")
    @ResponseBody
    @Transactional(readOnly = true)
    public Map<String, Object> listData(@RequestParam int page, @RequestParam int rows,
                                                      @RequestParam String sord, @RequestParam String sidx,
                                                      @RequestParam(required = false) String name,long id) {
        PageRequest pageRequest = new PageRequest(page-1, rows,
                sord.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
                !StringUtils.isEmpty(sidx) ? sidx : "id");
        ERPUser erpUser = erpUserRepository.findOne(id);
        Agency agency = agencyRepository.findByErpUser(erpUser);
        Specification specification = Specifications.where(OrganizationRepositorySpecifications.filterByAgency(agency))
                .and(OrganizationRepositorySpecifications.filterByName(name));
        Page pageData = organizationRepository.findAll(specification,pageRequest);
        return JqGridDataGenerator.getDataJson(pageData);
    }

    /**
     * 更新组织
     * @return
     */
    @RequestMapping(value = "/organizations/new", method = RequestMethod.GET)
    @ResponseBody
    @Transactional(readOnly = true)
    public ModelAndView create(@RequestParam(required = false) Long id) {

        if (id != null && id != 0) {
            if (organizationRepository.exists(id)) {
                Organization org = organizationRepository.findOne(id);
                if (org.validDate != null) org.validDateStr = org.validDate.toString("yyyy-MM-dd");
                ModelAndView model = new ModelAndView("/organizations/form", map(
                        entry("organization", org)));
                model.addObject("pageContent", "更新");
                return model;
            }
        }
        ModelAndView model = new ModelAndView("/organizations/form", map(
                entry("organization", new Organization()
                )));
        model.addObject("pageContent", "新建");
        return model;
    }

    /**
     * 新建组织
     * @param organization 组织实例
     * @return
     */
    @RequestMapping(value = "/organizations/new", method = RequestMethod.POST)
    @Transactional
    public ModelAndView saveOrganizations(@Valid @ModelAttribute Organization organization, BindingResult bindingResult,@AuthenticationPrincipal ERPUser erpUser) {
        ModelAndView mav = new ModelAndView("/message");
        if (bindingResult.hasErrors()) {
            return new ModelAndView("/organizations/form");
        }
        boolean isCreated = false;
        if (organization.id == 0){
            isCreated = true;
        }
        String msg = "保存成功";
        Organization updateOrg = organizationRepository.findOne(organization.id);
        if (updateOrg != null) {
            updateOrg.serialNum = organization.serialNum;
            updateOrg.name = organization.name;
            updateOrg.taxNumber = organization.taxNumber;
            updateOrg.contact = organization.contact;
            updateOrg.contactPhone = organization.contactPhone;
            updateOrg.contactAddress = organization.contactAddress;
            updateOrg.bankName = organization.bankName;
            updateOrg.bankAccount = organization.bankAccount;
            updateOrg.businessLicenseImageUrl = organization.businessLicenseImageUrl;
            updateOrg.shopQuota = organization.shopQuota;
            updateOrg.validDate = organization.validDate;
            updateOrg.tried = organization.tried;
            organizationRepository.save(updateOrg);
        }
        if (isCreated){
            organization.checkDay = 1;
            organization.isCheckPd = false;
            organization = organizationRepository.save(organization);
            Agency agency = agencyRepository.findByErpUser(erpUser);
            if (agency.organizations == null) agency.organizations = new ArrayList<Organization>();
            agency.organizations.add(organization);
            //创建组织角色
            agencyRepository.save(agency);
            ERPRole erpRole = new ERPRole();
            erpRole.role = organization.serialNum + "管理员";
            erpRole.authorityMask = 137438953408L;
            erpRole.ver = 1;
            erpRole.organization = organization;
            erpRole = erpRoleRepository.save(erpRole);
            //创建组织总店
            Shop shop = new Shop();
            shop.name = "总店";
            shop.address = organization.contactAddress;
            shop.shopCode = "ZD";
            shop.organization = organization;
            shop.openingHours = "8:00-18:00";
            shop.phone = organization.contactPhone;
            shop =  shopRepository.save(shop);
            //创建组织管理员
            ERPUser user = new ERPUser();
            user.username = "admin"+organization.serialNum;
            user.password = DEFAULT_PWD;
            user.realName = "组织管理员";
            user.enable = true;
            user.organization = organization;
            user.role = erpRole;
            user.shops = new ArrayList<>();
            user.shops.add(shop);
            erpUserRepository.save(user);
            msg += " /n 初始化账户为:" + user.username + " 密码:111111";
        }
        mav.addObject("message",msg);
        mav.addObject("responsePage","/organizations/list");
        return mav;
    }

    /**
     * 删除组织
     * @param ids 组织名s
     * @return
     */
    @RequestMapping(value = "/organizations/delete", method = RequestMethod.GET)
    @Transactional(readOnly = true)
    public String delete(@RequestParam String ids) {
        String[] idStr = ids.split(",");
        for (int x = 0; x < idStr.length; x++) {
            organizationRepository.delete(Long.parseLong(idStr[x]));
        }
        return "success";
    }

    static class OrganizationValidator implements Validator {
        public OrganizationValidator (OrganizationRepository organizationRepository){
            this.organizationRepository = organizationRepository;
        }
        private OrganizationRepository organizationRepository;
        @Override
        public void validate(Object obj, Errors errors) {
            Organization organization = (Organization) obj;
            String name = organization.name;
            if (!StringUtils.hasLength(name)) {
                errors.rejectValue("name", "required", "不能为空");
            }
            String contactAddress = organization.contactAddress;
            if (!StringUtils.hasLength(contactAddress)) {
                errors.rejectValue("contactAddress", "required", "不能为空");
            }
            String serialNum = organization.serialNum;
            if (!StringUtils.hasLength(serialNum)) {
                errors.rejectValue("serialNum", "required", "不能为空");
            } else {
                if (organization.id == 0) {
                    Organization org = organizationRepository.findBySerialNum(serialNum);
                    if (org != null) {
                        errors.rejectValue("serialNum", "required", "改注册号已被注册!");
                    }
                }
            }
            String taxNumber = organization.taxNumber;
            if (!StringUtils.hasLength(taxNumber)) {
                errors.rejectValue("taxNumber", "required", "不能为空");
            }
            String bankName = organization.bankName;
            if (!StringUtils.hasLength(bankName)) {
                errors.rejectValue("bankName", "required", "不能为空");
            }
            String bankAccount = organization.bankAccount;
            if (!StringUtils.hasLength(bankAccount)) {
                errors.rejectValue("bankAccount", "required", "不能为空");
            }
            String contact = organization.contact;
            if (!StringUtils.hasLength(contact)) {
                errors.rejectValue("contact", "required", "不能为空");
            }
            String contactPhone = organization.contactPhone;
            if (!StringUtils.hasLength(contactPhone)) {
                errors.rejectValue("contactPhone", "required", "不能为空");
            }

            if(organization.shopQuota == null || organization.shopQuota == 0) {
                errors.rejectValue("shopQuota", "required", "必须大于0");
            }
            if(organization.validDate == null) {
                errors.rejectValue("validDate", "required", "不能为空");
            }
        }

        /**
         * This Validator validates *just* Organization instances
         */
        @Override
        public boolean supports(Class<?> clazz) {
            return Organization.class.equals(clazz);
        }
    }
}
