package za.ac.cput.logisticmanagementsystem.service;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

import za.ac.cput.logisticmanagementsystem.domain.Invoice;
import za.ac.cput.logisticmanagementsystem.factory.InvoiceFactory;
import za.ac.cput.logisticmanagementsystem.repository.InvoiceRepository;

import java.util.Date;

/**
 * InvoiceServiceTest.java
 * Test class for InvoiceService
 * Author: Tebogo Pii 230226442
 * Date: 27-28 July 2026
 */

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class InvoiceServiceTest {

    @Autowired
    private InvoiceService service;

    @Autowired
    private InvoiceRepository repository;

    private static String invoiceId;
    private static final Date dateIssued = new Date();

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    @Order(1)
    void a_testCreate() {
        Invoice invoice = InvoiceFactory.buildInvoice(2500.50, "Pending", dateIssued);
        Invoice created = service.create(invoice);
        assertNotNull(created);
        invoiceId = created.getInvoiceId();
        System.out.println("Created: " + created);
    }

    @Test
    @Order(2)
    void b_testRead() {
        Invoice invoice = InvoiceFactory.buildInvoice(1500.00, "Paid", dateIssued);
        Invoice created = service.create(invoice);
        Invoice read = service.read(created.getInvoiceId());
        assertNotNull(read);
        assertEquals(1500.00, read.getTotal());
    }

    @Test
    @Order(3)
    void c_testUpdate() {
        Invoice invoice = InvoiceFactory.buildInvoice(1000.00, "Pending", dateIssued);
        Invoice created = service.create(invoice);
        
        Invoice updated = new Invoice.Builder()
                .invoiceId(created.getInvoiceId())
                .total(2000.00)
                .paymentStatus("Paid")
                .dateIssued(dateIssued)
                .build();
        
        Invoice result = service.update(updated);
        assertNotNull(result);
        assertEquals(2000.00, result.getTotal());
    }

    @Test
    @Order(4)
    void d_testUpdatePaymentStatus() {
        Invoice invoice = InvoiceFactory.buildInvoice(1800.00, "Pending", dateIssued);
        Invoice created = service.create(invoice);
        Invoice updated = service.updatePaymentStatus(created.getInvoiceId(), "Overdue");
        assertNotNull(updated);
        assertEquals("Overdue", updated.getPaymentStatus());
    }

    @Test
    @Order(5)
    void e_testGetAll() {
        service.create(InvoiceFactory.buildInvoice(1000.00, "Paid", dateIssued));
        service.create(InvoiceFactory.buildInvoice(2000.00, "Pending", dateIssued));
        assertTrue(service.getAll().size() >= 2);
    }

    @Test
    @Order(6)
    void f_testDelete() {
        Invoice invoice = InvoiceFactory.buildInvoice(900.00, "Paid", dateIssued);
        Invoice created = service.create(invoice);
        boolean deleted = service.delete(created.getInvoiceId());
        assertTrue(deleted);
        assertNull(service.read(created.getInvoiceId()));
    }

    @Test
    void testCreateWithNull() {
        assertNull(service.create(null));
    }

    @Test
    void testReadNonExistent() {
        assertNull(service.read("invalid-id"));
    }

    @Test
    void testUpdateWithNull() {
        assertNull(service.update(null));
    }

    @Test
    void testUpdatePaymentStatusWithInvalidInput() {
        Invoice invoice = InvoiceFactory.buildInvoice(1200.00, "Pending", dateIssued);
        Invoice created = service.create(invoice);
        
        assertNull(service.updatePaymentStatus("invalid-id", "Paid"));
        assertNull(service.updatePaymentStatus(created.getInvoiceId(), null));
        assertNull(service.updatePaymentStatus(created.getInvoiceId(), ""));
    }
}


