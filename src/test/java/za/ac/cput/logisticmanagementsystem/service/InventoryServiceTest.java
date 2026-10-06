package za.ac.cput.logisticmanagementsystem.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import za.ac.cput.logisticmanagementsystem.domain.Inventory;
import za.ac.cput.logisticmanagementsystem.repository.InventoryRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private InventoryRepository repository;

    private InventoryService service;

    @BeforeEach
    void setUp() {
        service = new InventoryServiceImpl(repository);
    }

    @Test
    void createsAnInventoryItemAndGeneratesItsId() {
        when(repository.existsById(anyString())).thenReturn(false);
        when(repository.save(any(Inventory.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Inventory created = service.addStock(inventory(null, 20));

        assertNotNull(created.getInventoryId());
        assertEquals("SKU-01", created.getSku());
        verify(repository).save(created);
    }

    @Test
    void deductsStockAndPersistsTheUpdatedQuantity() {
        Inventory item = inventory("inventory-1", 10);
        when(repository.findById("inventory-1")).thenReturn(Optional.of(item));
        when(repository.save(any(Inventory.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Inventory updated = service.deductStock("inventory-1", 3);

        assertEquals(7, updated.getQuantityAvailable());
        verify(repository).save(item);
    }

    @Test
    void refusesDeductionWhenStockIsInsufficient() {
        when(repository.findById("inventory-1"))
                .thenReturn(Optional.of(inventory("inventory-1", 2)));

        assertThrows(IllegalStateException.class,
                () -> service.deductStock("inventory-1", 3));
        verify(repository, never()).save(any(Inventory.class));
    }

    @Test
    void rejectsNegativeAvailableQuantity() {
        assertThrows(IllegalArgumentException.class,
                () -> service.addStock(inventory(null, -1)));
    }

    private Inventory inventory(String id, int quantity) {
        return new Inventory.Builder()
                .setInventoryId(id)
                .setItemName("Laptop")
                .setSku("SKU-01")
                .setCompanyId("COMP-01")
                .setQuantityAvailable(quantity)
                .setUnitWeight(2.5)
                .build();
    }
}
