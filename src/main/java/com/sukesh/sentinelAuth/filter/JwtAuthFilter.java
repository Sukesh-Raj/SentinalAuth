package com.sukesh.sentinelAuth.filter;

import com.sukesh.sentinelAuth.security.CustomUserDetailsService;
import com.sukesh.sentinelAuth.util.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private CustomUserDetailsService customUserDetailsService;

    private JwtUtils jwtUtils;

    @Autowired
    public JwtAuthFilter (CustomUserDetailsService customUserDetailsService,JwtUtils jwtUtils)
    {
        this.customUserDetailsService = customUserDetailsService;
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        String token = null;
        String userId = null;
        if(header!=null && header.startsWith("Bearer "))
        {
            token = header.substring(7);
            System.out.println("token " +token);
            userId = jwtUtils.getSubject(token);
            System.out.println("userId " + userId);
        }
        System.out.println("get");
        if(userId!=null && SecurityContextHolder.getContext().getAuthentication()==null)
        {
            System.out.println("inside validation");
            UserDetails userDetails = customUserDetailsService.loadUserByUserId(userId);

            if(jwtUtils.validateToken(token,userDetails,userId))
            {
                System.out.println("inside securitycontext");
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                null// Make sure this isn't returning a null value
                        );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                System.out.println(SecurityContextHolder.getContext().getAuthentication());
            }
        }
        filterChain.doFilter(request,response);
    }
}
