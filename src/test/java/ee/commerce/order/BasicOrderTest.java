package ee.commerce.order;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for BasicOrder class.
 * Tests the base component of the Decorator pattern.
 */
@DisplayName("BasicOrder Tests")
class BasicOrderTest {
    
    @Test
    @DisplayName("Should create valid basic order")
    void testCreateBasicOrder() {
        BasicOrder order = new BasicOrder("Test Product", new BigDecimal("100.00"));
        
        assertNotNull(order);
        assertEquals("Test Product", order.getProductName());
        assertEquals(new BigDecimal("100.00"), order.getBasePrice());
        assertEquals(new BigDecimal("100.00"), order.calculateTotal());
        assertEquals("Test Product", order.getDescription());
    }
    
    @Test
    @DisplayName("Should throw exception for null product name")
    void testNullProductName() {
        assertThrows(IllegalArgumentException.class, () -> {
            new BasicOrder(null, new BigDecimal("100.00"));
        });
    }
    
    @Test
    @DisplayName("Should throw exception for empty product name")
    void testEmptyProductName() {
        assertThrows(IllegalArgumentException.class, () -> {
            new BasicOrder("   ", new BigDecimal("100.00"));
        });
    }
    
    @Test
    @DisplayName("Should throw exception for null base price")
    void testNullBasePrice() {
        assertThrows(IllegalArgumentException.class, () -> {
            new BasicOrder("Product", null);
        });
    }
    
    @Test
    @DisplayName("Should throw exception for negative base price")
    void testNegativeBasePrice() {
        assertThrows(IllegalArgumentException.class, () -> {
            new BasicOrder("Product", new BigDecimal("-10.00"));
        });
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"0.00", "0.01", "99.99", "1000.00", "9999.99"})
    @DisplayName("Should accept valid price ranges")
    void testValidPriceRanges(String price) {
        BasicOrder order = new BasicOrder("Product", new BigDecimal(price));
        assertEquals(new BigDecimal(price), order.calculateTotal());
    }
    
    @Test
    @DisplayName("Should implement equals correctly")
    void testEquals() {
        BasicOrder order1 = new BasicOrder("Product", new BigDecimal("100.00"));
        BasicOrder order2 = new BasicOrder("Product", new BigDecimal("100.00"));
        BasicOrder order3 = new BasicOrder("Other", new BigDecimal("100.00"));
        
        assertEquals(order1, order2);
        assertNotEquals(order1, order3);
    }
    
    @Test
    @DisplayName("Should implement hashCode correctly")
    void testHashCode() {
        BasicOrder order1 = new BasicOrder("Product", new BigDecimal("100.00"));
        BasicOrder order2 = new BasicOrder("Product", new BigDecimal("100.00"));
        
        assertEquals(order1.hashCode(), order2.hashCode());
    }
    
    @Test
    @DisplayName("Should provide meaningful toString")
    void testToString() {
        BasicOrder order = new BasicOrder("Product", new BigDecimal("100.00"));
        String result = order.toString();
        
        assertTrue(result.contains("Product"));
        assertTrue(result.contains("100"));
    }
}
