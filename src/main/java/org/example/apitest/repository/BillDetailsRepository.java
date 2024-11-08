package org.example.apitest.repository;

import org.example.apitest.model.BillDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillDetailsRepository extends JpaRepository<BillDetails, Long> {
    List<BillDetails> findBillDetailsByBillId(Long billId);
    void deleteByBillId(Long billId);

    @Query("SELECT bd.product.name, SUM(bd.quantity) AS totalQuantity " +
            "FROM BillDetails bd " +
            "GROUP BY bd.product.id " +
            "ORDER BY totalQuantity DESC")
    List<Object[]> findTop10BestSellingProducts();
}
