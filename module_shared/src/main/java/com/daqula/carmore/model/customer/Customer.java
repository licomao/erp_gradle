package com.daqula.carmore.model.customer;

import com.daqula.carmore.annotation.ApiJsonIgnore;
import com.daqula.carmore.model.BaseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.Collection;

/** 顾客 */
@Entity
public class Customer extends BaseEntity implements UserDetails {

    /** App登录票据 */
    @Column(nullable = false)
    public String token;

    /** 手机号 */
    @Column(nullable = false)
    public String mobile;

    //*************************************************************************
    // User Detail Implementation
    //*************************************************************************

    @Override
    @ApiJsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    @ApiJsonIgnore
    public String getPassword() {
        return token;
    }

    @Override
    @ApiJsonIgnore
    public String getUsername() {
        return mobile;
    }

    @Override
    @ApiJsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @ApiJsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @ApiJsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @ApiJsonIgnore
    public boolean isEnabled() {
        return true;
    }

}
