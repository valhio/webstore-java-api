package com.github.valhio.storeapi.service;

import com.github.valhio.storeapi.exception.domain.ProductNotFoundException;
import com.github.valhio.storeapi.exception.domain.ProductReviewNotFoundException;
import com.github.valhio.storeapi.exception.domain.UserNotFoundException;
import com.github.valhio.storeapi.model.ProductReview;
import com.github.valhio.storeapi.model.ReviewLike;
import com.github.valhio.storeapi.request.ProductReviewRequest;

import java.util.List;

public interface ProductReviewService {

    ProductReview addProductReview(ProductReviewRequest productReviewRequest, String userId) throws UserNotFoundException, ProductNotFoundException;

    ProductReview updateProductReview(String productId, String reviewText, int rating) throws ProductReviewNotFoundException;

    ProductReview update(ProductReview productReview);

    void deleteProductReview(String productReviewId);

    List<ProductReview> getAllProductReviewsForProduct(String productId) throws ProductReviewNotFoundException;

    ProductReview findById(String productReviewId) throws ProductReviewNotFoundException;

    List<ReviewLike> likeOrDislikeReview(String reviewId, String userId) throws UserNotFoundException, ProductReviewNotFoundException;
}