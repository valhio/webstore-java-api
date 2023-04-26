package com.github.valhio.storeapi.service.impl;

import com.github.valhio.storeapi.exception.domain.ProductReviewNotFoundException;
import com.github.valhio.storeapi.exception.domain.UserNotFoundException;
import com.github.valhio.storeapi.model.ProductReview;
import com.github.valhio.storeapi.model.ReviewComment;
import com.github.valhio.storeapi.model.User;
import com.github.valhio.storeapi.repository.ReviewCommentRepository;
import com.github.valhio.storeapi.service.ProductReviewService;
import com.github.valhio.storeapi.service.ReviewCommentService;
import com.github.valhio.storeapi.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewCommentServiceImpl implements ReviewCommentService {

    private final ReviewCommentRepository reviewCommentRepository;
    private final UserService userService;
    private final ProductReviewService productReviewService;

    public ReviewCommentServiceImpl(ReviewCommentRepository reviewCommentRepository, UserService userService, ProductReviewService productReviewService) {
        this.reviewCommentRepository = reviewCommentRepository;
        this.userService = userService;
        this.productReviewService = productReviewService;
    }

    @Override
    public List<ReviewComment> findAllByReviewId(Long productReviewId) {
        return reviewCommentRepository.findAllByReviewId(productReviewId);
    }

    @Override
    public ReviewComment addComment(Long productReviewId, String email, String comment) throws UserNotFoundException, ProductReviewNotFoundException {
        User user = userService.findUserByEmail(email);
        ProductReview productReview = productReviewService.findById(productReviewId);

        ReviewComment reviewComment = new ReviewComment();
        reviewComment.setUser(user);
        reviewComment.setReview(productReview);
        reviewComment.setComment(comment);
        reviewComment.setCommentDate(new java.util.Date());

        return reviewCommentRepository.save(reviewComment);
    }

    @Override
    public boolean hasCommented(Long productReviewId, String email) {
        return reviewCommentRepository.existsByReviewIdAndUserEmail(productReviewId, email);
    }
}
