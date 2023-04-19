package com.github.valhio.storeapi.repository;

import com.github.valhio.storeapi.model.Favorite;
import com.github.valhio.storeapi.model.Product;
import com.github.valhio.storeapi.model.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
@Repository
public interface WishlistRepository extends PagingAndSortingRepository<Favorite, Long> {
    @Modifying
    @Query("DELETE FROM Favorite f WHERE f.user = ?1 AND f.product = ?2")
    void removeByUserIdAndProductId(User userId, Product productId);

    @Query("SELECT f FROM Favorite f WHERE f.user.userId = ?1 AND f.product.id = ?2")
    Optional<Favorite> findByUserIdAndProductId(String userId, Long productId);

    @Query("SELECT f FROM Favorite f WHERE f.user.userId = ?1")
    Iterable<Favorite> findAllByUserId(String userId);
}
