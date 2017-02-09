package com.daqula.carmore.model.template;

import com.daqula.carmore.model.BaseEntity;
import com.daqula.carmore.model.shop.Organization;
import com.daqula.carmore.model.shop.Shop;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;


/**
 * 指纹机
 * Created by swj on 2015/11/5.
 */
@Entity
public class FingerPrintScanner extends BaseEntity{

    /**
     * 设备序列号
     */
    public String usbSn;

    /**
     * 设备VID
     */
    public String vid;

    /**
     * 设备PID
     */
    public String pid;

    /**
     * 设备识别码
     */
    public String sensorSN;

    /**
     * 预留字段 所属门店
     */
    @ManyToOne(targetEntity = Shop.class)
    public Shop shop;

    /**
     * 预留字段 所属组织
     */
    @ManyToOne(targetEntity = Organization.class)
    public Organization organization;
}
