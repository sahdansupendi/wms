package com.amz.wms.controller.user;


import com.amz.wms.dto.ApiResponse;
import com.amz.wms.dto.user.UserRequest;
import com.amz.wms.dto.user.UserResponse;
import com.amz.wms.dto.user.UserUpdateRequest;
import com.amz.wms.entity.Users;
import com.amz.wms.exception.ResourceNotFoundException;
import com.amz.wms.service.UserService;
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


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponse> registerUser(@Valid @RequestBody UserRequest request) throws ResourceNotFoundException {
        Users users = userService.register(request);
        return ResponseEntity.ok(ApiResponse.success("Register User Successfully",UserResponse.fromUser(users)));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<UserResponse>> userUpdate(
            @Valid  @RequestBody UserUpdateRequest request, HttpServletRequest userid) {
        Users users = userService.updateUser(request);
        return ResponseEntity.ok(ApiResponse.success("Update User Successfully",UserResponse.fromUser(users)));

    }

    @GetMapping("/all")
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


    @GetMapping
    public ResponseEntity<ApiResponse<UserResponse>> getUserByUsername(@RequestParam("username") String username) {
        Users users = userService.getByUsername(username);
        return ResponseEntity.ok(ApiResponse.success(UserResponse.fromUser(users)));
    }

    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Integer>> getCountUsers() {
        Integer userCount = userService.countUsers();
        return ResponseEntity.ok(ApiResponse.success(userCount));
    }
}
