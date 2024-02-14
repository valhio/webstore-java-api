package com.github.valhio.storeapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.valhio.storeapi.enumeration.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.PostPersist;
import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "invoices")
public class Invoice extends Auditable<String> {

    @Id
    private String id;

    private String invoiceNumber;
    private LocalDateTime invoiceDate;
    private LocalDateTime invoiceDueDate;
    private String invoiceStatus; // This field will be used to store the status of the invoice (e.g. "Paid", "Unpaid", "Refunded", etc.)
    private String invoiceNotes;
    private String invoiceTerms; // This field will be used to store the terms of the invoice (e.g. "Payment due within 30 days", "Payment due within 15 days", etc.)
    private PaymentMethod invoicePaymentMethod; // This field will be used to store the payment method of the invoice (e.g. "Credit Card", "PayPal", "Bank Transfer", etc.)
    private Double invoiceSubTotal; // This field will be used to store the subtotal of the invoice (e.g. the sum of all the products' prices)
    private Double invoiceDiscount; // This field will be used to store the discount amount of the invoice (e.g. $10 discount, $5 discount, etc.)
    private Double invoiceTaxPercent;
    private Double invoiceTaxAmount;
    private String invoiceShipping; // This field will be used to store the shipping method (e.g. "Free Shipping", "Express Shipping", etc.)
    private Double invoiceShippingCost;
    private Double invoiceTotal;
    private String invoiceAmountDue;
    private String invoiceAmountPaid;
    private String invoiceAmountRefunded;

    private User user;

    @JsonIgnoreProperties("invoice")
    private Order order;

}