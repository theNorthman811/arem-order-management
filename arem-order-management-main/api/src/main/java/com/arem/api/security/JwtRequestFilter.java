package com.arem.api.security;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.arem.core.auth.SellerDetails;
import com.arem.core.auth.CustomerDetails;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

	private static final Logger logger = LoggerFactory.getLogger(JwtRequestFilter.class);

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
				String path = request.getServletPath();
				logger.debug("JwtRequestFilter processing request: {} {}", request.getMethod(), path);
				
				if (path.equals("/api/auth/login") || path.equals("/api/auth/register") || 
				    path.equals("/api/auth/login-client") || path.equals("/api/auth/register-client") ||
				    path.equals("/api/v1/products/public")) {
					logger.debug("Skipping JWT validation for path: {}", path);
					chain.doFilter(request, response);
					return;
				}
				// 
		final String requestTokenHeader = request.getHeader("Authorization");
		logger.debug("Authorization header: {}", requestTokenHeader != null ? "present" : "missing");

		String username = null;
		String jwtToken = null;
		
		if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
			jwtToken = requestTokenHeader.substring(7);
			logger.debug("JWT token extracted, length: {}", jwtToken.length());
			try {
				username = jwtTokenUtil.getUsernameFromToken(jwtToken);
				logger.debug("Username extracted from token: {}", username);
			} catch (Exception e) {
				logger.error("Unable to get JWT Token or JWT Token has expired: {}", e.getMessage());
			}
		} else {
			logger.warn("JWT Token does not begin with Bearer String or is missing");
		}

		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			try {
				logger.debug("Loading user details for username: {}", username);
				UserDetails userDetails = userDetailsService.loadUserByUsername(username);
				logger.debug("User details loaded: {}", userDetails.getClass().getSimpleName());
				
				boolean isValidToken = false;
				
				if (userDetails instanceof SellerDetails) {
					logger.debug("Validating token for SellerDetails");
					isValidToken = jwtTokenUtil.validateToken(jwtToken, (SellerDetails) userDetails);
				} else if (userDetails instanceof CustomerDetails) {
					logger.debug("Validating token for CustomerDetails");
					isValidToken = jwtTokenUtil.validateToken(jwtToken, (CustomerDetails) userDetails);
				} else {
					logger.debug("Validating token for generic UserDetails");
					isValidToken = jwtTokenUtil.validateToken(jwtToken, userDetails);
				}
				
				logger.debug("Token validation result: {}", isValidToken);
				
				if (isValidToken) {
					UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
							userDetails, null, userDetails.getAuthorities());
					authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(authentication);
					logger.debug("Authentication set in SecurityContext for user: {}", username);
				} else {
					logger.warn("Token validation failed for user: {}", username);
				}
			} catch (Exception e) {
				logger.error("Error processing JWT token for user {}: {}", username, e.getMessage(), e);
			}
		}
		chain.doFilter(request, response);
	}

}