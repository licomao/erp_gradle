package com.daqula.carmore.security;

import com.daqula.carmore.model.acl.ERPUser;
import com.daqula.carmore.repository.ERPUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("userDetailsService")
@Qualifier("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private ERPUserRepository userRepository;

    @Autowired(required = false)
    public ResourceBundleMessageSource messageSource;

    @Transactional(readOnly=true)
    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        ERPUser user = userRepository.findByUsernameAndDeleted(username, false);
        if (user == null) {
            throw new UsernameNotFoundException(messageSource == null ? String.format("Login as %s failed", username)
                    : messageSource.getMessage("login.error", new String[]{username}, LocaleContextHolder.getLocale()));
        }
        //user.roles.size(); // fetch roles
        return user;
    }

}