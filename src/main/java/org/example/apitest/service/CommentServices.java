package org.example.apitest.service;

import lombok.AllArgsConstructor;
import org.example.apitest.model.Comments;
import org.example.apitest.model.request.CommentRequest;
import org.example.apitest.repository.CommentsRepository;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CommentServices {
    private CommentsRepository commentRepository;
    public Comments createReview(CommentRequest commentRequest) {
        Comments comments = new Comments();
        comments.setNote(commentRequest.getReview());
        comments.setRating(commentRequest.getRating());
        comments.setBillId(commentRequest.getBillId());
        comments.setProductId(commentRequest.getProductId());
        comments.setUserId(commentRequest.getUserId());
        return commentRepository.save(comments);
    }
}
