package com.example.wms.controller.user.login;


import com.example.wms.dto.ApiResponse;
import com.example.wms.dto.login.LoginRequest;
import com.example.wms.dto.login.LoginResponse;
import com.example.wms.entity.Users;
import com.example.wms.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class LoginController {

    final LoginService loginService;

    @PutMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest loginRequest, HttpSession session) {
        Users user = loginService.login(loginRequest);

        // simpan ke session
        session.setAttribute("username", user.getUsername());
        session.setAttribute("roleid", user.getRoleid());


        return ResponseEntity.ok(ApiResponse.success("Login User Successfully",LoginResponse.fromUser(user)));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(ApiResponse.success("Logout Successfully"));
    }
}
