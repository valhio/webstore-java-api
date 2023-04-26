package com.github.valhio.storeapi.controller;

import com.github.valhio.storeapi.exception.domain.ProductReviewNotFoundException;
import com.github.valhio.storeapi.exception.domain.UserNotFoundException;
import com.github.valhio.storeapi.model.ReviewComment;
import com.github.valhio.storeapi.model.UserPrincipal;
import com.github.valhio.storeapi.request.ReviewCommentRequest;
import com.github.valhio.storeapi.service.ReviewCommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/review-comment")
public class ReviewCommentController {

    private final ReviewCommentService reviewCommentService;

    public ReviewCommentController(ReviewCommentService reviewCommentService) {
        this.reviewCommentService = reviewCommentService;
    }

    @PostMapping("/review/{productReviewId}/add")
    public ResponseEntity<ReviewComment> addComment(@PathVariable Long productReviewId, @RequestBody ReviewCommentRequest reviewCommentRequest, @AuthenticationPrincipal UserPrincipal auth) throws UserNotFoundException, ProductReviewNotFoundException {
        return ResponseEntity.ok(reviewCommentService.addComment(productReviewId, auth.getEmail(), reviewCommentRequest.getComment()));
    }

    @GetMapping("/review/{productReviewId}/all")
    public ResponseEntity<?> getAllCommentsForReview(@PathVariable Long productReviewId) {
        return ResponseEntity.ok(reviewCommentService.findAllByReviewId(productReviewId));
    }

    @GetMapping("/review/{productReviewId}/has-commented")
    public ResponseEntity<Boolean> hasCommented(@PathVariable Long productReviewId, @AuthenticationPrincipal UserPrincipal auth) {
        return ResponseEntity.ok(reviewCommentService.hasCommented(productReviewId, auth.getEmail()));
    }

}
