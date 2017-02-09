package com.daqula.carmore.model.admin;

import com.daqula.carmore.model.BaseEntity;
import com.daqula.carmore.model.shop.Organization;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Created by thy on 2015/9/11.
 * 供应商信息
 */

@Entity
public class Supplier extends BaseEntity {

    /** 供应商名称 **/
    @NotEmpty
    public String name;

    /** 联系方式 **/
    public String contactInfo;

    /** 邮箱 **/
    public String email;

    /** 传真 **/
    public String fax;

    /** 所属组织 **/
    @JsonIgnore
    @ManyToOne
    public Organization organization;

    /** 供应商描述 **/
    public String description;
}
