package za.ac.cput.logisticmanagementsystem.factory;

import za.ac.cput.logisticmanagementsystem.domain.Inventory;
import za.ac.cput.logisticmanagementsystem.util.Helper;

import java.util.UUID;

public class InventoryFactory {

    public static Inventory createInventory(String itemName,
                                            String sku,
                                            int quantity,
                                            double unitWeight,
                                            String companyId) {
        if (Helper.isNullOrEmpty(itemName) || Helper.isNullOrEmpty(sku)
                || Helper.isNullOrEmpty(companyId) || quantity < 0
                || !Double.isFinite(unitWeight) || unitWeight < 0) {
            return null;
        }

        return new Inventory.Builder()
                .setInventoryId(UUID.randomUUID().toString())
                .setItemName(itemName.trim())
                .setSku(sku.trim())
                .setQuantityAvailable(quantity)
                .setUnitWeight(unitWeight)
                .setCompanyId(companyId.trim())
                .build();
    }
}
