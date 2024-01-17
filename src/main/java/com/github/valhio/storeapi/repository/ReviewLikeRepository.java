package com.github.valhio.storeapi.repository;

import com.github.valhio.storeapi.model.ReviewLike;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewLikeRepository extends MongoRepository<ReviewLike, String> {

    boolean existsByReview_IdAndUser_Email(String productReviewId, String email);

    @Query("Select r from ReviewLike r where r.review.id = ?1 and r.user.email = ?2")
    Optional<ReviewLike> findByProductReviewIdAndUserEmail(String productReviewId, String email);

    List<ReviewLike> findAllByReview_Id(String productReviewId);
}
