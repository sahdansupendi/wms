package com.example.wms.controller.user;


import com.example.wms.dto.ApiResponse;
import com.example.wms.dto.user.UserRequest;
import com.example.wms.dto.user.UserResponse;
import com.example.wms.dto.user.UserUpdateRequest;
import com.example.wms.entity.Users;
import com.example.wms.exception.ResourceNotFoundException;
import com.example.wms.repository.UserRepository;
import com.example.wms.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    final UserService userService;


    @PostMapping("/registeruser")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponse> registerUser(@Valid @RequestBody UserRequest request) throws ResourceNotFoundException {
        Users users = userService.register(request);
        return ResponseEntity.ok(ApiResponse.success("Register User Successfully",UserResponse.fromUser(users)));
    }

    @PutMapping("/updateuser")
    public ResponseEntity<ApiResponse<UserResponse>> userUpdate(
            @Valid  @RequestBody UserUpdateRequest request, HttpServletRequest userid) {
        Users users = userService.updateUser(request);
        return ResponseEntity.ok(ApiResponse.success("Update User Successfully",UserResponse.fromUser(users)));

    }

    @GetMapping("/getalluser")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        List<Users> users = userService.getAllUsers();

        List<UserResponse> responses = users.stream()
                .map(UserResponse::fromUser)
                .toList();

        return ResponseEntity.ok(ApiResponse.success(responses));

    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById() {
        Users users = userService.getByUserId();
        return ResponseEntity.ok(ApiResponse.success(UserResponse.fromUser(users)));
    }


    @GetMapping("/getbyusername")
    public ResponseEntity<ApiResponse<UserResponse>> getUserByUsername() {
        Users users = userService.getByUsername();
        return ResponseEntity.ok(ApiResponse.success(UserResponse.fromUser(users)));
    }

    @GetMapping("/countusers")
    public ResponseEntity<ApiResponse<Integer>> getCountUsers() {
        Integer userCount = userService.countUsers();
        return ResponseEntity.ok(ApiResponse.success(userCount));
    }
}
