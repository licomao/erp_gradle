package com.daqula.carmore.model.customer;

import com.daqula.carmore.annotation.ApiJsonIgnore;
import com.daqula.carmore.model.BaseEntity;
import com.daqula.carmore.model.template.VehicleModel;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * 顾客车型
 */
@Entity
public class VehicleInfo extends BaseEntity {

    /** Vin码，由OBD抓取，或门店前台确认后填入 */
    public String vinCode;

    /** Vin码照片URL，由用户上传 */
    public String vinImageUrl;

    /** 车型 */
    @ManyToOne
    @ApiJsonIgnore
    public VehicleModel model;

    /** 轮胎型号 */
    public String tire;

    /** 车牌号 */
    public String plateNumber;

    /** 上路时间 */
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    public DateTime onRoadDate;

    /** 已行驶里程 */
    public int mileage;

    /** 已行驶里程最后更新时间 */
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    public DateTime mileageUpdatedDate;

    /** obd序列号 */
    public String obdSN;

    /** 上次保养里程 */
    public int lastMaintenanceMileage;

    /** 上次保养里程更新时间 */
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    public DateTime lastMaintenanceDate;

    /** 门店是否确认车型数据正确 */
    public boolean verified;

    /** 汽车排量 */
    public String engineDisplacement;

    //**************************************************************************
    // Serialization / Deserialization
    //**************************************************************************

    public VehicleInfo clone() {
        VehicleInfo vehicleInfo = new VehicleInfo();
        vehicleInfo.vinCode = vinCode;
        vehicleInfo.vinImageUrl = vinCode;
        vehicleInfo.model = model;
        vehicleInfo.tire = tire;
        vehicleInfo.plateNumber = plateNumber;
        vehicleInfo.onRoadDate = onRoadDate;
        vehicleInfo.mileage = mileage;
        vehicleInfo.mileageUpdatedDate = mileageUpdatedDate;
        vehicleInfo.obdSN = obdSN;
        vehicleInfo.lastMaintenanceMileage = lastMaintenanceMileage;
        vehicleInfo.lastMaintenanceDate = lastMaintenanceDate;
        vehicleInfo.verified = verified;
        vehicleInfo.engineDisplacement = engineDisplacement;
        return vehicleInfo;
    }

    @JsonProperty
    public void setModel(VehicleModel model) {
        this.model = model;
    }

    @JsonProperty("brandName")
    public String getBrandName() {
        return model != null ? model.brand : "未知";
    }

    @JsonProperty("lineName")
    public String getLineName() {
        return model != null ? model.line : "未知";
    }

    @JsonProperty("versionName")
    public String getVersionName() {
        return model != null ? model.version : "未知";
    }

    @JsonProperty("modelId")
    public long getModelId() {
        return model.id;
    }

    @JsonProperty("vehicleClazz")
    public String getVehicleClazz() {
        return model != null ? model.getVehicleClazz().toString() : VehicleModel.VehicleClass.A.toString();
    }

    public String getName() {
        return getBrandName() + " " + getLineName() + " " + getVersionName();
    }

}
