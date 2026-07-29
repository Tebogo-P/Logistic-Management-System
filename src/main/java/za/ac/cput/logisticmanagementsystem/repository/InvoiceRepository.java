
package za.ac.cput.logisticmanagementsystem.repository;

import org.springframework.stereotype.Repository;
import za.ac.cput.logisticmanagementsystem.domain.Invoice;

import java.util.HashSet;
import java.util.Set;

/**
 * InvoiceRepository.java
 * Repository implementation for Invoice entity
 * Author: Tebogo Pii 230226442
 * Date: 27 July 2026
 */

@Repository
public class InvoiceRepository implements IInvoiceRepository{

    private static InvoiceRepository repository = null;
    private Set<Invoice> invoiceDB;

    private InvoiceRepository() {
        invoiceDB = new HashSet<>();
    }

    public static InvoiceRepository getRepository() {
        if (repository == null) {
            repository = new InvoiceRepository();
        }
        return repository;
    }

    @Override
    public Invoice create(Invoice invoice) {
        if (invoice == null) return null;
        boolean success = invoiceDB.add(invoice);
        return success ? invoice : null;
    }

    @Override
    public Invoice read(String invoiceId) {
        return invoiceDB.stream()
                .filter(invoice -> invoice.getInvoiceId().equals(invoiceId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Invoice update(Invoice invoice) {
        Invoice oldInvoice = read(invoice.getInvoiceId());
        if (oldInvoice != null) {
            invoiceDB.remove(oldInvoice);
            invoiceDB.add(invoice);
            return invoice;
        }
        return null;
    }

    @Override
    public boolean delete(String invoiceId) {
        Invoice invoiceToDelete = read(invoiceId);
        if (invoiceToDelete == null) return false;
        return invoiceDB.remove(invoiceToDelete);
    }

    
    public Set<Invoice> getAll() {
        return invoiceDB;
    }
}
