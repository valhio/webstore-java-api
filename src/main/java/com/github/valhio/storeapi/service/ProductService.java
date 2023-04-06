package com.github.valhio.storeapi.service;

import com.github.valhio.storeapi.exception.domain.ProductNotFoundException;
import com.github.valhio.storeapi.model.Product;
import org.springframework.data.domain.Page;

public interface ProductService {

    Page<Product> findAll(Integer pageNo, Integer pageSize, String sortBy, String keyword);

    Page<Product> findAll2(Integer pageNo, Integer pageSize, String sortBy, String keyword);

    Product findById(Long id) throws ProductNotFoundException;

    Product save(Product product);

    Product update(Product product);

    void deleteById(Long id);

    void removeQuantity(Long id, int quantity) throws ProductNotFoundException;
}
