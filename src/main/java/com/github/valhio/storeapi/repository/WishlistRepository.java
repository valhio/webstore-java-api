package com.github.valhio.storeapi.repository;

import com.github.valhio.storeapi.model.Favorite;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WishlistRepository extends MongoRepository<Favorite, String> {

    void removeByUser_IdAndProduct_Id(String userId, String productId);

    Optional<Favorite> findByUser_IdAndProduct_Id(String userId, String productId);

    Iterable<Favorite> findAllByUser_Id(String userId);
}
