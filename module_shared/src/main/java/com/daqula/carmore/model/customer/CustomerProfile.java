package com.daqula.carmore.model.customer;

import com.daqula.carmore.model.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;

/** 顾客信息 */
@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="profile_type",
        discriminatorType=DiscriminatorType.STRING)
public abstract class CustomerProfile extends BaseEntity {

    public static final String PROFILE_TYPE_APP = "app";
    public static final String PROFILE_TYPE_ERP = "erp";

    @ManyToOne(optional = false)
    public Customer customer;

    @Column(name = "profile_type", insertable = false, updatable = false)
    public String profileType;

    /** 车辆信息 */
    @OneToMany(cascade = CascadeType.ALL,
               orphanRemoval = true)
    @JoinColumn(name = "profile_id")
    public List<VehicleInfo> vehicles;
}
