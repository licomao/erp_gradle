package com.daqula.carmore.controller;

import com.daqula.carmore.AuthorityConst;
import com.daqula.carmore.model.acl.ERPUser;
import com.daqula.carmore.model.customer.Customer;
import com.daqula.carmore.model.customer.CustomerERPProfile;
import com.daqula.carmore.model.customer.VehicleInfo;
import com.daqula.carmore.model.order.SettleOrder;
import com.daqula.carmore.model.shop.Organization;
import com.daqula.carmore.model.shop.StaffAttendance;
import com.daqula.carmore.model.template.VehicleModel;
import com.daqula.carmore.repository.*;
import com.daqula.carmore.repository.specification.SettleOrderSpecifications;
import com.daqula.carmore.repository.specification.StaffAttendanceSpecifications;
import com.daqula.carmore.util.*;
import org.jadira.usertype.spi.utils.lang.StringUtils;
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
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.*;

import static com.daqula.carmore.util.CollectionUtil.entry;
import static com.daqula.carmore.util.CollectionUtil.map;

@Controller
public class CustomerController {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private VehicleInfoRepository vehicleInfoRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerProfileRepository customerProfileRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private VehicleModelRepository vehicleModelRepository;

    @Autowired
    private SettleOrderRepository settleOrderRepository;

    /**
     * Register binding validators
     *
     * @param binder
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        CustomerERPProfileValidator customerERPProfileValidator = new CustomerERPProfileValidator();
        binder.addValidators(customerERPProfileValidator);
    }

    /**
     * 获取客户列表
     * @return
     */
    @RequestMapping("/customer/list")
    @Transactional(readOnly = true)
    public ModelAndView getCustomerList( @AuthenticationPrincipal ERPUser user) {
        ModelAndView mav = new ModelAndView("/customer/list");

        if (!user.checkAuthority(AuthorityConst.MANAGE_ORG_CUSTOMER)) {
            mav.setViewName("/noauthority");
            return mav;
        }
        return  mav;
    }

    /**
     * 创建或者更新账户信息
     * @return
     */
    @RequestMapping("/customer/tosave")
    @Transactional(readOnly = true)
    public ModelAndView create() {
        ModelAndView mav = new ModelAndView("/customer/form");
        CustomerERPProfile customerErpProfile = new CustomerERPProfile();
        Customer customer = new Customer();
        customerErpProfile.customer = customer;
        customerErpProfile.vehicles = new ArrayList<>();
        customerErpProfile.vehicles.add(new VehicleInfo());
        customerErpProfile.organization = (Organization)request.getSession().getAttribute("ORGANIZATIONS");
        mav.addObject("customerErpProfile",customerErpProfile);

        List<String> vehicleModels = vehicleModelRepository.getBrandNames();
        mav.addObject("vehicleModels",vehicleModels);
        return mav;
    }

    /**
     * 获取车系信息
     * @param brand 品牌
     * @return
     */
    @RequestMapping(value = "/customer/getlines", method = RequestMethod.GET)
    @ResponseBody
    @Transactional(readOnly = true)
    public List<String> getLines(String brand) {
        try {
            brand = URLDecoder.decode(brand, "UTF-8");
//            URLDecoder.decode(inputStr, "UTF-8")：
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<String> brandLines = vehicleModelRepository.getBrandLines(brand);
        return brandLines;
    }

    /**
     * 获取车型信息
     * @param brand 品牌
     * @param line 车系
     * @return
     */
    @RequestMapping(value = "/customer/getversions", method = RequestMethod.GET)
    @ResponseBody
    @Transactional(readOnly = true)
    public List<VehicleModel> getVersions(String brand, String line) {
        try {
            brand = URLDecoder.decode(brand, "UTF-8");
            line = URLDecoder.decode(line, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vehicleModelRepository.getBrandVersions(brand, line);
    }

    /**
     * 更新顾客信息
     * @param tel 顾客手机
     * @return
     */
    @RequestMapping("/customer/edit")
    @Transactional(readOnly = true)
    public ModelAndView edit(String tel, @AuthenticationPrincipal ERPUser user) {
        ModelAndView mav = new ModelAndView("/customer/form");

        CustomerERPProfile customerErpProfile = customerProfileRepository.findUsableERPProfileByMobileAndOrgId(tel, user.organization.id);
        if (customerErpProfile.vehicles == null) {
            customerErpProfile.vehicles = new ArrayList<>();
        }
        if (customerErpProfile.vehicles.size() == 0) {
            customerErpProfile.vehicles.add(new VehicleInfo());
        }
        mav.addObject("customerErpProfile",customerErpProfile);

        List<String> vehicleModels = vehicleModelRepository.getBrandNames();
        mav.addObject("vehicleModels",vehicleModels);

        mav.addObject("isUpdate","true");
        return mav;
    }

    /**
     * 删除账户
     * @param tel 手机
     * @return
     */
    @RequestMapping(value = "/customer/savedelete", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public Map<String, Object>  deleteRow(@RequestParam String tel, @AuthenticationPrincipal ERPUser user) {
        CustomerERPProfile cus = customerProfileRepository.findUsableERPProfileByMobileAndOrgId(tel, user.organization.id);
        cus.deleted = true;
        customerProfileRepository.save(cus);
        return  map(entry("msg", true));
    }


    /**
     * 检查车牌号是否已经存在
     * @param profileId
     * @param plateNumber
     * @param user
     * @return
     */
    @RequestMapping(value = "/customer/checkplatenumber", method = RequestMethod.GET)
    @ResponseBody
    @Transactional
    public Boolean checkPlateNumber(@RequestParam String profileId,String plateNumber, @AuthenticationPrincipal ERPUser user) {
        boolean checkBol = false;
        if (StringUtils.isNotEmpty(plateNumber)) {
            Organization organization = organizationRepository.findOne(user.organization.id);
            List<VehicleInfo> vehicleInfos = vehicleInfoRepository.findByPlateNumberInCustomerERPProfile(plateNumber, organization);
            if (vehicleInfos.size() > 0) {
                if (StringUtils.isEmpty(profileId)) {
                        checkBol = true;

                } else {
                    for (VehicleInfo vehicleInfo : vehicleInfos){
                        CustomerERPProfile customerERPProfile = customerProfileRepository.findERPProfileByPlateNumberAndOrganizationId(vehicleInfo,organization.id);
                        if (Long.parseLong(profileId) != customerERPProfile.id) {
                            checkBol = true;
                        }
                    }

                }
            }
        }
        return  checkBol;
    }


    /**
     * 保存账户信息
     * @param gender 性别
     * @param customerErpProfile 账户数据
     * @param bindingResult
     * @param profileId profileId
     * @return
     */
    @RequestMapping(value = "/customer/save", method = RequestMethod.POST)
    @Transactional
    public ModelAndView saveCustomer(String gender, @Valid @ModelAttribute CustomerERPProfile customerErpProfile, String datas, String profileId, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ModelAndView("/customer/form");
        }
        ModelAndView mav = new ModelAndView("/message");

        //save vehicleInfo
        VehicleInfo vehicleInfo = null;
        String[] data = datas.split(",");
        List<VehicleInfo> vehicleList = new ArrayList<>();
        int CAR_INFO_COUNT = 12;
        for(int index = 0 ; index < data.length;index++) {
            if (index % CAR_INFO_COUNT == 0) {
                vehicleInfo = vehicleInfoRepository.findOne(Long.parseLong(data[index]));
                if (vehicleInfo == null) {
                    vehicleInfo = new VehicleInfo();
                }
            }else if(index % CAR_INFO_COUNT == 1) {
                vehicleInfo.plateNumber = data[index].trim();
            }else if(index % CAR_INFO_COUNT == 2) {
                vehicleInfo.vinCode = data[index];
            }else if(index % CAR_INFO_COUNT == 3) {
                vehicleInfo.engineDisplacement = data[index];
            }else if(index % CAR_INFO_COUNT == 4) {
                vehicleInfo.obdSN = data[index];
            }else if(index % CAR_INFO_COUNT == 5) {//品牌
            }else if(index % CAR_INFO_COUNT == 6) {//车系
            }else if(index % CAR_INFO_COUNT == 7) {//车型
                String version = data[index];
                VehicleModel vehicleModel = vehicleModelRepository.findOne(Long.parseLong(version));
                vehicleInfo.model = vehicleModel;
            }else if(index % CAR_INFO_COUNT == 8) {
                vehicleInfo.mileage = Integer.parseInt(data[index]);
            }else if(index % CAR_INFO_COUNT == 9) {
                if(!StringUtils.isEmpty(data[index])) {
                    String date = data[index].replace('/','-');
                    vehicleInfo.mileageUpdatedDate = DateTime.parse(date);
                }
            }else if(index % CAR_INFO_COUNT == 10) {
                vehicleInfo.lastMaintenanceMileage = Integer.parseInt(data[index]);
                if(index == data.length - 1) {//如果没有最后的更新时间
                    vehicleInfo.verified = true;
                    vehicleList.add(vehicleInfo);
                }
            }else if(index % CAR_INFO_COUNT == 11) {
                if(!StringUtils.isEmpty(data[index])) {
                    String date = data[index].replace('/','-');
                    vehicleInfo.lastMaintenanceDate = DateTime.parse(date);
                }

                vehicleInfo.verified = true;
                vehicleList.add(vehicleInfo);
            }
        }

        Organization org = (Organization)request.getSession().getAttribute("ORGANIZATIONS");
        if (org == null) {
            mav.setViewName("/logut");
            return mav;
        }
        org = organizationRepository.findOne(org.id);
        customerErpProfile.id = Long.parseLong(profileId);
        if (customerErpProfile.id == 0) { //新建
            List<Customer> customers = customerRepository.findByMobile(customerErpProfile.customer.mobile);
            if (customers.size() == 0) {

                Customer customer = new Customer();
                customer.token = UUID.randomUUID().toString();
                customer.mobile = customerErpProfile.customer.mobile;
                customerErpProfile.customer = customerRepository.save(customer);
            } else {
                customerErpProfile.customer = customers.get(0);
            }

            customerErpProfile.gender = Integer.parseInt(gender);
            customerErpProfile.vehicles = vehicleList;
            customerErpProfile.organization = org;
            customerProfileRepository.save(customerErpProfile);
        } else { //更新
            CustomerERPProfile customerERPProfileUpdate = (CustomerERPProfile) customerProfileRepository.findOne(customerErpProfile.id);

            if (customerErpProfile.customer.mobile.equals(customerERPProfileUpdate.customer.mobile)) {
                customerERPProfileUpdate.realName = customerErpProfile.realName;
                customerERPProfileUpdate.gender = Integer.parseInt(gender);

                customerERPProfileUpdate.vehicles.clear();
                customerERPProfileUpdate.vehicles.addAll(vehicleList);
                customerERPProfileUpdate.organization = org;
            } else {
                List<Customer> customers = customerRepository.findByMobile(customerERPProfileUpdate.customer.mobile);
                if (customers.size() == 0) {
                    Customer customer = new Customer();
                    customer.token = UUID.randomUUID().toString();
                    customer.mobile = customerErpProfile.customer.mobile;
                    customerERPProfileUpdate.customer = customerRepository.save(customer);
                } else {
                    customers.get(0).mobile = customerErpProfile.customer.mobile;
                    customerERPProfileUpdate.customer = customers.get(0);
                }

                customerERPProfileUpdate.realName = customerErpProfile.realName;
                customerERPProfileUpdate.gender = Integer.parseInt(gender);

                customerERPProfileUpdate.vehicles.clear();
                customerERPProfileUpdate.vehicles.addAll(vehicleList);
                customerERPProfileUpdate.organization = org;
                customerProfileRepository.save(customerERPProfileUpdate);
            }
        }

        mav.addObject("message","保存成功");
        mav.addObject("responsePage","/customer/list");
        return mav;
    }

    @RequestMapping(value = "/vehicleinfo/excel/export", method = RequestMethod.GET)
    public void download(HttpServletResponse res, @AuthenticationPrincipal ERPUser user) throws IOException {
        Organization organization = organizationRepository.findOne(user.organization.id);
        List<Object[]> vehicleByOrganization = vehicleInfoRepository.findVehicleByOrganization(user.organization);

        List<ExcelExportVehicleInfo> list = new ArrayList<>();
        for (Object[] data : vehicleByOrganization) {
            ExcelExportVehicleInfo excelExportVehicleInfo = new ExcelExportVehicleInfo();
            int length = data.length;
            if(length > 0){
                VehicleInfo vehicleInfo = (VehicleInfo) data[0];
                if(vehicleInfo != null){
                    VehicleModel model = vehicleInfo.model;
                    if(model!=null){
                        excelExportVehicleInfo.brand = model.brand;
                        excelExportVehicleInfo.version = model.version;
                    }
                    excelExportVehicleInfo.plateNumber = vehicleInfo.plateNumber;

                }
            }
            if (length > 2){
                Integer gender = (Integer) data[2];
                excelExportVehicleInfo.gender = gender == 0? "男": "女";
            }
            if(length > 3)
                excelExportVehicleInfo.mobile = (String) data[3];
            if (length > 1)
                excelExportVehicleInfo.name = (String) data[1];
            list.add(excelExportVehicleInfo);
        }
        OutputStream os = res.getOutputStream();
        try {
            res.reset();
            res.setHeader("Content-Disposition", "attachment; filename=vehicle_"+ organization.serialNum +".xls");
            res.setContentType("application/octet-stream; charset=utf-8");
            ExcelUtil<ExcelExportVehicleInfo> util = new ExcelUtil<ExcelExportVehicleInfo>(ExcelExportVehicleInfo.class);// 创建工具类.
            util.exportExcel(list, "顾客信息", 65536, os);// 导出
        } finally {
            if (os != null) {
                os.close();
            }
        }
//        Object object = objects[1];
//        System.out.println(objects);



//        System.out.println();
        /*vehicleMap = JqGridDataGenerator.getDataJson(vehicleInfoObjs);



        Organization organization = organizationRepository.findOne(user.organization.id);
        Specification<StaffAttendance> specification = Specifications.where(StaffAttendanceSpecifications.filterByStaffName(staffAttendance.staff.name))
                .and(StaffAttendanceSpecifications.filterByOrganization(organization))
                .and(StaffAttendanceSpecifications.filterByStaffShop(staffAttendance.staff.shop))
                .and(StaffAttendanceSpecifications.filterByGtWorkDate(staffAttendance.workDate))
                .and(StaffAttendanceSpecifications.filterByLtWorkDate(staffAttendance.workDateEnd));
        List<StaffAttendance> staffAttendances = staffAttendanceRepository.findAll(specification);
        List<ExcelExportStaffAttendance> list = new ArrayList<>();
//        SimpleDateFormat format = new SimpleDateFormat("YYYY/MM/DD");
//        SimpleDateFormat formatTime = new SimpleDateFormat("YYYY/MM/DD");
        for (StaffAttendance sa : staffAttendances) {
            ExcelExportStaffAttendance excelExportStaffAttendance = new ExcelExportStaffAttendance();
            excelExportStaffAttendance.staffName = sa.staff.name;
            excelExportStaffAttendance.mobile = sa.staff.phone;
            excelExportStaffAttendance.shopName = sa.staff.shop.name;
            excelExportStaffAttendance.jobName = sa.staff.job.name;
            excelExportStaffAttendance.workDate = new DateTime(sa.workDate).toString("yyyy-MM-dd");
            excelExportStaffAttendance.startTime = sa.arriveDate.toString("yyyy-MM-dd : HH:mm:ss");
//            excelExportStaffAttendance.startTime = sa.arriveDate.toString("yyyy-MM-dd : HH:MM:SS");
            if (sa.leaveDate != null)excelExportStaffAttendance.endTime = sa.leaveDate.toString("yyyy-MM-dd : HH:mm:ss");
            list.add(excelExportStaffAttendance);
        }
        OutputStream os = res.getOutputStream();
        try {
            res.reset();
            res.setHeader("Content-Disposition", "attachment; filename=kaoqing_"+ organization.serialNum +".xls");
            res.setContentType("application/octet-stream; charset=utf-8");
            ExcelUtil<ExcelExportStaffAttendance> util = new ExcelUtil<ExcelExportStaffAttendance>(ExcelExportStaffAttendance.class);// 创建工具类.
            util.exportExcel(list, "考勤记录", 65536, os);// 导出
        } finally {
            if (os != null) {
                os.close();
            }
        }*/
    }


    /**
     * 显示顾客车辆信息
     * @param tel 手机
     * @param carNum 车牌号
     * @return
     */
    @RequestMapping(value = "/customer/list/data")
    @ResponseBody
    @Transactional(readOnly = true)
    public Map<String, Object> getAccountManagerList(@RequestParam int page, @RequestParam int rows,
                                                     @RequestParam String sord, @RequestParam String sidx, @RequestParam String tel, @RequestParam String carNum, @AuthenticationPrincipal ERPUser user) {

        PageRequest pageRequest = new PageRequest(page - 1, rows,
                sord.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
                !StringUtils.isEmpty(sidx) ? sidx : "id");
        Map<String, Object> vehicleMap;
        Page<Object[]> vehicleInfoObjs;

        if(StringUtils.isEmpty(carNum)) {
            if(StringUtils.isEmpty(tel)) {
                vehicleInfoObjs = vehicleInfoRepository.findVehicleByOrganization(user.organization, pageRequest);
            }else {
                vehicleInfoObjs = vehicleInfoRepository.findVehicleByTelAndOrganization(tel, user.organization, pageRequest);
            }
        }else {
            if(StringUtils.isEmpty(tel)) {
                vehicleInfoObjs = vehicleInfoRepository.findVehicleByPlateNumberAndOrganization(carNum, user.organization, pageRequest);
            }else {
                vehicleInfoObjs = vehicleInfoRepository.findVehicleByPlateNumberAndTelAndOrganization(carNum, tel, user.organization, pageRequest);
            }
        }

        vehicleMap = JqGridDataGenerator.getDataJson(vehicleInfoObjs);
        return vehicleMap;
    }

    @RequestMapping(value="/customer/confirmmobile")
    @ResponseBody
    @Transactional(readOnly = true)
    public String confirmMobile(String mobile, String profileId, @AuthenticationPrincipal ERPUser user) {
        String isDuplicated = "true";

        CustomerERPProfile customerERPProfile = customerProfileRepository.findUsableERPProfileByMobileAndOrgId(mobile, user.organization.id);

        if(customerERPProfile == null) {
            isDuplicated = "false";
        } else if (customerERPProfile.id == Long.parseLong(profileId)) {
            isDuplicated = "false";
        }

        return isDuplicated;
    }

    @RequestMapping(value="/customer/confirmissale")
    @ResponseBody
    @Transactional(readOnly = true)
    public Boolean confirmissale(String id) {
        Specification<SettleOrder> specification = Specifications.where(SettleOrderSpecifications.filteredByVehicleInfoId(Long.parseLong(id)));
        PageRequest pageRequest = new PageRequest(0, 1, Sort.Direction.ASC,"id");
        Page pageData = settleOrderRepository.findAll(specification,pageRequest);

        if (pageData.getTotalPages() == 0) {
            return true;
        }

        return false;
    }

    @RequestMapping(value = "/customer/find/{mobile}")
    @ResponseBody
    public Map<String, Object> findErpProfile(@PathVariable String mobile, @AuthenticationPrincipal ERPUser user){
        Map<String, Object> result = new HashMap<>();
        List<Customer> customers = customerRepository.findByMobile(mobile);
        Organization organization = organizationRepository.findOne(user.organization.id);
        CustomerERPProfile customerERPProfile = customerProfileRepository.findERPProfileByCustomer(customers.get(0), organization);
        if(customerERPProfile != null && customerERPProfile.vehicles.size() > 0) {
            result.put("customerERPProfile",customerERPProfile);

        }

        return result;
    }


    static class CustomerERPProfileValidator implements Validator {
        /**
         * This Validator validates *just* StockItem instances
         */
        @Override
        public boolean supports(Class<?> clazz) {
            return CustomerERPProfile.class.equals(clazz);
        }

        @Override
        public void validate(Object obj, Errors errors) {
            CustomerERPProfile customerERPProfile = (CustomerERPProfile)obj;

            if (StringUtils.isEmpty(customerERPProfile.realName)) {
                errors.rejectValue("username", "required", "不能为空");
            }

            if (StringUtils.isEmpty(customerERPProfile.customer.mobile)) {
                errors.rejectValue("password", "required", "不能为空");
            }
        }
    }
}
