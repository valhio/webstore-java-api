package com.github.valhio.storeapi.service;

import com.github.valhio.storeapi.exception.domain.ProductNotFoundException;
import com.github.valhio.storeapi.exception.domain.UserNotFoundException;
import com.github.valhio.storeapi.model.Favorite;
import com.github.valhio.storeapi.model.Product;
import com.github.valhio.storeapi.model.User;
import com.github.valhio.storeapi.repository.WishlistRepository;
import org.springframework.stereotype.Service;

@Service
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserService userService;
    private final ProductService productService;

    public WishlistServiceImpl(WishlistRepository wishlistRepository, UserService userService, ProductService productService) {
        this.wishlistRepository = wishlistRepository;
        this.userService = userService;
        this.productService = productService;
    }

    @Override
    public Favorite addProductToFavourite(String userId, Long productId) throws UserNotFoundException, ProductNotFoundException {
        User user = userService.findUserByUserId(userId);
        Product product = productService.findById(productId);
        return wishlistRepository.save(new Favorite(user, product));
    }

    @Override
    public void removeProductFromFavourite(String userId, Long productId) throws UserNotFoundException, ProductNotFoundException {
        Favorite favoriteByUserIdAndProductId = this.findFavouriteByUserIdAndProductId(userId, productId);
        wishlistRepository.delete(favoriteByUserIdAndProductId);
    }

    @Override
    public Favorite findFavouriteByUserIdAndProductId(String userId, Long productId) throws ProductNotFoundException {
        return wishlistRepository.findByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found in favourites"));
    }

    @Override
    public Iterable<Favorite> findAllFavouritesByUserId(String userId) {
        return wishlistRepository.findAllByUserId(userId);
    }
}
