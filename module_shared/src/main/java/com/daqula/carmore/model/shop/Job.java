package com.daqula.carmore.model.shop;

import com.daqula.carmore.model.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * 工种信息
 */
@Entity
public class Job extends BaseEntity {

    /** 工种名称 **/
    public String name;

    /** 工种所属组织 **/
    @ManyToOne
    public Organization organization;

}
