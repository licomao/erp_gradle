package com.daqula.carmore.model.shop;

import com.daqula.carmore.model.BaseEntity;

import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;

import org.joda.time.DateTime;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.List;

/**
 * 员工信息
 */
@Entity
public class Staff extends BaseEntity {

    /** 员工姓名 **/
    @NotEmpty
    public String name;

    /** 员工身份证 **/
    @NotEmpty
    public String identityCard;

    /** 员工电话 **/
    @NotEmpty
    public String phone;

    /** 员工所属门店 **/
    @OneToOne
    public Shop shop;

    /** 员工工种 **/
    @OneToOne
    public Job job;

    /** 员工指纹 **/
//    @Basic(fetch = FetchType.LAZY)
//    @Type(type="text")
    @Column(length = 2000)
    public String fingerPrint;

    /** 员工入职日期 **/
    @DateTimeFormat(pattern="yyyy/MM/dd")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    public DateTime entryDate;

    /** 员工离职日期 **/
    @DateTimeFormat(pattern="yyyy/MM/dd")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    public DateTime dimissionDate;

    /** 员工状态 试用=1; 正式=2; 离职=3 **/
    public String status;

    /** 员工试用期 **/
    public double probation;

    /** 月名义工作天数 **/
    public double workDay;

    /** 出勤记录一对多 **/
    @OneToMany(cascade = CascadeType.ALL,
            orphanRemoval = true)
//    @JoinColumn(name = "staff_id")
    public List<StaffAttendance> staffAttendances;

}
