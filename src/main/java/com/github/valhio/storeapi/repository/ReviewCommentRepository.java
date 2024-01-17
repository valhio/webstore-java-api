package com.github.valhio.storeapi.repository;

import com.github.valhio.storeapi.model.ReviewComment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewCommentRepository extends MongoRepository<ReviewComment, String> {

    List<ReviewComment> findAllByReview_Id(String productReviewId);

    @Query("{'review.id': ?0, 'user.email': ?1}")
    boolean existsByReview_IdAndUserEmail(String productReviewId, String email);
}
