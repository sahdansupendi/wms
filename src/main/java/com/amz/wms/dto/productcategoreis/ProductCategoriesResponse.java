package com.amz.wms.dto.productcategoreis;

import com.amz.wms.dto.user.UserResponse;
import com.amz.wms.entity.ProductCategories;
import com.amz.wms.entity.Users;
import com.amz.wms.enumz.UserRoleType;

public record ProductCategoriesResponse(
        String pcid,
        String name,
        String description
) {
    public static ProductCategoriesResponse fromUser(ProductCategories productCategories){

        return new ProductCategoriesResponse(
                productCategories.getPcid(),
                productCategories.getName(),
                productCategories.getDescription()
        );
    }
}
