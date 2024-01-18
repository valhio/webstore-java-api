package com.github.valhio.storeapi.service.impl;

import com.github.valhio.storeapi.exception.domain.ProductNotFoundException;
import com.github.valhio.storeapi.exception.domain.ProductReviewNotFoundException;
import com.github.valhio.storeapi.exception.domain.UserNotFoundException;
import com.github.valhio.storeapi.model.Product;
import com.github.valhio.storeapi.model.ProductReview;
import com.github.valhio.storeapi.model.ReviewLike;
import com.github.valhio.storeapi.repository.ProductReviewRepository;
import com.github.valhio.storeapi.repository.ReviewCommentRepository;
import com.github.valhio.storeapi.repository.ReviewLikeRepository;
import com.github.valhio.storeapi.request.ProductReviewRequest;
import com.github.valhio.storeapi.service.ProductReviewService;
import com.github.valhio.storeapi.service.ProductService;
import com.github.valhio.storeapi.service.UserService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ProductReviewServiceImpl implements ProductReviewService {

    private final ProductReviewRepository productReviewRepository;
    private final UserService userService;
    private final ProductService productService;
    private final ReviewLikeRepository reviewLikeRepository;
    private final ReviewCommentRepository reviewCommentRepository;

    public ProductReviewServiceImpl(ProductReviewRepository productReviewRepository, UserService userService, ProductService productService, ReviewLikeRepository reviewLikeRepository, ReviewCommentRepository reviewCommentRepository) {
        this.productReviewRepository = productReviewRepository;
        this.userService = userService;
        this.productService = productService;
        this.reviewLikeRepository = reviewLikeRepository;
        this.reviewCommentRepository = reviewCommentRepository;
    }

    @Override
    public ProductReview addProductReview(ProductReviewRequest productReviewRequest, String userId) throws UserNotFoundException, ProductNotFoundException {
        ProductReview productReview = new ProductReview();
        productReview.setRating(productReviewRequest.getRating());
        productReview.setTitle(productReviewRequest.getTitle());
        productReview.setReviewText(productReviewRequest.getReviewText());
        productReview.setProduct(productService.findById(productReviewRequest.getProductId()));
        productReview.setUser(userService.findUserByUserId(userId));
        productReview.setReviewDate(new Date());

        ProductReview review = productReviewRepository.save(productReview);

        Product product = productService.findById(productReviewRequest.getProductId());
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

    public ProductReview update(ProductReview productReview) {
        return productReviewRepository.save(productReview);
    }

    @Override
    public void deleteProductReview(String productReviewId) {
        ProductReview productReview = productReviewRepository.findById(productReviewId).orElse(null);
        if (productReview != null) {
            // Delete associated likes
            reviewLikeRepository.deleteAll(productReview.getLikes());

            // Delete associated comments
            reviewCommentRepository.deleteAll(productReview.getComments());

            // Delete the product review itself
            productReviewRepository.delete(productReview);
        }
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

    @Override
    public List<ReviewLike> likeOrDislikeReview(String reviewId, String userId) throws UserNotFoundException, ProductReviewNotFoundException {
        ProductReview productReview = productReviewRepository.findById(reviewId)
                .orElseThrow(() -> new ProductReviewNotFoundException("Product review with id: " + reviewId + " was not found."));

        Optional<ReviewLike> existingLike = productReview.getLikes()
                .stream()
                .filter(like -> like.getUserId().equals(userId))
                .findFirst();

        // Remove existing like if it exists
        existingLike.ifPresent(like -> productReview.getLikes().remove(like));

        // Add new like if it doesn't exist
        if (!existingLike.isPresent()) {
            ReviewLike newLike = new ReviewLike();
            newLike.setUserId(userId);
            newLike.setReviewId(reviewId);
            newLike.setLikeDate(new java.util.Date());
            reviewLikeRepository.save(newLike);

            productReview.getLikes().add(newLike);
        }

        productReviewRepository.save(productReview);

        return productReview.getLikes();
    }
}