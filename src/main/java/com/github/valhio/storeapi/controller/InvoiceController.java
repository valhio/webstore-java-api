package com.github.valhio.storeapi.controller;

import com.github.valhio.storeapi.exception.domain.InvoiceNotFoundException;
import com.github.valhio.storeapi.model.Invoice;
import com.github.valhio.storeapi.model.UserPrincipal;
import com.github.valhio.storeapi.service.InvoiceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("api/v1/invoice")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @GetMapping("/{invoiceNumber}")
    public ResponseEntity<Invoice> getInvoiceByInvoiceNumber(@AuthenticationPrincipal UserPrincipal auth, @PathVariable String invoiceNumber) throws InvoiceNotFoundException {
        Invoice invoice = invoiceService.findByInvoiceNumber(invoiceNumber);

        if (auth.getEmail().equals(invoice.getUser().getEmail()))
            return ResponseEntity.ok(invoice);
        else
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

    }
}