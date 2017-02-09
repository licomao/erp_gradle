package com.daqula.carmore.security;

import com.daqula.carmore.model.customer.Customer;
import com.daqula.carmore.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("customerDetailsService")
@Qualifier("customerDetailsService")
public class CustomerDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private CustomerRepository customerRepository;

    @Transactional(readOnly=true)
    @Override
    public UserDetails loadUserByUsername(final String mobile) throws UsernameNotFoundException {
        List<Customer> customers = customerRepository.findByMobile(mobile);
//        Customer customer = customerRepository.findByMobile(mobile);
        if (customers.size() == 0) {
            throw new UsernameNotFoundException(String.format("Customer %s login failed.", mobile));
        }
        return customers.get(0);
    }

}