package ee.commerce.order.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages product inventory and stock levels.
 * 
 * Thread-safe implementation using ConcurrentHashMap to handle
 * concurrent inventory operations safely.
 * 
 * Following best practices:
 * - Singleton pattern for centralized inventory management
 * - Thread-safety for concurrent access
 * - Logging for all inventory changes
 */
public class InventoryManager {
    
    private static final Logger logger = LoggerFactory.getLogger(InventoryManager.class);
    private static final InventoryManager INSTANCE = new InventoryManager();
    
    // Thread-safe map for inventory tracking
    private final Map<Integer, Integer> stock = new ConcurrentHashMap<>();
    
    /**
     * Private constructor for singleton pattern.
     */
    private InventoryManager() {
        initializeInventory();
    }
    
    /**
     * Gets the singleton instance.
     * 
     * @return inventory manager instance
     */
    public static InventoryManager getInstance() {
        return INSTANCE;
    }
    
    /**
     * Initializes inventory with default stock levels.
     */
    private void initializeInventory() {
        // Initialize stock for all products in catalog
        stock.put(1, 15); // Sülearvuti
        stock.put(2, 25); // Nutitelefon
        stock.put(3, 50); // Kõrvaklapid
        stock.put(4, 100); // Raamat
        stock.put(5, 10); // Kohvimasin
        
        logger.info("Inventory initialized with {} products", stock.size());
    }
    
    /**
     * Checks if a product is in stock.
     * 
     * @param productId the product ID
     * @return true if product has stock
     */
    public boolean isInStock(int productId) {
        Integer quantity = stock.get(productId);
        return quantity != null && quantity > 0;
    }
    
    /**
     * Gets the current stock level for a product.
     * 
     * @param productId the product ID
     * @return current stock quantity, or 0 if product not found
     */
    public int getStock(int productId) {
        return stock.getOrDefault(productId, 0);
    }
    
    /**
     * Reserves stock for a product (decreases inventory).
     * This should be called when an order is successfully paid.
     * 
     * @param productId the product ID
     * @param quantity the quantity to reserve
     * @return true if stock was successfully reserved
     */
    public synchronized boolean reserveStock(int productId, int quantity) {
        if (quantity <= 0) {
            logger.warn("Invalid reserve quantity: {}", quantity);
            return false;
        }
        
        Integer currentStock = stock.get(productId);
        if (currentStock == null) {
            logger.error("Product {} not found in inventory", productId);
            return false;
        }
        
        if (currentStock < quantity) {
            logger.warn("Insufficient stock for product {}: requested {}, available {}", 
                       productId, quantity, currentStock);
            return false;
        }
        
        int newStock = currentStock - quantity;
        stock.put(productId, newStock);
        
        logger.info("Reserved {} units of product {}. New stock level: {}", 
                   quantity, productId, newStock);
        
        // Alert if stock is running low
        if (newStock < 5) {
            logger.warn("LOW STOCK ALERT: Product {} has only {} units remaining", 
                       productId, newStock);
        }
        
        return true;
    }
    
    /**
     * Releases reserved stock (increases inventory).
     * This should be called when an order is cancelled or payment fails.
     * 
     * @param productId the product ID
     * @param quantity the quantity to release
     * @return true if stock was successfully released
     */
    public synchronized boolean releaseStock(int productId, int quantity) {
        if (quantity <= 0) {
            logger.warn("Invalid release quantity: {}", quantity);
            return false;
        }
        
        Integer currentStock = stock.get(productId);
        if (currentStock == null) {
            logger.error("Product {} not found in inventory", productId);
            return false;
        }
        
        int newStock = currentStock + quantity;
        stock.put(productId, newStock);
        
        logger.info("Released {} units of product {}. New stock level: {}", 
                   quantity, productId, newStock);
        
        return true;
    }
    
    /**
     * Gets all inventory as a map.
     * Returns a copy to prevent external modification.
     * 
     * @return copy of inventory map
     */
    public Map<Integer, Integer> getAllInventory() {
        return new ConcurrentHashMap<>(stock);
    }
    
    /**
     * Resets inventory to initial state.
     * Useful for testing or system reset.
     */
    public void resetInventory() {
        stock.clear();
        initializeInventory();
        logger.info("Inventory reset to initial state");
    }
}
