package ee.commerce.order.model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Represents a product in the catalog.
 * Immutable value object containing product information.
 */
public class Product {
    
    private final int id;
    private final String name;
    private final BigDecimal price;
    private final String description;
    
    /**
     * Creates a new product.
     * 
     * @param id unique product identifier
     * @param name product name
     * @param price product price
     * @param description product description
     */
    public Product(int id, String name, BigDecimal price, String description) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be null or empty");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Product price cannot be null or negative");
        }
        
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description != null ? description : "";
    }
    
    public int getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public String getDescription() {
        return description;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id == product.id;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return String.format("Product{id=%d, name='%s', price=%.2f€}", 
            id, name, price);
    }
}
