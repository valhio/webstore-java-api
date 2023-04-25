package com.github.valhio.storeapi.service;

import com.github.valhio.storeapi.exception.domain.ProductNotFoundException;
import com.github.valhio.storeapi.exception.domain.ProductReviewNotFoundException;
import com.github.valhio.storeapi.exception.domain.UserNotFoundException;
import com.github.valhio.storeapi.model.ProductReview;
import com.github.valhio.storeapi.request.ProductReviewRequest;

import java.util.List;

public interface ProductReviewService {

    ProductReview addProductReview(ProductReviewRequest productReviewRequest) throws UserNotFoundException, ProductNotFoundException;

    ProductReview updateProductReview(Long productId, String reviewText, int rating) throws ProductReviewNotFoundException;

    void deleteProductReview(Long productReviewId);

    List<ProductReview> getAllProductReviewsForProduct(Long productId) throws ProductReviewNotFoundException;


}
