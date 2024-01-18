package com.github.valhio.storeapi.repository;

import com.github.valhio.storeapi.model.ReviewLike;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewLikeRepository extends MongoRepository<ReviewLike, String> {
    long deleteByReviewIdAndUserId(String reviewId, String userId);
    Optional<ReviewLike> findByReviewIdAndUserId(String reviewId, String userId);
    Optional<ReviewLike> findByIdAndUserId(String id, String userId);

    long deleteByUserIdAndId(String userId, String id);

    @Query("{'review.id': ?0, 'user.id': ?1}")
    Optional<ReviewLike> existsByReview_IdAndUser_Id(String productReviewId, String id);

//    @Query("Select r from ReviewLike r where r.review.id = ?1 and r.user.email = ?2")
    @Query("{'review.id': ?0, 'user.email': ?1}")
    Optional<ReviewLike> findByProductReviewIdAndUserEmail(String productReviewId, String email);


    @Query("{'review.id': ?0}")
    List<ReviewLike> findAllByReview_Id(String productReviewId);
}
