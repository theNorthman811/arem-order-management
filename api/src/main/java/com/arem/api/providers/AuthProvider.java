package com.arem.api.providers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import com.arem.core.auth.SellerDetails;
import com.arem.core.auth.CustomerDetails;
import com.arem.core.model.Seller;
import com.arem.core.model.Customer;
import com.arem.dataservice.services.ISellerService;
import com.arem.dataservice.services.ICustomerService;

@Component
public class AuthProvider implements AuthenticationProvider, UserDetailsService
{
	
	private static final Logger logger = LoggerFactory.getLogger(AuthProvider.class);
	
	@Autowired
	private ISellerService sellerService;
	
	@Autowired
	private ICustomerService customerService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException
	{
		String username = authentication.getName();
		String password = authentication.getCredentials().toString();
		
		logger.debug("Attempting authentication for user: '{}'", username);
		
		if (username == null || username.trim().isEmpty()) {
			logger.error("Username is null or empty");
			throw new BadCredentialsException("Username cannot be empty");
		}
		
		// D'abord essayer de trouver un Seller (admin)
		Seller seller = sellerService.getSellerByEmail(username);
		if (seller != null)
		{
			logger.debug("Found seller: '{}', checking password", username);
			
			if (passwordEncoder.matches(password, seller.getPassword()))
			{
				logger.debug("Password matches for seller: '{}'", username);
				SellerDetails userDetails = new SellerDetails(seller);
				return new UsernamePasswordAuthenticationToken(
					userDetails, password, userDetails.getAuthorities());
			}
			
			logger.error("Invalid password for seller: '{}'", username);
			throw new BadCredentialsException("Invalid password");
		}
		
		// Si pas de Seller, essayer de trouver un Customer
		Customer customer = customerService.getCustomerByEmail(username);
		if (customer != null)
		{
			logger.debug("Found customer: '{}', checking password", username);
			
			if (passwordEncoder.matches(password, customer.getPassword()))
			{
				logger.debug("Password matches for customer: '{}'", username);
				CustomerDetails userDetails = new CustomerDetails(customer);
				return new UsernamePasswordAuthenticationToken(
					userDetails, password, userDetails.getAuthorities());
			}
			
			logger.error("Invalid password for customer: '{}'", username);
			throw new BadCredentialsException("Invalid password");
		}
		
		logger.error("User not found: '{}'", username);
		throw new UsernameNotFoundException("User not found: " + username);
	}
	
	@Override
	public boolean supports(Class<?> authentication)
	{
		return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
	}
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		logger.debug("loadUserByUsername called with: '{}'", username);
		
		// D'abord essayer de trouver un Seller (admin)
		Seller seller = sellerService.getSellerByEmail(username);
		if (seller != null) {
			logger.debug("Found seller: {}", username);
			return new SellerDetails(seller);
		}
		
		// Si pas de Seller, essayer de trouver un Customer par email
		Customer customer = customerService.getCustomerByEmail(username);
		if (customer != null) {
			logger.debug("Found customer: {}", username);
			return new CustomerDetails(customer);
		}
		
		logger.error("User not found in loadUserByUsername: {}", username);
		throw new UsernameNotFoundException("User not found: " + username);
	}
	
	public Seller getCurrentUser()
	{
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null && auth.getPrincipal() instanceof SellerDetails)
		{
			SellerDetails userDetails = (SellerDetails) auth.getPrincipal();
			return userDetails.getSeller();
		}
		throw new IllegalStateException("No authenticated user found");
	}
	
	public Customer getCurrentCustomer()
	{
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null && auth.getPrincipal() instanceof CustomerDetails)
		{
			CustomerDetails userDetails = (CustomerDetails) auth.getPrincipal();
			return userDetails.getCustomer();
		}
		throw new IllegalStateException("No authenticated customer found");
	}
	
	public Object getCurrentUserOrCustomer()
	{
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			if (auth.getPrincipal() instanceof SellerDetails) {
				SellerDetails userDetails = (SellerDetails) auth.getPrincipal();
				return userDetails.getSeller();
			} else if (auth.getPrincipal() instanceof CustomerDetails) {
				CustomerDetails userDetails = (CustomerDetails) auth.getPrincipal();
				return userDetails.getCustomer();
			}
		}
		throw new IllegalStateException("No authenticated user found");
	}

}
