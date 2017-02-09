package com.daqula.carmore.model.admin;

import com.daqula.carmore.model.BaseEntity;
import com.daqula.carmore.model.shop.Shop;
import com.daqula.carmore.model.template.City;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

/**
 * App的活动和广告
 */
@Entity
public class Campaign extends BaseEntity {

    /** 全部通用 */
    public static int CAMPAIGN_TYPE_GENERAL = 0;
    /** 城市 */
    public static int CAMPAIGN_TYPE_CITY = 1;
    /** 附近，默认3公里范围 */
    public static int CAMPAIGN_TYPE_NEARBY = 2;

    /** 推广范围 */
    public int compaignType;

    /** 是否显示在首页Banner上 */
    public boolean onBanner;

    /** 打开时WebView的Url，与shop二选一 */
    public String url;

    /** 对应门店 */
    @ManyToOne
    public Shop shop;

    /** Banner图片Url */
    @Column(nullable = false)
    public String bannerImageUrl;

    /** 摘要说明，显示在活动界面 */
    @Column(nullable = false)
    public String summary;

    @ManyToOne
    @JsonSerialize(using = NameSerializer.class)
    public City city;

    /** 推送中心点纬度 */
    public double latitude;

    /** 推送中心点经度 */
    public double longitude;

    /** 发布日期 */
    @DateTimeFormat(pattern="yyyy/MM/dd")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    public DateTime publishDate;

    /** 按地理距离查询时用来排序，这个字段本来不应该映射保存，但如果加了Transient注解的话，
     * JPQL的查询又不会返回，只好放在这里 */
    @Column(updatable = false)
    public Double distance;
}
