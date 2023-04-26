package com.github.valhio.storeapi.controller;

import com.github.valhio.storeapi.exception.domain.ProductReviewNotFoundException;
import com.github.valhio.storeapi.exception.domain.UserNotFoundException;
import com.github.valhio.storeapi.model.ReviewLike;
import com.github.valhio.storeapi.model.UserPrincipal;
import com.github.valhio.storeapi.service.ReviewLikeService;
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
    public void addLike(@PathVariable Long productReviewId, @AuthenticationPrincipal UserPrincipal auth) throws UserNotFoundException, ProductReviewNotFoundException {
        reviewLikeService.addLike(productReviewId, auth.getEmail());
    }

    @DeleteMapping("/review/{productReviewId}/vote/remove")
    public void removeLike(@PathVariable Long productReviewId, @AuthenticationPrincipal UserPrincipal auth) {
        reviewLikeService.removeLike(productReviewId, auth.getEmail());
    }

    @GetMapping("/review/{productReviewId}/vote/count")
    public int getLikesCount(@PathVariable Long productReviewId) {
        return reviewLikeService.getLikesCount(productReviewId);
    }
}
