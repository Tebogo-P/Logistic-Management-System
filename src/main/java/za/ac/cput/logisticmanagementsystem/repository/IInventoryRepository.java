package za.ac.cput.logisticmanagementsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import za.ac.cput.logisticmanagementsystem.domain.Inventory;

public interface IInventoryRepository extends JpaRepository<Inventory, String> {
    boolean existsByCompanyId(String companyId);
}
