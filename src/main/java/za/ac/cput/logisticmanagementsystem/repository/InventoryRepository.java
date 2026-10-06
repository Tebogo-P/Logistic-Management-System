package za.ac.cput.logisticmanagementsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.ac.cput.logisticmanagementsystem.domain.Inventory;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, String> {
}
