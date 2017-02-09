package com.daqula.carmore.controller;

import com.daqula.carmore.AuthorityConst;
import com.daqula.carmore.model.acl.ERPUser;
import com.daqula.carmore.model.shop.*;
import com.daqula.carmore.repository.*;
import com.daqula.carmore.repository.specification.ExpenseSpecifications;
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
import java.util.*;
import java.util.concurrent.ExecutionException;

import static com.daqula.carmore.util.FreeMarkerUtil.getAuthorityModel;

/**
 * 费用管理Controller
 * Created by Chexnin on 2015/10/22 0009.
 */
@Controller
public class ExpenseController {

    @Autowired
    public ShopRepository shopRepository;

    @Autowired
    public OrganizationRepository organizationRepository;

    @Autowired
    public ExpenseRepository expenseRepository;

    @InitBinder("expense")
    public void initBinder(WebDataBinder binder) {
        ExpenseValidator expenseValidator = new ExpenseValidator();
        expenseValidator.setRepository(expenseRepository,shopRepository);
        binder.addValidators(expenseValidator);
    }

    /**
     * TO费用管理查询
     * @return
     */
    @RequestMapping(value = "/expense/list" ,method = RequestMethod.GET)
    @Transactional(readOnly = true)
    public ModelAndView list (@AuthenticationPrincipal ERPUser user, HttpSession session) {
        ModelAndView mav = new ModelAndView("/expense/list");
        if (!user.checkAuthority(AuthorityConst.MANAGE_ORG_EXPENSE)) {
            mav.setViewName("/noauthority");
            return mav;
        }

        List<String> years = new ArrayList<String>();
        List<String> months = new ArrayList<String>();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        for (int i = -1; i < 5; i++) {
            years.add(i+1, calendar.get(calendar.YEAR) + i + "");
        }
        for (int m = 1; m < 13; m++) {
            months.add(m -1, m + "");
        }

        List<Shop> shopList = SessionUtil.getShopList(session, organizationRepository, shopRepository);

        if (shopList == null) {
            mav.setViewName("/logout");
            return mav;
        }

        mav.addObject("shops",shopList);
        mav.addObject("months",months);
        mav.addObject("years",years);
        mav.addObject("monthNow",calendar.get(calendar.MONTH)+1);
        mav.addObject("yearNow",calendar.get(calendar.YEAR)+"");
        return mav;
    }

    /**
     *获取费用列表数据
     * @param page
     * @param rows
     * @param sord
     * @param sidx
     * @param expense 费用列表查询条件
     * @return
     */
    @RequestMapping(value = "/expense/list/data", method = RequestMethod.GET)
    @ResponseBody
    @Transactional(readOnly = true)
    public Map<String, Object> listData(@RequestParam int page, @RequestParam int rows,
                                        @RequestParam String sord, @RequestParam String sidx, @ModelAttribute Expense expense, HttpSession session) {
        PageRequest pageRequest = new PageRequest(page - 1, rows,
                sord.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
                !StringUtils.isEmpty(sidx) ? sidx : "id");
        Organization organization = (Organization) session.getAttribute("ORGANIZATIONS");
        Long organizationId = organization.id;
        organization = organizationRepository.findOne(organizationId);

        Specification<Expense> specification = Specifications.where(ExpenseSpecifications.filterByShop(expense.shop))
                .and(ExpenseSpecifications.filterByYear(expense.year))
                .and(ExpenseSpecifications.filterByOrganization(organization))
                .and(ExpenseSpecifications.filterByDeleted(expense.deleted))
                .and(ExpenseSpecifications.filterByMonth(expense.month));
        Page<Expense> pageData = expenseRepository.findAll(specification,pageRequest);
        for(Expense expenseFind : pageData) {
            expenseFind.notePerson = expenseFind.updatedBy;
            expenseFind.operateDate = expenseFind.updatedDate;
        }

        return JqGridDataGenerator.getDataJson(pageData);
    }

    /**
     * 进入费用信息页面
     * @param id 费用的id
     * @param doType 类型，0新增，1修改,2查看
     * @return
     */
    @RequestMapping(value = "/expense/form", method = RequestMethod.GET)
    @Transactional(readOnly = true)
    public ModelAndView form(String id, HttpSession session, String doType,String shopId){
        ModelAndView mav = new ModelAndView("/expense/form");
        Shop shop = shopRepository.findOne(Long.parseLong(shopId));
        Expense expense = new Expense();
        List<String> years = new ArrayList<String>();
        List<String> months = new ArrayList<String>();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        for (int i = -1; i < 5; i++) {
            years.add(i+1, calendar.get(Calendar.YEAR) + i + "");
        }
        for (int m = 1; m < 13; m++) {
            months.add(m -1, m + "");
        }

        if (!id.equals("0")) {
            expense = expenseRepository.findOne(Long.parseLong(id));
            if (expense == null) {
                mav.setViewName("/message");
                mav.addObject("message","未找到相关的费用信息");
                mav.addObject("responsePage","/expense/list");
                return mav;
            }
        } else {
            expense.month = calendar.get(calendar.MONTH)+1;
            expense.year = calendar.get(calendar.YEAR);
        }
        expense.shop = shop;
        mav.addObject("doType",doType);
        mav.addObject("months",months);
        mav.addObject("years",years);
        mav.addObject("expense", expense);
        return mav;
    }

    /**
     * 保存费用信息
     * @param expense
     * @param doType 类型，0新增，1修改,2查看
     * @return
     */
    @RequestMapping(value = "/expense/save", method = RequestMethod.POST)
    @Transactional
    public ModelAndView save(@Valid @ModelAttribute Expense expense, BindingResult bindingResult, HttpSession session, String doType){
        ModelAndView mav = new ModelAndView("/message");
        String message="保存成功";
        if (bindingResult.hasErrors()) {
            List<String> years = new ArrayList<String>();
            List<String> months = new ArrayList<String>();

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            for (int i = -1; i < 5; i++) {
                years.add(i+1, calendar.get(Calendar.YEAR) + i + "");
            }
            for (int m = 1; m < 13; m++) {
                months.add(m -1, m + "");
            }

            mav.addObject("errorMessage", bindingResult.getFieldError().getDefaultMessage());
            mav.addObject("doType",doType);
            mav.addObject("months",months);
            mav.addObject("years",years);
            mav.setViewName("/expense/form");
            return mav;
        }

        if (expense.id != 0) {
            Expense expenseFind = expenseRepository.findOne(expense.id);
            expenseFind.month = expense.month;
            expenseFind.year = expense.year;
            expenseFind.rentExpense = expense.rentExpense;
            expenseFind.propertyExpense = expense.propertyExpense;
            expenseFind.waterExpense = expense.waterExpense;
            expenseFind.electricExpense = expense.electricExpense;
            expenseFind.netPhoneExpense = expense.netPhoneExpense;
            expenseFind.equipRepairsExpense = expense.equipRepairsExpense;
            expenseFind.staffBaseExpense = expense.staffBaseExpense;
            expenseFind.staffCommissionExpense = expense.staffCommissionExpense;
            expenseFind.staffPerformanceExpense = expense.staffPerformanceExpense;
            expenseFind.otherExpense = expense.otherExpense;
            expenseRepository.save(expenseFind);
        } else {
            Shop shop = (Shop) session.getAttribute("SHOP");
            expense.shop = shop ;
            expenseRepository.save(expense);
        }

        mav.addObject("message",message);
        mav.addObject("responsePage","/expense/list");
        return mav;
    }

    /**
     * 作废/启用费用信息
     * @param id 费用信息的id
     * @return
     */
    @RequestMapping(value = "/expense/delete", method = RequestMethod.GET)
    @Transactional
    public ModelAndView delete(String id) {
        ModelAndView mav = new ModelAndView("/message");
        String message = "删除成功";
        Expense expense = expenseRepository.findOne(Long.parseLong(id));

        if (expense.deleted) {
            Specification<Expense> specificationS = Specifications.where(ExpenseSpecifications.filterByYear(expense.year))
                    .and(ExpenseSpecifications.filterByMonth(expense.month))
                    .and(ExpenseSpecifications.filterByDeleted(false));
            Expense expenseFind = (Expense) expenseRepository.findOne(specificationS);
            if (expenseFind != null && expenseFind.id != expense.id) {
                message = "已有相同的年度月度费用,无法启用";
                mav.addObject("message", message);
                mav.addObject("responsePage", "/expense/list");
                return mav;
            } else {
                message = "启用成功";
                expense.deleted = false;
                expenseRepository.save(expense);
            }
        } else {
            expense.deleted = true;
            expenseRepository.save(expense);
        }

        mav.addObject("message", message);
        mav.addObject("responsePage", "/expense/list");
        return mav;
    }

    /**
     * valid
     */
    static class ExpenseValidator implements Validator {

        @Autowired
        private ExpenseRepository expenseRepository;

        @Autowired
        private ShopRepository shopRepository;

        @Override
        public boolean supports(Class<?> clazz) {
            return Expense.class.equals(clazz);
        }

        /**
         * 添加Repository到验证器
         * @param expenseRepository
         */
        public void setRepository(ExpenseRepository expenseRepository,ShopRepository shopRepository) {
            this.expenseRepository = expenseRepository;
            this.shopRepository = shopRepository;
        }

        @Override
        public void validate(Object obj, Errors errors) {
            Expense expense = (Expense) obj;
            int year = expense.year;
            int month = expense.month;
            Shop shop = shopRepository.findOne(expense.shop.id);
            Specification<Expense> specificationS = Specifications.where(ExpenseSpecifications.filterByYear(year))
                    .and(ExpenseSpecifications.filterByMonth(month))
                    .and(ExpenseSpecifications.filterByShop(shop))
                    .and(ExpenseSpecifications.filterByDeleted(false));
            Expense expenseFind = (Expense) expenseRepository.findOne(specificationS);
            if (expenseFind != null && expenseFind.id != expense.id) {
                errors.rejectValue("year", "", "已有相同的年度月度费用");
            }
        }
    }

}