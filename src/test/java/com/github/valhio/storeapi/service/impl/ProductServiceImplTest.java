package com.github.valhio.storeapi.service.impl;

import com.github.valhio.storeapi.exception.domain.ProductNotFoundException;
import com.github.valhio.storeapi.model.Product;
import com.github.valhio.storeapi.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository repository;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void testFindAll() {
        // Setup
        int pageNo = 0;
        int pageSize = 10;
        String sortBy = "name-asc";
        String keyword = "test";
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.ASC, "name"));
        Page<Product> pageResult = new PageImpl<>(Collections.singletonList(new Product()), pageRequest, 1);
        when(repository.findByNameContaining(keyword, pageRequest)).thenReturn(pageResult);

        // Execution
        Page<Product> products = productService.findAll(pageNo, pageSize, sortBy, keyword);

        // Assertions
        verify(repository, times(1)).findByNameContaining(keyword, pageRequest);
        assertEquals(pageResult, products);
    }

    @Nested
    @DisplayName("Test find by id")
    class FindById {
        @Test
        @DisplayName("Test find by id when product exists")
        void testFindByIdWhenProductExists() throws ProductNotFoundException {
            Product product = new Product();
            when(repository.findById(1L)).thenReturn(java.util.Optional.of(product));

            Product result = productService.findById(1L);

            verify(repository, times(1)).findById(1L);
            assertEquals(product, result);
        }

        @Test
        @DisplayName("Test find by id when product does not exist should throw exception")
        void testFindByIdWhenProductDoesNotExistShouldThrowException() {
            when(repository.findById(1L)).thenReturn(java.util.Optional.empty());

            assertThrows(ProductNotFoundException.class, () -> productService.findById(1L));

            verify(repository, times(1)).findById(1L);
        }
    }

    @Test
    @DisplayName("Test save product")
    void testSaveProduct() {
        Product product = new Product();
        when(repository.save(product)).thenReturn(product);

        Product result = productService.save(product);

        verify(repository, times(1)).save(product);
        assertEquals(product, result);
    }

    @Test
    @DisplayName("Test update product")
    void testUpdateProduct() {
        Product product = new Product();
        when(repository.save(product)).thenReturn(product);

        Product result = productService.save(product);

        verify(repository, times(1)).save(product);
        assertEquals(product, result);
    }

    @Test
    @DisplayName("Test delete product")
    void testDeleteProduct() {
        productService.deleteById(1L);

        verify(repository, times(1)).deleteById(1L);
    }

    @Nested
    @DisplayName("Test remove quantity from product")
    class RemoveQuantity{
        @Test
        @DisplayName("Test remove quantity from product when product exists")
        void testRemoveQuantityFromProductWhenProductExists() throws ProductNotFoundException {
            Product product = new Product();
            product.setQuantity(10);
            when(repository.findById(1L)).thenReturn(java.util.Optional.of(product));

            productService.removeQuantity(1L, 5);

            verify(repository, times(1)).findById(1L);
            verify(repository, times(1)).save(product);
            assertEquals(5, product.getQuantity());
        }

        @Test
        @DisplayName("Test remove quantity from product when product does not exist should throw exception")
        void testRemoveQuantityFromProductWhenProductDoesNotExistShouldThrowException() {
            when(repository.findById(1L)).thenReturn(java.util.Optional.empty());

            assertThrows(ProductNotFoundException.class, () -> productService.removeQuantity(1L, 5));

            verify(repository, times(1)).findById(1L);
            verify(repository, times(0)).save(any(Product.class));
        }
    }
}