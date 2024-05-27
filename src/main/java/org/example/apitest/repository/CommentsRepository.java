package org.example.apitest.repository;

import org.example.apitest.model.Comments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentsRepository extends JpaRepository<Comments, Long> {
    Comments findByProductIdAndBillIdAndUserId(Long productId, Long billId, Long userId);

    List<Comments> findCommentByProductId(Long productId);
}
