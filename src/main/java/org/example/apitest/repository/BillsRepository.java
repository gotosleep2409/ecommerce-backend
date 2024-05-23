package org.example.apitest.repository;

import org.example.apitest.model.Bills;
import org.example.apitest.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BillsRepository extends PagingAndSortingRepository<Bills, Long>, CrudRepository<Bills, Long> {
    Page<Bills> findByUserId(Pageable pageable, Long id);
}
