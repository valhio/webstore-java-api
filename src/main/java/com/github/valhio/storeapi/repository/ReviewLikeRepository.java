package com.github.valhio.storeapi.repository;

import com.github.valhio.storeapi.model.ReviewLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM ReviewLike r WHERE r.review.id = ?1 AND r.user.email = ?2")
    boolean existsByReview_IdAndUser_Email(Long productReviewId, String email);

    @Query("SELECT r FROM ReviewLike r WHERE r.review.id = ?1")
    Collection<Object> findAllByReview_Id(Long productReviewId);
}
