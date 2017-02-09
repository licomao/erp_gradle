package com.daqula.carmore.model.shop;

import com.daqula.carmore.model.BaseEntity;
import com.daqula.carmore.model.acl.ERPUser;
import com.daqula.carmore.model.admin.Agency;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTime;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

/**
 * 连锁组织
 */
@Entity
public class Organization extends BaseEntity {

    /** 名称 */
    @Column(nullable = false)
    @NotEmpty
    public String name;

    /** 注册号 */
    @Column(nullable = false)
    @NotEmpty
    public String serialNum;

    /** 可自行创建门店的配额 */
    public Integer shopQuota;

    /** 营业执照图片地址 */
    public String businessLicenseImageUrl;

    /** 税号 */
    public String taxNumber;

    /** 银行帐号 */
    public String bankAccount;

    /** 开户行 */
    public String bankName;

    /** 联系人 */
    public String contact;

    /** 联系人电话 */
    public String contactPhone;

    /** 联系人地址 */
    public String contactAddress;

    /*** 是否验证盘点 */
    public boolean isCheckPd;

    /*** check日期*/
    public int checkDay = 1;


    /**有效期*/
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern="yyyy-MM-dd")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    public DateTime validDate;

    @Transient
    public String validDateStr;

    /** 试用帐户 */
    public Boolean tried;

    @JsonIgnore
    @ManyToOne
    public Agency agency;
}
