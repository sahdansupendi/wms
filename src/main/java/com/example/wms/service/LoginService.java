package com.example.wms.service;

import com.example.wms.dto.login.LoginRequest;
import com.example.wms.entity.Users;
import com.example.wms.exception.AuthenticationFailedException;
import com.example.wms.exception.ResourceNotFoundException;
import com.example.wms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final UserRepository userRepository;

    public Users login(LoginRequest loginRequest) {
        Users user = userRepository.findByUsername(loginRequest.username())
                .orElseThrow(() -> new ResourceNotFoundException("Username " + loginRequest.username() + " tidak ditemukan"));
        String userid = user.getUserid();
        String pwd = DigestUtils.md5DigestAsHex((userid + loginRequest.password()).getBytes());

        if (!user.getPassword().equals(pwd)) {
            throw new AuthenticationFailedException("Password atau Username salah");
        }

        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new AuthenticationFailedException("User tidak aktiff");
        }

        return user;
    }

}
