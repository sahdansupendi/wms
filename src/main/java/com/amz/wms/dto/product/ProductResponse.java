package com.amz.wms.dto.product;

import com.amz.wms.entity.Products;

import java.math.BigDecimal;

public record ProductResponse(
        String prodid,
        String pcid,
        String sku,
        String prodName,
        String unitOfMeasure,
        BigDecimal weight
) {
    public static ProductResponse fromProduct(Products products){

        return new ProductResponse(
                products.getProdid(),
                products.getPcid(),
                products.getSku(),
                products.getProdName(),
                products.getUnitOfMeasure(),
                products.getWeight()
        );
    }
}
