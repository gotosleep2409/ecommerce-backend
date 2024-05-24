package org.example.apitest.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.apitest.exception.ApiException;
import org.example.apitest.helper.ResponseBuilder;
import org.example.apitest.model.Comments;
import org.example.apitest.model.request.CommentRequest;
import org.example.apitest.service.CommentServices;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CommentController {
    private final CommentServices commentServices;
    @PostMapping("/create")
    public ResponseEntity<ResponseBuilder<Comments>> createComment(@Valid @RequestBody CommentRequest commentsRequest) throws ApiException {
        Comments comments = commentServices.createReview(commentsRequest);
        return ResponseEntity.ok(ResponseBuilder.buildResponse(comments, "Create Comments successfully", HttpStatus.OK));
    }
}
