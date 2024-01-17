package com.github.valhio.storeapi.service.impl;

import com.github.valhio.storeapi.enumeration.PaymentMethod;
import com.github.valhio.storeapi.exception.domain.InvoiceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceImplTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @InjectMocks
    private InvoiceServiceImpl invoiceService;


    @Nested
    @DisplayName("Test create invoice from order")
    class CreateInvoiceFromOrder {

        @Test
        @DisplayName("Test create invoice from order with success")
        void testCreateInvoiceFromOrder() {
            Order order = new Order();
            order.setId(1L);
            order.setOrderId("orderNumber");
            order.setOrderDate(LocalDateTime.now());
            order.setProductsTotal(100.0);
            order.setDiscount(10.0);
            order.setDeliveryFee(7.0);
            order.setTotalAmount((order.getProductsTotal() + order.getDeliveryFee()) - order.getDiscount());
            order.setPaymentMethod(PaymentMethod.CARD_PAYMENT);

            when(invoiceRepository.save(any())).thenReturn(new Invoice());

            Invoice invoice = invoiceService.createInvoiceFromOrder(order);

            assertNotNull(invoice);
            assertEquals(order.getId(), invoice.getOrder().getId());
            assertEquals(order.getOrderId(), invoice.getOrder().getOrderId());
            assertEquals(order.getOrderDate(), invoice.getInvoiceDate());
            assertEquals(order.getProductsTotal(), invoice.getInvoiceSubTotal());
            assertEquals(order.getDiscount(), invoice.getInvoiceDiscount());
            assertEquals(order.getDeliveryFee(), invoice.getInvoiceShippingCost());
            assertEquals(order.getTotalAmount(), invoice.getInvoiceTotal());
            assertEquals(order.getPaymentMethod(), invoice.getInvoicePaymentMethod());
            verify(invoiceRepository, times(1)).save(any());
        }

    }

    @Nested
    @DisplayName("Test save invoice")
    class SaveInvoice {

        @Test
        @DisplayName("Test save invoice with success")
        void testSaveInvoice() {
            Invoice expected = new Invoice();
            expected.setId(1L);
            when(invoiceRepository.save(any())).thenReturn(expected);

            Invoice invoice = invoiceService.save(new Invoice());

            assertNotNull(invoice);
            assertEquals(expected.getId(), invoice.getId());
            verify(invoiceRepository, times(1)).save(any());
        }

        @Test
        @DisplayName("Test save invoice with null invoice should throw exception")
        void testSaveInvoiceWithNullInvoice() {
            assertThrows(IllegalArgumentException.class, () -> invoiceService.save(null));
            verify(invoiceRepository, never()).save(any());
        }

    }

    @Nested
    @DisplayName("Test find by id")
    class FindById {

        @Test
        @DisplayName("Test find by id with success")
        void testFindById() throws InvoiceNotFoundException {
            Invoice expected = new Invoice();
            expected.setId(1L);
            when(invoiceRepository.findById(any())).thenReturn(java.util.Optional.of(expected));

            Invoice invoice = invoiceService.findById(1L);

            assertNotNull(invoice);
            assertEquals(expected.getId(), invoice.getId());
            verify(invoiceRepository, times(1)).findById(any());
        }

        @Test
        @DisplayName("Test find by id should throw exception when invoice not found")
        void testFindByIdWithNullId() {
            when(invoiceRepository.findById(any())).thenReturn(java.util.Optional.empty());
            assertThrows(InvoiceNotFoundException.class, () -> invoiceService.findById(1L));
        }

    }

    @Nested
    @DisplayName("Test find by invoice number")
    class FindByInvoiceNumber {

        @Test
        @DisplayName("Test find by invoice number with success")
        void testFindByInvoiceNumber() throws InvoiceNotFoundException {
            Invoice expected = new Invoice();
            expected.setId(1L);
            expected.setInvoiceNumber("invoiceNumber");
            when(invoiceRepository.findByInvoiceNumber(any())).thenReturn(java.util.Optional.of(expected));

            Invoice invoice = invoiceService.findByInvoiceNumber("invoiceNumber");

            assertNotNull(invoice);
            assertEquals(expected.getId(), invoice.getId());
            assertEquals(expected.getInvoiceNumber(), invoice.getInvoiceNumber());
            verify(invoiceRepository, times(1)).findByInvoiceNumber(any());
        }

        @Test
        @DisplayName("Test find by invoice number should throw exception when invoice not found")
        void testFindByInvoiceNumberWithNullInvoiceNumber() {
            when(invoiceRepository.findByInvoiceNumber(any())).thenReturn(java.util.Optional.empty());
            assertThrows(InvoiceNotFoundException.class, () -> invoiceService.findByInvoiceNumber("invoiceNumber"));
        }

    }

    @Nested
    @DisplayName("Test find by order number")
    class FindByOrderNumber {

        @Test
        @DisplayName("Test find by order number with success")
        void testFindByOrderNumber() throws InvoiceNotFoundException {
            Invoice expected = new Invoice();
            expected.setId(1L);
            expected.setInvoiceNumber("invoiceNumber");
            when(invoiceRepository.findByOrderNumber(any())).thenReturn(java.util.Optional.of(expected));

            Invoice invoice = invoiceService.findByOrderNumber("orderNumber");

            assertNotNull(invoice);
            assertEquals(expected.getId(), invoice.getId());
            assertEquals(expected.getInvoiceNumber(), invoice.getInvoiceNumber());
            verify(invoiceRepository, times(1)).findByOrderNumber(any());
        }

        @Test
        @DisplayName("Test find by order number should throw exception when invoice not found")
        void testFindByOrderNumberWithNullOrderNumber() {
            when(invoiceRepository.findByOrderNumber(any())).thenReturn(java.util.Optional.empty());
            assertThrows(InvoiceNotFoundException.class, () -> invoiceService.findByOrderNumber("orderNumber"));
        }

    }

    @Nested
    @DisplayName("Test find all")
    class FindAll {

        @Test
        @DisplayName("Test find all with success")
        void testFindAll() {
            when(invoiceRepository.findAll()).thenReturn(List.of(new Invoice(), new Invoice()));

            Iterable<Invoice> all = invoiceService.findAll();

            assertNotNull(all);
            assertEquals(2, ((List<Invoice>) all).size());
            verify(invoiceRepository, times(1)).findAll();
        }

    }

    @Nested
    @DisplayName("Test delete by id")
    class DeleteById {

        @Test
        @DisplayName("Test delete by id with success")
        void testDeleteById() throws InvoiceNotFoundException {
            when(invoiceRepository.findById(any())).thenReturn(java.util.Optional.of(new Invoice()));
            doNothing().when(invoiceRepository).delete(any());

            invoiceService.deleteById(1L);

            verify(invoiceRepository, times(1)).findById(any());
            verify(invoiceRepository, times(1)).delete(any());
        }

        @Test
        @DisplayName("Test delete by id should throw exception when invoice not found")
        void testDeleteByIdWithNullId() {
            when(invoiceRepository.findById(any())).thenReturn(java.util.Optional.empty());
            assertThrows(InvoiceNotFoundException.class, () -> invoiceService.deleteById(1L));
            verify(invoiceRepository, times(1)).findById(any());
            verify(invoiceRepository, never()).delete(any());
        }

    }

    @Nested
    @DisplayName("Test delete by invoice number")
    class DeleteByInvoiceNumber {

        @Test
        @DisplayName("Test delete by invoice number with success")
        void testDeleteByInvoiceNumber() throws InvoiceNotFoundException {
            when(invoiceRepository.findByInvoiceNumber(any())).thenReturn(java.util.Optional.of(new Invoice()));
            doNothing().when(invoiceRepository).delete(any());

            invoiceService.deleteByInvoiceNumber("invoiceNumber");

            verify(invoiceRepository, times(1)).findByInvoiceNumber(any());
            verify(invoiceRepository, times(1)).delete(any());
        }

        @Test
        @DisplayName("Test delete by invoice number should throw exception when invoice not found")
        void testDeleteByInvoiceNumberWithNullInvoiceNumber() {
            when(invoiceRepository.findByInvoiceNumber(any())).thenReturn(java.util.Optional.empty());
            assertThrows(InvoiceNotFoundException.class, () -> invoiceService.deleteByInvoiceNumber("invoiceNumber"));
            verify(invoiceRepository, times(1)).findByInvoiceNumber(any());
            verify(invoiceRepository, never()).delete(any());
        }

    }

}