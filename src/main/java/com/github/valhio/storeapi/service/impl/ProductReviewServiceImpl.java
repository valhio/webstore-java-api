package com.github.valhio.storeapi.service.impl;

import com.github.valhio.storeapi.exception.domain.ProductNotFoundException;
import com.github.valhio.storeapi.exception.domain.ProductReviewNotFoundException;
import com.github.valhio.storeapi.exception.domain.UserNotFoundException;
import com.github.valhio.storeapi.model.Product;
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
//        productReview.setProductId(productReviewRequest.getProductId());
        productReview.setProduct(productService.findById(productReviewRequest.getProductId()));
        productReview.setUser(userService.findUserByUserId(productReviewRequest.getUserId()));
        productReview.setReviewDate(new Date());

        ProductReview review = productReviewRepository.save(productReview);

        Product product = productService.findById(productReviewRequest.getProductId());
//        product.getReviewIds().add(review.getId());
        product.getProductReviews().add(review);
        productService.update(product);

        return review;
    }

    @Override
    public ProductReview updateProductReview(String productId, String reviewText, int rating) throws ProductReviewNotFoundException {
        ProductReview productReview = productReviewRepository.findById(productId)
                .orElseThrow(() -> new ProductReviewNotFoundException("Product review not found with id: " + productId));

        productReview.setReviewText(reviewText);
        productReview.setRating(rating);
        return productReviewRepository.save(productReview);
    }

    @Override
    public void deleteProductReview(String productReviewId) {
        productReviewRepository.deleteById(productReviewId);
    }

    @Override
    public List<ProductReview> getAllProductReviewsForProduct(String productId) throws ProductReviewNotFoundException {
        return productReviewRepository.findAllByProductId(productId)
                .orElseThrow(() -> new ProductReviewNotFoundException("Product review not found with id: " + productId));
    }

    @Override
    public ProductReview findById(String productReviewId) throws ProductReviewNotFoundException {
        return productReviewRepository.findById(productReviewId)
                .orElseThrow(() -> new ProductReviewNotFoundException("Product review not found with id: " + productReviewId));
    }
}