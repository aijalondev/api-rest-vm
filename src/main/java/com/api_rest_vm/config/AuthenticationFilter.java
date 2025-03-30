package com.api_rest_vm.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.api_rest_vm.exception.AuthenticationException;
import com.api_rest_vm.exception.TokenException;
import com.api_rest_vm.service.TokenService;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuthenticationFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UserDetailsService userDetailsService;

    @SuppressWarnings("null")
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) {

        String authHeader = request.getHeader("Authorization");

        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                chain.doFilter(request, response);
                return;
            }

            String token = authHeader.substring(7);

            Optional<String> optionalUsername = Optional.ofNullable(tokenService.extractUsername(token));

            if (optionalUsername.isPresent() && SecurityContextHolder.getContext().getAuthentication() == null) {

                String username = optionalUsername.get();
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (tokenService.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    throw new TokenException("Invalid or expired token");
                }
            }

            chain.doFilter(request, response);
        } catch (Exception e) {
            throw new AuthenticationException("Authentication failed");
        }
    }
}
