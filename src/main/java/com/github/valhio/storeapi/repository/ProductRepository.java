package com.github.valhio.storeapi.repository;

import com.github.valhio.storeapi.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {

    @Query(value = "{ '_id': ?0 }")
    Optional<Product> findProductWithReviewsById(String productId);

    // '$or' is
    @Query("{'$or': [{'name': { $regex: ?0, $options: 'i' }}, {'description': { $regex: ?0, $options: 'i' }}, {'price': { $regex: ?0, $options: 'i' }}]}")
    Page<Product> findAllContaining(String keyword, Pageable paging);

    @Query("{'name': { $regex: ?0, $options: 'i' }, 'quantity': { $gt: 0 }}")
    Page<Product> findByNameContaining(String name, Pageable paging);

}

