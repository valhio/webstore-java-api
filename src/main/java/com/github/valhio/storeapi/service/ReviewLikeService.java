package com.github.valhio.storeapi.service;

import com.github.valhio.storeapi.exception.domain.ProductReviewNotFoundException;
import com.github.valhio.storeapi.exception.domain.UserNotFoundException;
import com.github.valhio.storeapi.model.ReviewLike;

import java.util.List;

public interface ReviewLikeService {

    ReviewLike addLike(String productReviewId, String email) throws UserNotFoundException, ProductReviewNotFoundException;

    void removeLike(String productReviewId, String email);

    int getLikesCount(String productReviewId);

    boolean hasLiked(String productReviewId, String email);

    List<ReviewLike> findAllByReviewId(String productReviewId);

}