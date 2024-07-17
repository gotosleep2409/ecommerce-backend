package org.example.apitest.repository;

import org.example.apitest.model.Bills;
import org.example.apitest.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BillsRepository extends PagingAndSortingRepository<Bills, Long>, CrudRepository<Bills, Long> {
    Page<Bills> findByUserId(Pageable pageable, Long id);
    /*@Query("SELECT e FROM Bills e WHERE e.paymentMethod LIKE :paymentMethod AND e.paymentStatus LIKE :paymentStatus AND e.status LIKE :status AND e.code LIKE :code")
    Page<Bills> getPageFilter(@Param("paymentMethod") String paymentMethod, @Param("paymentStatus") String paymentStatus, @Param("status") String status, @Param("code") String code, PageRequest paging);*/

    @Query("SELECT e FROM Bills e WHERE e.paymentMethod LIKE :paymentMethod AND e.paymentStatus LIKE :paymentStatus AND e.status LIKE :status AND e.code LIKE :code")
    Page<Bills> getPageFilter(
            @Param("paymentMethod") String paymentMethod,
            @Param("paymentStatus") String paymentStatus,
            @Param("status") String status,
            @Param("code") String code,
            Pageable pageable
    );

}
