package com.github.valhio.storeapi.repository;

import com.github.valhio.storeapi.model.ReviewLike;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewLikeRepository extends CrudRepository<ReviewLike, Long> {
}
