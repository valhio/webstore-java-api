package com.github.valhio.storeapi.repository;

import com.github.valhio.storeapi.model.OrderItem;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderItemRepository extends MongoRepository<OrderItem, String> {

}
