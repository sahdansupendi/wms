package com.amz.wms.service;


import com.amz.wms.dto.productcategoreis.ProductCategoriesRequest;
import com.amz.wms.entity.ProductCategories;
import com.amz.wms.exception.ResourceNotFoundException;
import com.amz.wms.exception.ValidationException;
import com.amz.wms.repository.ProductCategoriesRepository;
import com.amz.wms.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductCategoriesService {
    private final ProductCategoriesRepository productCategoriesRepository;
    private final ProductRepository productRepository;

    public ProductCategories registerProdCat(ProductCategoriesRequest request) {
        //validasi jika pcid sudah ada
        if (productCategoriesRepository.existsByPcid(request.pcid())) {
            throw new ValidationException("Product Categories Name already exists" , Map.of("pcid", "Product Categories Name must be unique"));
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        ProductCategories productCategories = ProductCategories.builder()
                .pcid(request.pcid())
                .name(request.name())
                .description(request.description())
                .createuser(username)
                .build();

        return productCategoriesRepository.save(productCategories);
    }

    public ProductCategories updateProdCat(ProductCategoriesRequest request) {
        // Cek pcid
        ProductCategories productCategories = productCategoriesRepository.findByPcid(request.pcid())
                .orElseThrow(() -> new ResourceNotFoundException("Product Categories " + request.pcid() + " tidak ditemukan"));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        productCategories.setPcid(request.pcid());
        productCategories.setName(request.name());
        productCategories.setDescription(request.description());
        productCategories.setUpdateuser(username);

        return productCategoriesRepository.save(productCategories);

    }

    public ProductCategories getByPcid (String pcid) {
        return productCategoriesRepository.findByPcid(pcid.trim())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product Categories " + pcid + " tidak ditemukan"));
    }

    public List<ProductCategories> getAllPcid() {
        List<ProductCategories> productCategories = productCategoriesRepository.findAll(Sort.by("createdate").ascending());

        if (productCategories.isEmpty()){
            throw new ResourceNotFoundException("Data user tidak ditemukan");
        }
        return productCategories;
    }

    public ProductCategories deleteByPcid (String pcid) {
        ProductCategories productCategories = productCategoriesRepository.findByPcid(pcid)
                .orElseThrow(() -> new ResourceNotFoundException("Product Categories " + pcid + " tidak ditemukan"));

        productCategoriesRepository.delete(productCategories);

        return productCategories;
    }
}
