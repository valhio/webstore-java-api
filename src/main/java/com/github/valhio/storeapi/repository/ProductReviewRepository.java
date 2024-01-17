package com.github.valhio.storeapi.repository;

import com.github.valhio.storeapi.model.ProductReview;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductReviewRepository extends MongoRepository<ProductReview, String> {

    Optional<List<ProductReview>> findAllByProductId(String productId);

}
