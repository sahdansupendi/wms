package com.amz.wms.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Products {

    @Id
    @NotBlank(message = "Prodid cannot be blank")
    private String prodid;

    @NotBlank(message = "Product Categories cannot be blank")
    private String pcid;

    @NotBlank(message = "SKU cannot be blank")
    private String sku;

    @NotBlank(message = "Product Name cannot be blank")
    @Column(name = "prod_name")
    private String prodName;

    @NotBlank(message = "Unit Of Measure cannot be blank")
    @Column(name = "unit_of_measure")
    private String unitOfMeasure;

    @NotNull(message = "Weight cannot be null")
    private BigDecimal weight;

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
        this.sku = sku.trim().toLowerCase();
        this.prodName = prodName.trim().toLowerCase();
        this.unitOfMeasure = unitOfMeasure.trim().toLowerCase();
    }
}
