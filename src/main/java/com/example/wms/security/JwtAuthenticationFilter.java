package com.example.wms.security;

import com.example.wms.dto.JwtErrorResponseWriter;
import com.example.wms.exception.AuthenticationFailedException;
import com.example.wms.exception.AuthenticationFailedExceptionJWT;
import com.example.wms.service.JwtService;
import com.example.wms.service.TokenBlacklistService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    @Autowired
    private JwtErrorResponseWriter errorResponseWriter;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        request.setAttribute("originalPath", request.getRequestURI());

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {

            String token = authHeader.substring(7);


            if (!jwtService.isTokenValid(token)) {
                throw new AuthenticationFailedExceptionJWT("Token expired atau tidak valid");
            }


            // Cek apakah token sudah di-blacklist (sudah logout)
            if (tokenBlacklistService.isBlacklisted(token)) {
                throw new AuthenticationFailedExceptionJWT("Token sudah tidak aktif, silakan login kembali");
            }

            String tokenType = jwtService.extractTokenType(token);

            if (!"access".equals(tokenType)) {
                throw new AuthenticationFailedExceptionJWT("Gunakan access token, bukan refresh token");
            }

            //ambil data dari token
            String username = jwtService.extractUsername(token);
            String userid = jwtService.extractUserid(token);
            String rolename = jwtService.extractRoleName(token);
            Date expiedAt = jwtService.extractExpiredAt(token);

            //simpan ke request (bukan session)
            request.setAttribute("username", username);
            request.setAttribute("userid", userid);
            request.setAttribute("role", rolename);
            request.setAttribute("expiredAt", expiedAt);
            request.setAttribute("tokenType", tokenType);

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(username,null,new ArrayList<>());

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            filterChain.doFilter(request, response);


        }catch (AuthenticationFailedExceptionJWT e) {
            errorResponseWriter.writeUnauthorized(response,request.getRequestURI(),e.getMessage());
        }
    }
}
