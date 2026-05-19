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

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    final ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponse<ProductResponse>> registerProduct(@Valid @RequestBody ProductRequest request) throws ResourceNotFoundException {
        Products products = productService.registerProduct(request);
        return ResponseEntity.ok(ApiResponse.success(ProductResponse.fromUser(products)));
    }

    @PutMapping("/{prodid}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(@PathVariable("prodid") String prodid, @Valid @RequestBody ProductRequest request) throws ResourceNotFoundException {
        Products products = productService.updateProduct(prodid, request);
        return ResponseEntity.ok(ApiResponse.success(ProductResponse.fromUser(products)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<ProductResponse>> getProductByUsername(@RequestParam("prodName") String prodName) {
        Products products = productService.getByProdName(prodName);
        return ResponseEntity.ok(ApiResponse.success(ProductResponse.fromUser(products)));
    }

    @GetMapping("/{prodid}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductByProid(@PathVariable String prodid) {
        Products products = productService.getByProdid(prodid);
        return ResponseEntity.ok(ApiResponse.success(ProductResponse.fromUser(products)));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getAllProducts() {
        List<Products> products = productService.getAllProducts();

        List<ProductResponse> responses = products.stream()
                .map(ProductResponse::fromUser)
                .toList();

        return ResponseEntity.ok(ApiResponse.success(responses));

    }

    @DeleteMapping("/{prodid}")
    public ResponseEntity<ApiResponse> deleteProdId(@PathVariable String prodid) {
        productService.deleteByProdid(prodid);
        return ResponseEntity.ok(ApiResponse.success("Delete Product success"));
    }
}
