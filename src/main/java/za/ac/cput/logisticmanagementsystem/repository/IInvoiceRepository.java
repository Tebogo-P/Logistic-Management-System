package za.ac.cput.logisticmanagementsystem.repository;

import za.ac.cput.logisticmanagementsystem.domain.Invoice;
import java.util.Set;

/**
 * IInvoiceRepository.java
 * Repository interface for Invoice entity
 * Author: Tebogo Pii 230226442
 * Date: 27 July 2026
 */

public interface IInvoiceRepository extends IRepository<Invoice, String> {
    Set<Invoice> getAll();
}
