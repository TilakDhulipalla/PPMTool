package com.hcl.ppmtool.security;

import java.io.IOException;
import java.util.Collections;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.hcl.ppmtool.domain.User;
import com.hcl.ppmtool.services.CustomUserDetailsService;

import static com.hcl.ppmtool.security.SecurityConstants.HEADER_STRING;
import static com.hcl.ppmtool.security.SecurityConstants.TOKEN_PREFIX;


public class JwtAuthenticationFilter extends OncePerRequestFilter {

	    @Autowired
	    private JwtTokenProvider tokenProvider;

	    @Autowired
	    private CustomUserDetailsService customUserDetailsService;

	
	@Override
	protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		 try {

	            String jwt = getJWTFromRequest(httpServletRequest);

	            if(StringUtils.hasText(jwt)&& tokenProvider.validateToken(jwt)){
	                Long userId = tokenProvider.getUserIdFromJWT(jwt);
	                User userDetails = customUserDetailsService.loadUserById(userId);

	                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
	                        userDetails, null, Collections.emptyList());

	                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
	                SecurityContextHolder.getContext().setAuthentication(authentication);

	            }

	        }catch (Exception ex){
	            logger.error("Could not set user authentication in security context", ex);
	        }


	        filterChain.doFilter(httpServletRequest, httpServletResponse);

	    }



	    private String getJWTFromRequest(HttpServletRequest request){
	        String bearerToken = request.getHeader(HEADER_STRING);

	        if(StringUtils.hasText(bearerToken)&&bearerToken.startsWith(TOKEN_PREFIX)){
	            return bearerToken.substring(7, bearerToken.length());
	        }

	        return null;
	    }
	
		
	}


