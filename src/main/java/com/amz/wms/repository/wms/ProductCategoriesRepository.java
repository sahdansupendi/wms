package com.amz.wms.repository.wms;

import com.amz.wms.entity.ProductCategories;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductCategoriesRepository extends JpaRepository<ProductCategories, Long> {
    Optional<ProductCategories> findByPcid(String pcid);

    Optional<ProductCategories> findByName(String name);

    boolean existsByPcid(String pcid);
}
