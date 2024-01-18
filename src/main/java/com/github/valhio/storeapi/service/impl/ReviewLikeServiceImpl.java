package com.github.valhio.storeapi.service.impl;

import com.github.valhio.storeapi.exception.domain.ProductReviewNotFoundException;
import com.github.valhio.storeapi.exception.domain.ReviewLikeNotFoundException;
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
import java.util.Optional;

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
    public List<ReviewLike> addLike(String productReviewId, String id) throws UserNotFoundException, ProductReviewNotFoundException {
        if (this.hasLiked(productReviewId, id)) {
            throw new IllegalArgumentException("User has already liked the review.");
        }

        try {
            ProductReview productReview = productReviewService.findById(productReviewId);
            User user = userService.findUserByUserId(id);

            ReviewLike reviewLike = new ReviewLike();
            reviewLike.setUserId(user.getId());
            reviewLike.setReviewId(productReviewId);
            reviewLike.setLikeDate(new Date());
            ReviewLike savedReviewLike = reviewLikeRepository.save(reviewLike);

            productReview.getLikes().add(savedReviewLike);
            ProductReview updatedProductReview = productReviewService.update(productReview);

            return updatedProductReview.getLikes();
        } catch (UserNotFoundException | ProductReviewNotFoundException e) {
            throw e;
        }
    }


    @Override
    public List<ReviewLike> removeLike(String productReviewId, String userId) throws ProductReviewNotFoundException, ReviewLikeNotFoundException {
        Optional<ReviewLike> reviewLike = reviewLikeRepository.findByReviewIdAndUserId(productReviewId, userId);

        if (reviewLike.isPresent()) {
            ProductReview productReview = productReviewService.findById(productReviewId);

            boolean likeRemoved = productReview.getLikes()
                    .removeIf(like -> like.getId().equals(reviewLike.get().getId()));

            if (!likeRemoved) {
                throw new ReviewLikeNotFoundException("ReviewLike not found in the product review.");
            }

            ProductReview updated = productReviewService.update(productReview);
            reviewLikeRepository.deleteByReviewIdAndUserId(productReviewId, userId);
            return updated.getLikes();
        }

        throw new ReviewLikeNotFoundException("ReviewLike not found.");
    }


    @Override
    public int getLikesCount(String productReviewId) {
        return reviewLikeRepository.findAllByReview_Id(productReviewId).size();
    }

    @Override
    public boolean hasLiked(String productReviewId, String id) {
        Optional<ReviewLike> reviewLike = reviewLikeRepository.existsByReview_IdAndUser_Id(productReviewId, id);
        return reviewLike.isPresent();
    }

    @Override
    public List<ReviewLike> findAllByReviewId(String productReviewId) {
        return reviewLikeRepository.findAllByReview_Id(productReviewId);
    }
}