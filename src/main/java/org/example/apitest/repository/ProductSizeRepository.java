package org.example.apitest.repository;

import org.example.apitest.model.ProductSize;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductSizeRepository extends JpaRepository<ProductSize, Long> {
    ProductSize findByProductIdAndSizeName(Long productId, String sizeName);

    @Query("SELECT ps.product.name, SUM(ps.quantity) AS totalQuantity " +
            "FROM ProductSize ps " +
            "GROUP BY ps.product.id " +
            "ORDER BY totalQuantity DESC")
    List<Object[]> findTop10ProductsByStock(Pageable pageable);
}
