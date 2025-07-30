package com.arem.core.auth;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.arem.core.model.Customer;

public class CustomerDetails implements UserDetails {
    
    private static final long serialVersionUID = 1L;
    
    private final Customer customer;
    
    public CustomerDetails(Customer customer) {
        this.customer = customer;
    }
    
    public Customer getCustomer() {
        return customer;
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_CUSTOMER"));
    }
    
    @Override
    public String getPassword() {
        // Retourner le mot de passe du customer
        return customer.getPassword() != null ? customer.getPassword() : "";
    }
    
    @Override
    public String getUsername() {
        return customer.getEmail();
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
        return true;
    }
} 