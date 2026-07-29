package za.ac.cput.logisticmanagementsystem.controller;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import za.ac.cput.logisticmanagementsystem.domain.Invoice;
import za.ac.cput.logisticmanagementsystem.factory.InvoiceFactory;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class InvoiceControllerTest {

    @Autowired
    private InvoiceController controller;

    private static Invoice invoice;

    @Test
    @Order(1)
    void create() {
        invoice = InvoiceFactory.buildInvoice(2500.50, "Pending", new Date());

        ResponseEntity<Invoice> response = controller.create(invoice);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Invoice savedInvoice = response.getBody();
        assertNotNull(savedInvoice);
        assertEquals(invoice.getInvoiceId(), savedInvoice.getInvoiceId());
        System.out.println("Saved data: " + savedInvoice);
    }

    @Test
    @Order(2)
    void read() {
        ResponseEntity<Invoice> response = controller.read(invoice.getInvoiceId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(invoice.getInvoiceId(), response.getBody().getInvoiceId());
        System.out.println("Read: " + response.getBody());
    }

    @Test
    @Order(3)
    void update() {
        Invoice updated = new Invoice.Builder()
                .invoiceId(invoice.getInvoiceId())
                .total(3000.00)
                .paymentStatus("Paid")
                .dateIssued(invoice.getDateIssued())
                .build();

        ResponseEntity<Invoice> response = controller.update(updated);

        assertNotNull(response.getBody());
        assertEquals("Paid", response.getBody().getPaymentStatus());
        System.out.println("Updated: " + response.getBody());
    }

    @Test
    @Order(4)
    void deleteTest() {
        controller.delete(invoice.getInvoiceId());
        System.out.println("Delete executed.");
    }

    @Test
    @Order(5)
    void getAll() {
        ResponseEntity<?> response = controller.getAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        System.out.println("Get All successful.");
    }
}