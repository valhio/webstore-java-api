package com.github.valhio.storeapi.service;

import com.github.valhio.storeapi.exception.domain.ProductNotFoundException;
import com.github.valhio.storeapi.exception.domain.UserNotFoundException;
import com.github.valhio.storeapi.model.Favorite;

public interface WishlistService {

    Favorite findFavouriteByUserIdAndProductId(String userId, String productId) throws ProductNotFoundException;

    Iterable<Favorite> findAllFavouritesByUserId(String userId);

    Favorite addProductToFavourite(String userId, String productId) throws UserNotFoundException, ProductNotFoundException;

    void removeProductFromFavourite(String userId, String productId) throws UserNotFoundException, ProductNotFoundException;
}
