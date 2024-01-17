package com.github.valhio.storeapi.service;

import com.github.valhio.storeapi.exception.domain.ProductNotFoundException;
import com.github.valhio.storeapi.exception.domain.ProductReviewNotFoundException;
import com.github.valhio.storeapi.exception.domain.UserNotFoundException;
import com.github.valhio.storeapi.model.ProductReview;
import com.github.valhio.storeapi.request.ProductReviewRequest;

import java.util.List;

public interface ProductReviewService {

    ProductReview addProductReview(ProductReviewRequest productReviewRequest) throws UserNotFoundException, ProductNotFoundException;

    ProductReview updateProductReview(String productId, String reviewText, int rating) throws ProductReviewNotFoundException;

    void deleteProductReview(String productReviewId);

    List<ProductReview> getAllProductReviewsForProduct(String productId) throws ProductReviewNotFoundException;


    ProductReview findById(String productReviewId) throws ProductReviewNotFoundException;
}