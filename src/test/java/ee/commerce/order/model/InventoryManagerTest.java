package ee.commerce.order.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for InventoryManager singleton.
 * Tests thread-safety and inventory operations.
 */
@DisplayName("Inventory Manager Tests")
class InventoryManagerTest {
    
    private InventoryManager inventory;
    
    @BeforeEach
    void setUp() {
        inventory = InventoryManager.getInstance();
        // Reset inventory to known state
        inventory.releaseStock(1, 100); // Ensure enough stock
    }
    
    @Test
    @DisplayName("Should return singleton instance")
    void testSingletonInstance() {
        InventoryManager instance1 = InventoryManager.getInstance();
        InventoryManager instance2 = InventoryManager.getInstance();
        
        assertSame(instance1, instance2);
    }
    
    @Test
    @DisplayName("Should check stock availability")
    void testStockAvailability() {
        assertTrue(inventory.isInStock(1));
        assertTrue(inventory.getStock(1) >= 5);
        
        // Should return false for non-existent products
        assertFalse(inventory.isInStock(999));
    }
    
    @Test
    @DisplayName("Should get current stock level")
    void testGetStockLevel() {
        int stock = inventory.getStock(1);
        assertTrue(stock >= 0);
        
        // Non-existent product should return 0
        assertEquals(0, inventory.getStock(999));
    }
    
    @Test
    @DisplayName("✅ AC5: Should reserve stock when payment succeeds")
    void testReserveStock() {
        int initialStock = inventory.getStock(1);
        assertTrue(inventory.reserveStock(1, 2));
        
        int newStock = inventory.getStock(1);
        assertEquals(initialStock - 2, newStock);
    }
    
    @Test
    @DisplayName("Should not reserve insufficient stock")
    void testInsufficientStock() {
        int currentStock = inventory.getStock(1);
        
        // Try to reserve more than available
        assertFalse(inventory.reserveStock(1, currentStock + 100));
        
        // Stock should remain unchanged
        assertEquals(currentStock, inventory.getStock(1));
    }
    
    @Test
    @DisplayName("Should release stock back to inventory")
    void testReleaseStock() {
        int initialStock = inventory.getStock(2);
        
        // Reserve some stock
        assertTrue(inventory.reserveStock(2, 3));
        assertEquals(initialStock - 3, inventory.getStock(2));
        
        // Release it back
        inventory.releaseStock(2, 3);
        assertEquals(initialStock, inventory.getStock(2));
    }
    
    @Test
    @DisplayName("Should reject zero quantity reservations")
    void testZeroQuantityReservation() {
        int initialStock = inventory.getStock(3);
        
        // Zero quantity is invalid
        assertFalse(inventory.reserveStock(3, 0));
        assertEquals(initialStock, inventory.getStock(3));
    }
    
    @Test
    @DisplayName("Should handle negative quantity gracefully")
    void testNegativeQuantity() {
        int initialStock = inventory.getStock(3);
        
        assertFalse(inventory.reserveStock(3, -5));
        assertEquals(initialStock, inventory.getStock(3));
    }
    
    @Test
    @DisplayName("Should be thread-safe for concurrent reservations")
    void testThreadSafety() throws InterruptedException {
        final int productId = 4;
        final int threads = 10;
        final int reservationsPerThread = 2;
        
        // Ensure we have enough stock
        inventory.releaseStock(productId, 100);
        int initialStock = inventory.getStock(productId);
        
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);
        
        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                try {
                    inventory.reserveStock(productId, reservationsPerThread);
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await(5, TimeUnit.SECONDS);
        executor.shutdown();
        
        int expectedStock = initialStock - (threads * reservationsPerThread);
        int actualStock = inventory.getStock(productId);
        
        assertEquals(expectedStock, actualStock, 
            "Concurrent reservations should be thread-safe");
    }
    
    @Test
    @DisplayName("Should handle concurrent reserve and release operations")
    void testConcurrentReserveAndRelease() throws InterruptedException {
        final int productId = 5;
        final int operations = 20;
        
        inventory.releaseStock(productId, 50);
        int initialStock = inventory.getStock(productId);
        
        ExecutorService executor = Executors.newFixedThreadPool(4);
        CountDownLatch latch = new CountDownLatch(operations);
        
        // Half reserve, half release
        for (int i = 0; i < operations / 2; i++) {
            executor.submit(() -> {
                try {
                    inventory.reserveStock(productId, 1);
                } finally {
                    latch.countDown();
                }
            });
            
            executor.submit(() -> {
                try {
                    inventory.releaseStock(productId, 1);
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await(5, TimeUnit.SECONDS);
        executor.shutdown();
        
        // Stock should be consistent (no data corruption)
        int finalStock = inventory.getStock(productId);
        assertTrue(finalStock >= 0, "Stock should never be negative");
    }
    
    @Test
    @DisplayName("Should initialize with default stock levels")
    void testDefaultStockLevels() {
        // Based on initial stock in InventoryManager
        assertTrue(inventory.isInStock(1)); // Laptop - 15 units
        assertTrue(inventory.isInStock(2)); // Phone - 25 units
        assertTrue(inventory.isInStock(3)); // Headphones - 50 units
        assertTrue(inventory.isInStock(4)); // Book - 100 units
        assertTrue(inventory.isInStock(5)); // Coffee machine - 10 units
    }
}
