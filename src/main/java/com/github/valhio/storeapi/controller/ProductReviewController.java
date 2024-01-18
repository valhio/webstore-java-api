package com.github.valhio.storeapi.controller;

import com.github.valhio.storeapi.exception.domain.ProductNotFoundException;
import com.github.valhio.storeapi.exception.domain.ProductReviewNotFoundException;
import com.github.valhio.storeapi.exception.domain.UserNotFoundException;
import com.github.valhio.storeapi.model.ProductReview;
import com.github.valhio.storeapi.model.UserPrincipal;
import com.github.valhio.storeapi.request.ProductReviewRequest;
import com.github.valhio.storeapi.service.ProductReviewService;
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
}