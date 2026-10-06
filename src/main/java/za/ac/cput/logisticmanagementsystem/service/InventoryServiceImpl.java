package za.ac.cput.logisticmanagementsystem.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.ac.cput.logisticmanagementsystem.domain.Inventory;
import za.ac.cput.logisticmanagementsystem.repository.InventoryRepository;

import java.util.List;
import java.util.UUID;

@Service
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository repository;

    public InventoryServiceImpl(InventoryRepository repository) {
        this.repository = repository;
    }

    @Override
    public Inventory addStock(Inventory inventory) {
        validate(inventory);
        if (inventory.getInventoryId() == null || inventory.getInventoryId().isBlank()) {
            inventory.setInventoryId(UUID.randomUUID().toString());
        }
        if (repository.existsById(inventory.getInventoryId())) {
            throw new IllegalStateException("An inventory item with this ID already exists.");
        }
        return repository.save(inventory);
    }

    @Override
    public Inventory update(Inventory inventory) {
        validate(inventory);
        if (inventory.getInventoryId() == null || inventory.getInventoryId().isBlank()) {
            throw new IllegalArgumentException("Inventory ID is required.");
        }
        if (!repository.existsById(inventory.getInventoryId())) {
            throw new java.util.NoSuchElementException("Inventory item was not found.");
        }
        return repository.save(inventory);
    }

    @Override
    public Inventory updateQuantity(String inventoryId, int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Available quantity cannot be negative.");
        }
        Inventory inventory = read(inventoryId);
        inventory.setQuantityAvailable(quantity);
        return repository.save(inventory);
    }

    @Override
    public boolean delete(String inventoryId) {
        Inventory inventory = read(inventoryId);
        repository.delete(inventory);
        return true;
    }

    @Override
    @Transactional
    public Inventory deductStock(String inventoryId, int quantity) {
        if (quantity < 1) {
            throw new IllegalArgumentException("Quantity to deduct must be at least 1.");
        }
        Inventory inventory = read(inventoryId);
        if (inventory.getQuantityAvailable() < quantity) {
            throw new IllegalStateException("Insufficient stock.");
        }
        inventory.setQuantityAvailable(inventory.getQuantityAvailable() - quantity);
        return repository.save(inventory);
    }

    @Override
    public boolean checkAvailability(String inventoryId, int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative.");
        }
        return repository.findById(inventoryId)
                .map(inventory -> inventory.getQuantityAvailable() >= quantity)
                .orElse(false);
    }

    @Override
    public Inventory read(String inventoryId) {
        return repository.findById(inventoryId)
                .orElseThrow(() -> new java.util.NoSuchElementException("Inventory item was not found."));
    }

    @Override
    public List<Inventory> getAll() {
        return repository.findAll();
    }

    private void validate(Inventory inventory) {
        if (inventory == null) {
            throw new IllegalArgumentException("Inventory details are required.");
        }
        if (inventory.getItemName() == null || inventory.getItemName().isBlank()
                || inventory.getSku() == null || inventory.getSku().isBlank()
                || inventory.getCompanyId() == null || inventory.getCompanyId().isBlank()) {
            throw new IllegalArgumentException("Item name, SKU, and company ID are required.");
        }
        if (inventory.getQuantityAvailable() < 0) {
            throw new IllegalArgumentException("Available quantity cannot be negative.");
        }
        if (!Double.isFinite(inventory.getUnitWeight()) || inventory.getUnitWeight() < 0) {
            throw new IllegalArgumentException("Unit weight must be a finite, non-negative number.");
        }
    }
}
