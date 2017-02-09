package com.daqula.carmore.controller;

import com.daqula.carmore.AuthorityConst;
import com.daqula.carmore.model.acl.ERPUser;
import com.daqula.carmore.model.shop.Job;
import com.daqula.carmore.model.shop.Organization;
import com.daqula.carmore.model.shop.Shop;
import com.daqula.carmore.model.shop.Staff;
import com.daqula.carmore.repository.JobRepository;
import com.daqula.carmore.repository.OrganizationRepository;
import com.daqula.carmore.repository.ShopRepository;
import com.daqula.carmore.repository.StaffRepository;
import com.daqula.carmore.repository.specification.JobSpecifications;
import com.daqula.carmore.repository.specification.StaffSpecifications;
import com.daqula.carmore.util.BeanUtil;
import com.daqula.carmore.util.JqGridDataGenerator;
import com.daqula.carmore.util.SessionUtil;
import com.daqula.carmore.util.StringUtil;

import org.joda.time.DateTime;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.daqula.carmore.util.FreeMarkerUtil.getAuthorityModel;

/**
 * 员工管理Controller
 * Created by Chexnin on 2015/10/9 0009.
 */
@Controller
public class StaffController {

    @Autowired
    public StaffRepository staffRepository;

    @Autowired
    public ShopRepository shopRepository;

    @Autowired
    public OrganizationRepository organizationRepository;

    @Autowired
    public JobRepository jobRepository;

    @InitBinder("staff")
    public void initBinder(WebDataBinder binder) {
        StaffValidator staffValidator = new StaffValidator();
        staffValidator.setRepository(staffRepository,shopRepository);
        binder.addValidators(staffValidator);
    }

    /**
     * TO员工管理查询
     * @return
     */
    @RequestMapping(value = "/staff/list" ,method = RequestMethod.GET)
    @Transactional(readOnly = true)
    public ModelAndView list (@AuthenticationPrincipal ERPUser user, HttpSession session) {
        ModelAndView mav = new ModelAndView("/staff/list");

        if (!user.checkAuthority(AuthorityConst.MANAGE_ORG_STAFF)) {
            mav.setViewName("/noauthority");
            return mav;
        }

        List<Shop> shops = SessionUtil.getShopList(session, organizationRepository, shopRepository);

        if (shops == null) {
            mav.setViewName("/logout");
            return mav;
        }

        mav.addObject("AUTHORITY", getAuthorityModel());
        mav.addObject("user",user);
        mav.addObject("shops",shops);
        return mav;
    }

    /**
     *获取员工列表数据
     * @param page
     * @param rows
     * @param sord
     * @param sidx
     * @param staff 员工列表查询条件
     * @return
     */
    @RequestMapping(value = "/staff/list/data", method = RequestMethod.GET)
    @ResponseBody
    @Transactional(readOnly = true)
    public Map<String, Object> listData(@RequestParam int page, @RequestParam int rows,
                                        @RequestParam String sord, @RequestParam String sidx, @ModelAttribute Staff staff, HttpSession session, @AuthenticationPrincipal ERPUser user) {
        PageRequest pageRequest = new PageRequest(page - 1, rows,
                sord.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
                !StringUtils.isEmpty(sidx) ? sidx : "id");
        Organization organization = organizationRepository.findOne(user.organization.id);

        Specification<Staff> specification = Specifications.where(StaffSpecifications.filterByName(staff.name))
                .and(StaffSpecifications.filterByShop(staff.shop))
                .and(StaffSpecifications.filterByOrganization(organization))
                .and(StaffSpecifications.filterByStatus(staff.status))
                .and(StaffSpecifications.filterByDeleted(false));
        Page pageData = staffRepository.findAll(specification,pageRequest);
        return JqGridDataGenerator.getDataJson(pageData);
    }

    /**
     * 进入员工信息页面
     * @param id 员工的id
     * @param doType 类型，新增=0，修改=1，查看=2
     * @return
     */
    @RequestMapping(value = "/staff/form", method = RequestMethod.GET)
    @Transactional(readOnly = true)
    public ModelAndView form(String id, Integer doType, HttpSession session, @AuthenticationPrincipal ERPUser user){
        ModelAndView mav = new ModelAndView("/staff/form");
        Staff staff = new Staff();
        Organization organization = organizationRepository.findOne(user.organization.id);

        List<Shop> shops = SessionUtil.getShopList(session, organizationRepository, shopRepository);

        if (shops == null) {
            mav.setViewName("/logout");
            return mav;
        }
        Specification<Job> specification = Specifications.where(JobSpecifications.filterByDeleted(false))
                .and(JobSpecifications.filterByOrganization(organization));
        List<Job> jobs = jobs = (List<Job>) jobRepository.findAll(specification);
        if (!id.equals("0") && doType != 0) {
            staff = staffRepository.findOne(Long.parseLong(id));
            if (staff == null) {
                mav.setViewName("/message");
                mav.addObject("message","未找到相关的员工信息");
                mav.addObject("responsePage","/staff/list");
                return mav;
            }
        }

        mav.addObject("doType", doType);
        mav.addObject("staff", staff);
        mav.addObject("shops", shops);
        mav.addObject("jobs", jobs);
        return mav;
    }

    /**
     * 保存员工信息
     * @param staff
     * @param doType 类型，新增=0，修改=1，查看=2
     * @return
     */
    @RequestMapping(value = "/staff/save", method = RequestMethod.POST)
    @Transactional
    public ModelAndView save(@Valid @ModelAttribute Staff staff, BindingResult bindingResult, HttpSession session, int doType, @AuthenticationPrincipal ERPUser user){
        ModelAndView mav = new ModelAndView("/message");
        String message="保存成功";
        if (bindingResult.hasErrors()) {
            List<Shop> shops = SessionUtil.getShopList(session, organizationRepository, shopRepository);
            Organization organization = organizationRepository.findOne(user.organization.id);

            if (shops == null) {
                mav.setViewName("/logout");
                return mav;
            }
            Specification<Job> specification = Specifications.where(JobSpecifications.filterByDeleted(false))
                    .and(JobSpecifications.filterByOrganization(organization));
            List<Job> jobs = (List<Job>) jobRepository.findAll(specification);

            mav.setViewName("/staff/form");
            mav.addObject("shops", shops);
            mav.addObject("jobs", jobs);
            mav.addObject("doType", doType);
            return mav;
        }

        if (staff.id != 0){
            Staff updateStaff = staffRepository.findOne(staff.id);
//            staff.
//            updateStaff.fingerprint = staff.fingerprint;
//            updateStaff.staffAttendances = staff.staffAttendances;
//            updateStaff

            staff.staffAttendances = updateStaff.staffAttendances;
            BeanUtil.copyFields(staff, updateStaff);
            staffRepository.save(updateStaff);
        } else {
            staffRepository.save(staff);
        }

        mav.addObject("message",message);
        mav.addObject("responsePage","/staff/list");
        return mav;
    }

    /**
     * 删除员工信息
     * @param id 员工的id
     * @return
     */
    @RequestMapping(value = "/staff/delete", method = RequestMethod.GET)
    @Transactional
    public ModelAndView delete(String id) {
        ModelAndView mav = new ModelAndView();

        Staff staff = staffRepository.findOne(Long.parseLong(id));
        staff.deleted = true;

        mav.setViewName("/message");
        mav.addObject("message", "删除成功");
        mav.addObject("responsePage", "/staff/list");
        return mav;
    }

    /**
     * valid
     */
    static class StaffValidator implements Validator {

        @Autowired
        private StaffRepository staffRepository;

        private ShopRepository shopRepository;

        @Override
        public boolean supports(Class<?> clazz) {
            return Staff.class.equals(clazz);
        }

        /**
         * 添加Repository到验证器
         * @param staffRepository
         */
        public void setRepository(StaffRepository staffRepository,ShopRepository shopRepository) {
            this.staffRepository = staffRepository;
            this.shopRepository = shopRepository;
        }

        @Override
        public void validate(Object obj, Errors errors) {
            Staff staff = (Staff) obj;

            String name = staff.name;
            if (!StringUtils.hasLength(name)) {
                errors.rejectValue("name", "required", "不能为空");
            }
            String identityCard = staff.identityCard;
            if (!StringUtils.hasLength(identityCard)) {
                errors.rejectValue("identityCard", "required", "不能为空");
            } else if (!StringUtil.CheckIDCard(identityCard)) {
                errors.rejectValue("identityCard", "required", "请输入正确的身份证号");
            }
            String phone = staff.phone;
            if (!StringUtils.hasLength(identityCard)) {
                errors.rejectValue("phone", "required", "不能为空");
            } else if (!StringUtil.isMobile(phone)) {
                errors.rejectValue("phone", "required", "手机号必须为11位");
            } else {
                Shop shop = shopRepository.findOne(staff.shop.id);
                Specification<Staff> specificationS = Specifications.where(StaffSpecifications.filterByPhone(phone))
                        .and(StaffSpecifications.filterByShop(shop));
                Staff staffFind = (Staff) staffRepository.findOne(specificationS);
                if (staffFind != null && staffFind.id != staff.id)
                errors.rejectValue("phone", "required", "已有重复手机号");
            }
            DateTime entryDate = staff.entryDate;
            if (entryDate == null) {
                errors.rejectValue("entryDate", "required", "不能为空");
            }
            String status = staff.status;
            DateTime dimissionDate = staff.dimissionDate;
            if (status.equals("3") && dimissionDate == null) {
                errors.rejectValue("dimissionDate", "required", "离职状态时不能为空");
            }
        }
    }

}