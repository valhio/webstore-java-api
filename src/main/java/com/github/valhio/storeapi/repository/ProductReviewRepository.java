package com.github.valhio.storeapi.repository;

import com.github.valhio.storeapi.model.ProductReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductReviewRepository extends JpaRepository<ProductReview, Long> {

    @Query("SELECT pr FROM ProductReview pr WHERE pr.product.id = ?1")
    Optional<List<ProductReview>> findAllByProductId(Long productId);

}
