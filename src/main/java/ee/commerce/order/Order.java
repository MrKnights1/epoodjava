package ee.commerce.order;

import java.math.BigDecimal;

/**
 * Base component interface for the Decorator pattern.
 * Represents an order in the e-commerce system.
 * 
 * This interface defines the contract that all orders (basic and decorated) must follow.
 * It supports the Open/Closed Principle - open for extension (via decorators), 
 * closed for modification.
 */
public interface Order {
    
    /**
     * Calculates the total price of the order including all decorations/services.
     * 
     * @return the total price as BigDecimal for precise monetary calculations
     */
    BigDecimal calculateTotal();
    
    /**
     * Returns a detailed description of the order including all added services.
     * 
     * @return human-readable description of the order
     */
    String getDescription();
}
