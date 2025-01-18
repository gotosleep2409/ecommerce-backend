package org.example.apitest.repository;

import org.example.apitest.model.FAQ;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FAQRepository extends JpaRepository<FAQ, Long> {
    FAQ findByQuestion(String question);

    Page<FAQ> findAll(Pageable pageable);
}
