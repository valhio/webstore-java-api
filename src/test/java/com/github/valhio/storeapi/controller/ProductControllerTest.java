package com.github.valhio.storeapi.controller;

import com.github.valhio.storeapi.domain.HttpResponse;
import com.github.valhio.storeapi.exception.domain.ProductNotFoundException;
import com.github.valhio.storeapi.model.Product;
import com.github.valhio.storeapi.service.ProductService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {
    private static final Long PRODUCT_ID = 1L;
    private static final String PRODUCT_NAME = "Product";
    private static final Integer PRODUCT_PRICE = 10;
    private final ProductNotFoundException exception =
            new ProductNotFoundException("Entity with id: " + PRODUCT_ID + " was not found.");
    @Mock
    private ProductService productService;
    @InjectMocks
    private ProductController productController;
    private Product product;
    @Captor
    private ArgumentCaptor<Product> productCaptor;

    @Captor
    private ArgumentCaptor<Long> productIdCaptor;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(PRODUCT_ID);
        product.setName(PRODUCT_NAME);
        product.setPrice(PRODUCT_PRICE);
    }

    @Nested
    @DisplayName("Fetch All Products")
    class FetchAllProducts {
        @Test
        void testFetchAll() {
            // given
            Page<Product> products = mock(Page.class);
            when(productService.findAll(any(Integer.class), any(Integer.class), any(String.class), any(String.class)))
                    .thenReturn(products);

            // when
            ResponseEntity<HttpResponse> response = productController.fetchAll(0, 10, "id", "");

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getStatus()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getMessage()).isEqualTo("Products Retrieved");
            assertThat(response.getBody().getData().get("products")).isEqualTo(products);
        }
    }

    @Nested
    @DisplayName("Find By Id")
    class FindById {

        @Test
        void testFindByIdReturnsProduct() throws ProductNotFoundException {
            // given
            when(productService.findById(PRODUCT_ID)).thenReturn(product);

            // when
            ResponseEntity<Product> response = productController.findById(PRODUCT_ID);

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isEqualTo(product);
        }

        @Test
        void testFindByIdThrowsProductNotFoundException() throws ProductNotFoundException {
            // given
            when(productService.findById(PRODUCT_ID)).thenThrow(exception);

            // when
            try {
                productController.findById(PRODUCT_ID);
            } catch (ProductNotFoundException e) {
                // then
                assertThat(e.getMessage()).isEqualTo("Entity with id: " + PRODUCT_ID + " was not found.");
            }
        }
    }

    @Nested
    @DisplayName("Process Creation Form")
    class ProcessCreationForm {

        @Test
        void testProcessCreationForm() {
            // given
            when(productService.save(productCaptor.capture())).thenReturn(product);

            // when
            ResponseEntity<HttpResponse> response = productController.processCreationForm(product);

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getStatus()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody().getStatusCode()).isEqualTo(HttpStatus.CREATED.value());
            assertThat(response.getBody().getMessage()).isEqualTo("Product Created");
            assertThat(response.getBody().getData().get("product")).isEqualTo(product);
            assertThat(productCaptor.getValue()).isEqualTo(product);
        }
    }

    @Nested
    @DisplayName("Process Update Form")
    class ProcessUpdateForm {
        @Test
        void testProcessUpdateForm() {
            // given
            Product expected = new Product();
            expected.setName("Product Updated");
            when(productService.update(productCaptor.capture())).thenReturn(expected);

            // when
            ResponseEntity<HttpResponse> response = productController.processUpdateForm(product, PRODUCT_ID);
            Product actuall = (Product) response.getBody().getData().get("product");

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getStatus()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getStatusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(response.getBody().getMessage()).isEqualTo("Product Updated");
            assertThat(response.getBody().getData().get("product")).isEqualTo(expected);
            assertThat(actuall.getName()).isEqualTo(expected.getName());
            assertThat(actuall.getId()).isEqualTo(expected.getId());
            assertThat(productCaptor.getValue()).isEqualTo(product);
        }
    }

    @Nested()
    @DisplayName("Delete Product")
    class DeleteProduct {

        @SneakyThrows
        @Test
        void testDeleteProduct() {
            // given
            when(productService.findById(productIdCaptor.capture())).thenReturn(product);

            // when
            ResponseEntity<HttpResponse> response = (ResponseEntity<HttpResponse>) productController.deleteEntity(PRODUCT_ID);

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getStatus()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getStatusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(response.getBody().getMessage()).isEqualTo("Product Deleted");
            assertEquals(Date.class, response.getBody().getTimeStamp().getClass());
            assertThat(response.getBody().getData().get("product")).isEqualTo(product);
            assertThat(productIdCaptor.getValue()).isEqualTo(PRODUCT_ID);
            verify(productService, times(1)).findById(productIdCaptor.getValue());
            verify(productService, times(1)).deleteById(productIdCaptor.getValue());
        }

        @Test
        void testDeleteProductThrowsProductNotFoundException() throws ProductNotFoundException {
            // given
            when(productService.findById(PRODUCT_ID)).thenThrow(exception);

            // when
            try {
                productController.deleteEntity(PRODUCT_ID);
            } catch (ProductNotFoundException e) {
                // then
                assertThat(e.getMessage()).isEqualTo("Entity with id: " + PRODUCT_ID + " was not found.");
                verify(productService, times(1)).findById(PRODUCT_ID);
            }
        }
    }
}