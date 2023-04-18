package com.github.valhio.storeapi.service.impl;

import com.github.valhio.storeapi.exception.domain.InvoiceNotFoundException;
import com.github.valhio.storeapi.model.Invoice;
import com.github.valhio.storeapi.model.Order;
import com.github.valhio.storeapi.repository.InvoiceRepository;
import com.github.valhio.storeapi.service.InvoiceService;
import org.springframework.stereotype.Service;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;

    public InvoiceServiceImpl(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }


    @Override
    public Invoice createInvoiceFromOrder(Order order) {
        Invoice invoice = new Invoice();
        invoice.setOrder(order);
        invoice.setInvoiceDate(order.getOrderDate());
        invoice.setInvoiceSubTotal(order.getProductsTotal());
        invoice.setInvoiceDiscount(order.getDiscount());
        invoice.setInvoiceShippingCost(order.getDeliveryFee());
        invoice.setInvoiceTotal(order.getTotalAmount());
        invoice.setInvoicePaymentMethod(order.getPaymentMethod());
        invoice.setUser(order.getUser());
        invoiceRepository.save(invoice);
        return invoice;
    }

    @Override
    public Invoice save(Invoice invoice) {
        if (invoice == null) throw new IllegalArgumentException("Invoice cannot be null");
        return invoiceRepository.save(invoice);
    }

    @Override
    public Invoice findById(Long id) throws InvoiceNotFoundException {
        return invoiceRepository.findById(id).orElseThrow(() -> new InvoiceNotFoundException("Invoice not found by id: " + id));
    }

    @Override
    public Invoice findByInvoiceNumber(String invoiceNumber) throws InvoiceNotFoundException {
        return invoiceRepository.findByInvoiceNumber(invoiceNumber).orElseThrow(() -> new InvoiceNotFoundException("Invoice not found by invoice number: " + invoiceNumber));
    }

    @Override
    public Invoice findByOrderNumber(String orderNumber) throws InvoiceNotFoundException {
        return invoiceRepository.findByOrderNumber(orderNumber).orElseThrow(() -> new InvoiceNotFoundException("Invoice not found by order number: " + orderNumber));
    }

    @Override
    public Iterable<Invoice> findAll() {
        return invoiceRepository.findAll();
    }

    @Override
    public void deleteById(Long id) throws InvoiceNotFoundException {
        Invoice byId = this.findById(id);
        invoiceRepository.delete(byId);
    }

    @Override
    public void deleteByInvoiceNumber(String invoiceNumber) throws InvoiceNotFoundException {
        Invoice byInvoiceNumber = this.findByInvoiceNumber(invoiceNumber);
        invoiceRepository.delete(byInvoiceNumber);
    }
}
