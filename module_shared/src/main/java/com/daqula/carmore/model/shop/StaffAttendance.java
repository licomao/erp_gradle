package com.daqula.carmore.model.shop;

import com.daqula.carmore.model.BaseEntity;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import java.util.Date;

/**
 * 员工出勤表
 */
@Entity
public class StaffAttendance extends BaseEntity {

    /** 员工信息 **/
    @ManyToOne
    public Staff staff;

    /** 上班日期 **/
    @DateTimeFormat(pattern="yyyy/MM/dd")
    public Date workDate;

    @Transient
    @DateTimeFormat(pattern="yyyy/MM/dd")
    public Date workDateEnd;

    /** 上班时间 **/
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    public DateTime arriveDate;

    /** 下班时间 **/
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    public DateTime leaveDate;


}
