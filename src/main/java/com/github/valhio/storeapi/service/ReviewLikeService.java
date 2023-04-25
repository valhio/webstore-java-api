package com.github.valhio.storeapi.service;

import com.github.valhio.storeapi.exception.domain.ProductReviewNotFoundException;
import com.github.valhio.storeapi.exception.domain.UserNotFoundException;
import com.github.valhio.storeapi.model.ReviewLike;

public interface ReviewLikeService {

    ReviewLike addLike(Long productReviewId, String userId) throws UserNotFoundException, ProductReviewNotFoundException;

    void removeLike(Long productReviewId, String userId);

    boolean hasLiked(Long productReviewId, String userId);

}
