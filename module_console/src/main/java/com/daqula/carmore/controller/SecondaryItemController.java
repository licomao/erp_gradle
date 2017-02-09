package com.daqula.carmore.controller;

import com.daqula.carmore.model.admin.SecondaryCategory;
import com.daqula.carmore.repository.SecondaryCategoryRepository;
import com.daqula.carmore.util.JqGridDataGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
import java.util.Map;

import static com.daqula.carmore.util.CollectionUtil.entry;
import static com.daqula.carmore.util.CollectionUtil.map;

/**
 * 二级分类Controller
 * Created by shd
 */
@Controller
public class SecondaryItemController {

    @Autowired
    private SecondaryCategoryRepository secondaryCategoryRepository;

    /**
     * Register binding validators
     *
     * @param binder
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SecondaryItemValidator secondaryItemValidator= new SecondaryItemValidator();
        binder.addValidators(secondaryItemValidator);
    }

    /**
     * 获取商品列表
     * @return
     */
    @RequestMapping("/secondaryitem/list")
    @Transactional(readOnly = true)
    public String list() {
        return "/secondaryitem/list";
    }

    /**
     * 获取添加或更新分类商品的信息
     * @param id 商品id
     * @return
     */
    @RequestMapping("/secondaryitem/tosave")
    @ResponseBody
    @Transactional(readOnly = true)
    public ModelAndView toSave(@RequestParam(required = false) Long id) {
        ModelAndView mav;
        if(id != null && id >= 0) {
            SecondaryCategory secondaryItem = secondaryCategoryRepository.findOne(id);
            mav = new ModelAndView("/secondaryitem/form", map(entry("secondaryCategory", secondaryItem)));
        } else {
            mav = new ModelAndView("/secondaryitem/form", map(entry("secondaryCategory", new SecondaryCategory())));
        }
        return mav;
    }

    /**
     * 删除分类商品
     * @param id 商品id
     * @return
     */
    @RequestMapping("/secondaryitem/delete")
    @Transactional
    public String delete(@RequestParam Long id) {
        SecondaryCategory secondaryCategory = secondaryCategoryRepository.findOne(id);
        secondaryCategory.deleted = true;
        secondaryCategoryRepository.save(secondaryCategory);
        return "redirect:/secondaryitem/list";
    }

    /**
     * 保存分类商品
     * @param secondaryCategory 分类商品信息
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/secondaryitem/save", method = RequestMethod.POST)
    @Transactional
    public String save(@Valid @ModelAttribute SecondaryCategory secondaryCategory, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "/secondaryitem/form";
        }
        secondaryCategoryRepository.save(secondaryCategory);
        return "redirect:/secondaryitem/list";
    }

    /**
     * 根据条件获取分类商品
     * @param rootCategory 顶级商品id
     * @return
     */
    @RequestMapping(value = "/secondaryitem/list/data")
    @ResponseBody
    @Transactional(readOnly = true)
    public Map<String, Object> listData(@RequestParam int page, @RequestParam int rows,
                                                                @RequestParam String sord, @RequestParam String sidx,
                                                                @RequestParam int rootCategory) {

        PageRequest pageRequest = new PageRequest(page-1, rows,
                sord.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
                !StringUtils.isEmpty(sidx) ? sidx : "id");

        Page<SecondaryCategory> secondaryCategoryList = secondaryCategoryRepository.findByRootCategoryAndDeleted(rootCategory, false, pageRequest);
        return JqGridDataGenerator.getDataJson(secondaryCategoryList);
    }

    static class SecondaryItemValidator implements Validator {

        @Override
        public boolean supports(Class<?> clazz) {
            return SecondaryCategory.class.equals(clazz);
        }

        @Override
        public void validate(Object obj, Errors errors) {
            SecondaryCategory secondaryCategory = (SecondaryCategory)obj;

            if (!StringUtils.hasLength(secondaryCategory.name)) {
                errors.rejectValue("name", "required", "不能为空");
            }
        }
    }
}
