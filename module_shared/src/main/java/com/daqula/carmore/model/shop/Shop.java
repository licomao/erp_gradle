package com.daqula.carmore.model.shop;

import com.daqula.carmore.model.BaseEntity;
import com.daqula.carmore.model.template.City;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * 门店信息
 */
@Entity
public class Shop extends BaseEntity {

    /** 门店名称 **/
    @NotEmpty
    public String name;

    /** 门店代号 **/
    public String shopCode;

    /** 门店类型  0: 总店 1:分店 **/
    public int shopType;

    /** 所属组织 **/
    @ManyToOne
    @JsonSerialize(using = BaseEntity.IDSerializer.class)
    public Organization organization;

    /** 门店地址 */
    @NotEmpty
    public String address;

    /** 营业时间，24小时制，如8:00-18:00 */
    @NotEmpty
    public String openingHours;

    /** 联系电话 */
    @NotEmpty
    public String phone;

    /** 门店描述 */
    public String description;

    /** 纬度 */
    public double latitude;

    /** 经度 */
    public double longitude;

    /** 特色标签 */
    public String promotionTag;

    /** 门店照片 */
    public String imageUrl;

    /** 综合评价 */
    public float rating;

    /** 评分次数，每次用户评价后，更新综合评价的公式为
     * (rating * ratingCount + newRating) / (ratingCount+1) */
    public long ratingCount;

    /** 按地理距离查询时用来排序，这个字段本来不应该映射保存，但如果加了Transient注解的话，
     * JPQL的查询又不会返回，只好放在这里 */
    @Column(updatable = false)
    public Double distance;

    /** 所在城市 **/
    @ManyToOne
    public City city;
}
