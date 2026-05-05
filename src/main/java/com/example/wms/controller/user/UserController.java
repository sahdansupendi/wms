package com.example.wms.controller.user;


import com.example.wms.dto.ApiResponse;
import com.example.wms.dto.user.UserRequest;
import com.example.wms.dto.user.UserResponse;
import com.example.wms.dto.user.UserUpdateRequest;
import com.example.wms.entity.Users;
import com.example.wms.exception.ResourceNotFoundException;
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
    public ResponseEntity<ApiResponse> registerUser(@RequestBody @Valid UserRequest request) throws ResourceNotFoundException {
        Users users = userService.register(request);
        return ResponseEntity.ok(ApiResponse.success("Register User Successfully",UserResponse.fromUser(users)));
    }

    @PutMapping("/updateuser")
    public ResponseEntity<ApiResponse<UserResponse>> userUpdate(
            @RequestParam("userid") String userid, @RequestBody @Valid UserUpdateRequest request) {
        Users users = userService.updateUser(request,userid);
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
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(HttpServletRequest request) {
        String userid = request.getAttribute("userid").toString();
        Users users = userService.getByUserId(userid);
        return ResponseEntity.ok(ApiResponse.success(UserResponse.fromUser(users)));
    }


    @GetMapping("/getbyusername")
    public ResponseEntity<ApiResponse<UserResponse>> getUserByUsername(@RequestParam("username") String username) {
        Users users = userService.getByUsername(username);
        return ResponseEntity.ok(ApiResponse.success(UserResponse.fromUser(users)));
    }
}
