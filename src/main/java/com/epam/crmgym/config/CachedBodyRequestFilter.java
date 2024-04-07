package com.epam.crmgym.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

public class CachedBodyRequestFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        CachedBodyHttpServletRequest cachedRequest = new CachedBodyHttpServletRequest((HttpServletRequest) servletRequest);
        filterChain.doFilter(cachedRequest, servletResponse);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization logic
    }

    @Override
    public void destroy() {
        // Cleanup logic
    }
}
