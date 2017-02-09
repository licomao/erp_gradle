package com.daqula.carmore.model.acl;

import com.daqula.carmore.model.BaseEntity;
import com.daqula.carmore.model.shop.Organization;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * ERP用户角色
 */
@Entity
public class ERPRole extends BaseEntity implements GrantedAuthority {

    public static final String ROLE_NO_BODY = "ROLE_NO_BODY";

    public ERPRole() {}

    public ERPRole(String roleName) {
        this.role = roleName;
    }

    @Column(unique = true, nullable = false)
    public String role;

    /** 该角色权限掩码，以二进制表示，每一位代表一个功能的权限, 最多可有63位可用 */
    public long authorityMask;

    /** 组织 */
    @ManyToOne
    public Organization organization;

    //*************************************************************************
    // UserDetail GrantedAuthority
    //*************************************************************************

    @Override
    public String getAuthority() {
        return role;
    }

    //*************************************************************************
    // Object Methods
    //*************************************************************************

    public int hashCode() {
        return this.role.hashCode();
    }

    public String toString() {
        return this.role;
    }
}
