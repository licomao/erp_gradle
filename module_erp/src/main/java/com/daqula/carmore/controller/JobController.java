package com.daqula.carmore.controller;

import com.daqula.carmore.AuthorityConst;
import com.daqula.carmore.model.acl.ERPUser;
import com.daqula.carmore.model.shop.Job;

import com.daqula.carmore.model.shop.Organization;
import com.daqula.carmore.repository.JobRepository;
import com.daqula.carmore.repository.OrganizationRepository;
import com.daqula.carmore.repository.specification.JobSpecifications;
import com.daqula.carmore.repository.specification.StaffSpecifications;
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
import java.util.Map;

import static com.daqula.carmore.util.FreeMarkerUtil.getAuthorityModel;

/**
 * Created by chenxin on 2015/10/10 0010.
 */
@Controller
public class JobController {

    @Autowired
    public JobRepository jobRepository;

    @Autowired
    public OrganizationRepository organizationRepository;

    @InitBinder("job")
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(new JobValidator());
    }

    /**
     * TO职位查询
     * @return
     */
    @RequestMapping(value = "/job/list" ,method = RequestMethod.GET)
    @Transactional(readOnly = true)
    public ModelAndView list (@AuthenticationPrincipal ERPUser user) {
        ModelAndView mav = new ModelAndView("/job/list");

        if (!user.checkAuthority(AuthorityConst.MANAGE_ORG_JOB)) {
            mav.setViewName("/noauthority");
            return mav;
        }

        mav.addObject("AUTHORITY", getAuthorityModel());
        mav.addObject("user",user);
        return mav;
    }

    /**
     *获取职位列表数据
     * @param page
     * @param rows
     * @param sord
     * @param sidx
     * @param job 员工列表查询条件
     * @return
     */
    @RequestMapping(value = "/job/list/data", method = RequestMethod.GET)
    @ResponseBody
    @Transactional(readOnly = true)
    public Map<String, Object> listData(@RequestParam int page, @RequestParam int rows,
                                        @RequestParam String sord, @RequestParam String sidx, @ModelAttribute Job job, HttpSession session) {
        PageRequest pageRequest = new PageRequest(page - 1, rows,
                sord.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
                !StringUtils.isEmpty(sidx) ? sidx : "id");
        Organization organization = (Organization) session.getAttribute("ORGANIZATIONS");
        Long organizationId = organization.id;
        organization = organizationRepository.findOne(organizationId);

        Specification<Job> specification = Specifications.where(JobSpecifications.filterByName(job.name))
                .and(JobSpecifications.filterByOrganization(organization));
        Page pageData = jobRepository.findAll(specification,pageRequest);
        return JqGridDataGenerator.getDataJson(pageData);
    }

    /**
     * 进入职位信息页面
     * @param id 职位的id
     * @return
     */
    @RequestMapping(value = "/job/form", method = RequestMethod.GET)
    @Transactional(readOnly = true)
    public ModelAndView form(String id){
        ModelAndView mav = new ModelAndView("/job/form");
        Job job = new Job();

        if (!id.equals("0")) {
            job = jobRepository.findOne(Long.parseLong(id));
            if (job == null) {
                mav.setViewName("/message");
                mav.addObject("message","未找到相关的职位信息");
                mav.addObject("responsePage","/job/list");
                return mav;
            }
        }

        mav.addObject("job", job);
        return mav;
    }

    /**
     * 保存职位信息
     * @param job
     * @return
     */
    @RequestMapping(value = "/job/save", method = RequestMethod.POST)
    @Transactional
    public ModelAndView save(@Valid @ModelAttribute Job job, BindingResult bindingResult, HttpSession session){
        ModelAndView mav = new ModelAndView("/message");
        String message="保存成功";
        if (bindingResult.hasErrors()) {
            return new ModelAndView("/job/form");
        }

        Organization organization = (Organization) session.getAttribute("ORGANIZATIONS");
        Long organizationId = organization.id;
        organization = organizationRepository.findOne(organizationId);

        Job jobToUpdate = jobRepository.findOne(job.id);
        if (jobToUpdate != null) {
            jobToUpdate.organization = organization;
            jobToUpdate.name = job.name;
        } else {
            job.organization = organization;
            jobRepository.save(job);
        }

        mav.addObject("message",message);
        mav.addObject("responsePage","/job/list");
        return mav;
    }

    /**
     * 保存作废，启用
     * @param id
     * @return
     */
    @RequestMapping(value = "/job/enable", method = RequestMethod.GET)
    @Transactional
    public ModelAndView enable(String id){
        ModelAndView mav = new ModelAndView("/message");
        String message="修改成功";

        Job job = jobRepository.findOne(Long.parseLong(id));
        job.deleted = !job.deleted;
        jobRepository.save(job);

        mav.addObject("message",message);
        mav.addObject("responsePage","/job/list");
        return mav;
    }

    /**
     * valid
     */
    static class JobValidator implements Validator {

        @Override
        public boolean supports(Class<?> clazz) {
            return Job.class.equals(clazz);
        }

        @Override
        public void validate(Object obj, Errors errors) {
            Job job = (Job) obj;

            String name = job.name;
            if (!StringUtils.hasLength(name)) {
                errors.rejectValue("name", "required", "不能为空");
            }
        }
    }

}
