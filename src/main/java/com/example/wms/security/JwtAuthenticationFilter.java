package com.example.wms.security;

import com.example.wms.exception.AuthenticationFailedException;
import com.example.wms.service.JwtService;
import com.example.wms.service.TokenBlacklistService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.equals("/api/auth/login") || path.equals("/api/auth/blacklist") || path.equals("/api/auth/refresh");
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        try {

            String authHeader = request.getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new AuthenticationFailedException("Token tidak valid / belum login");
            }

            String token = authHeader.substring(7);


            if (!jwtService.isTokenValid(token)) {
                throw new AuthenticationFailedException("Token expired atau tidak valid");
            }

            if (!jwtService.isTokenValid(token)) {
                throw new AuthenticationFailedException("Token expired atau tidak valid");
            }

            // Cek apakah token sudah di-blacklist (sudah logout)
            if (tokenBlacklistService.isBlacklisted(token)) {
                throw new AuthenticationFailedException("Token sudah tidak aktif, silakan login kembali");
            }

            String tokenType = jwtService.extractTokenType(token);

            if (!"access".equals(tokenType)) {
                throw new AuthenticationFailedException("Gunakan access token, bukan refresh token");
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

            filterChain.doFilter(request, response);

        }catch (AuthenticationFailedException e) {
            writeErrorResponse(request,response,e.getMessage());
        }catch (Exception e){
            writeErrorResponse(request,response,"Terjadi kesalahan autentikasi");
        }
    }

    private void writeErrorResponse(HttpServletRequest request,
                                    HttpServletResponse response,
                                    String message) throws IOException {
        Map<String,Object> body = new LinkedHashMap<>();
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        body.put("error", "Unauthorized");
        body.put("message", message);
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("path", request.getRequestURI());

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        new ObjectMapper().writeValue(response.getWriter(), body);
    }
}
