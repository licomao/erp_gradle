package com.daqula.carmore.controller;

import com.daqula.carmore.AuthorityConst;
import com.daqula.carmore.model.acl.ERPUser;
import com.daqula.carmore.model.shop.Organization;
import com.daqula.carmore.model.shop.Shop;
import com.daqula.carmore.model.shop.Staff;
import com.daqula.carmore.model.shop.StaffAttendance;
import com.daqula.carmore.repository.OrganizationRepository;
import com.daqula.carmore.repository.ShopRepository;
import com.daqula.carmore.repository.StaffAttendanceRepository;
import com.daqula.carmore.repository.StaffRepository;
import com.daqula.carmore.repository.specification.StaffAttendanceSpecifications;
import com.daqula.carmore.repository.specification.StaffSpecifications;
import com.daqula.carmore.util.*;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.daqula.carmore.util.FreeMarkerUtil.getAuthorityModel;

/**
 * Created by chenxin on 2015/10/12 0012.
 */
@Controller
public class StaffAttendanceController {

    @Autowired
    public ShopRepository shopRepository;

    @Autowired
    public OrganizationRepository organizationRepository;

    @Autowired
    public StaffAttendanceRepository staffAttendanceRepository;

    @Autowired
    public StaffRepository staffRepository;

    /**
     * TO员工考勤查询
     * @return
     */
    @RequestMapping(value = "/staffattendance/list" ,method = RequestMethod.GET)
    @Transactional(readOnly = true)
    public ModelAndView list (@AuthenticationPrincipal ERPUser user, HttpSession session) {
        ModelAndView mav = new ModelAndView("/staffattendance/list");

        if (!user.checkAuthority(AuthorityConst.MANAGE_ORG_STAFFATTENDANCE)) {
            mav.setViewName("/noauthority");
            return mav;
        }

        List<Shop> shops = SessionUtil.getShopList(session, organizationRepository, shopRepository);

        if (shops == null) {
            mav.setViewName("/logout");
            return mav;
        }
        String workDate = (new SimpleDateFormat("yyyy/MM/dd")).format(new Date());

        mav.addObject("AUTHORITY", getAuthorityModel());
        mav.addObject("user",user);
        mav.addObject("workDate",workDate);
        mav.addObject("shops",shops);
        return mav;
    }

    /**
     *获取员工考勤列表数据
     * @param page
     * @param rows
     * @param sord
     * @param sidx
     * @param staffAttendance 员工考勤列表查询条件
     * @return
     */
    @RequestMapping(value = "/staffattendance/list/data", method = RequestMethod.GET)
    @ResponseBody
    @Transactional(readOnly = true)
    public Map<String, Object> listData(@RequestParam int page, @RequestParam int rows,
                                        @RequestParam String sord, @RequestParam String sidx, @ModelAttribute StaffAttendance staffAttendance, HttpSession session, @AuthenticationPrincipal ERPUser user) {
        PageRequest pageRequest = new PageRequest(page - 1, rows,
                sord.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
                !StringUtils.isEmpty(sidx) ? sidx : "id");
        Organization organization = organizationRepository.findOne(user.organization.id);

        Specification<StaffAttendance> specification = Specifications.where(StaffAttendanceSpecifications.filterByStaffName(staffAttendance.staff.name))
                .and(StaffAttendanceSpecifications.filterByOrganization(organization))
                .and(StaffAttendanceSpecifications.filterByStaffShop(staffAttendance.staff.shop))
                .and(StaffAttendanceSpecifications.filterByGtWorkDate(staffAttendance.workDate))
                .and(StaffAttendanceSpecifications.filterByStaffDeleted(false))
                .and(StaffAttendanceSpecifications.filterByLtWorkDate(staffAttendance.workDateEnd));
        Page<StaffAttendance> pageData = staffAttendanceRepository.findAll(specification,pageRequest);
        return JqGridDataGenerator.getDataJson(pageData);
    }

    @RequestMapping(value = "/staffattendance/excel/export", method = RequestMethod.GET)
    public void download(HttpServletResponse res,@ModelAttribute StaffAttendance staffAttendance, @AuthenticationPrincipal ERPUser user) throws IOException {
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
        }
    }





    /**
     * 进入员工考勤页面
     * @return
     */
    @RequestMapping(value = "/staffattendance/form", method = RequestMethod.GET)
    public ModelAndView form(@AuthenticationPrincipal ERPUser user, HttpSession session){
        ModelAndView mav = new ModelAndView("/staffattendance/form");

        if (!user.checkAuthority(AuthorityConst.MANAGE_ORG_DOSTAFFATTENDANCE)) {
            mav.setViewName("/noauthority");
            return mav;
        }
        Shop shop = (Shop) session.getAttribute("SHOP");
        mav.addObject("loginShop",shop);
        mav.addObject("AUTHORITY", getAuthorityModel());
        mav.addObject("user",user);
        mav.addObject("staffattendance",new StaffAttendance());
        return mav;
    }

    /**
     * 进入员工考勤页面
     * @return
     */
    @RequestMapping(value = "/staffattendance/formTest", method = RequestMethod.GET)
    public ModelAndView formTest(@AuthenticationPrincipal ERPUser user, HttpSession session){
        ModelAndView mav = new ModelAndView("/staffattendance/formTest");

        if (!user.checkAuthority(AuthorityConst.MANAGE_ORG_FINGERATTENDANCE)) {
            mav.setViewName("/noauthority");
            return mav;
        }
        Shop shop = (Shop) session.getAttribute("SHOP");
        List<Staff> staffList = staffRepository.findByShopAndDeletedAndFingerPrintNotNull(shop, false);

        mav.addObject("staffList", staffList);
        mav.addObject("AUTHORITY", getAuthorityModel());
        mav.addObject("user",user);
        mav.addObject("staffattendance",new StaffAttendance());
        return mav;
    }

    /**
     *获取员工考勤单条数据
     * @param phone 用于存储员工的手机
     * @return
     */
    @RequestMapping(value = "/staffattendance/findStaff", method = RequestMethod.GET)
    @ResponseBody
    @Transactional(readOnly = true)
    public Map<String, Object> listData(@RequestParam("phone") String phone, @RequestParam("shopId")String shopId, @AuthenticationPrincipal ERPUser user) {
        Map<String, Object> map = new HashMap<String, Object>(3);
        StaffAttendance staffAttendance = null;
        long sId = 0;
        if (!StringUtil.IsNullOrEmpty(shopId)){
            sId = Long.parseLong(shopId);
        }
        Shop shop = shopRepository.findOne(sId);

        Specification<Staff> specificationS = Specifications.where(StaffSpecifications.filterByPhone(phone))
                .and(StaffSpecifications.filterByShop(shop))
                .and(StaffSpecifications.filterByStatusNot("3"));
        Staff staffFind = (Staff) staffRepository.findOne(specificationS);
//        Staff staff = staffRepository.findByIdAndStatusNot(staffFind.id, "3");
        if (staffFind != null) {
            Specification<StaffAttendance> specificationSa = Specifications.where(StaffAttendanceSpecifications.filterByStaff(staffFind)).and(StaffAttendanceSpecifications.filterByWorkdate(null));
            staffAttendance = (StaffAttendance) staffAttendanceRepository.findOne(specificationSa);
            if (staffAttendance == null) {
                staffAttendance = new StaffAttendance();
                staffAttendance.staff = staffFind;
            } else {/*
                staffAttendance.arriveDate = staffAttendance.arriveDate.plusHours(8);
                if (staffAttendance.leaveDate != null) {
                    staffAttendance.leaveDate = staffAttendance.leaveDate.plusHours(8);
                }*/
            }
        }

        map.put("staffattendance", staffAttendance);
        return map;
    }

    /**
     * 保存上下班时间
     * @param staffAttendance
     * @return
     */
    @RequestMapping(value = "/staffattendance/save", method = RequestMethod.POST)
    @Transactional
    public ModelAndView save (@ModelAttribute StaffAttendance staffAttendance) {
        ModelAndView mav = new ModelAndView("/message");
        String message="保存成功";

        Specification<StaffAttendance> specificationSa = Specifications.where(StaffAttendanceSpecifications.filterByStaff(staffAttendance.staff)).and(StaffAttendanceSpecifications.filterByWorkdate(null));
        StaffAttendance staffAttendanceUpdate = (StaffAttendance) staffAttendanceRepository.findOne(specificationSa);
        if (staffAttendanceUpdate == null){
            Staff staffFind = staffRepository.findOne(staffAttendance.staff.id);
            staffAttendance.staff = staffFind;
            staffAttendance.arriveDate = new DateTime();
            staffAttendance.workDate = new Date((new SimpleDateFormat("yyyy/MM/dd")).format(new Date()));
            staffAttendanceRepository.save(staffAttendance);
        } else {
            staffAttendanceUpdate.leaveDate = new DateTime();
            staffAttendanceRepository.save(staffAttendanceUpdate);
        }

        mav.addObject("message",message);
        mav.addObject("responsePage","/staffattendance/form");
        return mav;
    }

    /**
     * 指纹上下班打卡
     * @param id
     * @return
     */
    @RequestMapping(value = "/staffattendance/findbyid/{id}")
    @ResponseBody
    @Transactional(readOnly = false)
    public Map<String, Object> findById(@PathVariable String id, Integer stype) throws ParseException {
        Map<String, Object> result = new HashMap<>();
//        Staff staff = staffRepository.findByIdAndStatusNot(Long.valueOf(id), "3");
        Staff staff = staffRepository.findOne(Long.valueOf(id));
        if(staff != null){
            //指纹验证成功    存在这个人
//            StaffAttendance staffAttendance = new StaffAttendance();
            if(staff.status.equals("3")){
                result.put("result", false);
                result.put("message", "该员工已离职!");
                return result;
            }

            if (staff.status.equals("3")){
                result.put("result", false);
                result.put("message", "您已离职, 无法打卡!");
            }{
                String message = "";
                Boolean foo = true;
                SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
                Date workDate = format.parse(format.format(new Date()));

                StaffAttendance staffAttendance = staffAttendanceRepository.findByWorkDateAndStaff(workDate, staff);

                if(staffAttendance == null) {
                    staffAttendance = new StaffAttendance();
                    staffAttendance.staff = staff;
                    //今天还没打卡的情况
                    if(stype == 1){
                        //打卡类型 打上班卡
                        staffAttendance.arriveDate = new DateTime();
                        staffAttendance.workDate = workDate;
                        staffAttendanceRepository.save(staffAttendance);
                        result.put("result", true);
//                    result.put("message", "上班打卡成功!");
                    } else if(stype == 2){
                        //打卡类型 打下班卡
                        foo = false;
                        message = "今日还没有打卡上班!请先打卡上班!";
//                    result.put("result", false);
//                    result.put("message", "今日还没有打卡上班!请先打卡上班!");
//                    result.put("staffAttendance", staffAttendance);
                    }
                } else {
                    //今天已有打卡记录
                    if(stype == 2){
                        //打卡类型 打下班卡
                        if(staffAttendance.leaveDate != null){
                            foo = false;
                            message = "今天已有下班打卡记录,请不要重复打卡!";
//                        result.put("result", false);
//                        result.put("message", "今天已有下班打卡记录,请不要重复打卡!")
                        }else{
                            staffAttendance.leaveDate = new DateTime();
//                        foo = true;
//                        result.put("result", true);
                        }
//                    result.put("message", "下班打卡成功!");
//                    result.put("staffAttendance", staffAttendance);
                    } else if(stype == 1){
                        //打卡类型 打上班卡
                        foo = false;
                        message = "今天已有上班打卡记录,请不要重复打卡!";
//                    result.put("result", false);
//                    result.put("message", "今天已有上班打卡记录,请不要重复打卡!");
                    }
                }
                result.put("result", foo);
                result.put("message", message);
                result.put("staffAttendance", staffAttendance);
            }


        }

        return result;
    }

    /**
     * 测试代码
     * @param stype
     * @return
     * @throws ParseException
     *//*
    @RequestMapping(value = "/staffattendance/findbyid2")
    @ResponseBody
    @Transactional(readOnly = false)
    public Map<String, Object> findById2(Integer stype) throws ParseException {
        Map<String, Object> result = new HashMap<>();
        String fingerStr = "mspZVoOkm01qwQ+dzWPBE6TRYgEKodFugQ+pU2sBCZ5TcwENrtBKgQeo0FLBCqzJFwEg0socASHazhgBKb1EdcEMPkVwwQoxwWRBCp7BaMEHpDk9gQwPQWeBCC2iRUEPDi94AQWbpVNBCRU9cYEPLL1uQQylumQBC5kpdcEGJc0YgSjVrkyBCoc7OIEQf7ghgQvmLCkBDnCeR4EQgaNhwQmNHjEBCHO6UAEOJNJsAQspNHHBCie7fIERmrpNwRGNVFgBDS3UTgENL1E3wQ8sUy5BMpICEGltcXUDCA8UFxkbHyAjIgIQY2pvdQQJDhQZHB8hIiIjAhBscHJ0AQYNEhQXGh8gIiMBEF1iaW51BAkPFRofIiQkIyQCD21xc3V3BAsPExYaHSAhABBYXWNpcHYFChEWGyAkJycmJQMPcnV2AQQJDREVGR0gIQAQWV1iZnB3BQ0VGiAjJikpKSkEDnYBAwYKDhMWGRwfABBYW2BjbncGEBkfJCYpLC0tLwUNAgQGCg4SFRgbABBVVlthbHYJFR8lKCotLzEwMwYLBAYKDhMWABBVU1lgawERHCQpKywtLzMzNAAA/wAQRERNXm4DGSQqLS0tLS8zNDYAAP8AEEhDSVtyCiIqKy0tLCsuMTY5AAD/ABBBQ0ZUAxclLS4wLi4tMjVARQAA/wEPRP///yIqLS0wLzAuNDhEGPAEGbSpUBQhBijFM+LxiFiJS7BpctTfrXsj4r5lZ3P7x1JXk1fRc8tJCbvsRQWrs3zF7AD0lSV92216DPUbqFtE+/Lpfp5N2HxMXAllnyXj2Ga2/EY0gO4OiKj3uKQIkYdavjlj+tz1DYMLOo6gNrpJMncx5hwV1xxgmnf7K0V3S3SBVHsw3Qcynp8JgGWbCuyHHmYz3pioPyRphNa4oOzt1Ro6bKC4MguBtIn02O3tyiu7P+0s4/M3c5gTmn/ZP6KBeVX1bh+Ms45B5L19YkGE5tcCxog3HQdQRhKx573LMyKcO2FCMKvA0j8kpWuGY0+p3j5tyM275YIfIAqZ8abzcjWHYCjTGzxkCGKNW0mZPsTUidYK+SocrYY9uIt4dxgYGBgYGBgYGBgYGBgYGBgYGBgYGBgYGBgYGBgYGBgYGBgYGBgYGBg=";

        Staff staff = staffRepository.findByFingerPrint(fingerStr);
//        Staff staff = staffRepository.findOne(Long.valueOf(id));
        if(staff != null){
            //指纹验证成功    存在这个人
//            StaffAttendance staffAttendance = new StaffAttendance();

            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
            Date workDate = format.parse(format.format(new Date()));

            StaffAttendance staffAttendance = staffAttendanceRepository.findByWorkDateAndStaff(workDate, staff);

            String message = "";
            Boolean foo = true;
            if(staffAttendance == null) {
                staffAttendance = new StaffAttendance();
                staffAttendance.staff = staff;
                //今天还没打卡的情况
                if(stype == 1){
                    //打卡类型 打上班卡
                    staffAttendance.arriveDate = new DateTime();
                    staffAttendance.workDate = workDate;
                    staffAttendanceRepository.save(staffAttendance);
                    result.put("result", true);
//                    result.put("message", "上班打卡成功!");
                } else if(stype == 2){
                    //打卡类型 打下班卡
                    foo = false;
                    message = "今日还没有打卡上班!请先打卡上班!";
//                    result.put("result", false);
//                    result.put("message", "今日还没有打卡上班!请先打卡上班!");
//                    result.put("staffAttendance", staffAttendance);
                }
            } else {
                //今天已有打卡记录
                if(stype == 2){
                    //打卡类型 打下班卡
                    if(staffAttendance.leaveDate != null){
                        foo = false;
                        message = "今天已有下班打卡记录,请不要重复打卡!";
//                        result.put("result", false);
//                        result.put("message", "今天已有下班打卡记录,请不要重复打卡!")
                    }else{
                        staffAttendance.leaveDate = new DateTime();
//                        foo = true;
//                        result.put("result", true);
                    }
//                    result.put("message", "下班打卡成功!");
//                    result.put("staffAttendance", staffAttendance);
                } else if(stype == 1){
                    //打卡类型 打上班卡
                    foo = false;
                    message = "今天已有上班打卡记录,请不要重复打卡!";
//                    result.put("result", false);
//                    result.put("message", "今天已有上班打卡记录,请不要重复打卡!");
                }
            }
            result.put("result", foo);
            result.put("message", message);
            result.put("staffAttendance", staffAttendance);
        }

        return result;
    }*/

}
