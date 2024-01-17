package com.github.valhio.storeapi.service;

import com.github.valhio.storeapi.exception.domain.ProductNotFoundException;
import com.github.valhio.storeapi.model.Product;
import org.springframework.data.domain.Page;

public interface ProductService {

    Page<Product> findAll(Integer pageNo, Integer pageSize, String sortBy, String keyword);

    Product findById(String id) throws ProductNotFoundException;

    Product save(Product product);

    Product update(Product product);

    void deleteById(String id);

    void removeQuantity(String id, int quantity) throws ProductNotFoundException;
}