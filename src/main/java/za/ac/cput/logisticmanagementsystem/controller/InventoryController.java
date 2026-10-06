package za.ac.cput.logisticmanagementsystem.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.logisticmanagementsystem.domain.Inventory;
import za.ac.cput.logisticmanagementsystem.factory.InventoryFactory;
import za.ac.cput.logisticmanagementsystem.service.InventoryService;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping({"/api/inventory", "/inventory"})
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class InventoryController {

    private final InventoryService service;

    public InventoryController(InventoryService service) {
        this.service = service;
    }

    @PostMapping("/create")
    public ResponseEntity<Inventory> create(
            @RequestBody(required = false) Inventory inventory,
            @RequestParam(required = false) String itemName,
            @RequestParam(required = false) String sku,
            @RequestParam(required = false) Integer quantity,
            @RequestParam(required = false) Double unitWeight,
            @RequestParam(required = false) String companyId) {
        if (inventory == null) {
            if (itemName == null || sku == null || quantity == null
                    || unitWeight == null || companyId == null) {
                throw new IllegalArgumentException("Inventory details are required.");
            }
            inventory = InventoryFactory.createInventory(itemName, sku, quantity, unitWeight, companyId);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(service.addStock(inventory));
    }

    @GetMapping({"/all", "/getall"})
    public List<Inventory> getAll() {
        return service.getAll();
    }

    @GetMapping({"/{id}", "/read/{id}"})
    public Inventory read(@PathVariable String id) {
        return service.read(id);
    }

    @PutMapping("/update")
    public Inventory update(@RequestBody Inventory inventory) {
        return service.update(inventory);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/deduct/{id}/{qty}")
    public Inventory deduct(@PathVariable String id, @PathVariable int qty) {
        return service.deductStock(id, qty);
    }

    @PatchMapping("/update-quantity/{id}")
    public Inventory updateQuantity(@PathVariable String id, @RequestParam int quantity) {
        return service.updateQuantity(id, quantity);
    }

    @GetMapping("/available/{id}/{qty}")
    public boolean available(@PathVariable String id, @PathVariable int qty) {
        return service.checkAvailability(id, qty);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> badRequest(IllegalArgumentException exception) {
        return ResponseEntity.badRequest().body(Map.of("message", exception.getMessage()));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, String>> notFound(NoSuchElementException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", exception.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> conflict(IllegalStateException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("message", exception.getMessage()));
    }
}
