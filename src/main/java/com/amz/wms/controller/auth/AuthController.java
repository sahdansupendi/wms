package com.amz.wms.controller.auth;


import com.amz.wms.dto.ApiResponse;
import com.amz.wms.dto.auth.AuthRequest;
import com.amz.wms.dto.auth.AuthResponse;
import com.amz.wms.entity.Users;
import com.amz.wms.service.AuthService;
import com.amz.wms.service.JwtService;
import com.amz.wms.service.TokenBlacklistService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    final AuthService authService;
    private final JwtService jwtService;
    private final TokenBlacklistService tokenBlacklistService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody AuthRequest authRequest, HttpServletRequest request) {
        Users user = authService.login(authRequest);

        // generate JWT
        String token = jwtService.generateToken(user);

        // refresh token
        String refreshtoken = jwtService.generateRefreshToken(user);

        // generate ExpiedIn
        Date expiredAt = jwtService.extractExpiredAt(token);

        /*// simpan ke session
        session.setAttribute("username", user.getUsername());
        session.setAttribute("roleid", user.getRoleid());
        session.setAttribute("token", token);*/



        return ResponseEntity.ok(ApiResponse.success("Login User Successfully", AuthResponse.fromUser(user,token,expiredAt,refreshtoken)));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@RequestHeader("Refresh-Token")String refreshToken) {
        AuthResponse response = authService.refresh(refreshToken);
        return ResponseEntity.ok(ApiResponse.success("Refresh Token Successfully", response));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(@RequestHeader("Refresh-Token")String refreshToken, HttpServletRequest request) {
        authService.logout(request,refreshToken);
        return ResponseEntity.ok(ApiResponse.success("Logout Successfully"));
    }

    @GetMapping("/blacklist")
    public ResponseEntity<ApiResponse> getBlacklist() {
        return ResponseEntity.ok(ApiResponse.success(tokenBlacklistService.getAllTokens()));
    }
}
