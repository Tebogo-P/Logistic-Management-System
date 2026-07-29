package za.ac.cput.logisticmanagementsystem.repository;

import org.junit.jupiter.api.*;
import za.ac.cput.logisticmanagementsystem.domain.Invoice;
import za.ac.cput.logisticmanagementsystem.factory.InvoiceFactory;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;

/**
 * InvoiceRepositoryTest.java
 * Test class for InvoiceRepository
 * Author: Tebogo Pii 230226442
 * Date: 27 July 2026
 */

@TestMethodOrder(MethodOrderer.MethodName.class)
class InvoiceRepositoryTest {

    private static final IInvoiceRepository repository = InvoiceRepository.getRepository();
    private static Invoice invoice = InvoiceFactory.buildInvoice(1000.0, "Pending", new Date());

    @Test
    void createInvoice() {
        Invoice created = repository.create(invoice);
        assertNotNull(created);
        assertEquals(invoice.getInvoiceId(), created.getInvoiceId());
        System.out.println("Created: " + created);
    }

    @Test
    void readInvoice() {
        Invoice read = repository.read(invoice.getInvoiceId());
        assertNotNull(read);
        assertEquals(invoice.getTotal(), read.getTotal());
        System.out.println("Read: " + read);
    }

    @Test
    void updateInvoice() {
        Invoice updatedInvoice = new Invoice.Builder()
                .invoiceId(invoice.getInvoiceId()) // Use existing ID
                .total(2000.0)
                .paymentStatus("Paid")
                .dateIssued(invoice.getDateIssued())
                .build();

        Invoice updated = repository.update(updatedInvoice);
        assertNotNull(updated);
        assertEquals("Paid", updated.getPaymentStatus());
        assertEquals(2000.0, updated.getTotal());
        System.out.println("Updated: " + updated);
        invoice = updated; // Update the static invoice variable for subsequent tests
    }

    @Test
    void getAllInvoice() {
        assertFalse(repository.getAll().isEmpty());
        System.out.println("All invoices: " + repository.getAll());
    }

    @Test
    void deleteInvoice() {
        boolean deleted = repository.delete(invoice.getInvoiceId());
        assertTrue(deleted);
        System.out.println("Deleted Successfully: " + invoice.getInvoiceId());
    }
}
