package com.github.valhio.storeapi.service;

import com.github.valhio.storeapi.exception.domain.ProductReviewNotFoundException;
import com.github.valhio.storeapi.exception.domain.UserNotFoundException;
import com.github.valhio.storeapi.model.ReviewComment;

import java.util.List;

public interface ReviewCommentService {

    List<ReviewComment> findAllByReviewId(String productReviewId);

    ReviewComment addComment(String productReviewId, String email, String comment) throws UserNotFoundException, ProductReviewNotFoundException;

    boolean hasCommented(String productReviewId, String email);

//    void deleteComment(String productReviewId, String email, String commentId);

//    ReviewComment updateComment(String productReviewId, String email, Long commentId, String comment);

}