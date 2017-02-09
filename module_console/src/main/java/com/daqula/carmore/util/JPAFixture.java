package com.daqula.carmore.util;

import com.daqula.carmore.controller.PaymentController;
import com.daqula.carmore.model.acl.ERPRole;
import com.daqula.carmore.model.acl.ERPUser;
import com.daqula.carmore.model.admin.*;
import com.daqula.carmore.model.customer.*;
import com.daqula.carmore.model.order.Comment;
import com.daqula.carmore.model.order.OrderDetail;
import com.daqula.carmore.model.order.SettleOrder;
import com.daqula.carmore.model.shop.*;
import com.daqula.carmore.model.template.City;
import com.daqula.carmore.model.template.VehicleModel;
import com.daqula.carmore.repository.*;
import org.joda.time.DateTime;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;

public class JPAFixture {

    ApplicationContext applicationContext;
    ShopRepository shopRepository;
    OrganizationRepository organizationRepository;
    ERPUserRepository erpUserRepository;
    ERPRoleRepository erpRoleRepository;
    AgencyRepository agencyRepository;

    public JPAFixture(ApplicationContext context) {
        applicationContext = context;
        shopRepository = applicationContext.getBean(ShopRepository.class);
        organizationRepository = applicationContext.getBean(OrganizationRepository.class);
        erpUserRepository = applicationContext.getBean(ERPUserRepository.class);
        erpRoleRepository = applicationContext.getBean(ERPRoleRepository.class);
        agencyRepository = applicationContext.getBean(AgencyRepository.class);
    }

    /**
     * 添加测试用户
     */


    public void initOrg() {
        Organization organization = new Organization();
        organization.bankAccount = "6225882128964410000";
        organization.bankName = "招商银行";
        organization.name = "上海达丘拉网络科技有限公司";
        organization.businessLicenseImageUrl = "http://www.163.com";
        organization.contact = "dbcooper";
        organization.contactAddress = "上海市闵行区龙里路518号211室";
        organization.contactPhone = "18664842521";
        organization.serialNum = "12312312";
        organization.shopQuota = 12;
        organization.taxNumber = "121212";
        organization = organizationRepository.save(organization);

        ERPRole erpRole = new ERPRole();
        erpRole.role = "系统管理员";
        erpRole.authorityMask = 137438953408L;
        erpRole.ver = 1;
        erpRole.organization = organization;
        erpRole = erpRoleRepository.save(erpRole);

        ERPRole erpRole1 = new ERPRole();
        erpRole1.role = "代理商";
        erpRole1.ver = 1;
        erpRole1.authorityMask = 137438953408L;
        erpRole1.organization = organization;
        erpRole1 = erpRoleRepository.save(erpRole1);


        Shop shop = new Shop();
        shop.name = "ERP平台";
        shop.address = "上海市闵行区龙里路518号211室";
        shop.shopCode = "ERPPT";
        shop.organization = organization;
        shop.openingHours = "8:00";
        shop.phone = "400-960-1310";
        shop =  shopRepository.save(shop);

        ERPUser user = new ERPUser();
        user.username = "administrator";
        user.password = "zhaitech";
        user.realName = "系统管理员";
        user.enable = true;
        user.organization = organization;
        user.role = erpRole;
        user.shops = new ArrayList<>();
        user.shops.add(shop);
        erpUserRepository.save(user);

        Agency agency = new Agency();
        agency.erpUser = user;
        agency.organizations = new ArrayList<>();
        agency.organizations.add(organization);
        agencyRepository.save(agency);
    }
}
