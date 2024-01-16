package com.github.valhio.storeapi.repository;

import com.github.valhio.storeapi.model.GroceryItem;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends MongoRepository<GroceryItem, String> {

    @Query("{name:'?0'}")
    GroceryItem findItemByName(String name);

    @Query(value="{category:'?0'}", fields="{'name' : 1, 'quantity' : 1}")
    List<GroceryItem> findAll(String category);

    // find all
    @Query(value="{}", fields="{'name' : 1, 'quantity' : 1}")
    List<GroceryItem> findAll();

    public long count();

    GroceryItem save(GroceryItem item);

}