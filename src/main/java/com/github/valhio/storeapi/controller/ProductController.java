package com.github.valhio.storeapi.controller;

import com.github.valhio.storeapi.domain.HttpResponse;
import com.github.valhio.storeapi.exception.domain.ProductNotFoundException;
import com.github.valhio.storeapi.model.Product;
import com.github.valhio.storeapi.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

/*
    The ProductController class is a REST API controller for handling products in a store. It contains several API endpoints for creating,
    retrieving, and updating products, as well as retrieving products.
*/

//@CrossOrigin
@RestController
@RequestMapping("api/v1/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("")
    public ResponseEntity<HttpResponse> fetchAll(@RequestParam(defaultValue = "0") Integer page,
                                                 @RequestParam(defaultValue = "10") Integer size,
                                                 @RequestParam(defaultValue = "id") String sort,
                                                 @RequestParam(defaultValue = "") String keyword
    ) {
        Page<Product> data = productService.findAll(page, size, sort, keyword);
        return ResponseEntity.ok(HttpResponse.builder()
                .timeStamp(new Date())
                .data(Map.of("products", data))
                .message("Products Retrieved")
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> findById(@PathVariable Long id) throws ProductNotFoundException {
        Product data = productService.findById(id);
        return ResponseEntity.ok(data);

//        return ResponseEntity.ok(HttpResponse.builder()
//                .timeStamp(new Date())
//                .data(Map.of("product", data))
//                .message("Product Retrieved")
//                .status(HttpStatus.OK)
//                .statusCode(HttpStatus.OK.value())
//                .build());
    }

    @PostMapping("/new")
    @ResponseBody
    @PreAuthorize("hasAnyAuthority('CREATE','UPDATE')")
    public ResponseEntity<HttpResponse> processCreationForm(@RequestBody Product product) {
        return ResponseEntity.ok(HttpResponse.builder()
                .timeStamp(new Date())
                .data(Map.of("product", productService.save(product)))
                .message("Product Created")
                .status(HttpStatus.CREATED)
                .statusCode(HttpStatus.CREATED.value())
                .build());
    }

    @PostMapping("/{id}/edit")
    @ResponseBody
    public ResponseEntity<HttpResponse> processUpdateForm(@RequestBody Product product,
                                                          @PathVariable String id) {
        product.setId(id);
        return ResponseEntity.ok(HttpResponse.builder()
                .timeStamp(new Date())
                .data(Map.of("product", productService.update(product)))
                .message("Product Updated")
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    @DeleteMapping("/{id}/delete")
    @ResponseBody
    public ResponseEntity<?> deleteEntity(@PathVariable Long id) throws ProductNotFoundException {
        Product dto = productService.findById(id);
        productService.deleteById(id);
        return ResponseEntity.ok(HttpResponse.builder()
                .timeStamp(new Date())
                .data(Map.of("product", dto))
                .message("Product Deleted")
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .build());
    }
}