package org.example.apitest.repository;
import org.example.apitest.model.DiscountCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DiscountCodeRepository extends PagingAndSortingRepository<DiscountCode, Long>, CrudRepository<DiscountCode, Long>  {
    Page<DiscountCode> findAll(Pageable pageable);

    Optional<DiscountCode> findByCode(String code);
}
