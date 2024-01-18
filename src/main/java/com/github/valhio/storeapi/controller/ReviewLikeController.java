package com.github.valhio.storeapi.controller;

import com.github.valhio.storeapi.exception.domain.ProductReviewNotFoundException;
import com.github.valhio.storeapi.exception.domain.ReviewLikeNotFoundException;
import com.github.valhio.storeapi.exception.domain.UserNotFoundException;
import com.github.valhio.storeapi.model.ReviewLike;
import com.github.valhio.storeapi.model.UserPrincipal;
import com.github.valhio.storeapi.service.ReviewLikeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/review-like")
public class ReviewLikeController {

    private final ReviewLikeService reviewLikeService;

    public ReviewLikeController(ReviewLikeService reviewLikeService) {
        this.reviewLikeService = reviewLikeService;
    }

    @PostMapping("/review/{productReviewId}/vote/add")
    public ResponseEntity<List<ReviewLike>> addLike(@PathVariable String productReviewId, @AuthenticationPrincipal UserPrincipal auth) throws UserNotFoundException, ProductReviewNotFoundException {
        try {
            List<ReviewLike> reviewLikes = reviewLikeService.addLike(productReviewId, auth.getUserId());
            return ResponseEntity.ok(reviewLikes);
        } catch (UserNotFoundException | ProductReviewNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @DeleteMapping("/review/{productReviewId}/vote/remove")
    public ResponseEntity<List<ReviewLike>> removeLike(@PathVariable String productReviewId, @AuthenticationPrincipal UserPrincipal auth) {
        try {
            List<ReviewLike> updatedLikes = reviewLikeService.removeLike(productReviewId, auth.getUserId());
            return ResponseEntity.ok(updatedLikes);
        } catch (ReviewLikeNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (ProductReviewNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }


    @GetMapping("/review/{productReviewId}/vote/count")
    public int getLikesCount(@PathVariable String productReviewId) {
        return reviewLikeService.getLikesCount(productReviewId);
    }

    @GetMapping("/review/{productReviewId}/vote/has-liked")
    public boolean hasLiked(@PathVariable String productReviewId, @AuthenticationPrincipal UserPrincipal auth) {
        final String id = auth.getUserId();
        return reviewLikeService.hasLiked(productReviewId, id);
    }

    @GetMapping("/review/{productReviewId}/all")
    public ResponseEntity<List<ReviewLike>> getAllLikes(@PathVariable String productReviewId) {
        return ResponseEntity.ok(reviewLikeService.findAllByReviewId(productReviewId));
    }
}