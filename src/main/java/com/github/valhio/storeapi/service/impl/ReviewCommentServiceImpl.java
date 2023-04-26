package com.github.valhio.storeapi.service.impl;

import com.github.valhio.storeapi.model.ReviewComment;
import com.github.valhio.storeapi.service.ReviewCommentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewCommentServiceImpl implements ReviewCommentService {

    private final ReviewRepository reviewRepository;

    @Override
    public List<ReviewComment> findAllByReviewId(Long productReviewId) {
        return null;
    }

    @Override
    public ReviewComment addComment(Long productReviewId, String email, String comment) {
        return null;
    }

    @Override
    public boolean hasCommented(Long productReviewId, String email) {
        return false;
    }
}
