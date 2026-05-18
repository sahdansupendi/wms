package com.example.wms.service;


import com.example.wms.dto.user.UserRequest;
import com.example.wms.dto.user.UserUpdateRequest;
import com.example.wms.entity.Users;
import com.example.wms.enumz.UserRoleType;
import com.example.wms.exception.ResourceNotFoundException;
import com.example.wms.exception.ValidationException;
import com.example.wms.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public Users register(UserRequest request) {
        // validasi jika username sudah ada
        if (userRepository.findByUsername(request.username()).isPresent()){
            throw new ValidationException("Username already exists" , Map.of("username", "Username must be unique"));
        }

        // validasi jika email sudah ada
        if (userRepository.findByEmail(request.email()).isPresent()){
            throw new ValidationException("Email already exists" , Map.of("email", "Email must be unique"));
        }


        // Pengecekan roleid apakah ada di enum
        UserRoleType.fromRoleId(request.roleid())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Roleid " + request.roleid() + " tidak ditemukan"));

        //Generate Userid
        String maxUserId = userRepository.getMaxId();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Users user = Users.builder()
                .userid(maxUserId)
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(maxUserId + request.password()))
                .roleid(request.roleid())
                .status(1)
                .createuser(username)
                .build();

        return userRepository.save(user);
    }

    public Users updateUser(UserUpdateRequest request){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userid =(String) auth.getDetails();

        Users user = userRepository.findByUserid(userid)
                .orElseThrow(() -> new ResourceNotFoundException("Userid " + userid + " tidak ditemukan"));

        // validasi untuk username jika sudah digunakan
        if (request.username() != null){
            boolean usernameExist = userRepository.existsByUsernameAndUseridNot(request.username(), userid);
            if (usernameExist){
                throw new ValidationException("Username already exists" , Map.of("username", "Username must be unique"));
            }
        }

        // validasi untuk email jika sudah digunakan
        if (request.email() != null){
            boolean emailExist = userRepository.existsByEmailAndUseridNot(request.email(),userid);
            if (emailExist){
                throw new ValidationException("Email already exists" , Map.of("email", "Email must be unique"));
            }
        }

        if (request.roleid() != null){
            // Pengecekan roleid apakah ada di enum
            UserRoleType.fromRoleId(request.roleid())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Roleid " + request.roleid() + " tidak ditemukan"));
        }

        String username = auth.getName();

        user.setEmail(request.email());
        user.setUsername(request.username());
        user.setRoleid(request.roleid());
        user.setUpdateuser(username);

        return userRepository.save(user);
    }

    public List<Users> getAllUsers() {
        List<Users> users = userRepository.findAll(Sort.by("createdate").ascending());

        if (users.isEmpty()){
            throw new ResourceNotFoundException("Data user tidak ditemukan");
        }
        return users;
    }

    public Users getByUserId () {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userid =(String) auth.getDetails();

        return userRepository.findByUserid(userid)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User ID " + userid + " tidak ditemukan"));
    }

    public Users getByUsername () {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        return userRepository.findByUsername(username.trim().toLowerCase())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Username " + username + " tidak ditemukan"));
    }

    public Integer countUsers(){
        return userRepository.countBy();
    }
}
