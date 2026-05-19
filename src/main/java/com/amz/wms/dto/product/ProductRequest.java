package com.amz.wms.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ProductRequest(
        @NotBlank(message = "Prodcut Categoreis cannot be blank") String pcid,
        @NotBlank(message = "Product Name cannot be blank") String prodName,
        @NotBlank(message = "Unit Of Measure cannot be blank") String unitOfMeasure,
        @NotNull(message = "Weight cannot be null")BigDecimal weight
        ) {
        public ProductRequest {
                if (pcid != null){
                        pcid = pcid.trim().toLowerCase();
                }
        }
}
