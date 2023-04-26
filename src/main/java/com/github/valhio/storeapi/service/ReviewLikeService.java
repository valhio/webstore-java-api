package com.github.valhio.storeapi.service;

import com.github.valhio.storeapi.exception.domain.ProductReviewNotFoundException;
import com.github.valhio.storeapi.exception.domain.UserNotFoundException;
import com.github.valhio.storeapi.model.ReviewLike;

import java.util.List;

public interface ReviewLikeService {

    ReviewLike addLike(Long productReviewId, String email) throws UserNotFoundException, ProductReviewNotFoundException;

    void removeLike(Long productReviewId, String email);

    int getLikesCount(Long productReviewId);

    boolean hasLiked(Long productReviewId, String email);

    List<ReviewLike> findAllByReviewId(Long productReviewId);

}
