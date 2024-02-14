package com.github.valhio.storeapi.service;

import com.github.valhio.storeapi.exception.domain.InvoiceNotFoundException;
import com.github.valhio.storeapi.model.Invoice;
import com.github.valhio.storeapi.model.Order;

public interface InvoiceService {

    Invoice createInvoiceFromOrder(Order order);

    Invoice save(Invoice invoice);

    Invoice findById(String id) throws InvoiceNotFoundException;

    Invoice findByInvoiceNumber(String invoiceNumber) throws InvoiceNotFoundException;

    Invoice findByOrderNumber(String orderNumber) throws InvoiceNotFoundException;

    Iterable<Invoice> findAll();

    void deleteById(String id) throws InvoiceNotFoundException;

    void deleteByInvoiceNumber(String invoiceNumber) throws InvoiceNotFoundException;

}
