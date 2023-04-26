package com.github.valhio.storeapi.repository;

import com.github.valhio.storeapi.model.ReviewComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewCommentRepository extends JpaRepository<ReviewComment, Long> {

    @Query("SELECT rc FROM ReviewComment rc WHERE rc.review.id = ?1")
    List<ReviewComment> findAllByReviewId(Long productReviewId);

    @Query("SELECT CASE WHEN COUNT(rc) > 0 THEN TRUE ELSE FALSE END FROM ReviewComment rc WHERE rc.review.id = ?1 AND rc.user.email = ?2")
    boolean existsByReviewIdAndUserEmail(Long productReviewId, String email);
}
