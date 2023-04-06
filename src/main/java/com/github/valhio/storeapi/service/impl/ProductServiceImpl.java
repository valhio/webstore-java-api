package com.github.valhio.storeapi.service.impl;

import com.github.valhio.storeapi.exception.domain.ProductNotFoundException;
import com.github.valhio.storeapi.model.Product;
import com.github.valhio.storeapi.repository.ProductRepository;
import com.github.valhio.storeapi.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private static final String WITH_ID_WAS_NOT_FOUND = "Entity with id: %d was not found.";

    private final ProductRepository repository;

    public ProductServiceImpl(ProductRepository repository) {
        this.repository = repository;
    }

    @Override
    public Page<Product> findAll(Integer pageNo, Integer pageSize, String sortBy, String keyword) {
        String[] params = sortBy.split("-"); // Ex: name-asc
        Pageable paging = PageRequest.of(pageNo, pageSize,
                Sort.by((params.length == 2 && params[1].equals("desc"))
                                ? Sort.Direction.DESC
                                : Sort.Direction.ASC,
                        params[0])
        );
        return repository.findByNameContaining(keyword, paging);
    }

    @Override
    public Page<Product> findAll2(Integer pageNo, Integer pageSize, String sortBy, String keyword) {
        return repository.findAll(PageRequest.of(pageNo, pageSize, Sort.by(sortBy)));
    }


    @Override
    public Product findById(Long id) throws ProductNotFoundException {
        return repository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(String.format(WITH_ID_WAS_NOT_FOUND, id)));
    }

    @Override
    public Product save(Product product) {
        return repository.save(product);
    }

    @Override
    public Product update(Product product) {
        return repository.save(product);
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public void removeQuantity(Long id, int quantity) throws ProductNotFoundException {
        Product product = findById(id);
        product.setQuantity(Math.max(product.getQuantity() - quantity, 0));
        update(product);
    }
}
