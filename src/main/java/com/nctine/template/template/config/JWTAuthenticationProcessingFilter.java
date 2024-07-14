package com.nctine.template.template.config;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Log4j2
@Component
public class JWTAuthenticationProcessingFilter extends OncePerRequestFilter {


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        try {
//            String rft = parseRft(request);
//            Optional<RefreshToken> refreshTokenOpt = refreshTokenService.findByToken(rft);
//
//            RefreshToken refreshToken = null;
//            if(refreshTokenOpt.isPresent()){
//                refreshToken = refreshTokenService.verifyExpiration(refreshTokenOpt.get());
//            }
//        } catch (Exception e) {
//            log.error("Cannot set user authentication: {}", e);
//        }

        filterChain.doFilter(request, response);
    }

    private String parseRft(HttpServletRequest request) {
        String headerRft = request.getHeader("RefreshToken");
        if (StringUtils.hasText(headerRft)) {
            return headerRft;
        }

        return null;
    }
}