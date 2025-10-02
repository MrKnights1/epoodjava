package ee.commerce.order;

import java.math.BigDecimal;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basic concrete implementation of Order interface.
 * Represents a simple order with a base price and product name.
 * 
 * This is the base component in the Decorator pattern.
 * All additional services will be added by wrapping this object with decorators.
 */
public class BasicOrder implements Order {
    
    private static final Logger logger = LoggerFactory.getLogger(BasicOrder.class);
    
    private final String productName;
    private final BigDecimal basePrice;
    
    /**
     * Creates a new basic order.
     * 
     * @param productName name of the ordered product
     * @param basePrice base price of the product (must be non-negative)
     * @throws IllegalArgumentException if productName is null/empty or basePrice is negative
     */
    public BasicOrder(String productName, BigDecimal basePrice) {
        if (productName == null || productName.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be null or empty");
        }
        if (basePrice == null || basePrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Base price cannot be null or negative");
        }
        
        this.productName = productName;
        this.basePrice = basePrice;
        
        logger.debug("Created BasicOrder: {} with price {}", productName, basePrice);
    }
    
    @Override
    public BigDecimal calculateTotal() {
        return basePrice;
    }
    
    @Override
    public String getDescription() {
        return productName;
    }
    
    /**
     * Gets the product name.
     * 
     * @return product name
     */
    public String getProductName() {
        return productName;
    }
    
    /**
     * Gets the base price.
     * 
     * @return base price
     */
    public BigDecimal getBasePrice() {
        return basePrice;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BasicOrder that = (BasicOrder) o;
        return Objects.equals(productName, that.productName) && 
               Objects.equals(basePrice, that.basePrice);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(productName, basePrice);
    }
    
    @Override
    public String toString() {
        return String.format("BasicOrder{product='%s', price=%.2f€}", 
                           productName, basePrice);
    }
}
