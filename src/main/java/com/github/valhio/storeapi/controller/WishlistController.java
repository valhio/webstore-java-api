package com.github.valhio.storeapi.controller;

import com.github.valhio.storeapi.exception.domain.ProductNotFoundException;
import com.github.valhio.storeapi.exception.domain.UserNotFoundException;
import com.github.valhio.storeapi.model.Favorite;
import com.github.valhio.storeapi.service.WishlistService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @PostMapping("/add/{productId}/{userId}")
    public ResponseEntity<Favorite> addProductToFavourites(@PathVariable String productId, @PathVariable String userId) throws UserNotFoundException, ProductNotFoundException {
        Favorite favoriteByUserIdAndProductId = null;
        try {
            favoriteByUserIdAndProductId = this.wishlistService.findFavouriteByUserIdAndProductId(userId, productId);
            return ResponseEntity.ok(favoriteByUserIdAndProductId);
        } catch (ProductNotFoundException e) {
            favoriteByUserIdAndProductId = wishlistService.addProductToFavourite(userId, productId);
            return ResponseEntity.ok(favoriteByUserIdAndProductId);
        }
    }

    @DeleteMapping("/remove/{productId}/{userId}")
    public ResponseEntity<Favorite> removeProductFromFavourites(@PathVariable String productId, @PathVariable String userId) throws UserNotFoundException, ProductNotFoundException {
        wishlistService.removeProductFromFavourite(userId, productId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/all/{userId}")
    public ResponseEntity<List<Favorite>> getAllFavouritesByUserId(@PathVariable String userId) {
        Iterable<Favorite> allFavouritesByUserId = wishlistService.findAllFavouritesByUserId(userId);
        return ResponseEntity.ok((List<Favorite>) allFavouritesByUserId);
    }

    @GetMapping("/status/{productId}/{userId}")
    public ResponseEntity<Favorite> getFavouriteByUserIdAndProductId(@PathVariable String productId, @PathVariable String userId) throws ProductNotFoundException {
        Favorite favoriteByUserIdAndProductId = wishlistService.findFavouriteByUserIdAndProductId(userId, productId);
        return ResponseEntity.ok(favoriteByUserIdAndProductId);
    }

}