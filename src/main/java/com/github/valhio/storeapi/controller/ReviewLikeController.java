package com.github.valhio.storeapi.controller;

import com.github.valhio.storeapi.exception.domain.ProductReviewNotFoundException;
import com.github.valhio.storeapi.exception.domain.UserNotFoundException;
import com.github.valhio.storeapi.service.ReviewLikeService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/review-like")
public class ReviewLikeController {

    private final ReviewLikeService reviewLikeService;

    public ReviewLikeController(ReviewLikeService reviewLikeService) {
        this.reviewLikeService = reviewLikeService;
    }

    @PostMapping("/review/{productReviewId}/vote/add")
    public void addLike(@PathVariable Long productReviewId) throws UserNotFoundException, ProductReviewNotFoundException {
        reviewLikeService.addLike(productReviewId, "a@a.com");
    }

    @GetMapping("/review/{productReviewId}/vote/count")
    public int getLikesCount(@PathVariable Long productReviewId) {
        return reviewLikeService.getLikesCount(productReviewId);
    }
}
