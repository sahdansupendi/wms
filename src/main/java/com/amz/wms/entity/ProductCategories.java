package com.amz.wms.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "product_categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductCategories {

    @Id
    @NotBlank(message = "Product Categories cannot be blank")
    private String pcid;

    @NotBlank(message = "Product Categoreis Name cannot be blank")
    private String name;

    private String description;

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
        this.pcid = pcid.trim().toLowerCase();
    }
}
