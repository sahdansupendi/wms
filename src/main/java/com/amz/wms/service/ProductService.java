package com.amz.wms.service;

import com.amz.wms.dto.product.ProductRequest;
import com.amz.wms.entity.Products;
import com.amz.wms.exception.ResourceNotFoundException;
import com.amz.wms.exception.ValidationException;
import com.amz.wms.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public Products registerProduct(ProductRequest request){
        // validasi jika product name sudah ada
        if (productRepository.findByProdName(request.prodName()).isPresent()){
            throw new ValidationException("Product Name already exists" , Map.of("prodName", "Product Name must be unique"));
        }

        //Generate prodId
        String maxProdId = productRepository.getMaxId();
        //Generate Category produk
        String pcid = request.pcid()+maxProdId;
        //Generate Category SKU
        String sku = "SKU"+maxProdId;

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Products product = Products.builder()
                .prodid(maxProdId)
                .pcid(pcid)
                .sku(request.sku())
                .prodName(request.prodName())
                .unitOfMeasure(request.unitOfMeasure())
                .weight(request.weight())
                .status(1)
                .createuser(username)
                .build();

        return productRepository.save(product);
    }

    public Products getByProdName (String prodName) {
        return productRepository.findByProdName(prodName.trim().toLowerCase())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product Name " + prodName + " tidak ditemukan"));
    }
}
