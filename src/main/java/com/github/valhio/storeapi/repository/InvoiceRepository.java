package com.github.valhio.storeapi.repository;

import com.github.valhio.storeapi.model.Invoice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvoiceRepository extends PagingAndSortingRepository<Invoice, Long> {

    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

    @Query("SELECT i FROM Invoice i WHERE i.order.orderId = ?1")
    Optional<Invoice> findByOrderNumber(String orderNumber);
}
