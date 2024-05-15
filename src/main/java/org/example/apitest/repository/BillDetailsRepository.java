package org.example.apitest.repository;

import org.example.apitest.model.BillDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillDetailsRepository extends JpaRepository<BillDetails, Long> {
    List<BillDetails> findBillDetailsByBillId(Long billId);
    void deleteByBillId(Long billId);
}
