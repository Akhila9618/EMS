package com.example.authservice.filters;

import java.io.IOException;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public  class RequestLoggingFilter extends OncePerRequestFilter {

	final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class); 

//	this gets executed for every received request
	@Override   // this overrider is compulsory
	protected void  doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)throws ServletException,IOException  {
		
			logger.info("----------------------");
			logger.info("Request received");
			logger.info("Request is :::: " + request.toString());
			logger.info("Path : {}", request.getRequestURI());
			logger.info("Method : {}", request.getMethod());
			logger.info("Time : {}", LocalDateTime.now());
			logger.info("----------------------------------------");

			 try {
				chain.doFilter(request, response);
			} catch (java.io.IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ServletException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

}
