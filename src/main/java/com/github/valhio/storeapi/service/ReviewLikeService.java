package com.github.valhio.storeapi.service;

import com.github.valhio.storeapi.exception.domain.ProductReviewNotFoundException;
import com.github.valhio.storeapi.exception.domain.ReviewLikeNotFoundException;
import com.github.valhio.storeapi.exception.domain.UserNotFoundException;
import com.github.valhio.storeapi.model.ReviewLike;

import java.util.List;

public interface ReviewLikeService {

    List<ReviewLike> addLike(String productReviewId, String id) throws UserNotFoundException, ProductReviewNotFoundException;

    List<ReviewLike> removeLike(String productReviewId, String id) throws ProductReviewNotFoundException, ReviewLikeNotFoundException;

    int getLikesCount(String productReviewId);

    boolean hasLiked(String productReviewId, String id);

    List<ReviewLike> findAllByReviewId(String productReviewId);

}