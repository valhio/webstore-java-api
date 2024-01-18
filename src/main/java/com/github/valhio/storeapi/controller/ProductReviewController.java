package com.github.valhio.storeapi.controller;

import com.github.valhio.storeapi.exception.domain.ProductNotFoundException;
import com.github.valhio.storeapi.exception.domain.ProductReviewNotFoundException;
import com.github.valhio.storeapi.exception.domain.UserNotFoundException;
import com.github.valhio.storeapi.model.ProductReview;
import com.github.valhio.storeapi.model.ReviewLike;
import com.github.valhio.storeapi.model.UserPrincipal;
import com.github.valhio.storeapi.request.ProductReviewRequest;
import com.github.valhio.storeapi.service.ProductReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/product-review")
public class ProductReviewController {

    private final ProductReviewService productReviewService;

    public ProductReviewController(ProductReviewService productReviewService) {
        this.productReviewService = productReviewService;
    }

    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<ProductReview> addProductReview(
            @RequestBody ProductReviewRequest productReviewRequest,
            @AuthenticationPrincipal UserPrincipal auth
    ) throws UserNotFoundException, ProductNotFoundException {
        return ResponseEntity.ok(productReviewService.addProductReview(productReviewRequest, auth.getUserId()));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ProductReview>> getAllProductReviewsForProduct(@RequestParam String productId) throws ProductReviewNotFoundException {
        return ResponseEntity.ok(productReviewService.getAllProductReviewsForProduct(productId));
    }


    @PostMapping("/{reviewId}/like")
    public ResponseEntity<List<ReviewLike>> likeOrDislikeReview(
            @PathVariable String reviewId,
            @AuthenticationPrincipal UserPrincipal auth) {
        System.out.println("reviewId = " + reviewId);
        try {
            List<ReviewLike> updatedLikes = productReviewService.likeOrDislikeReview(reviewId, auth.getUserId());
            return new ResponseEntity<>(updatedLikes, HttpStatus.OK);
        } catch (UserNotFoundException | ProductReviewNotFoundException e) {
            // Handle exceptions appropriately, e.g., return a 404 response for ProductReviewNotFoundException
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}