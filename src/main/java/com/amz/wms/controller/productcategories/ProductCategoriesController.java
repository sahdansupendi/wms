package com.amz.wms.controller.productcategories;

import com.amz.wms.dto.ApiResponse;
import com.amz.wms.dto.productcategoreis.ProductCategoriesRequest;
import com.amz.wms.dto.productcategoreis.ProductCategoriesResponse;
import com.amz.wms.entity.ProductCategories;
import com.amz.wms.exception.ResourceNotFoundException;
import com.amz.wms.service.ProductCategoriesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products-categories")
@RequiredArgsConstructor
public class ProductCategoriesController {
    private final ProductCategoriesService productCategoriesService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponse<ProductCategoriesResponse>> registerProdCat(@Valid @RequestBody ProductCategoriesRequest request) throws ResourceNotFoundException {
        ProductCategories productCategories = productCategoriesService.registerProdCat(request);
        return ResponseEntity.ok(ApiResponse.success(ProductCategoriesResponse.fromUser(productCategories)));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<ProductCategoriesResponse>> updateProdCat( @Valid @RequestBody ProductCategoriesRequest request) throws ResourceNotFoundException {
        ProductCategories productCategories = productCategoriesService.updateProdCat(request);
        return ResponseEntity.ok(ApiResponse.success(ProductCategoriesResponse.fromUser(productCategories)));
    }

    @GetMapping("/{pcid}")
    public ResponseEntity<ApiResponse<ProductCategoriesResponse>> getProdCatById(@PathVariable String pcid) {
        ProductCategories productCategories = productCategoriesService.getByPcid(pcid);
        return ResponseEntity.ok(ApiResponse.success(ProductCategoriesResponse.fromUser(productCategories)));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<ProductCategoriesResponse>>> getAllProdCat() {
        List<ProductCategories> productCategories = productCategoriesService.getAllPcid();

        List<ProductCategoriesResponse> responses = productCategories.stream()
                .map(ProductCategoriesResponse::fromUser)
                .toList();

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @DeleteMapping("/{pcid}")
    public ResponseEntity<ApiResponse> deleteProdCatById(@PathVariable String pcid) {
        productCategoriesService.deleteByPcid(pcid);
        return ResponseEntity.ok(ApiResponse.success("Delete Product Categories success"));
    }
}
