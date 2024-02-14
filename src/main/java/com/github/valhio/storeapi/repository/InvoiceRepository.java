package com.github.valhio.storeapi.repository;

import com.github.valhio.storeapi.model.Invoice;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvoiceRepository extends MongoRepository<Invoice, String> {

    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

    @Query("{'order.orderNumber': ?0}")
    Optional<Invoice> findByOrderNumber(String orderId);
}

