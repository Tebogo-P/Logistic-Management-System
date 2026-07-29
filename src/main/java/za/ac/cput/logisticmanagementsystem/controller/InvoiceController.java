package za.ac.cput.logisticmanagementsystem.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.logisticmanagementsystem.domain.Invoice;
import za.ac.cput.logisticmanagementsystem.factory.InvoiceFactory;
import za.ac.cput.logisticmanagementsystem.service.IInvoiceService;
import java.util.List;

/**
 * InvoiceService.java
 * Service implementation for Invoice business logic
 * Author: Tebogo Pii 230226442
 * Date: 29 July 2026
 */

@RestController
@RequestMapping("/api/invoice")
@CrossOrigin(origins = "http://localhost:3000")
public class InvoiceController {

    private final IInvoiceService invoiceService;

    public InvoiceController(IInvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @PostMapping("/create")
    public ResponseEntity<Invoice> create(@RequestBody Invoice payload) {
        if (payload == null) {
            return ResponseEntity.badRequest().build();
        }
        Invoice invoice = InvoiceFactory.buildInvoice(payload.getTotal(), payload.getPaymentStatus(), payload.getDateIssued());
        if (invoice == null) {
            return ResponseEntity.badRequest().build();
        }
        Invoice created = invoiceService.create(invoice);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/read/{id}")
    public ResponseEntity<Invoice> read(@PathVariable String id) {
        Invoice invoice = invoiceService.read(id);
        if (invoice == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(invoice);
    }

    @PutMapping("/update")
    public ResponseEntity<Invoice> update(@RequestBody Invoice invoice) {
        Invoice updated = invoiceService.update(invoice);
        if (updated == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        boolean deleted = invoiceService.delete(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/getall")
    public ResponseEntity<List<Invoice>> getAll() {
        return ResponseEntity.ok(invoiceService.getAll());
    }

    @PatchMapping("/update-payment/{id}")
    public ResponseEntity<Invoice> updatePaymentStatus(@PathVariable String id,
                                                       @RequestParam String paymentStatus) {
        Invoice updated = invoiceService.updatePaymentStatus(id, paymentStatus);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }
}



