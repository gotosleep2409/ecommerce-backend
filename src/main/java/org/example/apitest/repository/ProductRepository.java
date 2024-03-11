package org.example.apitest.repository;

import org.example.apitest.model.Category;
import org.example.apitest.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ProductRepository extends PagingAndSortingRepository<Product, Long>, CrudRepository<Product, Long> {
    Page<Product> findAllByNameContainsOrDescriptionContains(String name, String description, Pageable pageable);

    Page<Product> findAll(Pageable pageable);

    Page<Product> findByCategories(Optional<Category> category, Pageable pageable);

    @Query("SELECT p FROM Product p JOIN FETCH p.productSizes ps WHERE p.id = :productId")
    Product findProductWithProductSizesById(@Param("productId") Long productId);


}
