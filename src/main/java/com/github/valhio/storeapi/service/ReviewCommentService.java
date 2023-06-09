package com.github.valhio.storeapi.service;

import com.github.valhio.storeapi.exception.domain.ProductReviewNotFoundException;
import com.github.valhio.storeapi.exception.domain.UserNotFoundException;
import com.github.valhio.storeapi.model.ReviewComment;

import java.util.List;

public interface ReviewCommentService {

    List<ReviewComment> findAllByReviewId(Long productReviewId);

    ReviewComment addComment(Long productReviewId, String email, String comment) throws UserNotFoundException, ProductReviewNotFoundException;

    boolean hasCommented(Long productReviewId, String email);

//    void deleteComment(Long productReviewId, String email, Long commentId);

//    ReviewComment updateComment(Long productReviewId, String email, Long commentId, String comment);

}
