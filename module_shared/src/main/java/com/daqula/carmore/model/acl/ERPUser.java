package com.daqula.carmore.model.acl;

import com.daqula.carmore.model.BaseEntity;
import com.daqula.carmore.model.shop.Organization;
import com.daqula.carmore.model.shop.Shop;
import org.joda.time.DateTime;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * ERP用户
 */
@Entity
public class ERPUser extends BaseEntity implements UserDetails {

    /**用户名*/
    @Column(nullable = false)
    public String username;

    /**密码*/
    @Column(nullable = false)
    public String password;

    /**真实姓名*/
    public String realName;

    /**邮箱*/
    public String email;

    /**联系电话*/
    public String phone;

    /**用户角色*/
    @OneToOne
    public ERPRole role ;

    /**组织*/
    @ManyToOne
    public Organization organization;

    /**？？？？*/
    public boolean enable;

    /**用户所在门店*/
    @ManyToMany
    public List<Shop> shops;

    /**显示用的创建时间*/
    @Transient
    public String showedDate;

    @Column(length = 2000)
    public String fingerPrint;

    public ERPUser() {
        role = new ERPRole(ERPRole.ROLE_NO_BODY);
    }

    //*************************************************************************
    // User Detail Implementation
    //*************************************************************************

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<ERPRole> roles = new HashSet<>();
        roles.add(role);
        return roles;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enable;
    }

    //*************************************************************************
    // Domain Operation
    //*************************************************************************

    public static ERPUser createUser(String username, String password) {
        ERPUser user = new ERPUser();

        user.username = username;
        user.password = password; // TODO 加盐加密

        return user;
    }

    private long getAuthorityMask() {
        long mergedMask = Long.MAX_VALUE;
        mergedMask = mergedMask & role.authorityMask;
        return mergedMask;
    }

    public boolean checkAuthority(long askingAuthority) {
        return (getAuthorityMask() & askingAuthority) != 0;
    }

    /**
     * 是否组织过期
     * @return
     */
    private boolean checkOrgValidDate() {
        DateTime now = new DateTime();
        if(organization.validDate == null) {
            return true;
        }
        return organization.validDate.compareTo(now) < 0;
    }
}
