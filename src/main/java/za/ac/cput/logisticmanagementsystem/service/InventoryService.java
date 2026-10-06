package za.ac.cput.logisticmanagementsystem.service;

import za.ac.cput.logisticmanagementsystem.domain.Inventory;

import java.util.List;

public interface InventoryService {

    Inventory addStock(Inventory inventory);

    Inventory update(Inventory inventory);

    Inventory updateQuantity(String inventoryId, int quantity);

    boolean delete(String inventoryId);

    Inventory deductStock(String inventoryId, int quantity);

    boolean checkAvailability(String inventoryId, int quantity);

    Inventory read(String inventoryId);

    List<Inventory> getAll();
}
