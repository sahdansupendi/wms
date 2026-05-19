package com.amz.wms.service;

import com.amz.wms.dto.auth.AuthRequest;
import com.amz.wms.dto.auth.AuthResponse;
import com.amz.wms.entity.Users;
import com.amz.wms.exception.AuthenticationFailedException;
import com.amz.wms.exception.AuthenticationFailedExceptionJWT;
import com.amz.wms.exception.ResourceNotFoundException;
import com.amz.wms.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    @Autowired
    JwtService jwtService;

    public Users login(AuthRequest authRequest) {
        Users user = userRepository.findByUsername(authRequest.username())
                .orElseThrow(() -> new ResourceNotFoundException("Username " + authRequest.username() + " tidak ditemukan"));
        String userid = user.getUserid();
        String pwd = userid + authRequest.password();

        if (!passwordEncoder.matches(pwd, user.getPassword())) {
            throw new AuthenticationFailedException("Password atau Username salah");
        }

        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new AuthenticationFailedException("User tidak aktif");
        }

        return user;
    }

    public AuthResponse refresh(String refreshToken) {
        String tokenType = jwtService.extractTokenType(refreshToken);

        // Cek token valid
        if (!jwtService.isTokenValid(refreshToken)) {
            throw new AuthenticationFailedExceptionJWT("Refresh token expired, silakan login kembali");
        }

        // Cek tidak di-blacklist
        if (tokenBlacklistService.isBlacklisted(refreshToken)) {
            throw new AuthenticationFailedExceptionJWT("Refresh token sudah tidak aktif");
        }

        if (!"refresh".equals(tokenType)) {
            throw new AuthenticationFailedExceptionJWT("Gunakan refresh token, bukan access token");
        }

        String username = jwtService.extractUsername(refreshToken);

        Users users = userRepository.findByUsername(username)
                .orElseThrow(() -> new AuthenticationFailedException("Username " + username + " tidak ditemukan"));

        String newAccesToken = jwtService.generateToken(users);

        Date expiredAt = jwtService.extractExpiredAt(newAccesToken);

        return AuthResponse.fromUser(users,newAccesToken,expiredAt,refreshToken);
    }

    public void logout (HttpServletRequest request, String refreshToken) {
        String authHeader = request.getHeader("Authorization");
        String token = authHeader.substring(7);
        tokenBlacklistService.blacklist(token);
        tokenBlacklistService.blacklist(refreshToken);
    }

}
