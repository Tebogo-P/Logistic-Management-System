package za.ac.cput.logisticmanagementsystem.service;

import org.springframework.stereotype.Service;
import za.ac.cput.logisticmanagementsystem.domain.Invoice;
import za.ac.cput.logisticmanagementsystem.repository.IInvoiceRepository;
import za.ac.cput.logisticmanagementsystem.repository.InvoiceRepository;

import java.util.List;

/**
 * InvoiceService.java
 * Service implementation for Invoice business logic
 * Author: Tebogo Pii 230226442
 * Date: 27 July 2026
 */

@Service
public class InvoiceService implements IInvoiceService {

    private final IInvoiceRepository repository;

    public InvoiceService() {
        this.repository = InvoiceRepository.getRepository();
    }

    @Override
    public Invoice create(Invoice invoice) {
        if (invoice == null) {
            return null;
        }
        return repository.create(invoice);
    }

    @Override
    public Invoice read(String invoiceId) {
        return repository.read(invoiceId);
    }

    @Override
    public Invoice update(Invoice invoice) {
        if (invoice == null) {
            return null;
        }
        return repository.update(invoice);
    }

    @Override
    public boolean delete(String invoiceId) {
        return repository.delete(invoiceId);
    }

    @Override
    public List<Invoice> getAll() {
        return (List<Invoice>) repository.getAll();
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
            return repository.update(updated);
        }
        return null;
    }
}
