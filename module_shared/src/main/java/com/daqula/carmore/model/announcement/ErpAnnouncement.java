package com.daqula.carmore.model.announcement;
import com.daqula.carmore.model.BaseEntity;
import com.daqula.carmore.model.acl.ERPUser;
import com.daqula.carmore.model.shop.Organization;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTime;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

/**
 * Created by tianhongyu on 2015/9/6.
 */

/**
 * ERP公告信息
 */
@Entity
public class ErpAnnouncement extends BaseEntity{

    /** 公告标题 **/
    @NotEmpty
    public String title;

    /** 发布人 **/
    @NotEmpty
    public String publisher;

    /** 公告内容 **/
    @NotEmpty
    public String content;

    /** 发布时间 **/
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    public DateTime publishDate;

    /** 所属组织 */
    @ManyToOne
    public Organization organization;

    @Transient
    public boolean isNewInfo;
}
