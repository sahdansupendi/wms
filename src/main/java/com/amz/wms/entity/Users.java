package com.amz.wms.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Users {

    @Id
    @NotBlank(message = "Userid cannot be blank")
    private String userid;

    @Email(message = "Email must be valid")
    @Pattern(
            regexp = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$"
            ,message = "Format email tidak valid")
    @Column(unique = true)
    private String email;
    @NotBlank(message = "Password cannot be blank")
    private String password;
    @NotBlank(message = "Username cannot be blank")
    private String username;
    @NotBlank(message = "Roleid cannot be blank")
    private String roleid;
    private Integer status;
    @Column(name = "createdate",updatable = false)
    @CreationTimestamp
    private LocalDateTime createdate;
    private String createuser;
    @Column(name = "updatedate")
    @UpdateTimestamp
    private LocalDateTime updatedate;
    private String updateuser;

    @PrePersist
    @PreUpdate
    public void LowerCase(){
        this.email = email.trim().toLowerCase();
        this.username = username.trim().toLowerCase();
    }
}
