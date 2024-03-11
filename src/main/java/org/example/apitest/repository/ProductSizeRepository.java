package org.example.apitest.repository;

import org.example.apitest.model.ProductSize;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductSizeRepository extends JpaRepository<ProductSize, Long> {
    ProductSize findByProductIdAndSizeName(Long productId, String sizeName);
}
