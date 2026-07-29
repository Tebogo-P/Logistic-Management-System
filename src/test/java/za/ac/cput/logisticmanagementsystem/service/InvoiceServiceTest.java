package za.ac.cput.logisticmanagementsystem.service;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import za.ac.cput.logisticmanagementsystem.domain.Invoice;
import za.ac.cput.logisticmanagementsystem.factory.InvoiceFactory;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;

/**
 * InvoiceServiceTest.java
 * Service implementation for Invoice business logic tests
 * Author: Tebogo Pii 230226442
 * Date: 29 July 2026
 */

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class InvoiceServiceTest {

    private static final IInvoiceService service = new InvoiceService();
    private static final Invoice invoice = InvoiceFactory.buildInvoice(2500.50, "Pending", new Date());

    @Test
    @Order(1)
    void create() {
        Invoice created = service.create(invoice);
        assertNotNull(created);
        assertEquals(invoice.getInvoiceId(), created.getInvoiceId());
        System.out.println("Created: " + created);
    }

    @Test
    @Order(2)
    void read() {
        Invoice read = service.read(invoice.getInvoiceId());
        assertNotNull(read);
        assertEquals(2500.50, read.getTotal());
        System.out.println("Read: " + read);
    }

    @Test
    @Order(3)
    void update() {
        Invoice updatedInvoice = new Invoice.Builder()
                .invoiceId(invoice.getInvoiceId())
                .total(3000.00)
                .paymentStatus("Paid")
                .dateIssued(invoice.getDateIssued())
                .build();

        Invoice updated = service.update(updatedInvoice);
        assertNotNull(updated);
        assertEquals(3000.00, updated.getTotal());
        System.out.println("Updated: " + updated);
    }

    @Test
    @Order(4)
    void updatePaymentStatus() {
        Invoice updated = service.updatePaymentStatus(invoice.getInvoiceId(), "Overdue");
        assertNotNull(updated);
        assertEquals("Overdue", updated.getPaymentStatus());
        System.out.println("Updated Payment Status: " + updated);
    }

    @Test
    @Order(5)
    void getAll() {
        assertFalse(service.getAll().isEmpty());
        System.out.println("All invoices: " + service.getAll());
    }

    @Test
    @Order(6)
    void delete() {
        boolean deleted = service.delete(invoice.getInvoiceId());
        assertTrue(deleted);
        assertNull(service.read(invoice.getInvoiceId()));
        System.out.println("Deleted Successfully: " + invoice.getInvoiceId());
    }

    @Test
    void testCreateWithNullInvoice() {
        Invoice created = service.create(null);
        assertNull(created);
    }

    @Test
    void testUpdatePaymentStatusWithInvalidInput() {
        Invoice testInvoice = InvoiceFactory.buildInvoice(1200.00, "Pending", new Date());
        service.create(testInvoice);

        assertNull(service.updatePaymentStatus("invalid-id", "Paid"));
        assertNull(service.updatePaymentStatus(testInvoice.getInvoiceId(), null));
        assertNull(service.updatePaymentStatus(testInvoice.getInvoiceId(), ""));
    }
}
