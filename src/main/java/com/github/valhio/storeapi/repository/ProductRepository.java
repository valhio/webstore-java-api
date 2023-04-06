package com.github.valhio.storeapi.repository;

import com.github.valhio.storeapi.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends PagingAndSortingRepository<Product, Long> {

    @Query("select p from Product p where concat(" +
            "p.name" +
            ",p.description" +
            ",p.price" +
            ") like %?1%")
    Page<Product> findAllContaining(String keyword, Pageable paging);

    @Query("select p from Product p where p.name like %?1% and p.quantity > 0")
    Page<Product> findByNameContaining(String name, Pageable paging);

}

