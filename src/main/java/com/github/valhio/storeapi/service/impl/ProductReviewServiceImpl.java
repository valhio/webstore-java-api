package com.github.valhio.storeapi.service.impl;

import com.github.valhio.storeapi.exception.domain.ProductNotFoundException;
import com.github.valhio.storeapi.exception.domain.ProductReviewNotFoundException;
import com.github.valhio.storeapi.exception.domain.UserNotFoundException;
import com.github.valhio.storeapi.model.ProductReview;
import com.github.valhio.storeapi.repository.ProductReviewRepository;
import com.github.valhio.storeapi.request.ProductReviewRequest;
import com.github.valhio.storeapi.service.ProductReviewService;
import com.github.valhio.storeapi.service.ProductService;
import com.github.valhio.storeapi.service.UserService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ProductReviewServiceImpl implements ProductReviewService {

    private final ProductReviewRepository productReviewRepository;
    private final UserService userService;
    private final ProductService productService;


    public ProductReviewServiceImpl(ProductReviewRepository productReviewRepository, UserService userService, ProductService productService) {
        this.productReviewRepository = productReviewRepository;
        this.userService = userService;
        this.productService = productService;
    }

    @Override
    public ProductReview addProductReview(ProductReviewRequest productReviewRequest) throws UserNotFoundException, ProductNotFoundException {
        ProductReview productReview = new ProductReview();
        productReview.setRating(productReviewRequest.getRating());
        productReview.setTitle(productReviewRequest.getTitle());
        productReview.setReviewText(productReviewRequest.getReviewText());
        productReview.setReviewDate(new Date());
        productReview.setUser(userService.findUserByUserId(productReviewRequest.getUserId()));
        productReview.setProduct(productService.findById(productReviewRequest.getProductId()));
        return productReviewRepository.save(productReview);
    }

    @Override
    public ProductReview updateProductReview(Long productId, String reviewText, int rating) throws ProductReviewNotFoundException {
        ProductReview productReview = productReviewRepository.findById(productId)
                .orElseThrow(() -> new ProductReviewNotFoundException("Product review not found with id: " + productId));

        productReview.setReviewText(reviewText);
        productReview.setRating(rating);
        return productReviewRepository.save(productReview);
    }

    @Override
    public void deleteProductReview(Long productReviewId) {
        productReviewRepository.deleteById(productReviewId);
    }

    @Override
    public List<ProductReview> getAllProductReviewsForProduct(Long productId) throws ProductReviewNotFoundException {
        return productReviewRepository.findAllByProductId(productId)
                .orElseThrow(() -> new ProductReviewNotFoundException("Product review not found with id: " + productId));
    }
}
