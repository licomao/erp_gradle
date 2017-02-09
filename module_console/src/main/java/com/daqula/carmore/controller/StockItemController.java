package com.daqula.carmore.controller;

import com.daqula.carmore.model.admin.SecondaryCategory;
import com.daqula.carmore.model.admin.StockItem;
import com.daqula.carmore.model.shop.Organization;
import com.daqula.carmore.repository.OrganizationRepository;
import com.daqula.carmore.repository.SecondaryCategoryRepository;
import com.daqula.carmore.repository.StockItemRepository;
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
import java.util.List;
import java.util.Map;

import static com.daqula.carmore.util.CollectionUtil.entry;
import static com.daqula.carmore.util.CollectionUtil.map;

/**
 * 库存Controller
 * Created by shd
 */
@Controller
public class StockItemController {

    @Autowired
    private StockItemRepository stockItemRepository;

    @Autowired
    private SecondaryCategoryRepository secondaryCategoryRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    /**
     * Register binding validators
     *
     * @param binder
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        StockItemValidator stockItemValidator= new StockItemValidator();
        stockItemValidator.setRepository(stockItemRepository);

        binder.addValidators(stockItemValidator);
    }

    /**
     * 获取商品列表
     * @return
     */
    @RequestMapping("/stockitem/list")
    public String list() {
        return "/stockitem/list";
    }

    /**
     * 获取创建或更新表单内容
     * @param id 表单id
     * @return
     */
    @RequestMapping("/stockitem/tosave")
    @ResponseBody
    @Transactional(readOnly = true)
    public ModelAndView toSave(@RequestParam(required = false) Long id) {
        ModelAndView mav;
        if(id != null && id >= 0) {
            StockItem stockItem = stockItemRepository.findOne(id);
            mav = new ModelAndView("/stockitem/form", map(entry("stockItem", stockItem)));
        } else {
            mav = new ModelAndView("/stockitem/form", map(entry("stockItem", new StockItem())));
        }
        return mav;
    }

    /**
     * 获取分类商品信息
     * @param root 顶级分类id
     * @return
     */
    @RequestMapping("/stockitem/list/secondarydata")
    @ResponseBody
    @Transactional(readOnly = true)
    public Map<String, Object> secondarydata(Integer root,long orgId) {
        Organization organization = organizationRepository.findOne(orgId);
        List<SecondaryCategory> secondaryCategory = secondaryCategoryRepository.findByRootCategoryAndDeletedAndOrganization(root, false,organization);
        return map(entry("secondaryCategoryList",secondaryCategory));
    }

    /**
     * 删除商品
     * @param id 商品id
     * @return
     */
    @RequestMapping("/stockitem/delete")
    @Transactional
    public String delete(@RequestParam Long id) {
        StockItem stockItem = stockItemRepository.findOne(id);
        stockItem.deleted = true;
        stockItemRepository.save(stockItem);
        return "redirect:/stockitem/list";
    }

    /**
     * 保存商品信息
     * @param stockItem 商品内容
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/stockitem/save", method = RequestMethod.POST)
    @Transactional
    public String save(@Valid @ModelAttribute StockItem stockItem, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "/stockitem/form";
        }
        stockItemRepository.save(stockItem);
        return "redirect:/stockitem/list";
    }

    /**
     * 根据条件获取商品管理列表
     *
     * @param itemName 商品名字
     * @param rootCategory 顶级分类
     * @param isAppSale 是否是App推广
     * @return
     */
    @RequestMapping(value = "/stockitem/list/data")
    @ResponseBody
    @Transactional(readOnly = true)
    public Map<String, Object> listData(@RequestParam int page, @RequestParam int rows,
                                                                @RequestParam String sord, @RequestParam String sidx,
                                                                @RequestParam String itemName, @RequestParam int rootCategory,
                                                                @RequestParam int isAppSale) {
        PageRequest pageRequest = new PageRequest(page-1, rows,
                sord.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
                !StringUtils.isEmpty(sidx) ? sidx : "id");
        if(StringUtils.isEmpty(itemName)) {
            itemName ="";
        }
        Page<StockItem> stockItemList = stockItemRepository.findByNameLikeAndRootCategory(itemName, rootCategory, pageRequest);
        return JqGridDataGenerator.getDataJson(stockItemList);
    }

    static class StockItemValidator implements Validator {
        @Autowired
        private StockItemRepository stockItemRepository;

        /**
         * This Validator validates *just* StockItem instances
         */
        @Override
        public boolean supports(Class<?> clazz) {
            return StockItem.class.equals(clazz);
        }

        /**
         * 添加Repository到验证器
         * @param stockItemRepository
         */
        public void setRepository(StockItemRepository stockItemRepository) {
            this.stockItemRepository = stockItemRepository;
        }

        @Override
        public void validate(Object obj, Errors errors) {
            StockItem stockItem = (StockItem)obj;

            if(stockItem.appSort > 0 && stockItem.id == 0) {
                if(stockItemRepository.findByAppSort(stockItem.appSort).size() > 0) {
                    errors.rejectValue("appSort", "required", "该序号已被使用");
                }
            }

            if (!StringUtils.hasLength(stockItem.name)) {
                errors.rejectValue("name", "required", "不能为空");
            }

            if (!StringUtils.hasLength(stockItem.brandName)) {
                errors.rejectValue("brandName", "required", "不能为空");
            }
        }
    }
}
