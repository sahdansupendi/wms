package com.amz.wms.dto.productcategoreis;

import jakarta.validation.constraints.NotBlank;

public record ProductCategoriesRequest(
        @NotBlank(message = "Prodcut Categoreis cannot be blank") String pcid,
        @NotBlank(message = "Prodcut Categoreis Name cannot be blank") String name,
        String description

) {
    public ProductCategoriesRequest {
        if (pcid != null){
            pcid = pcid.trim().toLowerCase();
        }
    }
}
