package me.agohar.usersec.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

// @Component
// public class SimpleTokenAuthenticationFilter extends OncePerRequestFilter {

//     @Override
//     protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
//                                   FilterChain filterChain) throws ServletException, IOException {
        
//         String authHeader = request.getHeader("Authorization");
        
//         if (authHeader != null && authHeader.startsWith("Bearer ")) {
//             String token = authHeader.substring(7);
            
//             // Simple token validation for testing
//             if ("admin-token".equals(token)) {
//                 List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
//                 UsernamePasswordAuthenticationToken authentication = 
//                     new UsernamePasswordAuthenticationToken("admin@test.com", null, authorities);
//                 SecurityContextHolder.getContext().setAuthentication(authentication);
//             } else if ("user-token".equals(token)) {
//                 List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
//                 UsernamePasswordAuthenticationToken authentication = 
//                     new UsernamePasswordAuthenticationToken("user@test.com", null, authorities);
//                 SecurityContextHolder.getContext().setAuthentication(authentication);
//             }
//         }
        
//         filterChain.doFilter(request, response);
//     }
// }


