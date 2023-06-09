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
import java.util.List;

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
    public ReviewLike addLike(Long productReviewId, String email) throws UserNotFoundException, ProductReviewNotFoundException {
        if (this.hasLiked(productReviewId, email)) return null;

        ProductReview productReview = productReviewService.findById(productReviewId);
        User user = userService.findUserByEmail(email);

        ReviewLike reviewLike = new ReviewLike();
        reviewLike.setReview(productReview);
        reviewLike.setUser(user);
        reviewLike.setLikeDate(new Date());
        return reviewLikeRepository.save(reviewLike);
    }

    @Override
    public void removeLike(Long productReviewId, String email) {
        reviewLikeRepository.findByProductReviewIdAndUserEmail(productReviewId, email).ifPresent(reviewLikeRepository::delete);
    }

    @Override
    public int getLikesCount(Long productReviewId) {
        return reviewLikeRepository.findAllByReviewId(productReviewId).size();
    }

    @Override
    public boolean hasLiked(Long productReviewId, String email) {
        return reviewLikeRepository.existsByReview_IdAndUser_Email(productReviewId, email);
    }

    @Override
    public List<ReviewLike> findAllByReviewId(Long productReviewId) {
        return reviewLikeRepository.findAllByReviewId(productReviewId);
    }
}
