package com.daqula.carmore.model.shop;

import com.daqula.carmore.model.BaseEntity;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

/**
 * 门店费用管理
 * Created by mdc on 2015/9/9.
 */
@Entity
public class Expense extends BaseEntity {

    /** 年度 */
    public int year;

    /** 月度 */
    public int month;

    /** 月度房租费用 */
    public double rentExpense;

    /** 月度物业费用费用 */
    public double propertyExpense;

    /** 水费 */
    public double waterExpense;

    /** 电费 */
    public double electricExpense;

    /** 网费+电话费 */
    public double netPhoneExpense;

    /** 设备维护费 */
    public double equipRepairsExpense;

    /** 其他费用 */
    public double otherExpense;

    /** 员工基本工资总数 */
    public double staffBaseExpense;

    /** 员工绩效总数 */
    public double staffPerformanceExpense;

    /** 员工提成总数 */
    public double staffCommissionExpense;

    public double getSum(){
        return this.rentExpense + this.propertyExpense + this.waterExpense + this.electricExpense + this.netPhoneExpense
                + this.equipRepairsExpense + this.otherExpense + this.staffBaseExpense + this.staffCommissionExpense + this.staffPerformanceExpense;
    }

    /** 对应门店费用 */
    @ManyToOne
    public Shop shop;

    @Transient
    public String notePerson;

    @Transient
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    public DateTime operateDate;

}
