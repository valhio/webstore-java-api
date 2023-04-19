package com.github.valhio.storeapi.service;


import com.github.valhio.storeapi.exception.domain.ProductNotFoundException;
import com.github.valhio.storeapi.exception.domain.UserNotFoundException;
import com.github.valhio.storeapi.model.Favorite;

public interface WishlistService {

    Favorite findFavouriteByUserIdAndProductId(String userId, Long productId) throws ProductNotFoundException;

    Iterable<Favorite> findAllFavouritesByUserId(String userId);

    Favorite addProductToFavourite(String userId, Long productId) throws UserNotFoundException, ProductNotFoundException;

    void removeProductFromFavourite(String userId, Long productId) throws UserNotFoundException, ProductNotFoundException;
}
