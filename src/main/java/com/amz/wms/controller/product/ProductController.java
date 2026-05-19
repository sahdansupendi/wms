package com.amz.wms.controller.product;

import com.amz.wms.dto.ApiResponse;
import com.amz.wms.dto.product.ProductRequest;
import com.amz.wms.dto.product.ProductResponse;
import com.amz.wms.entity.Products;
import com.amz.wms.exception.ResourceNotFoundException;
import com.amz.wms.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    final ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponse<ProductResponse>> registerProduct(@Valid @RequestBody ProductRequest request) throws ResourceNotFoundException {
        Products products = productService.registerProduct(request);
        return ResponseEntity.ok(ApiResponse.success(ProductResponse.fromProduct(products)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<ProductResponse>> getProductByUsername(@RequestParam("prodName") String prodName) {
        Products products = productService.getByProdName(prodName);
        return ResponseEntity.ok(ApiResponse.success(ProductResponse.fromProduct(products)));
    }
}
