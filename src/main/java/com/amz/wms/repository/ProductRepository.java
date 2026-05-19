package com.amz.wms.repository;

import com.amz.wms.entity.Products;
import com.amz.wms.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Products, Long> {
    Optional<Products> findByProdName(String prodName);

    @Query("SELECT coalesce(lpad(cast(max(cast(prodid as integer)) + 1 as string ),5,'0'),'00001') as maxid FROM Products ")
    String getMaxId();

    Optional<Products> findByProdid(String prodid);

    boolean existsByProdNameAndProdidNot(String prodName, String prodid);

    Integer countBy();
}
