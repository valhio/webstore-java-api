package com.github.valhio.storeapi.service.impl;

import com.github.valhio.storeapi.exception.domain.ProductReviewNotFoundException;
import com.github.valhio.storeapi.exception.domain.UserNotFoundException;
import com.github.valhio.storeapi.model.ProductReview;
import com.github.valhio.storeapi.model.ReviewLike;
import com.github.valhio.storeapi.model.User;
import com.github.valhio.storeapi.repository.ReviewLikeRepository;
import com.github.valhio.storeapi.service.ProductReviewService;
import com.github.valhio.storeapi.service.ReviewLikeService;
import com.github.valhio.storeapi.service.UserService;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ReviewLikeServiceImpl implements ReviewLikeService {

    private final ReviewLikeRepository reviewLikeRepository;
    private final ProductReviewService productReviewService;
    private final UserService userService;

    public ReviewLikeServiceImpl(ReviewLikeRepository reviewLikeRepository, ProductReviewService productReviewService, UserService userService) {
        this.reviewLikeRepository = reviewLikeRepository;
        this.productReviewService = productReviewService;
        this.userService = userService;
    }

    @Override
    public ReviewLike addLike(Long productReviewId, String userId) throws UserNotFoundException, ProductReviewNotFoundException {
        ProductReview productReview = productReviewService.findById(productReviewId);
        User user = userService.findUserByUserId(userId);

        ReviewLike reviewLike = new ReviewLike();
        reviewLike.setReview(productReview);
        reviewLike.setUser(user);
        reviewLike.setLikeDate(new Date());
        return reviewLikeRepository.save(reviewLike);
    }

    @Override
    public void removeLike(Long productReviewId) {
        reviewLikeRepository.findById(productReviewId).ifPresent(reviewLikeRepository::delete);
    }

    @Override
    public boolean hasLiked(Long productReviewId, String userId) {
        return false;
    }
}
