package com.daqula.carmore.controller;

import com.daqula.carmore.AuthorityConst;
import com.daqula.carmore.model.acl.ERPUser;
import com.daqula.carmore.model.admin.SecondaryCategory;
import com.daqula.carmore.model.admin.Supplier;
import com.daqula.carmore.model.shop.CustomStockItem;
import com.daqula.carmore.model.shop.Organization;
import com.daqula.carmore.model.shop.Shop;
import com.daqula.carmore.repository.CustomStockItemRepository;
import com.daqula.carmore.repository.OrganizationRepository;
import com.daqula.carmore.repository.SecondaryCategoryRepository;
import com.daqula.carmore.repository.SupplierRepository;
import com.daqula.carmore.repository.specification.CustomStockItemRepositorySpecifications;
import com.daqula.carmore.util.ExcelImportCustomerStockItem;
import com.daqula.carmore.util.ExcelUtil;
import com.daqula.carmore.util.JqGridDataGenerator;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.daqula.carmore.util.CollectionUtil.entry;
import static com.daqula.carmore.util.CollectionUtil.map;

/**
 * 顾客库存Controller
 * Created by shd
 */
@Controller
public class CustomStockItemController {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private CustomStockItemRepository customStockItemRepository;

    @Autowired
    private SecondaryCategoryRepository secondaryCategoryRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    /**
     * Register binding validators
     *
     * @param binder
     */
    @InitBinder("customStockItem")
    public void initBinder(WebDataBinder binder) {
        CustomStockItemValidator customStockItemValidator = new CustomStockItemValidator();
        customStockItemValidator.setRepository(customStockItemRepository);

        binder.addValidators(customStockItemValidator);
    }

    /**
     * 获取商品列表
     *
     * @return
     */
    @RequestMapping(value = "/customstockitem/list")
    @Transactional(readOnly = true)
    public ModelAndView list(@AuthenticationPrincipal ERPUser user) {
        ModelAndView mav = new ModelAndView("/customstockitem/list");

        if (!user.checkAuthority(AuthorityConst.MANAGE_ORG_CUSTOMSTOCKITEM)) {
            mav.setViewName("/noauthority");
            return mav;
        }

        Shop shop = (Shop) request.getSession().getAttribute("SHOP");
        mav.addObject("shopType", shop == null ? "" : shop.shopType);
        mav.addObject("orgId", user.organization.id);
        return mav;
    }

    /**
     * 前往导入商品界面
     * @param user
     * @return
     */
    @RequestMapping(value = "/customstockitem/toimport" , method = RequestMethod.GET)
    @Transactional(readOnly = true)
    public ModelAndView toImport(@AuthenticationPrincipal ERPUser user,HttpSession session){
        ModelAndView mav = new ModelAndView("/customstockitem/import");
        int result = 0;
        if (session.getAttribute("importResult") != null) {
            result = (Integer) session.getAttribute("importResult");
        }
        List<ExcelImportCustomerStockItem> wrongList = (List<ExcelImportCustomerStockItem>)session.getAttribute("wrongList");
        if (wrongList != null){
            mav.addObject("wrongSize",wrongList.size());
            mav.addObject("wrongs",wrongList);
            mav.addObject("success",result);
            mav.addObject("description",true);
        } else {
            mav.addObject("description",false);
        }
        session.removeAttribute("wrongList");
        session.removeAttribute("importResult");
        return mav;
    }


    @RequestMapping(value = "/customerstockitem/excel/upload", method = RequestMethod.POST)
    @Transactional
    public ModelAndView importStockItem(@RequestParam("file") MultipartFile file, HttpSession session){
        ModelAndView mav = new ModelAndView("/message");
        String message = "导入成功";
        Organization organization = ((Organization) session.getAttribute("ORGANIZATIONS"));
        organization = organizationRepository.findOne(organization.id);
        String contentType = file.getContentType();
        String name = file.getName();
        String originalFilename = file.getOriginalFilename();
        String path = session.getServletContext().getRealPath("upload");
        String typeName = "";
        String[] split = originalFilename.split("\\.");
        if (split.length > 0) {
            typeName = split[split.length - 1];
            if (!"xls".equals(typeName) && !equals(typeName) && !"xlsx".equals(typeName)) {
                message = "文件类型错误!";
                mav.addObject("message", message);
                mav.addObject("responsePage", "/customstockitem/toimport");
                return mav;
            }
        }
        ExcelUtil<ExcelImportCustomerStockItem> util = new ExcelUtil<ExcelImportCustomerStockItem>(ExcelImportCustomerStockItem.class);
        int result = 0;
        long line = 2;
        List<ExcelImportCustomerStockItem> wrongList = new ArrayList<ExcelImportCustomerStockItem> ();
        List<ExcelImportCustomerStockItem> list = null;
        try {
            list  = util.importExcel("Sheet1", file.getInputStream(), typeName);
            CustomStockItem customStockItem;
            for (ExcelImportCustomerStockItem stockItem : list) {
                customStockItem = new CustomStockItem();
                stockItem.line = line;
                line ++;
                List<Supplier> suppliers = supplierRepository.findByOrganizationAndNameAndDeleted(organization, stockItem.supplier, false);
                if (suppliers.size() > 0) {
                    customStockItem.supplier = suppliers.get(0);
                    customStockItem.name = stockItem.name;
                    customStockItem.barCode = stockItem.barCode;
                    customStockItem.brandName = stockItem.brandName;
                    customStockItem.rootCategory = getRootCategory(stockItem.rootCategory);
                    customStockItem.organization = organization;
                    List<SecondaryCategory> secondaryCategories = secondaryCategoryRepository.findByRootCategoryAndDeletedAndOrganization(customStockItem.rootCategory,false,organization);
                    if (secondaryCategories.size() > 0) {
                        customStockItem.secondaryCategory = secondaryCategories.get(0);
                    }
                    customStockItem.isDistribution = getDistribution(stockItem.payType);
                    customStockItemRepository.save(customStockItem);
                    result ++;
                } else {
                    wrongList.add(stockItem);
                    continue;
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
            if (list != null)  wrongList.add(list.get(++ result));
        }
        session.setAttribute("importResult",result);
        session.setAttribute("wrongList",wrongList);
        mav.addObject("message", message);
        mav.addObject("responsePage", "/customstockitem/toimport");
        return mav;
    }

    public static int getDistribution(String name){
        int index = 0;
        switch (name) {
            case "现结":
                index = 3;
                break;
            case "月结":
                index = 0;
                break;
            case "铺货":
                index = 1;
                break;
        }
        return index;
    }

    public static int  getRootCategory(String name) {
        int index = 0;
//        1:机油;2:机滤;3:轮胎;4:;5:;6:;7:;8:;9:;10:;" +
//        "11:;12:;13:;14:;15:;16:;17:服务类;0:临时分类"}},
        switch (name) {
            case "临时分类":
                index = 0;
                break;
            case "机油":
                index = 1;
                break;
            case "机滤":
                index = 2;
                break;
            case "轮胎":
                index = 3;
                break;
            case "电瓶":
                index = 4;
                break;
            case "电子类产品":
                index = 5;
                break;
            case "美容类产品":
                index = 6;
                break;
            case "汽车用品":
                index = 7;
                break;
            case "养护产品":
                index = 8;
                break;
            case "耗材类产品":
                index = 9;
                break;
            case "灯具类产品":
                index = 10;
                break;
            case "雨刮类产品":
                index = 11;
                break;
            case "发动机配件类":
                index = 12;
                break;
            case "底盘配件类":
                index = 13;
                break;
            case "变速箱类":
                index = 14;
                break;
            case "电气类":
                index = 15;
                break;
            case "车身覆盖类":
                index = 16;
                break;
            case "服务类":
                index = 17;
        }
        return index;
    }


    /**
     * 获取创建或更新表单内容
     *
     * @param id 表单id
     * @return
     */
    @RequestMapping("/customstockitem/tosave")
    @Transactional(readOnly = true)
    public ModelAndView toSave(@RequestParam(required = false) Long id, @AuthenticationPrincipal ERPUser user) {
        ModelAndView mav = new ModelAndView("/customstockitem/form");
        Shop shop = (Shop) request.getSession().getAttribute("SHOP");
        if (id != null && id >= 0) {
            CustomStockItem customStockItem = customStockItemRepository.findOne(id);
            if (customStockItem.rootCategory == 17) {
                customStockItem.serviceName = customStockItem.name;
                customStockItem.name = "";
            }
            if (customStockItem.description != null) {
                customStockItem.description = customStockItem.description.replaceAll("<br>", "\r\n");
            }
            mav.addObject("customStockItem", customStockItem);
            mav.addObject("shopType", shop.shopType);
        } else {
            mav.addObject("customStockItem", new CustomStockItem());
            mav.addObject("shopType", shop.shopType);
        }
        List<Supplier> suppliers = supplierRepository.findByOrganizationAndDeleted(user.organization, false);
//        List<Supplier> suppliers = supplierRepository.findByOrganization(user.organization);
        mav.addObject("orgId",user.organization.id);
        mav.addObject("suppliers", suppliers);
        return mav;
    }

    /**
     * 获取分类商品信息
     *
     * @param root 顶级分类id
     * @return
     */
    @RequestMapping(value = "/customstockitem/list/secondarydata", method = RequestMethod.GET)
    @ResponseBody
    @Transactional(readOnly = true)
    public Map<String, Object> secondarydata(Integer root,long orgId) {
        Organization organization = organizationRepository.findOne(orgId);
        List<SecondaryCategory> secondaryCategory = secondaryCategoryRepository.findByRootCategoryAndDeletedAndOrganization(root, false,organization);
        return map(entry("secondaryCategoryList", secondaryCategory));
    }

    /**
     * 删除商品
     *
     * @param id 商品id
     * @return
     */
    @RequestMapping("/customstockitem/delete")
    @Transactional
    public ModelAndView delete(@RequestParam Long id) {
        ModelAndView mav = new ModelAndView("/message");

        CustomStockItem stockItem = customStockItemRepository.findOne(id);
        stockItem.deleted = true;
        customStockItemRepository.save(stockItem);

        mav.addObject("message", "删除成功");
        mav.addObject("responsePage", "/customstockitem/list");
        return mav;
    }

    /**
     * 保存商品信息
     *
     * @param customStockItem 商品内容
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/customstockitem/save", method = RequestMethod.POST)
    @Transactional
    public ModelAndView save(@AuthenticationPrincipal ERPUser user, @Valid @ModelAttribute CustomStockItem customStockItem, BindingResult bindingResult) {
        ModelAndView mav = new ModelAndView("/message");
        if (customStockItem.description != null) {
            customStockItem.description = customStockItem.description.replaceAll("\r\n", "<br>");
        }
//        String customStockItem.description;
        if (bindingResult.hasErrors()) {
            Shop shop = (Shop) request.getSession().getAttribute("SHOP");
            mav.setViewName("/customstockitem/form");
            mav.addObject("shopType", shop.shopType);
            mav.addObject("customStockItem", customStockItem);
            mav.addObject("orgId", user.organization.id);
            List<Supplier> suppliers = supplierRepository.findByOrganization(user.organization);
            mav.addObject("suppliers", suppliers);
            return mav;
        }
        if (customStockItem.rootCategory == 17) {
            customStockItem.name = customStockItem.serviceName;
        }
        if (customStockItem.id != 0) {
            CustomStockItem updatItem = customStockItemRepository.findOne(customStockItem.id);
            updatItem.organization = organizationRepository.findOne(user.organization.id);
            if (customStockItem.supplier != null) {
                Supplier supplier = supplierRepository.findOne(customStockItem.supplier.id);
                updatItem.supplier = supplier;
            }
            updatItem.name = customStockItem.name;
            updatItem.description = customStockItem.description;
            updatItem.barCode = customStockItem.barCode;
            updatItem.cost = customStockItem.cost;
            updatItem.isDistribution = customStockItem.isDistribution;
            updatItem.brandName = customStockItem.brandName;
            updatItem.rootCategory = customStockItem.rootCategory;
            updatItem.secondaryCategory = customStockItem.secondaryCategory;
            customStockItemRepository.save(updatItem);
        } else {
            customStockItem.organization = organizationRepository.findOne(user.organization.id);
            if (customStockItem.supplier != null) {
                Supplier supplier = supplierRepository.findOne(customStockItem.supplier.id);
                customStockItem.supplier = supplier;
            }
            customStockItemRepository.save(customStockItem);
        }
        mav.addObject("message", "保存成功");
        mav.addObject("responsePage", "/customstockitem/list");
        return mav;
    }

    /**
     * 根据条件获取商品管理列表
     *
     * @param itemName     商品名字
     * @param rootCategory 顶级分类
     * @return
     */
    @RequestMapping(value = "/customstockitem/list/data")
    @ResponseBody
    @Transactional(readOnly = true)
    public Map<String, Object> listData(@RequestParam int page, @RequestParam int rows,
                                        @RequestParam String sord, @RequestParam String sidx, long orgId,
                                        String itemName,  Integer rootCategory) {
        PageRequest pageRequest = new PageRequest(page - 1, rows,
                sord.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
                !StringUtils.isEmpty(sidx) ? sidx : "id");

        Organization organization = organizationRepository.findOne(orgId);
        Specification<CustomStockItem> specification = Specifications.where(CustomStockItemRepositorySpecifications.filterByRootCategory(rootCategory))
                .and(CustomStockItemRepositorySpecifications.filterByName(itemName))
                .and(CustomStockItemRepositorySpecifications.filterByOrganization(organization))
                .and(CustomStockItemRepositorySpecifications.filterDeleted(false));
        Page pageData = customStockItemRepository.findAll(specification, pageRequest);
//        Page<CustomStockItem> customStockItems = customStockItemRepository.findByNameLikeAndRootCategory(itemName, rootCategory, pageRequest);
        return JqGridDataGenerator.getDataJson(pageData);
    }

    /**
     * 根据条件获取商品管理列表
     *
     * @param itemName     商品名字
     * @param rootCategory 顶级分类
     * @return
     */
    @RequestMapping(value = "/customstockitem/organization/list/data")
    @ResponseBody
    @Transactional(readOnly = true)
    public Map<String, Object> listDataByOrganization(@RequestParam int page, @RequestParam int rows, @AuthenticationPrincipal ERPUser user,
                                                      @RequestParam String sord, @RequestParam String sidx, @RequestParam String itemName, @RequestParam Integer rootCategory) {
        Organization organization = new Organization();
        organization = organizationRepository.findOne(user.organization.id);
        PageRequest pageRequest = new PageRequest(page - 1, rows,
                sord.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
                !StringUtils.isEmpty(sidx) ? sidx : "id");
        if (rootCategory == 99) {
            rootCategory = null;
        }

        Specification<CustomStockItem> specification = Specifications.where(CustomStockItemRepositorySpecifications.filterByRootCategory(rootCategory))
                .and(CustomStockItemRepositorySpecifications.filterByOrganization(organization))
                .and(CustomStockItemRepositorySpecifications.filterByName(itemName))
                .and(CustomStockItemRepositorySpecifications.filterDeleted(false));
        Page pageData = customStockItemRepository.findAll(specification, pageRequest);

//        Page<CustomStockItem> customStockItems = customStockItemRepository.findByOrganizationAndNameLikeAndRootCategory(organization, "%" + itemName + "%", rootCategory, pageRequest);
        return JqGridDataGenerator.getDataJson(pageData);
    }

    static class CustomStockItemValidator implements Validator {
        @Autowired
        private CustomStockItemRepository customStockItemRepository;

        /**
         * This Validator validates *just* CustomStockItem instances
         */
        @Override
        public boolean supports(Class<?> clazz) {
            return CustomStockItem.class.equals(clazz);
        }

        /**
         * 添加Repository到验证器
         *
         * @param customStockItemRepository
         */
        public void setRepository(CustomStockItemRepository customStockItemRepository) {
            this.customStockItemRepository = customStockItemRepository;
        }

        @Override
        public void validate(Object obj, Errors errors) {
            CustomStockItem customStockItem = (CustomStockItem) obj;

            if (customStockItem.appSort > 0 && customStockItem.id == 0) {
                if (customStockItemRepository.findByAppSort(customStockItem.appSort).size() > 0) {
                    errors.rejectValue("appSort", "required", "该序号已被使用");
                }
            }

            if (customStockItem.rootCategory != 17) {
                if (!StringUtils.hasLength(customStockItem.name)) {
                    errors.rejectValue("name", "required", "不能为空");
                }
                if (!StringUtils.hasLength(customStockItem.brandName)) {
                    errors.rejectValue("brandName", "required", "不能为空");
                }
            }
        }
    }
}
