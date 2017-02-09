package com.daqula.carmore.model.template;

import com.daqula.carmore.annotation.ApiJsonIgnore;
import com.daqula.carmore.model.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.persistence.Entity;
import javax.persistence.Transient;

/**
 * 车型列表
 */
@Entity
public class VehicleModel extends BaseEntity {

    private final static Log log = LogFactory.getLog(VehicleModel.class);

    public final static int CLASS_A_PRICE = 15;
    public final static int CLASS_B_PRICE = 30;

    public enum VehicleClass {
        A, B, C
    }

    /** 品牌 */
    public String brand;

    /** 车系 */
    public String line;

    /** 车款车型 */
    public String version;

    /** 厂商指导价 */
    public String price;

    /** 年款 */
    public String producedYear;

    /** 排量 */
    public String engineDisplacement;

    /** 变速箱 */
    public String gearBox;

    /** 前轮规格 */
    public String frontTire;

    /** 备胎规格 */
    public String spareTire;

    /** 后轮规格 */
    public String backTire;

    /** 是否在售 */
    public String forSale;

    /** 首字母 */
    public String firstLetter;

    /** 制造商 **/
    public String manufacturer;

    /** 发动机 */
    public String engine;

    /** 车价档位, ABC */
    @Transient
    public VehicleClass clazz;

    @Transient
    @ApiJsonIgnore
    public VehicleClass getVehicleClazz() {
        float priceFloat = 0;
        try {
            priceFloat = Float.parseFloat(price.substring(0, price.length()-1));
        } catch (Exception e) {
            log.warn("Incorrect Vehicle price number format : "+price);
        }

        return (priceFloat > CLASS_B_PRICE) ? VehicleClass.C
             : (priceFloat > CLASS_A_PRICE) ? VehicleClass.B
                                            : VehicleClass.A;
    }
}
