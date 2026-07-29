package za.ac.cput.logisticmanagementsystem.service;

import org.springframework.stereotype.Service;
import za.ac.cput.logisticmanagementsystem.domain.Invoice;
import za.ac.cput.logisticmanagementsystem.repository.InvoiceRepository;

import java.util.List;
import java.util.Optional;

/**
 * InvoiceService.java
 * Service implementation for Invoice business logic
 * Author: Tebogo Pii 230226442
 * Date: 27 July 2026
 */

@Service
public class InvoiceService implements IInvoiceService {

    private final InvoiceRepository repository;

    public InvoiceService(InvoiceRepository repository) {
        this.repository = repository;
    }

    @Override
    public Invoice create(Invoice invoice) {
        if (invoice == null) {
            return null;
        }
        return repository.save(invoice);
    }

    @Override
    public Invoice read(String invoiceId) {
        Optional<Invoice> invoice = repository.findById(invoiceId);
        return invoice.orElse(null);
    }

    @Override
    public Invoice update(Invoice invoice) {
        if (invoice == null) {
            return null;
        }
        return repository.save(invoice);
    }

    @Override
    public boolean delete(String invoiceId) {
        try {
            repository.deleteById(invoiceId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<Invoice> getAll() {
        return repository.findAll();
    }

    @Override
    public Invoice updatePaymentStatus(String invoiceId, String paymentStatus) {
        Invoice invoice = read(invoiceId);
        if (invoice != null && paymentStatus != null && !paymentStatus.isEmpty()) {
            Invoice updated = new Invoice.Builder()
                    .invoiceId(invoice.getInvoiceId())
                    .total(invoice.getTotal())
                    .paymentStatus(paymentStatus)
                    .dateIssued(invoice.getDateIssued())
                    .build();
            return repository.save(updated);
        }
        return null;
    }
}
