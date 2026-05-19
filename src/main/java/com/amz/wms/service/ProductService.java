package com.amz.wms.service;

import com.amz.wms.dto.product.ProductRequest;
import com.amz.wms.entity.ProductCategories;
import com.amz.wms.entity.Products;
import com.amz.wms.entity.Users;
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
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductCategoriesRepository productCategoriesRepository;

    public Products registerProduct(ProductRequest request){
        // validasi jika product name sudah ada
        if (productRepository.findByProdName(request.prodName()).isPresent()){
            throw new ValidationException("Product Name already exists" , Map.of("prodName", "Product Name must be unique"));
        }

        //validasi jika pcid tidak ada
        if (!productCategoriesRepository.findByPcid(request.pcid()).isPresent()) {
            throw new ValidationException("Product Categories Name not found" , Map.of("pcid", "Product Categories not found"));
        }

        //Generate prodId
        String maxProdId = productRepository.getMaxId();
        //Generate Category SKU
        String sku = request.pcid()+maxProdId;

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Products product = Products.builder()
                .prodid(maxProdId)
                .pcid(request.pcid())
                .sku(sku)
                .prodName(request.prodName())
                .unitOfMeasure(request.unitOfMeasure())
                .weight(request.weight())
                .status(1)
                .createuser(username)
                .build();

        return productRepository.save(product);
    }

    public Products updateProduct(String prodid, ProductRequest request){
        Products products = productRepository.findByProdid(prodid.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Product Id " + prodid + " tidak ditemukan"));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // validasi untuk username jika sudah digunakan
        if (request.prodName() != null){
            boolean prodNameExist = productRepository.existsByProdNameAndProdidNot(request.prodName(), prodid);
            if (prodNameExist){
                throw new ValidationException("Product Name already exists" , Map.of("prodName", "Product Name must be unique"));
            }
        }

        String username = auth.getName();

        products.setPcid(request.pcid());
        products.setSku(request.pcid()+prodid);
        products.setProdName(request.prodName());
        products.setUnitOfMeasure(request.unitOfMeasure());
        products.setWeight(request.weight());
        products.setUpdateuser(username);

        return productRepository.save(products);

    }

    public Products getByProdName (String prodName) {
        return productRepository.findByProdName(prodName.trim().toLowerCase())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product Name " + prodName + " tidak ditemukan"));
    }

    public Products getByProdid (String prodid) {
        return productRepository.findByProdid(prodid.trim())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product Id " + prodid + " tidak ditemukan"));
    }

    public List<Products> getAllProducts() {
        List<Products> products = productRepository.findAll(Sort.by("createdate").ascending());

        if (products.isEmpty()){
            throw new ResourceNotFoundException("Data user tidak ditemukan");
        }
        return products;
    }

    public Products deleteByProdid (String prodid) {
        Products products = productRepository.findByProdid(prodid)
                .orElseThrow(() -> new ResourceNotFoundException("Product Id " + prodid + " tidak ditemukan"));

        productRepository.delete(products);

        return products;
    }
}
