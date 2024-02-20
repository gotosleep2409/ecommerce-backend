package org.example.apitest.config;

import org.example.apitest.constant.AppConstants;
import org.example.apitest.dto.AdminUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class JwtAuthorizationFilter extends OncePerRequestFilter{

    @Autowired
    private JwtTokenProvider tokenProvider;

    private Authentication getUsernamePasswordAuthentication(HttpServletRequest request) {
        String token = request.getHeader(AppConstants.HEADER_STRING)
                .replace(AppConstants.TOKEN_PREFIX, "");

        Date expiresAtToken = tokenProvider.getExpiresAtToken(token);
        if (Date.from(Instant.now()).after(expiresAtToken)) {
            return null;
        }

        String userId = tokenProvider.getUserIdFromToken(token);
        if (!userId.equals("1")) {
            return null;
        }
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.add(new SimpleGrantedAuthority("user.getRole().getName()"));
        UserPrincipal principal = new UserPrincipal(new AdminUser(userId, "admin", "123456"));
        return new UsernamePasswordAuthenticationToken(principal, null, grantedAuthorities);
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader(AppConstants.HEADER_STRING);
        if (header == null || !header.startsWith(AppConstants.TOKEN_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }
        Authentication authentication = getUsernamePasswordAuthentication(request);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }


}